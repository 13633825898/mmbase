/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.security.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.TypeDef;
import org.mmbase.module.builders.MultiRelations;
import org.mmbase.util.StringTagger;
import java.util.*;

/**
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicCloud implements Cloud, Cloneable {

    // link to cloud context
    private BasicCloudContext cloudContext = null;

    // link to typedef object for retrieving type info (builders, etc)
    private TypeDef typedef = null;

    // name of the cloud
    protected String name = null;

    // account of the current user (unique)
    // This is a unique number, unrelated to the user context
    // It is meant to uniquely identify this session to MMBase
    // It is NOT used for authorisation!
    protected String account = null;

    // language
    protected String language = null;

    // description
    // note: in future, this is dependend on language settings!
    protected String description = null;

    // transactions
    protected HashMap transactions = new HashMap();

    // node managers cache
    protected HashMap nodeManagerCache = new HashMap();

    // relation manager cache
    protected HashMap relationManagerCache = new HashMap();

    // parent Cloud, if appropriate
    protected BasicCloud parentCloud=null;

    // Authorization
    private Authorization authorization = null;

    // Authentication
    private Authentication authentication = null;

    // User context
    protected UserContext userContext = null;

    /**
     *  basic constructor for descendant clouds (i.e. Transaction)
     */
    BasicCloud(String cloudName, BasicCloud cloud) {
        cloudContext=cloud.cloudContext;
        parentCloud=cloud;
        typedef = cloud.typedef;
        language=cloud.language;
        if (cloudName==null) {
            name = cloud.name;
        } else {
            name = cloud.name+"."+cloudName;
        }
        description = cloud.description;
        authorization = cloud.authorization;
        authentication = cloud.authentication;
        userContext = cloud.userContext;
        account= cloud.account;
    }

    /**
     *  Constructor to call from the CloudContext class
     *  (package only, so cannot be reached from a script)
     */
    BasicCloud(String cloudName, CloudContext cloudcontext) {
        cloudContext=(BasicCloudContext)cloudcontext;
        typedef = cloudContext.mmb.getTypeDef();
        language = cloudContext.mmb.getLanguage();
        // determine security manager for this cloud

        authorization=((BasicCloudContext)cloudcontext).securityManager.getAuthorization();
        authentication=((BasicCloudContext)cloudcontext).securityManager.getAuthentication();
        userContext=new UserContext();

        // normally, we want the cloud to read it's context from an xml file.
        // the current system does not support multiple clouds yet,
        // so as a temporary hack we set default values

        name = cloudName;
        description = cloudName;
    }

	public Node getNode(int nodenumber) {
	    MMObjectNode node = BasicCloudContext.tmpObjectManager.getNode(account,""+nodenumber);
	    if (node==null) {
	        throw new BasicBridgeException("Node with number "+nodenumber+" does not exist.");
	    } else {
	        assert(Operation.READ,nodenumber);
	        if (node.getNumber()==-1) {
    	        return new BasicNode(node, getNodeManager(node.parent.getTableName()), nodenumber);
    	    } else {
    	        return new BasicNode(node, getNodeManager(node.parent.getTableName()));
    	    }
	    }
	}

	public Node getNode(String nodenumber) {
	    MMObjectNode node = BasicCloudContext.tmpObjectManager.getNode(account,""+nodenumber);
	    if (node==null) {
	        throw new BasicBridgeException("Node with number "+nodenumber+" does not exist.");
	    } else {
	        assert(Operation.READ,node.getNumber());
	        if (node.getNumber()==-1) {
    	        return new BasicNode(node, getNodeManager(node.parent.getTableName()), Integer.parseInt(nodenumber));
    	    } else {
    	        return new BasicNode(node, getNodeManager(node.parent.getTableName()));
    	    }
	    }
	}
	
	public Node getNodeByAlias(String aliasname) {
	    MMObjectNode node = BasicCloudContext.tmpObjectManager.getNode(account,aliasname);
	    if ((node==null) || (node.getNumber()==-1)) {
	        throw new BasicBridgeException("node with alias "+aliasname+" does not exist.");
	    } else {
	        assert(Operation.READ,node.getNumber());
    	    return new BasicNode(node, getNodeManager(node.parent.getTableName()));
	    }
	}

    public NodeManagerList getNodeManagers() {
        Vector nodeManagers = new Vector();
        for(Enumeration builders = cloudContext.mmb.getMMObjects(); builders.hasMoreElements();) {
            MMObjectBuilder bul=(MMObjectBuilder)builders.nextElement();
            if (!(bul instanceof org.mmbase.module.builders.MultiRelations)) {
                nodeManagers.add(bul.getTableName());
            }
        }
       return new BasicNodeManagerList(nodeManagers,this);
    }

    public NodeManager getNodeManager(String nodeManagerName) {
        // cache quicker, and you don't get 2000 nodetypes when you do a search....
        NodeManager nodeManager=(NodeManager)nodeManagerCache.get(nodeManagerName);
        if (nodeManager==null) {
            MMObjectBuilder bul=cloudContext.mmb.getMMObject(nodeManagerName);
            if (bul==null)
    	        throw new BasicBridgeException("Node manager with name "+nodeManagerName+" does not exist.");
            nodeManager=new BasicNodeManager(bul, this);
            nodeManagerCache.put(nodeManagerName,nodeManager);
        }
        return nodeManager;
    }

	/**
     * Retrieves a node manager (aka builder)
     * @param nodeManagerId ID of the NodeManager to retrieve
     * @return the requested <code>NodeManager</code> if the manager exists, <code>null</code> otherwise
     */
    NodeManager getNodeManager(int nodeManagerId) {
        return getNodeManager(typedef.getValue(nodeManagerId));
    }
 	
 	/**
     * Retrieves a RelationManager.
     * Note that the Relationmanager is very strict - you cannot retrieve a manager with source and destination reversed.
     * @param sourceManagerID number of the NodeManager of the source node
     * @param destinationManagerID number of the NodeManager of the destination node
     * @param roleID number of the role
     * @return the requested RelationManager
     */
    RelationManager getRelationManager(int sourceManagerId, int destinationManagerId, int roleId) {
        // cache. pretty ugly but at least you don't get 1000+ instances of a relationmanager
        RelationManager relManager=(RelationManager)relationManagerCache.get(""+sourceManagerId+"/"+destinationManagerId+"/"+roleId);
        if (relManager==null) {
            // XXX adapt for other dir too!
            Enumeration e =cloudContext.mmb.getTypeRel().search("WHERE snumber="+sourceManagerId+" AND dnumber="+destinationManagerId+" AND rnumber="+roleId);
            if (e.hasMoreElements()) {
                MMObjectNode node=(MMObjectNode)e.nextElement();
                relManager = new BasicRelationManager(node,this);
                relationManagerCache.put(""+sourceManagerId+"/"+destinationManagerId+"/"+roleId,relManager);
            }
        }
        return relManager;
    };

    public RelationManagerList getRelationManagers() {
        Vector v= new Vector();
        for(Enumeration e =cloudContext.mmb.getTypeRel().search("");e.hasMoreElements();) {
            v.add((MMObjectNode)e.nextElement());
        }
        return new BasicRelationManagerList(v,this);
    }

    public RelationManager getRelationManager(String sourceManagerName,
            String destinationManagerName, String roleName) {
        // uses getguesed number, maybe have to fix this later
        int r=cloudContext.mmb.getRelDef().getGuessedNumber(roleName);
        if (r==-1) {
            throw new BasicBridgeException("Role "+roleName+" does not exist.");
        }
        int n1=typedef.getIntValue(sourceManagerName);
        if (n1==-1) {
            throw new BasicBridgeException("Source type "+sourceManagerName+" does not exist.");
        }
        int n2=typedef.getIntValue(destinationManagerName);
        if (n2==-1) {
            throw new BasicBridgeException("Destination type "+destinationManagerName+" does not exist.");
        }
        RelationManager rm=getRelationManager(n1,n2,r);
        if (rm==null) {
            throw new BasicBridgeException("Relation manager from "+sourceManagerName+" to "+destinationManagerName+" as "+roleName+" does not exist.");
        } else {
            return rm;
        }
    };

	/**	
 	 * Create unique number
	 */
	static synchronized int uniqueId() {
		try {
			Thread.sleep(1); // A bit paranoid, but just to be sure that not two threads steal the same millisecond.
		} catch (Exception e) {
		}
		return (int)(java.lang.System.currentTimeMillis() % Integer.MAX_VALUE);		
	}

    public Transaction createTransaction() {
        return createTransaction(null);
    }

    public Transaction createTransaction(String name){
      getAccount();
      if (name==null) {
        name="Tran"+uniqueId();
      } else if (transactions.get(name)!=null) {
	        throw new BasicBridgeException("Transaction already exists name = " + name);
      }
      Transaction transaction = new BasicTransaction(name,this);
      transactions.put(name,transaction);
      return transaction;
    }

    public Transaction getTransaction(String name) {
        Transaction tran=(Transaction)transactions.get(name);
        if (tran==null) {
            tran = createTransaction(name);
        }
        return tran;
    }
	
    public CloudContext getCloudContext() {
        return cloudContext;
    }

    public String getName() {
        return name;
    }

    public String getDescription(){
        return description;
    }

  	/**
     * Retrieves the current user accountname (unique)
     * @return the account name
     */
    String getAccount() {
        if (account==null) {
            throw new org.mmbase.security.SecurityException("This is a read-only cloud. You cannot make edits, or log on to, a read-only cloud.");
        }
        return account;
    }
  	
    public boolean logon(String authenticatorName, Object[] parameters) {
        getAccount();
        return authentication.login(authenticatorName,userContext,parameters);
    }

    /**
    * Checks access rights.
    * @param operation the operation to check (READ, WRITE, CREATE, LINK, OWN)
    * @param nodeID the node on which to check the operation
    * @return <code>true</code> if acces sis granted, <code>false</code> otherwise
    */
    boolean check(Operation operation, int nodeID) {
        return authorization.check(userContext,nodeID,operation);
    }
  	
    /**
    * Asserts access rights. throws an exception if an operation is not allowed.
    * @param operation the operation to check (READ, WRITE, CREATE, LINK, OWN)
    * @param nodeID the node on which to check the operation
    */
    void assert(Operation operation, int nodeID) {
        authorization.assert(userContext,nodeID,operation);
    }
  	
    /**
    * initializes access rights for a newly created node
    * @param nodeID the node to init
    */
    void createSecurityInfo(int nodeID) {
        authorization.create(userContext,nodeID);
    }

    /**
    * removes access rights for a removed node
    * @param nodeID the node to init
    */
    void removeSecurityInfo(int nodeID) {
        authorization.remove(userContext,nodeID);
    }

    /**
    * updates access rights for a changed node
    * @param nodeID the node to init
    */
    void updateSecurityInfo(int nodeID) {
        authorization.update(userContext,nodeID);
    }

    /**
    * Copies the cloud and return the clone.
    * @return the copy of this <code>Cloud</code>
    */
    BasicCloud getCopy() {
        BasicCloud cloud=new BasicCloud(null,this);
        cloud.userContext=new UserContext();
        cloud.account="U"+uniqueId();
        return cloud;
    }  	

    public NodeList getList(String nodes, String nodeManagers, String fields, String where, String sorted, String direction, boolean distinct) {
  		
        String pars ="";
  		if (nodes!=null) {
  		    pars+=" NODES='"+nodes+"'";
  		}
  		if (nodeManagers!=null) {
  		    pars+=" TYPES='"+nodeManagers+"'";
  		}
  		if (fields!=null) {
  		    pars+=" FIELDS='"+fields+"'";
  		}
  		if (sorted!=null) {
  		    pars+=" SORTED='"+sorted+"'";
  		}
  		if (direction!=null) {
  		    pars+=" DIR='"+direction+"'";
  		}
  		StringTagger tagger= new StringTagger(pars,
  		                    ' ','=',',','\'');
  		
  		String sdistinct="";
        if (distinct) sdistinct="YES";

        Vector snodes = tagger.Values("NODES");
  		Vector sfields = tagger.Values("FIELDS");
  		Vector tables = tagger.Values("TYPES");
  		Vector orderVec = tagger.Values("SORTED");
  		Vector sdirection =tagger.Values("DIR"); // minstens een : UP
		if (direction==null) {
		    sdirection=new Vector();
		    sdirection.addElement("UP"); // UP == ASC , DOWN =DESC
		}
	  		
  		MultiRelations multirel = (MultiRelations)cloudContext.mmb.getMMObject("multirelations");
  		int nrfields = sfields.size();
  		if (where!=null) {
  		    if (where.trim().equals("")) {
  		        where = null;
  		    } else {
  		        where="WHERE "+where;
  		    }
  		}
  		Vector v = multirel.searchMultiLevelVector(snodes,sfields,sdistinct,tables,where,orderVec,sdirection,MultiRelations.SEARCH_BOTH);
  		if (v!=null) {
  		    NodeManager tempNodeManager = null;
  		    if (v.size()>0) {
  		        tempNodeManager = new VirtualNodeManager((MMObjectNode)v.get(0),this);
  		    }
  		    return new BasicNodeList(v,this,tempNodeManager);
		} else {
      		throw new BasicBridgeException("getList failed, parameters are invalid :" +pars);
        }
    }

}
