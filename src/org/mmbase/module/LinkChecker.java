/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.logging.*;
/**
 * The linkChecker module detects broken urls in the urls builder
 * and the jumpers builder. if the linkchecker module is active
 * it will at start up (5 minutes after the MMBase initialisation) and start perfoming checks
 * this wil only happend once every time time MMBase has been started.<BR>
 * for the LinckChecker to work the sendmail modules has to be configured and has to be active
 * 
 * @author Rob vermeulen
 * @author Kees Jongenburger
 * @version $Id: LinkChecker.java,v 1.7 2002-03-10 21:33:19 kees Exp $
 **/
public class LinkChecker extends ProcessorModule implements Runnable {

    private static Logger log = Logging.getLoggerInstance(LinkChecker.class.getName());
    Thread kicker = null;
    MMBase mmbase;
    MMObjectBuilder urls;
    MMObjectBuilder jumpers;
    SendMail sendmail;

    public void init() {
	super.init();
	mmbase=(MMBase)getModule("MMBASEROOT");
	urls=(MMObjectBuilder)mmbase.getMMObject("urls");
	jumpers=(MMObjectBuilder)mmbase.getMMObject("jumpers");
	sendmail=(SendMail)getModule("sendmail");
	log.info("Module LinkChecker started");
	start();
    }

    public Vector getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
	return(null);
    }

    public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
	log.debug("CMDS="+cmds);
	log.debug("VARS="+vars);
	return(false);
    }

    public String replace(scanpage sp, String cmds) {
	return "";
    }

    public String getModuleInfo() {
	return("This module checks all urls, Rob Vermeulen");
    }

    public void maintainance() {
    }
	
    /**
     * start the Thread
     * @deprecated start and stop methods of Thread should never be overwritten
     **/
    public void start() {
        /* Start up the main thread */
        if (kicker == null) {
            kicker = new Thread(this,"LinkChecker");
            kicker.start();
        }
    }

    /**
     * stop the Thead
     * @deprecated start and stop methods ot Threads should never be overwritten
     **/
    public void stop() {
        /* Stop thread */
        kicker.setPriority(Thread.MIN_PRIORITY);
        kicker.suspend();
        kicker.stop();
        kicker = null;
    }

    public void run () {
	try { Thread.sleep(300000); } catch (Exception wait) { } //wait 5 minutes
	log.service("LinkChecker starting to check all Jumpers and Urls");

	// init variables for mail.
        String from=getInitParameter("from");
        String to=getInitParameter("to");
	String data="";

        try {
	    // Get the urls builder,  Jumper builder, and sendmail module.
	    if(urls==null) {
		urls=(MMObjectBuilder)mmbase.getMMObject("urls");
	    }
	    if(jumpers==null) {
		jumpers=(MMObjectBuilder)mmbase.getMMObject("jumpers");
	    }
	    if(sendmail==null) {
		sendmail=(SendMail)getModule("sendmail");
	    }

	    // Get all urls.
	    if (urls != null){
		Enumeration e = urls.search("");
		while (e.hasMoreElements()) {
		    MMObjectNode url = (MMObjectNode)e.nextElement();
		    String number = ""+url.getValue("number");
		    String theUrl = ""+url.getValue("url");
		    // Check if an url is correct.
		    if(!checkUrl(theUrl)) {
			data+="Error in url "+theUrl +" (objectnumber="+number+")\n";
		    }
		    try { Thread.sleep(5000); } catch (Exception wait) { } //wait 5 seconds
		}
	    }
	    // Get all jumpers.
	    if (jumpers!= null){
		Enumeration e = jumpers.search("");
		while (e.hasMoreElements()) {
		    MMObjectNode jumper = (MMObjectNode)e.nextElement();
		    String number = ""+jumper.getValue("number");
		    String theUrl = ""+jumper.getValue("url");
		    // Check if an jumper is correct.
		    // only perform if the jumper contains http
		    // we can't check the other jumpers
		    if(theUrl.indexOf("http") != -1 && !checkUrl(theUrl)) {
			data+="Error in jumper "+theUrl +" (objectnumber="+number+")\n";
		    }
		    try { Thread.sleep(5000); } catch (Exception wait) { } //wait 5 seconds
		}
	    }

	    // Send Email if needed.
	    if(!data.equals("")) {
		Mail mail = new Mail(to,from);
		mail.setSubject("List of incorrect urls and jumpers");
		mail.setText(data);
		if (sendmail != null){
		sendmail.sendMail(mail);
		} else {
		    log.warn("LinckChecker requires the sentmail modules to be active");
		}
	    }
        } catch (Exception e) {
            log.error("LinkChecker -> Error in Run() "+e);
        }
    }

    /**
     * Checks if an url exists.
     * @param the url to check
     * @return false if the url does not exist
     * @return true if the url exists
     */
    public boolean checkUrl(String url) {
	URL urlToCheck;
	URLConnection uc;
	String header;

        try {
            urlToCheck = new URL(url);
	    uc = urlToCheck.openConnection();
	    header = uc.getHeaderField(0);	
	    if(header.indexOf("404")!=-1) return false;
        } catch (Exception e) {
	    // The hostname is incorrect, or the url does not contain the prefix http://
	    return false;
        }
	// I don't know if the url is wrong, so lets say it's correct.
	return true;
    }
}
