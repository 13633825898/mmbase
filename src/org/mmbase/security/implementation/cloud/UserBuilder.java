/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/**
 * Security from within MMBase
 * @author Eduard Witteveen
 */package org.mmbase.security.implementation.cloud;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class UserBuilder extends MMObjectBuilder {
    private static Logger log=Logging.getLoggerInstance(UserBuilder.class.getName());    
    private org.mmbase.util.Encode encoder = null;    
    
    public boolean init() {
        String encoding = (String) getInitParameters().get("encoding");
        if(encoding==null) {
            log.warn("no property 'encoding' defined in '"+getTableName()+".xml' using default encoding");
            encoder = new org.mmbase.util.Encode("MD5");
        }
        else {
            encoder = new org.mmbase.util.Encode(encoding);
        }
        log.info("Using " + encoder.getEncoding() + " as our encoding for password");
        return super.init();
    }

    public boolean setValue(MMObjectNode node,String fieldname, Object originalValue) {
        // the field with the name bar may not be changed.....
        if(fieldname.equals("username")) {
	    Object newValue = node.values.get(fieldname);
	    if(originalValue!=null && !originalValue.equals(newValue)) {
		// restore the original value...
                node.values.put(fieldname,originalValue);
                return false;
	    }
     	}
        else if(fieldname.equals("password")) {
            Object newValue = node.values.get(fieldname);
	    if(originalValue!=null && !originalValue.equals(newValue)) {
		// encode the new value...
                node.values.put(fieldname,encode((String)newValue));
            }
        }		
        return true;
    }   

    public void setDefaults(MMObjectNode node) {
	// set it to '""' so that we know the difference
	node.setValue("password","");
    }
    
    public boolean exists(String username, String password) {
       log.trace("username: '"+username+"' password: '"+password+"'");
        java.util.Enumeration e = searchWithWhere(" username = '"+username+"' ");
	while(e.hasMoreElements()) {       
            MMObjectNode node = (MMObjectNode) e.nextElement();
            if(encode(password).equals(node.getStringValue("password"))) {
                // found it !
                log.trace("username: '"+username+"' password: '"+password+"' found in node #" + node.getNumber());
                return true;
            }
            else {
                log.trace("username: '"+username+"' found in node #" + node.getNumber()+" --> PASSWORDS NOT EQUAL");
            }
        }        
        log.trace("username: '"+username+"' --> USERNAME NOT CORRECT");
        return false;
    }
	
    public String encode(String value) {
	return encoder.encode(value);
    }                
}
