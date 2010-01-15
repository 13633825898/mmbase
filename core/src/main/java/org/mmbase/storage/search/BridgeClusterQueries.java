/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;


public class BridgeClusterQueries extends ClusterQueries {
    private final Cloud cloud;
    public BridgeClusterQueries(Cloud cloud) {
        this.cloud = cloud;
    }

    @Override
    protected int getNumberForAlias(String alias) {
        return cloud.getNode(alias).getNumber();

    }

    @Override
    protected boolean isRelation(String builder) {
        NodeManager insrel = cloud.getNodeManager("insrel");
        NodeManager nm     = cloud.getNodeManager(builder);
        return nm.equals(insrel) || insrel.getDescendants().contains(nm);
    }

    @Override
    protected String getBuilder(int nodeNumber) {
        return cloud.getNode(nodeNumber).getNodeManager().getName();
    }

    @Override
    protected String getParentBuilder(String builder) {
        NodeManager parent = cloud.getNodeManager(builder).getParent();
        return parent == null ? null : parent.getName();
    }

    @Override
    public Field getField(String builder, String fieldName) {
        try {
            return cloud.getNodeManager(builder).getField(fieldName);
        } catch (NotFoundException nfe) {
            throw new IllegalArgumentException(nfe);
        }
    }

    @Override
    public String getTrueTableName(String table) {
        String tab = getTableName(table);
        if (cloud.hasRelationManager(tab)) {
            return cloud.getRelationManager(tab).getName();
        } else {
            return table;
        }
    }

    @Override
    protected boolean optimizeRelationStep(RelationStep rs, int sourceType, int destType, int role, int searchDir) {
        // TODO
        return false;
    }

    @Override
    protected String getBuilder(final String tableAlias, Map<String, Integer> roles) {
        String tableName = getTableName(tableAlias);
        try {
            RelationManager rm = cloud.getRelationManager(tableName);
            System.out.println(cloud);
            String r = rm.getForwardRole();
            roles.put(tableAlias, rm.getNumber());
            return rm.getName();
        } catch (NotFoundException nfe) {
            if (cloud.hasNodeManager(tableName)) {
                return tableName;
            } else {
                throw new IllegalArgumentException("'" + tableAlias + "' is neither a role, nor a builder");
            }
        }
    }


    @Override
    protected Field getNodeField(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }



}