/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;

/**
 * The module which provides access to a filesystem residing in
 * a database
 *
 * @author Daniel Ockeloen
 */
public class sessionInfo {

	private String classname = getClass().getName();

	private String hostname;
	private String cookie;
	private MMObjectNode node;

	Hashtable values = new Hashtable();
	Hashtable setvalues = new Hashtable();

	public void setNode(MMObjectNode node) {
		this.node=node;
	}

	public MMObjectNode getNode() {
		return(node);
	}
	
	public String getCookie() {
		return(cookie);
	}

	public String getValue(String wanted) {
		return((String)values.get(wanted));
	}

	public String setValue(String key,String value) {
		return((String)values.put(key,value));
	}

	/**
	* add a value to a set if its not made yet create it
	*/
	public void addSetValue(String key,String value) {

		//debug("addSetValue("+key+","+value+")");

		Vector v=(Vector)setvalues.get(key);
		if (v==null) {
			// not found so create it
			v=new Vector();
			v.addElement(value);
			setvalues.put(key,v);
			//debug("sessionset="+v.toString());	
		} else {
			if (!v.contains(value)) {
				v.addElement(value);
				//debug("sessionset="+v.toString());	
			}
		}
		//debug("addSetValue() -> getSetString("+key+"): " +getSetString(key));
	}


	/**
	* add a value to a set if its not made yet create it
	*/
	public void putSetValue(String key,String value) {

		//debug("putSetValue("+key+","+value+")");

		Vector v=(Vector)setvalues.get(key);
		if (v==null) {
			// not found so create it
			v=new Vector();
			v.addElement(value);
			setvalues.put(key,v);
			//debug("sessionset="+v.toString());	
		} else {
			v.addElement(value);
			//debug("sessionset="+v.toString());	
		}
	}


	/**
	* add a value to a set if its not made yet create it
	*/
	public void delSetValue(String key,String value) {
		Vector v=(Vector)setvalues.get(key);
		if (v!=null) {
			if (v.contains(value)) {
				v.removeElement(value);
				//debug("sessionset="+v.toString());	
			}
		}
	}


	/**
	* does this set contain the value ?
	*/
	public String containsSetValue(String key,String value) {
		Vector v=(Vector)setvalues.get(key);
		if (v!=null) {
			if (v.contains(value)) {
				return("YES");
			}
		}
		return("NO");
	}


	/**
	* does this set contain the value ?
	*/
	public String clearSet(String key) {
		//debug("sessionset="+key);	
		Vector v=(Vector)setvalues.get(key);
		if (v!=null) {
			v=new Vector();
			setvalues.put(key,v);
			//debug("sessionset="+v.toString());	
		}
		return("");
	}


	/**
	* return the String of a set
	*/
	public String getSetString(String key) {

		//debug("getSetString("+key+")");

		Vector v=(Vector)setvalues.get(key);
		if (v!=null) {
			String result="";
			Enumeration res=v.elements();
			while (res.hasMoreElements()) {
				String tmp=(String)res.nextElement();
				if (result.equals("")) {
					result=tmp;
				} else {
					result+=","+tmp;
				}
			}
			return(result);
		} else {
			// debug("getSetString("+key+"): ERROR: this key is non-existent!");
			return(null);
		}
	}

	/**
	* return the Count of a set
	*/
	public String getSetCount(String key) {

		Vector v=(Vector)setvalues.get(key);
		if (v!=null) {
			return(""+v.size());
		} else {
			return(null);
		}
	}


	/**
	* return the String of a set
	*/
	public String getAvgSet(String key) {
		Vector v=(Vector)setvalues.get(key);
		if (v!=null) {
			int total=0;
			int count=0;
			Enumeration res=v.elements();
			while (res.hasMoreElements()) {
				try {
					String tmp=(String)res.nextElement();
					int tmpi=Integer.parseInt(tmp);
					total+=tmpi;
					count++;
				} catch(Exception e) {}
			}
			int res1=total/count;
			return(""+res1);
		} else {
			return(null);
		}
	}

	public String getHostName() {
		return(hostname);
	}

	public sessionInfo(String hostname, String cookie) {
		this.hostname=hostname;
		this.cookie=cookie;
	}

	public sessionInfo(String hostname) {
		this.hostname=hostname;
	}

	private void debug( String msg )
	{
		System.out.println( "("+this+")"+ classname +": ["+cookie+"] "+ msg );
	}

	public String toString() {
		return("sessionInfo="+values.toString());
	}
}

