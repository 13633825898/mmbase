/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 * @application Tools
 * @author Daniel Ockeloen
 * @version $Id: Urls.java,v 1.22 2005-10-06 17:46:39 michiel Exp $
 */
public class Urls extends MMObjectBuilder {
    private static final Logger log = Logging.getLoggerInstance(Urls.class);

    public String getGUIIndicator(MMObjectNode node) {
        String str = node.getStringValue("url");
        if (str != null) {
            if (str.indexOf("http://") == 0) {
                str = str.substring(7);
            }
        }
        return str;
    }

    public String getGUIIndicator(String field,MMObjectNode node) {
        if (field.equals("url")) {
            String url = node.getStringValue("url");
            if (url != null) return "<a href=\"" + url + "\" target=\"extern\">" + url + "</a>";
        }
        return null;
    }


    public String getDefaultUrl(int src) {
        MMObjectNode node = getNode(src);
        return node.getStringValue("url");
    }

    /* (non-Javadoc)
     * @see org.mmbase.module.core.MMObjectBuilder#notify(org.mmbase.core.event.NodeEvent)
     */
    public void notify(NodeEvent event) {
         if(tableName.equals(event.getNode().getBuilder().getTableName())){
             Jumpers jumpers = (Jumpers)mmb.getBuilder("jumpers");
             if (jumpers == null) {
                 log.debug("Urls builder - Could not get Jumper builder");
             } else {
                 jumpers.delJumpCache(""+event.getNode().getNumber());
             }
         }
        super.notify(event);
    }
}
