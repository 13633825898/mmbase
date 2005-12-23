/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.database;

import java.sql.*;
import java.util.Map;

/**
 * Wraps  a java.sql.Connection object. Extendsing this makes it possible to intercept calls.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ConnectionWrapper.java,v 1.1 2005-12-23 14:56:39 michiel Exp $
 * @since MMBase-1.8
 */
public abstract class ConnectionWrapper implements Connection {
    /**
     * The wrapped connection
     */
    protected final Connection con;

    public  ConnectionWrapper(Connection c) {
        con = c;    
    }

    /**
     * Called just before every prepare statement. Can be overridden, because this default implementation is empty.
     */
    protected void setLastSQL(String sql) {
    }
    /**
     * {@inheritDoc}
     */
    public Statement createStatement() throws SQLException { 
        return con.createStatement(); 
    }
    /**
     * {@inheritDoc}
     */
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return con.createStatement(resultSetType, resultSetConcurrency);
    }
    /**
     * {@inheritDoc}
     */
    public Statement createStatement(int type, int concurrency, int holdability) throws SQLException {
        return con.createStatement(type, concurrency, holdability);
    }
    /**
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql);
    }
    /**
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql, autoGeneratedKeys);
    }

    /**
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql, columnIndexes);
    }

    /**
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql, columnNames);
    }
    /**
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(String sql, int type, int concurrency, int holdability) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql, type, concurrency, holdability);
    }

    /**
     * {@inheritDoc}
     */
    public CallableStatement prepareCall(String sql) throws SQLException {
        setLastSQL(sql);
        return con.prepareCall(sql);
    }
    /**
     * {@inheritDoc}
     */
    public String nativeSQL(String query) throws SQLException {
        setLastSQL(query);
        return con.nativeSQL(query);
    }
    /**
     * {@inheritDoc}
     */
    public void setAutoCommit(boolean enableAutoCommit) throws SQLException {
        con.setAutoCommit(enableAutoCommit);
    }
    /**
     * {@inheritDoc}
     */
    public boolean getAutoCommit() throws SQLException {
        return con.getAutoCommit();
    }
    /**
     * {@inheritDoc}
     */
    public void commit() throws SQLException {
        con.commit();
    }

    /**
     * {@inheritDoc}
     */
    public void rollback() throws SQLException {
        con.rollback();
    }
    /**
     * {@inheritDoc}
     */
    public void close() throws SQLException {
        con.close();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isClosed() throws SQLException {
        return con.isClosed();
    }


    /**
     * {@inheritDoc}
     */
    public DatabaseMetaData getMetaData() throws SQLException {
        return con.getMetaData();
    }


    /**
     * {@inheritDoc}
     */
    public void setReadOnly(boolean readOnly) throws SQLException {
        con.setReadOnly(readOnly);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReadOnly() throws SQLException {
        return con.isReadOnly();
    }
    /**
     * {@inheritDoc}
     */
    public void setCatalog(String catalog) throws SQLException {
        con.setCatalog(catalog);
    }

    /**
     * {@inheritDoc}
     */
    public String getCatalog() throws SQLException {
        return con.getCatalog();
    }

    /**
     * {@inheritDoc}
     */
    public void setTransactionIsolation(int level) throws SQLException {
        con.setTransactionIsolation(level);
    }

    /**
     * {@inheritDoc}
     */
    public int getTransactionIsolation() throws SQLException {
        return con.getTransactionIsolation();
    }

    /**
     * {@inheritDoc}
     */
    public SQLWarning getWarnings() throws SQLException {
        return con.getWarnings();
    }

    /**
     * clear Warnings
     */
    /**
     * {@inheritDoc}
     */
    public void clearWarnings() throws SQLException {
        con.clearWarnings();
    }

    /**
     * {@inheritDoc}
     */
    public CallableStatement prepareCall(String sql, int i, int y) throws SQLException {
        setLastSQL(sql);
        return con.prepareCall(sql,i,y);
    }

    /**
     * {@inheritDoc}
     */
    public void setTypeMap(Map mp) throws SQLException {
        con.setTypeMap(mp);
    }

    /**
     * {@inheritDoc}
     */
    public Map getTypeMap() throws SQLException {
        return con.getTypeMap();
    }


    /**
     * {@inheritDoc}
     */
    public PreparedStatement prepareStatement(String sql,int i, int y) throws SQLException {
        setLastSQL(sql);
        return con.prepareStatement(sql,i,y);
    }

    /**
     * {@inheritDoc}
     */
    public void setHoldability(int holdability) throws SQLException {
        con.setHoldability(holdability);
    }
    /**
     * {@inheritDoc}
     */
    public int getHoldability() throws SQLException {
        return con.getHoldability();
    }

    /**
     * {@inheritDoc}
     */
    public Savepoint setSavepoint() throws SQLException {
        return con.setSavepoint();
    }

    /**
     * {@inheritDoc}
     */
    public Savepoint setSavepoint(String name) throws SQLException {
        return con.setSavepoint(name);
    }

    /**
     * {@inheritDoc}
     */
    public void rollback(Savepoint savepoint) throws SQLException {
        con.rollback(savepoint);
    }
    /**
     * {@inheritDoc}
     */
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        con.releaseSavepoint(savepoint);
    }


    /**
     * {@inheritDoc}
     */
    public CallableStatement prepareCall(String sql, int type, int concurrency, int holdability) throws SQLException {
        setLastSQL(sql);
        return con.prepareCall(sql, type, concurrency, holdability);
    }


}


