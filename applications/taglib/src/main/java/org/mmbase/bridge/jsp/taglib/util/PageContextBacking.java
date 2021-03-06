/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;

import javax.servlet.jsp.PageContext;
import java.util.*;
import org.mmbase.util.Casting;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.bridge.jsp.taglib.ContentTag;
import org.mmbase.bridge.jsp.taglib.WriterHelper;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A basic implementation for the backing, using the PageContext itself. It can also store nulls, in
 * contradiction to PageContext.

 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id$
 */

public  class PageContextBacking extends AbstractMap<String, Object> implements Backing {

    private static final Logger log = Logging.getLoggerInstance(PageContextBacking.class);

    private static final int SCOPE = PageContext.PAGE_SCOPE;

    private final transient PageContext pageContext;

    // We also want to store null, pageContext cannot contain those.
    private final Set<String> nulls = new HashSet<String>();

    private final Set<String> jspvars = new HashSet<String>();

    private final Map<String, Object> unwrapped = new HashMap<String, Object>();

    public PageContextBacking(PageContext pc) {
        pageContext = pc;
    }

    @Override
    public void pushPageContext(PageContext pc) {
        assert pageContext == null || pageContext == pc;
        if (log.isDebugEnabled()) {
            log.debug("Pushing " + pageContext + " --> " + pc);
        }
    }

    @Override
    public void pullPageContext(PageContext pc) {

    }

    @Override
    public PageContext getPageContext() {
        return pageContext;
    }

    @Override
    public void setJspVar(PageContext pc, String jspvar, int vartype, Object value) {
        if (jspvar == null) return;
        if (value == null) return;
        jspvars.add(jspvar);
        // When it doesn't, it goes ok. (at least I think that this is the difference between orion and tomcat)
        if (vartype == WriterHelper.TYPE_STRING) {
            // string is final, the wrapped version cannot be string..
            Object v = Casting.unWrap(value);
            if (v == null) return;
            pc.setAttribute(jspvar, v);
        } else {
            pc.setAttribute(jspvar, value);
        }

    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return new AbstractSet<Map.Entry<String, Object>>() {
            Set<String> names = new HashSet<String>(Collections.list(pageContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE)));

            {
                names.addAll(Collections.list(pageContext.getAttributeNamesInScope(PageContext.REQUEST_SCOPE)));
                if (pageContext.getSession() != null) {
                    names.addAll(Collections.list(pageContext.getAttributeNamesInScope(PageContext.SESSION_SCOPE)));
                }
                names.addAll(Collections.list(pageContext.getAttributeNamesInScope(PageContext.APPLICATION_SCOPE)));
                names.addAll(nulls);
            }

            ///Collection<String> names = unwrapped.keySet();
            @Override
            public Iterator<Map.Entry<String, Object>> iterator() {
                return new Iterator<Map.Entry<String, Object>>() {
                    Iterator<String> i = names.iterator();
                    String name;
                    @Override
                    public Map.Entry<String, Object> next() {
                        name = i.next();

                        return new Map.Entry<String, Object>() {
                            @Override
                            public String  getKey() {
                                return name;
                            }
                            @Override
                            public Object getValue() {
                                try {
                                    return pageContext.findAttribute(name);
                                } catch (java.lang.IllegalStateException ise) {
                                    // e.g.: session invalid
                                    log.warn(ise);
                                    return null;
                                }
                            }
                            @Override
                            public Object setValue(Object value) {
                                Object was = pageContext.findAttribute(name);
                                if (value != null) {
                                    pageContext.setAttribute(name, jspvars.contains(name) ? value : Casting.wrap(value, (CharTransformer) pageContext.findAttribute(ContentTag.ESCAPER_KEY)), SCOPE);
                                } else {
                                    pageContext.removeAttribute(name, SCOPE);
                                    nulls.add(name);
                                }
                                return was;
                            }
                        };
                    }
                    @Override
                    public boolean hasNext() {
                        return i.hasNext();
                    }
                    @Override
                    public void remove() {
                        pageContext.removeAttribute(name, SCOPE);
                        nulls.remove(name);
                        unwrapped.remove(name);
                    }
                };
            }
            @Override
            public int size() {
                return names.size();
            }
        };
    }


    @Override
    public Object put(String key, Object value) {
        if (value == null) {
            nulls.add(key);
        } else {
            String k = key;
            Object v = jspvars.contains(key) ? value : Casting.wrap(value, (CharTransformer) pageContext.findAttribute(ContentTag.ESCAPER_KEY));
            pageContext.setAttribute(k, v, SCOPE);
        }
        return unwrapped.put(key, value);
    }

    @Override
    public Object get(Object key) {
        if (key instanceof String) {
            return pageContext.findAttribute((String) key);
        } else {
            return false;
        }
    }
    @Override
    public Object getOriginal(String key) {
        if (key == null) {
            return null; // pageContext cannot accept null keys
        }
        Object value = unwrapped.get(key);
        if (value != null) {
            return value;
        }
        if (pageContext.getRequest() == null) {
            log.warn("PageContext " + pageContext + " has no request.");

            // findAttribute would throw NPE. This is no good, you shouldn't have had the
            // pageContext in the first place. Probably it was stored in a the session or so?
            return null;
        } else {
            try {
                return pageContext.findAttribute(key);
            } catch (Exception e) {
                throw new RuntimeException(" for " + (key == null ? "NULL" : (key.getClass() + ":" + key)) + "  " + e.getMessage() , e);
            }
        }
    }

    @Override
    @SuppressWarnings("element-type-mismatch")
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return pageContext.findAttribute((String) key) != null ||  nulls.contains(key);
        } else {
            return false;
        }
    }

    @Override
    public boolean containsOwnKey(String key) {
        return unwrapped.containsKey(key);
    }

    @Override
    public Map<String, Object> getOriginalMap() {
        return unwrapped;
    }

    void release() {
        nulls.clear();
        unwrapped.clear();
        jspvars.clear();
    }

    @Override
    public boolean isELIgnored() {
        return false;
    }

    @Override
    public String toString() {
        return "PAGECONTEXT BACKING " + super.toString();
    }

}
