/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

import java.util.Map;
import java.sql.*;
import javax.sql.DataSource;

import org.mmbase.module.core.*;
import org.mmbase.storage.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: DatabaseStorageManager.java,v 1.2 2003-07-24 12:29:05 pierre Exp $
 */
public abstract class DatabaseStorageManager implements StorageManager {

    // logger
    private static Logger log = Logging.getLoggerInstance(DatabaseStorageManager.class);

    /**
     * The factory that created this manager
     */
    protected StorageManagerFactory factory;

    /**
     * The datasource through which to access the database. 
     */
    protected DataSource dataSource;

    /**
     * The currently active Connection.
     * This member is set by {!link #getActiveConnection()} and unset by {@link releaseActiveConnection()}
     */
    protected Connection activeConnection;
    
    /**
     * <code>true</code> if a transaction has been started.
     * This member is for state maitenance and may be true even if the storage does not support transactions
     */
    protected boolean inTransaction = false;

    /**
     * The transaction issolation level to use when starting a transaction.
     * This value is retrieved from the factory's 'database.transactionIsolationLevel' attribute, which is commonly set
     * to the highest (most secure) transaction isolation level available.
     */
    protected int transactionIsolation = Connection.TRANSACTION_NONE;
    
    /**
     * Constructor
     */
    public DatabaseStorageManager() {
    }
    
    public double getVersion() {
        return 1.0;
    }

    /**
     * Initializes the manager.
     * Called by a StorageManagerFactory when instantiating the manager with the getStorageManager() method.
     * @param factory the StorageManagerFactory instance that created this storage manager.
     * @throws StorageConfigurationException if the initialization failed
     */
    public void init(StorageManagerFactory factory) throws StorageException {
        this.factory = factory;
        dataSource = (DataSource)factory.getAttribute("database.dataSource");
        if (factory.supportsTransactions()) {
            transactionIsolation = ((Integer)factory.getAttribute("database.transactionIsolationLevel")).intValue();
        }
    }

    /**
     * Obtains an active connection, opening a new one if needed. 
     * This method sets and then returns the {@link #activeConnection} member.
     * If an active connection was allready open, and the manager is in a database transaction, that connection is returned instead.
     * Otherwise, the connection is closed before a new one is opened.
     * @throws SQLException if opening the connection failed
     */
    protected Connection getActiveConnection() throws SQLException {
        if (activeConnection != null) {
            if (factory.supportsTransactions() && inTransaction) {
                return activeConnection;
            } else {
                releaseActiveConnection();
            }
        }
        activeConnection = dataSource.getConnection();
        return activeConnection;
    }

    /**
     * Safely closes the active connection.
     * If a transaction has been started, the connection is not closed.
     */    
    protected void releaseActiveConnection() {
        if (!(inTransaction && factory.supportsTransactions()) && activeConnection != null) {
            try {
                activeConnection.close();
            } catch (SQLException se) {
                // if something went wrong, log, but do not throw exceptions
                log.error("Failure when closing connection: "+se.getMessage());
            }
            activeConnection = null;
        }
    }
    
    /**
     * Starts a transaction on this StorageManager instance.
     * All commands passed to the instance will be treated as being in this transaction.
     * If transactions are not supported by the database, no actual database transaction created, but the code continues as if it has.
     * @throws StorageException if a transaction is currently active, or a database error occurred
     */
    public void beginTransaction() throws StorageException {
        if (inTransaction) {
            throw new StorageException("Cannot start Transaction when one is already active.");
        } else if (factory.supportsTransactions()) {
            try {
                getActiveConnection();
                activeConnection.setTransactionIsolation(transactionIsolation);
                activeConnection.setAutoCommit(false);
            } catch (SQLException se) {
                releaseActiveConnection();
                throw new StorageException(se);
            } finally {
                releaseActiveConnection();
            }
        }
        inTransaction = true;
    }

    /**
     * Closes any transaction that was started and commits all changes.
     * If transactions are not supported by the database, nothing really happens (as changes are allready committed), but the code continues as if it has.
     * @throws StorageException if a transaction is not currently active, or a database error occurred
     */
    public void commit() throws StorageException {
        if (!inTransaction) {
            throw new StorageException("No transaction started.");
        } else {
            inTransaction = false;
            if (factory.supportsTransactions()) { 
                try {
                    activeConnection.commit();
                } catch (SQLException se) {
                    throw new StorageException(se);
                } finally {
                    releaseActiveConnection();
                }
            }
        }
    }

    /**
     * Cancels any transaction that was started and rollback changes.
     * If transactions are not supported by the database, nothing really happens (as changes are allready committed), 
     * but the code continues as if it has (through in that case it will return false).
     * @return <code>true</code> if changes were rolled back, <code>false</code> if the transaction was 
     * canceled but rollback could not take place.
     * @throws StorageException if a transaction is not currently active, or a database error occurred
     */
    public boolean rollback() throws StorageException {
        if (!inTransaction) {
            throw new StorageException("No transaction started.");
        } else {
            if (factory.supportsTransactions()) {
                inTransaction = false;
                try {
                    activeConnection.rollback();
                } catch (SQLException se) {
                    throw new StorageException(se);
                } finally {
                    releaseActiveConnection();
                }
            }
            return factory.supportsTransactions();
        }
    }

    /**
     * Gives an unique number for a new node, to be inserted in the storage.
     * This method should work with multiple mmbases
     * @return unique number
     */
    abstract public int createKey();

    /**
     * Retrieve a large text for a specified object field.
     * Implement this method to allow for optimization of storing and retrieving large texts.
     * @param node the node to retrieve the text from
     * @param fieldname the name of the field to retrieve
     * @return the retrieved text
     */
    abstract public String getText(MMObjectNode node,String fieldname);

    /**
     * Retrieve a large binary object (byte array) for a specified object field.
     * Implement this method to allow for optimization of storing and retrieving binary objects.
     * @param node the node to retrieve the byte array from
     * @param fieldname the name of the field to retrieve
     * @return the retrieved byte array
     */
    abstract public byte[] getBytes(MMObjectNode node,String fieldname);

    /**
     * This method inserts a new object, and registers the change.
     * Only fields with states of DBSTATE_PERSISTENT or DBSTATE_SYSTEM are stored.
     * @param node The node to insert
     * @return The (new) number for this node, or -1 if an error occurs.
     * @throws StorageException if an error occurred during insert
     */
    abstract public int insert(MMObjectNode node) throws StorageException;

    /**
     * Commit this node to the specified builder.
     * @param node The node to commit
     * @return <code>true</code> of succesful, false otherwise
     * @throws StorageException if an error occurred during commit
     */
    abstract public boolean commit(MMObjectNode node) throws StorageException;

    /**
     * Delete a node
     * @param node The node to delete
     * @return <code>true</code> if succesful
     * @throws StorageException if an error occurred during delete
     */
    abstract public boolean delete(MMObjectNode node) throws StorageException;

    /**
     * Select a node from a specified builder
     * @param builder The builder to select from
     * @param number the number of the node
     * @return the MMObjectNode that was found, or null f it doesn't exist
     * @throws StorageException if an error occurred during the get
     */
    abstract public MMObjectNode getNode(MMObjectBuilder builder, int number) throws StorageException;

    /**
     * Returns the nodetype for a specified nodereference
     * @param number the number of the node
     * @return int the object type or -1 if not found
     * @throws StorageException if an error occurred during selection
     */
    abstract public int getNodeType(int number) throws StorageException;

    /**
     * Create a storage element to store the specified builder's objects.
     * @param builder the builder to create the storage for
     * @return <code>true</code> if the storage was succesfully created
     * @throws StorageException if an error occurred during the creation fo the table
     */
    abstract public boolean create(MMObjectBuilder builder) throws StorageException;

    /**
     * Create the basic elements for this storage
     * @return <code>true</code> if the storage was succesfully created
     * @throws StorageException if an error occurred during the creation of the object storage
     */
    abstract public boolean create() throws StorageException;

    /**
     * Queries the database metadata to test whether a given table exists.
     * @param tableName name of the table to look for
     * @throws StorageException when the metadata could not be retrieved
     * @return <code>true</code> if the table exists
     */
    protected boolean hasTable(String tableName) throws StorageException {
        boolean result = false;
        try {
            getActiveConnection();
            DatabaseMetaData metaData = activeConnection.getMetaData();
            ResultSet res = metaData.getTables(null, null, tableName, null);
            result = res.next();
        } catch (Exception e) {
            throw new StorageException(e.getMessage());
        } finally {
            releaseActiveConnection();
        }
        return result;
    }

    /**
     * Determine if a storage element exists for storing the given builder's objects
     * @param builder the builder to check
     * @return <code>true</code> if a storage element exists
     * @throws StorageException if an error occurred while querying the storage 
     */
    public boolean created(MMObjectBuilder builder) throws StorageException {
        return hasTable(builder.getFullTableName());
    }

    /**
     * Determine if the basic storage elements exist
     * Basic storage elements include the 'object' storage (where all objects and their types are registered).
     * @return <code>true</code> if basic storage elements exist
     * @throws StorageException if an error occurred while querying the storage 
     */
    abstract public boolean created() throws StorageException;

    /**
     * Return the number of objects of a builder in the storage
     * @param builder the builder whose objects to count
     * @return the number of objects the builder has
     * @throws StorageException if the storage element for the builder does not exists
     */
    abstract public int size(MMObjectBuilder builder);

    /**
     * Return the total number of objects in the storage
     * @return the number of objects 
     * @throws StorageException if the basic storage elements do not exist
     */
    abstract public int size();

    /**
     * Drops the storage of this builder.
     * @param builder the builder whose storage to drop
     * @return <code>true</code> if succesful
     */
    abstract public boolean drop(MMObjectBuilder builder);

    /**
     * Adds a field to the storage of this builder.
     * @param builder the builder whose storage to change
     * @param fieldname the name fo the field to add
     * @return <code>true</code> if succesful
     */
    abstract public boolean addField(MMObjectBuilder builder,String fieldname);

    /**
     * Deletes a field from the storage of this builder.
     * @param builder the builder whose storage to change
     * @param fieldname the name fo the field to delete
     * @return <code>true</code> if succesful
     */
    abstract public boolean removeField(MMObjectBuilder builder,String fieldname);

    /**
     * Changes a field to the storage of this builder.
     * @param builder the builder whose storage to change
     * @param fieldname the name fo the field to change
     * @return <code>true</code> if succesful
     */
    abstract public boolean changeField(MMObjectBuilder builder,String fieldname);

    /**
     * Changes the storage of a builder to match its new configuration.
     * @param builder the builder whose storage to change
     * @return <code>true</code> if succesful
     */
    abstract public boolean updateStorage(MMObjectBuilder builder);

}

