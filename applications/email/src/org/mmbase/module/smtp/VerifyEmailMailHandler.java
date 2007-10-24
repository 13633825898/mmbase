/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.smtp;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;
import org.mmbase.bridge.*;
import java.util.*;
import java.text.*;
import java.io.*;
import javax.mail.*;

/**
 * This MailHandler explores the Message to see if it just a response to an email verification, and
 * if so, handles it. Otherwise ignores the message.
 * This Handler can be put in front of the {@link ChainedMailedHandler}.
 *
 * @version $Id: VerifyEmailMailHandler.java,v 1.3 2007-10-24 13:40:23 michiel Exp $
 */
public class VerifyEmailMailHandler implements MailHandler {
    private static final Logger log = Logging.getLoggerInstance(VerifyEmailMailHandler.class);


    public boolean handleMessage(Message message) {
        try {
            log.service("Verifying " + message.getSubject());
            Module emailModule = ContextProvider.getDefaultCloudContext().getModule("sendmail");
            String subjectField  = emailModule.getProperty("emailbuilder.subjectfield");
            String subject = message.getSubject();
            Object[] objs = new MessageFormat("{1}" + subjectField).parse(subject, new ParsePosition(0));
            if (objs == null) objs = new MessageFormat(subjectField).parse(subject, new ParsePosition(0));
            if (objs != null) {
                String key = (String) objs[0];
                Cloud cloud = CloudMailHandler.getCloud();
                return org.mmbase.datatypes.VerifyEmailProcessor.validate(cloud, key);
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e);
        }
        return false;
    }

    public boolean addMailbox(String user) {
        return true;
    }
    public void clearMailboxes() {
        return;
    }
    public int size() {
        return 1;
    }

    public static void main(String[] argv) {
        String subject = "Test {0}";
        for (String test : new String[] {"Re: Test @&#*(ARJK", "Test @&#*(ARJK", "  asdfjklajsdf ", "Antwoord op : Test aaaaa"}) {
            Object[] objs = new MessageFormat("{1}" + subject).parse(test, new ParsePosition(0));
            if (objs == null) objs = new MessageFormat(subject).parse(test, new ParsePosition(0));
            if (objs != null) {
                String key = (String) objs[0];
                System.out.println(key);
            } else {
                System.out.println("no match  for " + test);
            }
        }
    }

}
