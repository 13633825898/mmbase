/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 10 Dec 2000
 */
public class People extends MMObjectBuilder {
	
        // debug level make sure its false in cvs !
	public boolean debug = false;

        // cache the 100 most active people, enh. is to allow
        // people to set it in people.xml
	LRUHashtable cache = new LRUHashtable(100);
	
        /**
        * replace call, when called in format MMBASE-BUILDER-users-xxxxx
        */
	public String replace(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("number")) {
				int i=getNumber(sp.getSessionName());
				if (i!=-1) {
					return(""+i);
				} else {
					return("");
				}
			}
		}
        	return("");
    	}		

        /**
        * get the object number of this user based on the
        * current user/cookie as defined by the key
        */
	public int getNumber(String key) {

		Integer n=(Integer)cache.get(key);
		if (n!=null) {
			if (debug) debug("people positive cache");
			return(n.intValue());
		}

		// its not in cache so lets check since the current
		// way is only by users ask the users builder
		// in the future more ways can be added here
		Users bul=(Users)mmb.getMMObject("users");
		if (bul!=null) {
			int i=bul.getNumber(key);

			if (i!=-1) {
				// lets find a related people
				MMObjectNode node=getNode(i);
				Enumeration e=node.getRelatedNodes("people").elements();
				if (e.hasMoreElements()) {
					MMObjectNode node2=(MMObjectNode)e.nextElement();
					if (node2!=null) {
						int number=node2.getIntValue("number");
						cache.put(key,new Integer(number));
						if (debug) debug("people positive");
						return(number);
					}
				}
			}
		}
		return(-1);
	}

        /**
        * flush caches of the (cookie defined) user
        * also signals the session module
        */
	public void flushCache(String key) {
		cache.remove(key);
	}	

	
}
