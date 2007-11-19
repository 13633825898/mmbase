/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.sms;


/**
 * Representation of one SMS.
 *
 * @author Michiel Meeuwissen
 * @version $Id: SMS.java,v 1.3 2007-11-19 12:03:12 michiel Exp $
 */
public interface SMS {
    /**
     * Phone number (origin or destination, depending on context).
     */
    public String getMobile();

    /**
     * The operator which was used to receive or sent the message(.
     */
    public int getOperator();
    /**
     * The actual text.
     */
    public String getMessage();
}
