/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.module.database;

import java.sql.*;
import java.util.Map;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * MultiConnection is a replacement class for Connection it provides you a
 * multiplexed and reuseable connections from the connection pool.
 * The main function of this class is to 'log' (keep) the last sql statement passed to it.
 * Another function is to keep state (i.e. notifying that it is busy),
 * and to make itself available again to teh connectionpool once it is finished (closed).
 * <br />
 * This classs has been expanded with dummy code that enables it to be
 * easily adapted for compilation with jdk1.4 and JDBC 3.
 * In order to compile using this new API, comment-out the
 * Savepoint inner class.
 * If you actually use a JDBC 3 driver, you may also want to
 * adapt the new JDBC 3 methods, so that they pass their call to
 * the driver, instead of throwing an UnsupportedOperationException.
 *
 * @sql It would possibly be better to pass the logging of the sql query
 *      to the code that calls the conenction, rather than place it in
 *      the conenction itself, as it's implementation leads to conflicts
 *      between various JDBC versions.
 *      This also goes for freeing the connection once it is 'closed'.
 * @author vpro
 * @author Pierre van Rooden
 * @version $Id: MultiConnection.java,v 1.16 2002-11-07 07:17:10 kees Exp $
 */
public class MultiConnection implements Connection {
    // states
    public final static int CON_UNUSED = 0;
    public final static int CON_BUSY = 1;
    public final static int CON_FINISHED = 2;
    public final static int CON_FAILED = 3;
    
    // logging
    private static Logger log = Logging.getLoggerInstance(MultiConnection.class.getName());
    
    /**
     * @javadoc
     */
    Connection con=null;
    /**
     * @javadoc
     */
    MultiPool parent;
    /**
     * @javadoc
     */
    String lastSql;
    
    private long startTimeMillis=0;
    private int usage=0;
    public int state=0;
    
    /**
     * protected constructor for extending classes, so they can use
     * this with for example only a connection..
     */
    protected MultiConnection() {
        this.con = con;
        this.parent = null;
        state=CON_UNUSED;
    }
    
    /**
     * @javadoc
     */
    MultiConnection(MultiPool parent,Connection con) {
        this.con=con;
        this.parent=parent;
        state=CON_UNUSED;
    }
    
    /**
     * @javadoc
     */
    public String getStateString() {
        if (state==CON_FINISHED) {
            return "Finished";
        } else if (state==CON_BUSY) {
            return "Busy";
        } else if (state==CON_FAILED) {
            return "Failed";
        } else if (state==CON_UNUSED) {
            return "Unused";
        }
        return "Unknown";
    }
    
    /**
     * @javadoc
     */
    public void setLastSQL(String sql) {
        lastSql=sql;
        state=CON_BUSY;
    }
    
    /**
     * @javadoc
     */
    public String getLastSQL() {
        return lastSql;
    }
    
    /**
     * createStatement returns an SQL Statement object
     */
    public Statement createStatement() throws SQLException {
        MultiStatement s=new MultiStatement(this,con.createStatement());
        return s;
    }
    
    /**
     * prepareStatement creates a pre-compiled SQL PreparedStatement object.
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql);
    }
    
    /**
     * prepareCall create a pre-compiled SQL statement that is
     * a call on a stored procedure.
     */
    public CallableStatement prepareCall(String sql) throws SQLException {
        setLastSQL(sql);
        return con.prepareCall(sql);
    }
    
    /**
     *  Convert the given generic SQL statement to the drivers native SQL.
     */
    public String nativeSQL(String query) throws SQLException {
        setLastSQL(query);
        return con.nativeSQL(query);
    }
    
    /**
     * If "autoCommit" is true, then all subsequent SQL statements will
     * be executed and committed as individual transactions.  Otherwise
     * (if "autoCommit" is false) then subsequent SQL statements will
     * all be part of the same transaction , which must be explicitly
     * committed with either a "commit" or "rollback" call.
     * By default new connections are initialized with autoCommit "true".
     */
    public void setAutoCommit(boolean enableAutoCommit) throws SQLException {
        con.setAutoCommit(enableAutoCommit);
    }
    
    
    /**
     * get AutoCommit mode
     */
    public boolean getAutoCommit() throws SQLException {
        return(con.getAutoCommit());
    }
    
    /**
     * Perform commit
     */
    public void commit() throws SQLException {
        con.commit();
    }
    
    /**
     * Perform rollback
     */
    public void rollback() throws SQLException {
        con.rollback();
    }
    
    /**
     * Close connections
     */
    public void close() throws SQLException {
        log.debug("SQL querytimer measures " + (System.currentTimeMillis() - getStartTimeMillis()) + " mSec for query: " + getLastSQL());
        state=CON_FINISHED;
        parent.putBack(this);
    }
    
    /**
     * Close connections
     */
    public void realclose() throws SQLException {
        con.close();
    }
    
    /**
     * isClosed returns true if the connection is closed, which can
     * occur either due to an explicit call on "close" or due to
     * some fatal error on the connection.
     */
    public boolean isClosed() throws SQLException {
        return con.isClosed();
    }
    
    /**
     * Advanced features:
     * You can obtain a DatabaseMetaData object to get information
     * about the target database.
     */
    public DatabaseMetaData getMetaData() throws SQLException {
        return con.getMetaData();
    }
    
    /**
     * You can put a connection in read-only mode as a hint to enable
     * database optimizations.  Note that setReadOnly cannot be called
     * while in the middle of a transaction.
     */
    public void setReadOnly(boolean readOnly) throws SQLException {
        con.setReadOnly(readOnly);
    }
    
    /**
     * Is this database readonly ?
     */
    public boolean isReadOnly() throws SQLException {
        return con.isReadOnly();
    }
    
    /**
     * The "catalog" selects a sub-space of the target database.
     */
    public void setCatalog(String catalog) throws SQLException {
        con.setCatalog(catalog);
    }
    
    /**
     * The "catalog" name
     */
    public String getCatalog() throws SQLException {
        return con.getCatalog();
    }
    
    /**
     * You can call the following method to try to change the transaction
     * isolation level on a newly opened connection, using one of the
     * TRANSACTION_* values.  Use the DatabaseMetaData class to find what
     * isolation levels are supported by the current database.
     * Note that setTransactionIsolation cannot be called while in the
     * middle of a transaction.
     */
    public void setTransactionIsolation(int level) throws SQLException {
        con.setTransactionIsolation(level);
    }
    
    /**
     * @javadoc
     */
    public int getTransactionIsolation() throws SQLException {
        return con.getTransactionIsolation();
    }
    
    /**
     * getWarnings will return any warning information related to
     * the current connection.  Note that SQLWarning may be a chain.
     */
    public SQLWarning getWarnings() throws SQLException {
        return con.getWarnings();
    }
    
    /**
     * clear Warnings
     */
    public void clearWarnings() throws SQLException {
        con.clearWarnings();
    }
    
    /**
     * @javadoc
     */
    public boolean checkSQLError(Exception e) {
        log.error("JDBC CHECK ERROR="+e.toString());
        return(true);
    }
    
    /**
     * @javadoc
     */
    public void claim() {
        usage++;
        startTimeMillis = System.currentTimeMillis();
    }
    
    /**
     * @javadoc
     */
    public void release() {
        startTimeMillis=0;
    }
    
    /**
     * @javadoc
     */
    public int getUsage() {
        return usage;
    }
    
    /**
     * @javadoc
     */
    public int getStartTime() {
        return (int)(startTimeMillis/1000);
    }
    
    /**
     * @javadoc
     */
    public long getStartTimeMillis() {
        return startTimeMillis;
    }
    
    
    /**
     * @javadoc
     */
    public String toString() {
        return "'"+getLastSQL()+"'@"+hashCode();
    }
    
    /**
     * prepareCall create a pre-compiled SQL statement that is
     * a call on a stored procedure.
     */
    public CallableStatement prepareCall(String sql, int i, int y) throws SQLException {
        setLastSQL(sql);
        return con.prepareCall(sql,i,y);
    }
    
    /**
     * @javadoc
     */
    public void setTypeMap(Map mp) throws SQLException {
        con.setTypeMap(mp);
    }
    
    /**
     * @javadoc
     */
    public Map getTypeMap() throws SQLException {
        return con.getTypeMap();
    }
    
    /**
     * createStatement returns an SQL Statement object
     */
    public Statement createStatement(int i,int y) throws SQLException {
        return new MultiStatement(this,con.createStatement(i,y));
    }
    
    /**
     * prepareStatement creates a pre-compiled SQL PreparedStatement object.
     */
    public PreparedStatement prepareStatement(String sql,int i, int y) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql,i,y);
    }
    
    // Note: JDBC 1.4 methods
    
    /**
     * Changes the holdability of ResultSet objects created using this Connection
     * object to the given holdability.
     * @param holdability the holdability, one of ResultSet.HOLD_CURSORS_OVER_COMMIT or ResultSet.CLOSE_CURSORS_AT_COMMIT
     * @javadoc
     * @since MMBase 1.5, JDBC 1.4
     */
    public void setHoldability(int holdability) throws SQLException {
        throw new UnsupportedOperationException("only available in JDBC 1.4");
        //         con.setHoldability(holdability);
    }
    
    /**
     * Retrieves the current holdability of ResultSet objects created using this Connection object.
     * @return the holdability, one of ResultSet.HOLD_CURSORS_OVER_COMMIT or ResultSet.CLOSE_CURSORS_AT_COMMIT
     * @javadoc
     * @since MMBase 1.5, JDBC 1.4
     */
    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException("only available in JDBC 1.4");
        //         return con.getHoldability();
    }
    
    /**
     * Creates an unnamed savepoint in the current transaction and returns the new
     * Savepoint object that represents it.
     * @return the new Savepoint object
     * @since MMBase 1.5, JDBC 1.4
     */
    public Savepoint setSavepoint() throws SQLException {
        throw new UnsupportedOperationException("only available in JDBC 1.4");
        //         return con.setSavepoint();
    }
    
    /**
     * Creates a savepoint with the given name in the current transaction and
     * returns the new Savepoint object that represents it.
     * @param name  a String containing the name of the savepoint
     * @return the new Savepoint object
     * @since MMBase 1.5, JDBC 1.4
     */
    public Savepoint setSavepoint(String name) throws SQLException {
        throw new UnsupportedOperationException("only available in JDBC 1.4");
        //         return con.setSavepoint(name);
    }
    
    /**
     * Undoes all changes made after the given Savepoint object was set.
     * This method should be used only when auto-commit has been disabled.
     * @param savepoint the Savepoint object to roll back to
     * @since MMBase 1.5, JDBC 1.4
     */
    public void rollback(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException("only available in JDBC 1.4");
        //         con.rollback(savepoint);
    }
    
    /**
     * Removes the given Savepoint object from the current transaction.
     * Any reference to the savepoint after it have been removed will cause an SQLException to be thrown
     * @param savepoint the Savepoint object to remove
     * @since MMBase 1.5, JDBC 1.4
     */
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new UnsupportedOperationException("only available in JDBC 1.4");
        //         con.releaseSavepoint(savepoint);
    }
    
    /**
     * Creates a Statement object that will generate ResultSet objects with the given type,
     * concurrency, and holdability.
     * @param type one of the following ResultSet constants:
     *        ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE
     * @param concurrency - one of the following ResultSet constants:
     *        ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE
     * @param holdability - one of the following ResultSet constants:
     *        ResultSet.HOLD_CURSORS_OVER_COMMIT or ResultSet.CLOSE_CURSORS_AT_COMMIT
     * @return a new Statement object
     * @since MMBase 1.5, JDBC 1.4
     */
    public Statement createStatement(int type, int concurrency, int holdability)
    throws SQLException {
        throw new UnsupportedOperationException("only available in JDBC 1.4");
        //        return new MultiStatement(this,con.createStatement(type, concurrency,holdability));
    }
    
    /**
     * Creates a PreparedStatement object that will generate ResultSet objects with the given type,
     * concurrency, and holdability.
     * @param sql a String object that is the SQL statement to be sent to the database
     * @param type one of the following ResultSet constants:
     *        ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE
     * @param concurrency - one of the following ResultSet constants:
     *        ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE
     * @param holdability - one of the following ResultSet constants:
     *        ResultSet.HOLD_CURSORS_OVER_COMMIT or ResultSet.CLOSE_CURSORS_AT_COMMIT
     * @return a new PreparedStatement object
     * @since MMBase 1.5, JDBC 1.4
     */
    public PreparedStatement prepareStatement(String sql, int type, int concurrency, int holdability)
    throws SQLException {
        setLastSQL(sql);
        throw new UnsupportedOperationException("only available in JDBC 1.4");
        //         return con.prepareStatement(sql, type, concurrency, holdability);
    }
    
    /**
     * Creates a CallableStatement object that will generate ResultSet objects with the given type,
     * concurrency, and holdability.
     * @param sql a String object that is the SQL statement to be sent to the database
     * @param type one of the following ResultSet constants:
     *        ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, or ResultSet.TYPE_SCROLL_SENSITIVE
     * @param concurrency - one of the following ResultSet constants:
     *        ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE
     * @param holdability - one of the following ResultSet constants:
     *        ResultSet.HOLD_CURSORS_OVER_COMMIT or ResultSet.CLOSE_CURSORS_AT_COMMIT
     * @return a new CallableStatement object
     * @since MMBase 1.5, JDBC 1.4
     */
    public CallableStatement prepareCall(String sql, int type, int concurrency, int holdability)
    throws SQLException {
        setLastSQL(sql);
        throw new UnsupportedOperationException("only available in JDBC 1.4");
        //         return con.prepareCall(sql, type, concurrency, holdability);
    }
    
    /**
     * Creates a default PreparedStatement object that has the capability to retrieve auto-generated keys.
     * The given constant tells the driver whether it should make auto-generated keys available for retrieval.
     * This array is ignored if the SQL statement is not an INSERT statement.
     * @param sql a String object that is the SQL statement to be sent to the database
     * @param autoGeneratedKeys a flag indicating whether auto-generated keys should be returned;
     * @return a new PreparedStatement object
     * @since MMBase 1.5, JDBC 1.4
     */
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        setLastSQL(sql);
        throw new UnsupportedOperationException("only available in JDBC 1.4");
        //         return con.prepareStatement(sql, autoGeneratedKeys);
    }
    
    /**
     * Creates a default PreparedStatement object capable of returning the auto-generated keys designated by
     * the given array. This array contains the indexes of the columns in the target table that contain
     * the auto-generated keys that should be made available. This array is ignored if the SQL statement
     * is not an INSERT statement.
     * @param sql a String object that is the SQL statement to be sent to the database
     * @param columnIndexes an array of column indexes indicating the columns that should be returned from the inserted row or rows
     * @return a new PreparedStatement object
     * @since MMBase 1.5, JDBC 1.4
     */
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        setLastSQL(sql);
        throw new UnsupportedOperationException("only available in JDBC 1.4");
        //         return con.prepareStatement(sql, columnIndexes);
    }
    
    /**
     * Creates a default PreparedStatement object capable of returning the auto-generated keys designated by
     * the given array. This array contains the names of the columns in the target table that contain the
     * auto-generated keys that should be returned. This array is ignored if the SQL statement is not an
     * INSERT statement.
     * @param sql a String object that is the SQL statement to be sent to the database
     * @param columnNames an array of column names indicating the columns that should be returned from the inserted row or rows
     * @return a new PreparedStatement object
     * @since MMBase 1.5, JDBC 1.4
     */
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        setLastSQL(sql);
        throw new UnsupportedOperationException("only available in JDBC 1.4");
        //         return con.prepareStatement(sql, columnNames);
    }
    
    /**
     * Inner Class for the sole us of enabling the code to compile using jdk 1.3 or lower.
     * In order to compile using jdk1.4 and JDBC 3.0, you need to comment this class out.
     * @deprecated this inner class may be removed or commented-out in future releases.
     *              do not use!
     * @since MMBase 1.5
     */
    private class Savepoint {
    }
}


