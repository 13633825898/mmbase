/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard;

import org.w3c.dom.*;
import java.util.Date;

/**
 * EditWizard
 * @javadoc
 * @author Kars Veling
 * @since   MMBase-1.6
 * @version $Id: ConnectorCommand.java,v 1.5 2002-04-19 20:20:28 michiel Exp $
 */

public class ConnectorCommand {
    private String id;
    private String name;
    private Document xml;
    private Document responsexml;

    /**
     * @javadoc
     */
    public ConnectorCommand(String aname) throws WizardException {
        name = aname;
        id = new Date().getTime()+"";
        xml = Utils.parseXML("<"+name+" id=\"" + id + "\"/>");
        responsexml = Utils.parseXML("<response/>");
    }

    /**
     * @javadoc
     */
    public void addCommandNode(Node node) {
        Node newnode = xml.getDocumentElement().appendChild(xml.importNode(node.cloneNode(true), true));
    }

    /**
     * @javadoc
     */
    public void addCommandAttr(String name, String value) {
        Utils.setAttribute(xml.getDocumentElement(), name, value);
    }

    /**
     * @javadoc
     */
    public Document getCommandXML() {
        return xml;
    }

    /**
     * @javadoc
     */
    public Document getResponseXML() {
        return responsexml;
    }

    /**
     * @javadoc
     */
    public String getName() {
        return name;
    }

    /**
     * @javadoc
     */
    public String getId() {
        return id;
    }

    /**
     * @javadoc
     */
    public void setResponse(Node responsenode) {
        responsexml = Utils.emptyDocument();
        responsexml.appendChild(responsexml.importNode(responsenode.cloneNode(true), true));
    }

    /**
     * @javadoc
     */
    public boolean hasError() {
        return false;
    }

}
