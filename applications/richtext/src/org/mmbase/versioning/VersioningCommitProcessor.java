package org.mmbase.versioning;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.processors.CommitProcessor;

import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This commitprocessor copies on every commit the complete node to a 'versioning' table.
 * @author Sander de Boer
 * @author Michiel Meeuwissen
 * @version $Id: VersioningCommitProcessor.java,v 1.6 2008-03-25 09:41:51 michiel Exp $
 * @since
 */

public class VersioningCommitProcessor implements CommitProcessor {

    private static final Logger log = Logging.getLoggerInstance(VersioningCommitProcessor.class);

    private static final long serialVersionUID = 1L;

    public static final String COMMENTS_PROPERTY = "org.mmbase.versioning.comments";

    public static final String VERSION_FIELD   = "version";
    public static final String OBJECT_FIELD    = "object";
    public static final String COMMENTS_FIELD  = "comments";

    public void commit(Node node, Field field) {
        if (node.isChanged()) {
            log.debug("Commiting " + node + "setting version in " + field);
            NodeManager wo = node.getNodeManager();
            String versionBuilder = wo.getProperty("versionbuilder");

            if (versionBuilder == null || "".equals(versionBuilder)) {
                versionBuilder = wo.getName() + "_versions";
            }
            NodeManager wv = node.getCloud().getNodeManager(versionBuilder);
            log.debug("Found the version builder: '" + wv.getName() + "'");

            //clone this version to the versions builder
            Cloud cloud = node.getCloud();

            if (node.getNumber() > 0) { // TODO TODO http://www.mmbase.org/jira/browse/MMB-1632

                Node version = wv.createNode();
                //increase the version of the current node
                int newVersionNo = node.getIntValue(field.getName()) + 1;
                node.setIntValue(field.getName(), newVersionNo);

                cloneNode(node, version);

                version.setNodeValue(OBJECT_FIELD, node);
                version.setIntValue(VERSION_FIELD, newVersionNo);

                version.setStringValue(COMMENTS_FIELD, Casting.toString(cloud.getProperty(COMMENTS_PROPERTY)));
                Object validation = version.getCloud().getProperty(Cloud.PROP_IGNOREVALIDATION);
                version.getCloud().setProperty(Cloud.PROP_IGNOREVALIDATION, Boolean.TRUE);
                // shit..., node fields don't like new nodes.
                version.commit();
                version.getCloud().setProperty(Cloud.PROP_IGNOREVALIDATION, validation);
            }
            // could solve it by in this case using the _old values_ of the node.
            // But there are 2 bugs, which make this work around non-feasible:
            //  http://www.mmbase.org/jira/browse/MMB-1522.
            //  http://www.mmbase.org/jira/browse/MMB-1621 // This would also give a way to get  the 'old values'.
        } else {
            log.service("Node not changed");
        }
    }



    private void cloneNode(Node source, Node dest) {
        NodeManager sourceNm = source.getNodeManager();
        FieldIterator fields = sourceNm.getFields().fieldIterator();
        while (fields.hasNext()) {
           Field field = fields.nextField();
           if (field.getState() != Field.STATE_SYSTEM) {
               String fieldName = field.getName();
               dest.setValueWithoutProcess(fieldName, source.getValue(fieldName));
           }
        }
    }
}
