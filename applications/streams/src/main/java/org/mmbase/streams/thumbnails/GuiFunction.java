/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.thumbnails;

import org.mmbase.bridge.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class GuiFunction extends NodeFunction<String> {

    private static final Logger LOG = Logging.getLoggerInstance(GuiFunction.class);

    public GuiFunction() {
        super("gui", org.mmbase.util.functions.GuiFunction.PARAMETERS);
    }

    @Override
    protected String getFunctionValue(Node node, Parameters parameters) {
        LOG.debug("Field " + parameters.get(Parameter.FIELD));
        if (parameters.get(Parameter.FIELD) == null) {
            Node thumb = ThumbNailFunction.getThumbNail(node, null);
            Function fun = thumb.getFunction("gui");
            Parameters params = fun.createParameters();
            for (Parameter p : org.mmbase.util.functions.GuiFunction.PARAMETERS) {
                params.set(p, parameters.get(p));
            }
            return thumb.getFunctionValue("gui", params).toString();
        } else {
            return null;
        }
    }

}