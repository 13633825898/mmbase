/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage;

import java.util.Map;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.core.*;

/**
 * The StorageManager interface defines how to access a storage device.
 * It contains methods that can be used to query the storage, insert, update, or remove objects,
 * or to change object definitions (adding fields, etc.).
 *
 * @author Pierre van Rooden
 * @since MMBase-1.7
 * @version $Id: StorageManager.java,v 1.11 2003-08-01 08:09:53 pierre Exp $
 */
public interface StorageManager {

    /**
     * Returns the version of this factory implementation.
     * The factory uses this number to verify whether it can handle storage configuration files
     * that list version requirements.
     * @return the version as an integer
     */
    public double getVersion();

    /**
     * Initializes the manager.
     * Called by a StorageManagerFactory when instantiating the manager with the getStorageManager() method.
     * @param factory the StorageManagerFactory instance that created this storage manager.
     * @throws StorageConfigurationException if the initialization failed
     */
    public void init(StorageManagerFactory factory) throws StorageException;

    /**
     * Starts a transaction on this StorageManager instance.
     * All commands passed to the instance will be treated as being in this transaction.
     * If transactions are not supported by the storage, no actual storage transaction is started, but the code continues as if it has.
     * @throws StorageException if the transaction could not be created
     */
    public void beginTransaction() throws StorageException;

    /**
     * Closes any transaction that was started and commits all changes.
     * If transactions are not supported by the storage, nothing really happens (as changes are allready committed), but the code continues as if it has.
     * @throws StorageException if a transaction is not currently active, or an error occurred while committing
     */
    public void commit() throws StorageException;

    /**
     * Cancels any transaction that was started and rollback changes if possible.
     * If transactions are not supported by the storage, nothing really happens (as changes are allready committed),
     * but the code continues as if it has (through in that case it will return false).
     * @return <code>true</code> if changes were rolled back, <code>false</code> if the transaction was
     * canceled but the storage does not support rollback
     * @throws StorageException if a transaction is not currently active, or an error occurred during rollback
     */
    public boolean rollback() throws StorageException;

    /**
     * Gives an unique number for a new node, to be inserted in the storage.
     * This method should work with multiple mmbases
     * @return unique number
     */
    public int createKey();

    /**
     * Retrieve a large text for a specified object field.
     * Implement this method to allow for optimization of storing and retrieving large texts.
     * @param node the node to retrieve the text from
     * @param fieldname the name of the field to retrieve
     * @return the retrieved text
     * @throws StorageException if an error occurred while retrieving the text value
     */
    public String getStringValue(MMObjectNode node, FieldDefs field) throws StorageException;

    /**
     * Retrieve a large binary object (byte array) for a specified object field.
     * Implement this method to allow for optimization of storing and retrieving binary objects.
     * @param node the node to retrieve the byte array from
     * @param fieldname the name of the field to retrieve
     * @return the retrieved byte array
     * @throws StorageException if an error occurred while retrieving the binary value
     */
    public byte[] getBinaryValue(MMObjectNode node, FieldDefs field) throws StorageException;

    /**
     * This method inserts a new object, and registers the change.
     * Only fields with states of DBSTATE_PERSISTENT or DBSTATE_SYSTEM are stored.
     * @param node The node to insert
     * @return The (new) number for this node, or -1 if an error occurs.
     * @throws StorageException if an error occurred during insert
     */
    public int insert(MMObjectNode node) throws StorageException;

    /**
     * Commit this node to the specified builder.
     * @param node The node to commit
     * @throws StorageException if an error occurred during commit
     */
    public void commit(MMObjectNode node) throws StorageException;

    /**
     * Delete a node
     * @param node The node to delete
     * @return <code>true</code> if succesful
     * @throws StorageException if an error occurred during delete
     */
    public void delete(MMObjectNode node) throws StorageException;

    /**
     * Select a node from a specified builder
     * @param builder The builder to select from
     * @param number the number of the node
     * @return the MMObjectNode that was found, or null f it doesn't exist
     * @throws StorageException if an error occurred during the get
     */
    public MMObjectNode getNode(MMObjectBuilder builder, int number) throws StorageException;

    /**
     * Returns the nodetype for a specified nodereference
     * @param number the number of the node
     * @return int the object type or -1 if not found
     * @throws StorageException if an error occurred during selection
     */
    public int getNodeType(int number) throws StorageException;

    /**
     * Create a storage element to store the specified builder's objects.
     * @param builder the builder to create the storage element for
     * @throws StorageException if an error occurred during the creation of the storage element
     */
    public void create(MMObjectBuilder builder) throws StorageException;

    /**
     * Create the basic elements for this storage
     * @return <code>true</code> if the storage was succesfully created
     * @throws StorageException if an error occurred during the creation of the object storage
     */
    public void create() throws StorageException;

    /**
     * Determine if a storage element exists for storing the given builder's objects
     * @param builder the builder to check
     * @return <code>true</code> if the storage element exists, false if it doesn't
     * @throws StorageException if an error occurred while querying the storage
     */
    public boolean created(MMObjectBuilder builder) throws StorageException;

    /**
     * Determine if the basic storage elements exist
     * Basic storage elements include the 'object' storage (where all objects and their types are registered).
     * @return <code>true</code> if basic storage elements exist
     * @throws StorageException if an error occurred while querying the storage
     */
    public boolean created() throws StorageException;

    /**
     * Return the number of objects of a builder in the storage
     * @param builder the builder whose objects to count
     * @return the number of objects the builder has
     * @throws StorageException if the storage element for the builder does not exists
     */
    public int size(MMObjectBuilder builder) throws StorageException;

    /**
     * Return the total number of objects in the storage
     * @return the number of objects
     * @throws StorageException if the basic storage elements do not exist
     */
    public int size() throws StorageException;

    /**
     * Drops the storage of this builder.
     * @param builder the builder whose storage to drop
     * @return <code>true</code> if succesful
     */
    public boolean drop(MMObjectBuilder builder);

    /**
     * Adds a field to the storage of this builder.
     * @param builder the builder whose storage to change
     * @param fieldname the name fo the field to add
     * @return <code>true</code> if succesful
     */
    public boolean addField(MMObjectBuilder builder,String fieldname);

    /**
     * Deletes a field from the storage of this builder.
     * @param builder the builder whose storage to change
     * @param fieldname the name fo the field to delete
     * @return <code>true</code> if succesful
     */
    public boolean removeField(MMObjectBuilder builder,String fieldname);

    /**
     * Changes a field to the storage of this builder.
     * @param builder the builder whose storage to change
     * @param fieldname the name fo the field to change
     * @return <code>true</code> if succesful
     */
    public boolean changeField(MMObjectBuilder builder,String fieldname);

    /**
     * Changes the storage of a builder to match its new configuration.
     * @param builder the builder whose storage to change
     * @return <code>true</code> if succesful
     */
    public boolean updateStorage(MMObjectBuilder builder);

}

