/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is 'Media Competence'
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */

package nl.leocms.connectors.Exporter.output.Attachments;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.mmbase.util.logging.*;
import javax.xml.parsers.*;

import org.mmbase.bridge.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.connectors.Exporter.creatingXML;

public class Exporter implements Runnable

{
   private static final Logger log = Logging.getLoggerInstance(Exporter.class);

   public Exporter()
   {
   }


   public void run() {

      log.info("exporting attachments");

      try {
         DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
         Document document = domBuilder.newDocument();
         Element elemRoot = document.createElement("attachments");
         document.appendChild(elemRoot);
         Cloud cloud = CloudFactory.getCloud();
         NodeList nl = cloud.getNodeByAlias("publications").getRelatedNodes("attachments","posrel",null);
         for (int i = 0; i < nl.size(); i++){
            Element elemAttachment = document.createElement("attachment");
            elemRoot.appendChild(elemAttachment);

            Element elemAttachmentId = document.createElement("id");
            elemAttachmentId.appendChild(document.createTextNode(nl.getNode(i).getStringValue("number")));
            elemAttachment.appendChild(elemAttachmentId);

            Element elemAttachmentTitle = document.createElement("title");
            String sTitel_zichtbaar = nl.getNode(i).getStringValue("titel_zichtbaar");
            if (sTitel_zichtbaar!=null&&!sTitel_zichtbaar.equals("0")){
               elemAttachmentTitle.appendChild(document.createTextNode(nl.getNode(i).getStringValue("titel")));
            }
            elemAttachment.appendChild(elemAttachmentTitle);

            Element elemAttachmentDescr = document.createElement("description");
            elemAttachmentDescr.appendChild(document.createTextNode(nl.getNode(i).getStringValue("omschrijving")));
            elemAttachment.appendChild(elemAttachmentDescr);

            Element elemAttachmentFileName = document.createElement("filename");
            elemAttachmentFileName.appendChild(document.createTextNode(
            nl.getNode(i).getStringValue("filename")));
            elemAttachment.appendChild(elemAttachmentFileName);
         }
         creatingXML cxml = new creatingXML();
         cxml.create(document,"attachments");

      }
      catch (Exception e){
         System.out.println(e);
      }
   }
}
