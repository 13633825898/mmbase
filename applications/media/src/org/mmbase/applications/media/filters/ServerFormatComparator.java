 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import org.mmbase.applications.media.builders.MediaSources;
import org.mmbase.applications.media.Format;
import java.util.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;
import org.mmbase.util.logging.*;

/**
 * This can sort a list with the requested formats on top.
 * @author  Michiel Meeuwissen
 * @version $Id: ServerFormatComparator.java,v 1.3 2003-02-03 22:50:51 michiel Exp $
 */
public class ServerFormatComparator extends  FormatComparator {
    private static Logger log = Logging.getLoggerInstance(ServerFormatComparator.class.getName());

    public static final String CONFIG_TAG = MainFilter.FILTERCONFIG_TAG + ".preferredSource";
    public static final String FORMAT_ATT = "format";

    public  ServerFormatComparator() {};

    public void configure(XMLBasicReader reader, Element el) {
        preferredSources.clear();
        // reading preferredSource information    
        for( Enumeration e = reader.getChildElements(reader.getElementByPath(el, CONFIG_TAG));e.hasMoreElements();) {
            Element n3=(Element)e.nextElement();
            String format = reader.getElementAttributeValue(n3, FORMAT_ATT);
            preferredSources.add(Format.get(format));
            log.service("Adding preferredSource format: '"+format +"'");
        }
  
    }
    
}

