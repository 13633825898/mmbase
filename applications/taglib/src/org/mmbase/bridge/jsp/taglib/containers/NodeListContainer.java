/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.TagIdentifier;

/**
 * A NodeList Container can be used around node-list Tags. Basicly, it adminstrates a Query object.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: NodeListContainer.java,v 1.5 2003-08-29 12:12:24 keesj Exp $
 */
public interface NodeListContainer extends TagIdentifier, NodeListContainerOrListProvider {

    /**
     * Returns the currently by the container defined query object. Subtags can use this query
     * object to change it or to use it. 
     */
    Query getQuery();



    Cloud getCloud() throws JspTagException;

}
