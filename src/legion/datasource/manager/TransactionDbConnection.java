package legion.datasource.manager;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class TransactionDbConnection extends TransactionConnection implements Connection {
	private final static String CLOSED = "Attempted to use Connection after closed() was called.";

	/** The JDBC database connection */
	private Connection connection;
	private Transaction transaction;
	/** Marks whether connection is still usabled. */
	boolean closed;

	public TransactionDbConnection(Connection connection, Transaction transaction) throws Exception {
		this.connection = connection;
		this.transaction = transaction;
		//
		this.connection.setAutoCommit(false);
		closed = connection.isClosed();
	}

	// -------------------------------------------------------------------------------
	// ------------------------override_TransactionConnection-------------------------
	@Override
	public void commitAll() throws SQLException {
		assertOpen();
		connection.commit();
	}

	@Override
	public void release() throws SQLException {
		connection.setAutoCommit(true);
		connection.close();
		closed = true;
		connection = null;
	}

	@Override
	public void rollbackAll() throws SQLException {
		assertOpen();
		connection.rollback();
	}

	// -------------------------------------------------------------------------------
	// ------------------------------override_Connection------------------------------
	/**
	 * Throws an SQLException if closed is true.
	 * 
	 * @throws SQLException
	 */
	private void assertOpen() throws SQLException {
		if (closed)
			throw new SQLException(CLOSED);
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public void clearWarnings() throws SQLException {
		assertOpen();
		connection.clearWarnings();
	}

	/**
	 * Marks the connection as closed, and notifies the pool that the pooled
	 * connection is available. In accordance with the jdbc specification, this
	 * connection cannot be used after closed() is called. Any further usage will
	 * result in an SQLException.
	 * 
	 * @throws SQLException The database connection could not be closed.
	 */
	@Override
	public void close() throws SQLException {
		// close 由交易控制
	}

	/** JDK 1.6 */
	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return null;
	}

	/** JDK 1.6 */
	@Override
	public Blob createBlob() throws SQLException {
		return null;
	}

	/** JDK 1.6 */
	@Override
	public Clob createClob() throws SQLException {
		return null;
	}

	/** JDK 1.6 */
	@Override
	public NClob createNClob() throws SQLException {
		return null;
	}

	/** JDK 1.6 */
	@Override
	public SQLXML createSQLXML() throws SQLException {
		return null;
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public Statement createStatement() throws SQLException {
		assertOpen();
		return connection.createStatement();
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		assertOpen();
		return connection.createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		assertOpen();
		return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/** JDK 1.6 */
	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return null;
	}

	/**
	 * The fializer helps prevent <code>ConnectionPool</code> leakage.
	 */
	@Override
	protected void finalize() throws SQLException {
		if (!closed)
			/*
			 * If this DBConnection object is finalized while linked to a ConnnectionPool,
			 * it means that it was taken from a pool and not returned. We log this fact,
			 * close the underlying connection, and return it to the ConnectionPool.
			 */
			throw new SQLException(
					"A ConnectionImpl was finalized without being closed which will cause leakage of PooledConnections from the ConnectionPool.");
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public boolean getAutoCommit() throws SQLException {
		assertOpen();
		return connection.getAutoCommit();
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public String getCatalog() throws SQLException {
		assertOpen();
		return connection.getCatalog();
	}

	/** JDK 1.6 */
	@Override
	public Properties getClientInfo() throws SQLException {
		return null;
	}

	/** JDK 1.6 */
	@Override
	public String getClientInfo(String name) throws SQLException {
		return null;
	}

	/** JDBC_3_ANT_KEY_BEGIN */
	@Override
	public int getHoldability() throws SQLException {
		assertOpen();
		return connection.getHoldability();
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		assertOpen();
		return connection.getMetaData();
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public int getTransactionIsolation() throws SQLException {
		assertOpen();
		return connection.getTransactionIsolation();
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		assertOpen();
		return connection.getTypeMap();
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		assertOpen();
		return connection.getWarnings();
	}

	/**
	 * Returns true after closed() is called, and false prior to that.
	 * 
	 * @return a <code>boolean</code> value
	 */
	@Override
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public boolean isReadOnly() throws SQLException {
		assertOpen();
		return connection.isReadOnly();
	}

	/** JDK 1.6 */
	@Override
	public boolean isValid(int timeout) throws SQLException {
		return false;
	}

	/** JDK 1.6 */
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public String nativeSQL(String sql) throws SQLException {
		assertOpen();
		return connection.nativeSQL(sql);
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		assertOpen();
		return connection.prepareCall(sql);
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		assertOpen();
		return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		assertOpen();
		return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/**
	 * If pooling of <code>PreparedStatement</code>s is turned on, a pooled object
	 * may be returned, otherwise delegate to the wrapped jdbc 1.x
	 * {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		assertOpen();
		return connection.prepareStatement(sql);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		assertOpen();
		return connection.prepareStatement(sql, autoGeneratedKeys);
	}

	/**
	 * If pooling of <code>PreparedStatement</code>s is turned on in the
	 * DriverAdapterCPDS, a pooled object may be returned, otherwise delegate to the
	 * wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		assertOpen();
		return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		assertOpen();
		return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		assertOpen();
		return connection.prepareStatement(sql, columnIndexes);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		assertOpen();
		return connection.prepareStatement(sql, columnNames);
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public void commit() throws SQLException {
		throw new SQLException("處於交易連線不允許進行commit操作。");
		// assertOpen();
		// connection.commit();
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		throw new SQLException("處於交易連線不允許進行releaseSavepoint操作。");
		// assertOpen();
		// connection.releaseSavepoint(savepoint);
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public void rollback() throws SQLException {
		throw new SQLException("處於交易連線不允許進行rollback操作。");
		// assertOpen();
		// connection.rollback();
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		throw new SQLException("處於交易連線不允許進行rollback操作。");
		// assertOpen();
		// connection.rollback(savepoint);
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public void setAutoCommit(boolean b) throws SQLException {
		throw new SQLException("處於交易連線不允許進行setAutoCommit操作。");
		// assertOpen();
		// connection.setAutoCommit(b);
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public void setCatalog(String catalog) throws SQLException {
		assertOpen();
		connection.setCatalog(catalog);
	}
	
	/** JDK 1.6 */
	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
	}
	
	/** JDK 1.6 */
	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		assertOpen();
		connection.setHoldability(holdability);
	}
	
	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		assertOpen();
		connection.setReadOnly(readOnly);
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		throw new SQLException("處於交易連線不允許進行setSavepoint操作。");
//		assertOpen();
//		connection.setSavepoint();
	}
	
	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		throw new SQLException("處於交易連線不允許進行setSavepoint操作。");
//		assertOpen();
//		connection.setSavepoint(name);
	}

	/**
	 * Pass thru method to the wrapped jdbc 1.x {@link java.sql.Connection}.
	 * 
	 * @throws SQLException if this connection is closed or an error occurs the
	 *                      wrapped connection.
	 */
	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		assertOpen();
		connection.setTransactionIsolation(level);
	}

	@Override
	public String toString() {
		return connection.toString();
	}

	/** JDK 1.6 */
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}
	
	// -------------------------------------------------------------------------------
	// JDBC 4.0
	// will be commeted by the build process on a JDBC 2.0 system
	@Override
	@Deprecated
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		assertOpen();
		connection.setTypeMap(map);
	}
	
	@Override
	@Deprecated
	public void setSchema(String schema) throws SQLException {
		connection.setSchema(schema);
	}
	
	@Override
	@Deprecated
	public String getSchema() throws SQLException{
		return connection.getSchema();
	}
	
	@Override
	@Deprecated
	public void abort(Executor executor) throws SQLException{
		throw new SQLException("not implemented yet...");
//		connection.abort(executor);
	}
	
	@Override
	@Deprecated
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException{
		throw new SQLException("not implemented yet...");
		// connection.setNetworkTimeout(executor, milliseconds);
	}
	
	@Override
	@Deprecated
	public int getNetworkTimeout() throws SQLException{
		return connection.getNetworkTimeout();
	}

	/* JDBC_3_ANT_KEY_END */
}
