/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import javax.servlet.http.HttpServletRequest;
import org.mmbase.module.core.MMObjectNode;
import java.util.Vector;

/**
 * Though this interface the MediaSourceFilter is able to access filters written
 * by external parties. In the mediasourcefilter configuration file the external
 * filters can be specified.
 *
 * @author  Rob Vermeulen (VPRO)
 */
public interface MediaSourceFilterInterface {
    
    /**
     * this method will filter mediasources. A set with mediasources will be
     * passed as a parameter, and the method will return the filtered set of 
     * mediasources.
     * @param mediasources The set of mediasources available
     * @param mediafragment The requested mediafragment
     * @param request The servlet request of the user
     * @param wantedspeed
     * @param wantedchannels
     * @return A new set of filtered mediasources.
     */
    public Vector filterMediaSource(Vector mediasources, MMObjectNode mediafragment, HttpServletRequest request, int wantedspeed, int wantedchannels);
}

