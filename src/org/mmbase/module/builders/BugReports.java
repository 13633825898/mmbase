/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 * @version 3 Dec 2000
 */
public class BugReports extends MMObjectBuilder {

	private int starttime;

	public BugReports() {
		starttime=(int)(System.currentTimeMillis()/1000);
	}

	
	private boolean nodeChanged(String number,String builder,String ctype) {
		int nowtime=(int)(System.currentTimeMillis()/1000);
		if ((nowtime-starttime)>30) {
			System.out.println("BugReport ="+number+" "+ctype);
			if (ctype.equals("c") && builder.equals("bugreports")) changedReport(number);
		}
		return(true);
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
	        super.nodeLocalChanged(number,builder,ctype);
		return(nodeChanged( number, builder, ctype));
	}

	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
       		super.nodeRemoteChanged(number,builder,ctype);
		return(nodeChanged( number, builder, ctype));
	}

	private void changedReport(String number) {
		MMObjectNode node=getNode(number);


		String body=" You are getting this email because you either are the maintainer \n";
		body+=" submitter or have shown interest in the following bugreport \n\n";
		body+=" Bug #"+node.getIntValue("number")+" ("+node.getStringValue("issue")+")\n\n";
		body+=" Last bug in Time log : \n\n";
		Enumeration t=node.getRelatedNodes("mmevents").elements();
		String lastlog=null;
		while (t.hasMoreElements()) {
			MMObjectNode n3=(MMObjectNode)t.nextElement();
			String name=n3.getStringValue("name");
			int start=n3.getIntValue("start");
			lastlog="\t"+DateSupport.date2date(start)+" "+name+"\n";
		}
		if (lastlog!=null) body+=lastlog;

		// send the email
		Enumeration e=node.getRelatedNodes("people").elements();
		while (e.hasMoreElements()) {
			MMObjectNode n2=(MMObjectNode)e.nextElement();
			String firstname=n2.getStringValue("firstname");
			String lastname=n2.getStringValue("lastname");
			String email=n2.getStringValue("email");
			System.out.println("EMAIL="+email);	

			Mail mail=new Mail(email,"bugs@mmbase.org");
			mail.setSubject("Mail from BugTracker");
			mail.setDate();
			mail.setReplyTo("bugs@mmbase.org"); // should be from
			mail.setText(body);
			if (mmb.getSendMail().sendMail(mail)==false) {
				System.out.println("bugreports -> mail failed");
			}
		}
	}

}
