package com.finalist.cmsc.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import net.sf.mmapps.commons.util.StringUtil;

import com.finalist.cmsc.mmbase.PropertiesUtil;


public class PropertyTag extends SimpleTagSupport {

    /**
     * JSP variable name.
     */
    public String var;
    
	private String key;
	
	public void setKey(String key) {
		this.key = key;
	}

    public void setVar(String var) {
        this.var = var;
    }
    public String getVar() {
        return var;
    } 


	public void doTag() throws IOException {
        String property = PropertiesUtil.getProperty(key);

		PageContext ctx = (PageContext) getJspContext();
        if (!StringUtil.isEmpty(var)) {
            HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
            // put in variable
            if (property != null) {
                request.setAttribute(var, property);
            } else {
                request.removeAttribute(var);
            }
        }
        else {
            ctx.getOut().write(property);
        }
	}

}
