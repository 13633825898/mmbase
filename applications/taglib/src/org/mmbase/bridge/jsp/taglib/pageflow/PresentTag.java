/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.*;

import javax.servlet.jsp.JspTagException;


/**
 * A very simple tag to check if certain id is present in the parent context.
 *
 * @author Michiel Meeuwissen
 * @version $Id: PresentTag.java,v 1.17 2003-09-10 11:16:10 michiel Exp $
 */

public class PresentTag extends ContextReferrerTag implements Condition {

    protected Attribute inverse = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }

    public int doStartTag() throws JspTagException {
        if ((getContextProvider().getContextContainer().isPresent(getReferid())) != getInverse()) {
            return EVAL_BODY_BUFFERED; // EVAL_BODY_INCLUDE not supported by a lot of app-servers
        } else {
            return SKIP_BODY;
        }
    }

    // not needed if EVAL_BODY_INCLUDE
    public int doAfterBody() throws JspTagException {
        if (bodyContent != null) {
            try{
                if(bodyContent != null)
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch(java.io.IOException e){
                throw new TaglibException(e);
            }
        }
        return EVAL_PAGE;
    }
}
