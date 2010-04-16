/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers;

import java.util.*;
import org.mmbase.util.functions.Parameter;
import org.mmbase.bridge.*;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */

public abstract class AbstractRequest implements Request {

    private boolean valid = true;
    private final Map<Parameter<?>, Object> properties = new HashMap<Parameter<?>, Object>();

    public AbstractRequest() {
    }

    @Override
    public void invalidate() {
        valid = false;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public Cloud getCloud() {
        return ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
    }
    @Override
    public Locale getLocale () {
        return getCloud().getLocale();
    }

    @Override
    public <C> C setProperty(Parameter<C> name, C value) {
        return (C) properties.put(name, value);
    }

    @Override
    public<C> C getProperty(Parameter<C> name) {
        return (C) properties.get(name);
    }
}
