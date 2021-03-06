/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext;

import java.util.*;
import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.NodeSearchQuery;

/**
 * The implemention of 'context' is pluggable, and should be returned by {@link
 * Verify#getContextProvider}.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * MMBase-1.9.1
 */
public interface ContextProvider {


    Collection<NodeSearchQuery> getContextQueries();


    /**
     * Sets the context of the given ode.
     */
    void setContext(User user, MMObjectNode node, String context);

    /**
     * Returns the name of the context belonging to a certain context node.
     */
    String getContextName(MMObjectNode context);

    /**
     * Returns the MMObjectNode representing the 'context' of a certain other node
     */
    MMObjectNode getContextNode(MMObjectNode node);


    /**
     * Returns the MMObjectNode representing the 'context' which is identifier with the given name.
     */
    MMObjectNode getContextNode(String context);

    String getContext(User user, MMObjectNode node);

    Set<String> getPossibleContexts(User user, MMObjectNode node)  throws SecurityException;

    Set<String> getPossibleContexts(User user)  throws SecurityException;

    boolean mayDo(User user, MMObjectNode nodeId, Operation operation) throws SecurityException;

    boolean mayDoOnContext(MMObjectNode userNode, MMObjectNode contextNode, Operation operation, boolean checkOwnRights);

    /**
     * Whether, or not,  the user  is allowed to grant the security operation to the group or user on the context
     * node.
     */
    boolean mayGrant(User user,  MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation);
    /**
     * Whether, or not,  the user  is allowed to revoke the security operation to the group or user on the context
     * node.
     */
    boolean mayRevoke(User user, MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation);

    boolean grant(User user, MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation);

    boolean revoke(User user, MMObjectNode contextNode, MMObjectNode groupOrUserNode, Operation operation);


    Authorization.QueryCheck check(User userContext, org.mmbase.bridge.Query query, Operation operation);

    static class AllowingContexts {
        SortedSet<String> contexts;
        boolean inverse;
        AllowingContexts(SortedSet<String> c, boolean i) {
            contexts = c;
            inverse = i;
        }
        public String toString() {
            return (inverse ? "NOT IN " : "IN ") + contexts;
        }

    }


}
