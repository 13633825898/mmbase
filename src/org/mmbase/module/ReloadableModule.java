/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;


import org.mmbase.util.XMLModuleReader;
import org.mmbase.util.logging.*;


/**
 * A Reloadable Module has a 'reload' method. You can extend your own modules from this. If you need
 * to happen the reload automaticly, then {@link WatchedReloadableModule}.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id: ReloadableModule.java,v 1.4 2005-01-30 16:46:37 nico Exp $
 */
public abstract class ReloadableModule extends Module {

    private static final Logger log = Logging.getLoggerInstance(ReloadableModule.class);

    /**
     * Reloads the configuration file.
     *
     * The module cannot change class, so if you change that in the XML, an error is logged, and nothing will
     * happen.
     * 
     * This method should be called from your extension if and when the configuration must be reloaded.
     * 
     * @return Whether successful.
     */

    protected boolean reloadConfiguration(String s) {
        XMLModuleReader parser  = new XMLModuleReader(s);
        return reloadConfiguration(parser);
    }

    protected boolean reloadConfiguration(XMLModuleReader parser) {
        if (parser.getStatus().equals("inactive")) {
            log.error("Cannot set module to inactive. " + parser.getFileName() + " Canceling reload");
            return false;
        }
        String className = parser.getClassFile();
        if (! className.equals(getClass().getName())) {
            log.error("Cannot change the class of a module. " + className + " != " + getClass().getName() + " " + parser.getFileName()  + ". Canceling reload.");
            return false;
        }
        
        properties =  parser.getProperties();        
        setMaintainer(parser.getModuleMaintainer());
        setVersion(parser.getModuleVersion());        
        return true;
    }

    
    /**
     * This method should be called when the module should be reloaded. This default implementation is empty.
     */
    
    public void reload() {
    }


}
