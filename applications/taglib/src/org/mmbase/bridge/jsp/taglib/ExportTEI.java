/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;


/**
* The Export tag exports one jsp variable. Some other tags, such as
* CloudTag and NodeProviders can also export jsp variables by
* themselves.
*
* @author Michiel Meeuwissen
*/
public class ExportTEI extends TagExtraInfo {
    
    public ExportTEI() { 
        super(); 
    }

    public VariableInfo[] getVariableInfo(TagData data) {

        Object declare  = data.getAttribute("declare");
        int number;

        if ("false".equals(declare)) {
            number = 0;
        } else {
            number = 1;
        }
       
        VariableInfo[] variableInfo =  new VariableInfo[number];
        
        if (number > 0) {
            String typeAttribute    = (String) data.getAttribute("type"); 
            if (typeAttribute == null) typeAttribute = "Object";
            
            String type = "java.lang.Object";
            
            if ("Object".equalsIgnoreCase(typeAttribute)) {
                type = "java.lang.Object";
            } else if ("String".equalsIgnoreCase(typeAttribute)) {
                type = "java.lang.String";
            } else if ("Node".equalsIgnoreCase(typeAttribute)) {
                type = "org.mmbase.bridge.Node";
            } else {
                //type = "java.lang.Object"; 
                throw new RuntimeException("Unknown type '" + typeAttribute + "'");
            }
            
            String jspvarAttribute  = (String) data.getAttribute("jspvar"); 
            
            variableInfo[0] =  new VariableInfo(jspvarAttribute,
                                                type,
                                                true,
                                                VariableInfo.AT_BEGIN);
        }
        return variableInfo;
    }        
}
