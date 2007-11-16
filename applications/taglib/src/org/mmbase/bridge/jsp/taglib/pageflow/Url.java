/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import java.util.*;
import java.net.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mmbase.module.core.MMBase; // TODO
import org.mmbase.bridge.jsp.taglib.TaglibException;
import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;
import org.mmbase.util.Casting;
import org.mmbase.util.functions.*;
import org.mmbase.framework.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * <p>
 * A lazy 'URL' creator. A container object that contains all necessary information to construct an URL, but
 * will only do it on actual request (by the {@link #get}) method. This is also what is stored by an
 * url-tag with an id attribute.
 * </p>
 * <p>
 * The creation of the URL is delegated to the MMBase framework.
 * </p>
 * @version $Id: Url.java,v 1.32 2007-11-16 12:11:10 michiel Exp $;
 * @since MMBase-1.9
 */
public class Url implements Comparable, CharSequence, Casting.Unwrappable {
    private static final Logger log = Logging.getLoggerInstance(Url.class);
    private static  final Logger pageLog = Logging.getLoggerInstance(Logging.PAGE_CATEGORY);

    private final ContextReferrerTag tag;
    private final String page;
    //private final Component component;
    protected final Map<String, Object> params;
    protected final Map<String, Object> frameworkParams;
    private final String abs;
    private final boolean encodeUrl;
    private final boolean escapeAmps;

    private String cacheAmp = null;
    private String cacheNoAmp = null;
    private String string = null;
    private final boolean internal;
    private boolean action = false;

    private boolean legacy = false;

    public Url(UrlTag t,
               String p,
               Map<String, Object> framework,
               Map<String, Object> pars,
               boolean intern) throws JspTagException {
        tag = t;
        abs = t.getAbsolute();
        encodeUrl = t.encode();
        escapeAmps = t.escapeAmps();
        page = p;
        params = pars;
        frameworkParams = framework;
        internal = intern;
    }

    public Url(UrlTag t,
               Url u,
               Map<String, Object> framework,
               Map<String, Object> pars,
               boolean intern) throws JspTagException {
        tag = t;
        abs = t.getAbsolute();
        encodeUrl = t.encode();
        escapeAmps = t.escapeAmps();
        page = u.page;
        params = pars;
        frameworkParams = framework;
        internal = intern;
    }

    public Url(ContextReferrerTag t, String p, Component comp) throws JspTagException {
        tag = t;
        abs = "false";
        encodeUrl = true;
        escapeAmps = true;
        page = p;
        params = new HashMap<String, Object>();
        frameworkParams = null;
        internal = false;
    }

    public static Component getComponent(ContextReferrerTag tag) {
        HttpServletRequest req = (HttpServletRequest) tag.getPageContext().getRequest();
        State state =  State.getState(req);
        if (state.isRendering()) {
            return state.getBlock().getComponent();
        } else {
            return null;
        }
    }

    public void setAction() {
        action = true;
    }

    public void setLegacy() {
        this.legacy = true;
    }

    public String getLegacy(boolean writeamp) throws JspTagException {
        Map<String, Object> m = new HashMap<String, Object>();
        m.putAll(params);
        String res = BasicUrlConverter.getUrl(page, m, (HttpServletRequest)tag.getPageContext().getRequest(), writeamp).toString();
        pageLog.service("getting legacy: " + page + " -> " + res);
        return res;
      }

    /**
     * Returns the URL as a String, always without the application context.
     */

    public String get(boolean writeamp) throws JspTagException {
        if (legacy) {
            return getLegacy(writeamp);
        }

        String result = writeamp ? cacheAmp : cacheNoAmp;
        if (result != null) return result;

        // TODO we should not use core.
        Framework framework = MMBase.getMMBase().getFramework();
        // perhaps this?
        //Framework is of course always only relevant for local cloud context.
        //Framework framework = LocalContext.getCloudContext().getFramework();



        Parameters frameworkParameters = framework.createParameters();
        frameworkParameters.setAutoCasting(true);
        tag.fillStandardParameters(frameworkParameters);

        if (frameworkParams != null) {
            for (Map.Entry<String, Object> entry : frameworkParams.entrySet()) {
                frameworkParameters.set(entry.getKey(), entry.getValue());
            }
        }

        if (internal) {
            log.debug("Creating internal url link to page: " + page, new Exception());
            result = framework.getInternalUrl(page, params, frameworkParameters).toString();
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Creating normal url link to page: " + page + " " + params + " fw: " + frameworkParameters);
            }
            result = framework.getUrl(page, params, frameworkParameters, writeamp).toString();
        }

        if (writeamp) {
            cacheAmp = result;
        } else {
            cacheNoAmp = result;
        }
        return result;
    }

    protected boolean useAbsoluteAttribute(StringBuilder show, String p) throws JspTagException {

        if ("".equals(abs) || "false".equals(abs)) {
            return false;
        }

        HttpServletRequest req = (HttpServletRequest) tag.getPageContext().getRequest();

        if (abs.equals("true")) {
            show.append(req.getScheme()).append("://");
            show.append(req.getServerName());
            int port = req.getServerPort();
            String scheme = req.getScheme();
            show.append((port == 80 && "http".equals(scheme)) ||
                        (port == 443 && "https".equals(scheme))
                        ? "" : ":" + port);
        } else if (abs.equals("server")) {
            //show.append("/");
        } else if (abs.equals("context")) {

        } else {
            throw new JspTagException("Unknown value for 'absolute' attribute '" + abs + "' (must be either 'true', 'false', 'server' or 'context')");
        }
        if (! abs.equals("context")) {
            show.append(req.getContextPath());
        }
        char firstChar = p.charAt(0);
        try {
            URI uri;
            if (firstChar != '/') {
                uri = new URI("servlet", req.getServletPath() + "/../" + p, null);
            } else {
                uri = new URI("servlet", p, null);
            }
            uri = uri.normalize(); // resolves .. and so one
            show.append(uri.getSchemeSpecificPart());
        } catch (URISyntaxException  use) {
            throw new TaglibException(use.getMessage(), use);
        }
        return true;
    }

    public String get() {
        if (string != null) return string;
        try {
            String u = get(escapeAmps);
            StringBuilder show = new StringBuilder();
            if (! useAbsoluteAttribute(show, u)) {
                if (u.length() > 0 && u.charAt(0) == '/') {
                    HttpServletRequest req =  (HttpServletRequest) tag.getPageContext().getRequest();
                    show.insert(0, req.getContextPath());
                }
                show.append(u);
            }
            if (encodeUrl) {
                HttpServletResponse response = (HttpServletResponse)tag.getPageContext().getResponse();
                string = response.encodeURL(show.toString());
            } else {
                string = show.toString();
            }
        } catch (Throwable e){
            string =  e.toString();
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
        }
        return string;
    }

    protected void invalidate() {
        string = cacheAmp = cacheNoAmp = null;
    }

    public char charAt(int index) {
        return get().charAt(index);
    }
    public int length() {
        return get().length();
    }
    public CharSequence subSequence(int start, int end) {
        return get().subSequence(start, end);
    }

    public String toString() {
        // this means that it is written to page by ${_} and that consequently there _must_ be a body.
        // this is needed when body is not buffered.
        tag.haveBody();
        return get();
    }
    public int compareTo(Object o) {
        return toString().compareTo(Casting.toString(o));
    }
}
