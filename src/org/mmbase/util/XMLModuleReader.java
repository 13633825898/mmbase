/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util;

import org.xml.sax.InputSource;
import org.mmbase.util.xml.ModuleReader;

/**
 * @javadoc
 * @deprecated-now use org.mmbase.util.xml.ModuleReader
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id: XMLModuleReader.java,v 1.18 2005-09-12 14:07:39 pierre Exp $
 */
public class XMLModuleReader extends ModuleReader {

    public XMLModuleReader(String filename) {
        super(XMLBasicReader.getInputSource(filename));
    }

    public XMLModuleReader(InputSource is) {
        super(is);
    }

    /**
     * Get the version of this module
     */
    public int getModuleVersion() {
        return getVersion();
    }

    /**
     * Get the maintainer of this module
     */
    public String getModuleMaintainer() {
        return getMaintainer();
    }

}
