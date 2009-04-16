/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.media.urlcomposers.omroep;
import org.mmbase.applications.media.urlcomposers.RealURLComposer;


/**
 * An example. URL's from these kind of URLComposers can contain 'start' and 'end' arguments and so on.
 *
 * @author Michiel Meeuwissen
 * @version $Id: SimpleRealURLComposer.java,v 1.3 2009-04-16 10:28:07 michiel Exp $
 * @since MMBase-1.7
 */
public class SimpleRealURLComposer extends RealURLComposer {

    @Override
    public boolean canCompose() {
        return provider.getStringValue("host").equals("cgi.omroep.nl") && provider.getStringValue("rootpath").charAt(0) != '%';
    }

    @Override
    protected StringBuffer getURLBuffer() {
        StringBuffer buff = new StringBuffer("rtsp://streams.omroep.nl" + source.getStringValue("url"));
        RealURLComposer.getRMArgs(buff, fragment, info); // append time, title, als
        return buff;
    }
}


