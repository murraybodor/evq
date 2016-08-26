//package ca.aeso.evq.server.junit;
//
//import java.io.PrintWriter;
//import java.sql.CallableStatement;
//import java.sql.Connection;
//import java.sql.DatabaseMetaData;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.sql.SQLWarning;
//import java.sql.Savepoint;
//import java.sql.Statement;
//import java.util.Map;
//import java.util.Properties;
//
//import javax.naming.Reference;
//import javax.sql.DataSource;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//
///**
// * <p>
// * Title: UnitTestDataSource
// * </p>
// * <p>
// * Description: A very simple datasource. Creates a single Connection upon the
// * first call to getConnection(). Every subsequent call returns the same
// * Connection instance.
// * </p>
// * <p>
// * Copyright: Copyright (c) 2002
// * </p> - - - - - - - - - - - - - - - - -
// * <p>
// * You are welcome to do whatever you want to with this source file provided
// * that you maintain this comment fragment (between the dashed lines). Modify
// * it, change the package name, change the class name ... personal or business
// * use ... sell it, share it ... add a copyright for the portions you add ...
// * <p>
// * My goal in giving this away and maintaining the copyright is to hopefully
// * direct developers back to JavaRanch.
// * <p>
// * The original source can be found at <a
// * href=http://www.javaranch.com>JavaRanch</a>
// * <p> - - - - - - - - - - - - - - - - -
// * <p>
// * <p>
// * Company:
// * </p>
// * 
// * @author unascribed
// * @version 1.0
// */
//public class UnitTestDataSource extends Reference implements DataSource {
//
//	private static final long serialVersionUID = 3713188258099607586L;
//
//	protected Log logger = LogFactory.getLog(UnitTestDataSource.class);
//
//	private Connection connection;
//
//	private Properties jdbcProperties = new Properties();
//
//	String dbDriver;
//	String dbServer;
//	String dbLogin;
//	String dbPassword;
//
//	UnitTestDataSource() {
//		super(UnitTestDataSource.class.getName());
//	}
//
//	public void setJdbcProperties(Properties jdbcProps) {
//		this.jdbcProperties.clear();
//		this.jdbcProperties.putAll(jdbcProps);
//	}
//
//	/**
//	 * Method getConnection returns the global Connection instance every time.
//	 * 
//	 * @return Global Connection instance.
//	 * @throws java.sql.SQLException
//	 */
//	public synchronized Connection getConnection()
//	throws java.sql.SQLException {
//
//		if (connection == null) {
//			try {
//				Class.forName(dbDriver);
//			} catch (ClassNotFoundException cnfe) {
//				throw new java.sql.SQLException(cnfe.getMessage());
//			}
//			Properties props = new Properties();
//			props.put("user", dbLogin);
//			props.put("password", dbPassword);
//			props.putAll(jdbcProperties);
//
//			connection = new UnitTestConnection(DriverManager.getConnection(dbServer, props));
//		}
//		return connection;
//	}
//
//	/**
//	 * Method getConnection
//	 * 
//	 * @param parm1
//	 * @param parm2
//	 * @return
//	 * @throws java.sql.SQLException
//	 */
//	public synchronized Connection getConnection(String parm1, String parm2)
//	throws java.sql.SQLException {
//
//		if (connection == null) {
//			connection = getConnection();
//		}
//
//		return connection;
//	}
//
//	/**
//	 * Method getLogWriter not yet implemented.
//	 * 
//	 * @return
//	 * @throws java.sql.SQLException
//	 */
//	public PrintWriter getLogWriter()
//	throws java.sql.SQLException {
//
//		/** @todo: Implement this javax.sql.DataSource method */
//		throw new java.lang.UnsupportedOperationException("Method getLogWriter() not yet implemented.");
//	}
//
//	/**
//	 * Method getLoginTimeout not yet implemented.
//	 * 
//	 * @return
//	 * @throws java.sql.SQLException
//	 */
//	public int getLoginTimeout()
//	throws java.sql.SQLException {
//
//		/** @todo: Implement this javax.sql.DataSource method */
//		throw new java.lang.UnsupportedOperationException("Method getLoginTimeout() not yet implemented.");
//	}
//
//	/**
//	 * Method setLogWriter not yet implemented.
//	 * 
//	 * @param parm1
//	 * @throws java.sql.SQLException
//	 */
//	public void setLogWriter(PrintWriter parm1)
//	throws java.sql.SQLException {
//
//		/** @todo: Implement this javax.sql.DataSource method */
//		throw new java.lang.UnsupportedOperationException("Method setLogWriter() not yet implemented.");
//	}
//
//	/**
//	 * Method setLoginTimeout not yet implemented.
//	 * 
//	 * @param parm1
//	 * @throws java.sql.SQLException
//	 */
//	public void setLoginTimeout(int parm1)
//	throws java.sql.SQLException {
//
//		/** @todo: Implement this javax.sql.DataSource method */
//		throw new java.lang.UnsupportedOperationException("Method setLoginTimeout() not yet implemented.");
//	}
//
//	public class UnitTestConnection implements Connection {
//
//		private Connection unitTestConnection;
//		private boolean closeable = false;
//
//		UnitTestConnection(Connection con) {
//			unitTestConnection = con;
//		}
//
//		public void clearWarnings()
//		throws SQLException {
//			unitTestConnection.clearWarnings();
//		}
//
//		public Connection getWrappedConnection() {
//			return unitTestConnection;
//		}
//
//		public void setConnectionCloseable(boolean closeable) {
//			this.closeable = closeable;
//		}
//
//		public void forceClose()
//		throws SQLException {
//			unitTestConnection.close();
//		}
//
//		public void close()
//		throws SQLException {
//			if (closeable) {
//				unitTestConnection.close();
//			}
//		}
//
//		public void commit()
//		throws SQLException {
//			if (getAutoCommit()) {
//				unitTestConnection.commit();
//			}
//		}
//
//		public Statement createStatement()
//		throws SQLException {
//			return unitTestConnection.createStatement();
//		}
//
//		public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
//		throws SQLException {
//			return unitTestConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
//		}
//
//		public Statement createStatement(int resultSetType, int resultSetConcurrency)
//		throws SQLException {
//			return unitTestConnection.createStatement(resultSetType, resultSetConcurrency);
//		}
//
//		public boolean getAutoCommit()
//		throws SQLException {
//			return unitTestConnection.getAutoCommit();
//		}
//
//		public String getCatalog()
//		throws SQLException {
//			return unitTestConnection.getCatalog();
//		}
//
//		public int getHoldability()
//		throws SQLException {
//			return unitTestConnection.getHoldability();
//		}
//
//		public DatabaseMetaData getMetaData()
//		throws SQLException {
//			return unitTestConnection.getMetaData();
//		}
//
//		public int getTransactionIsolation()
//		throws SQLException {
//			return unitTestConnection.getTransactionIsolation();
//		}
//
//		public Map getTypeMap()
//		throws SQLException {
//			return unitTestConnection.getTypeMap();
//		}
//
//		public SQLWarning getWarnings()
//		throws SQLException {
//			return unitTestConnection.getWarnings();
//		}
//
//		public boolean isClosed()
//		throws SQLException {
//			return unitTestConnection.isClosed();
//		}
//
//		public boolean isReadOnly()
//		throws SQLException {
//			return unitTestConnection.isReadOnly();
//		}
//
//		public String nativeSQL(String sql)
//		throws SQLException {
//			return unitTestConnection.nativeSQL(sql);
//		}
//
//		public CallableStatement prepareCall(String sql,
//				int resultSetType,
//				int resultSetConcurrency,
//				int resultSetHoldability)
//		throws SQLException {
//			return unitTestConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
//		}
//
//		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
//		throws SQLException {
//			return unitTestConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
//		}
//
//		public CallableStatement prepareCall(String sql)
//		throws SQLException {
//			return unitTestConnection.prepareCall(sql);
//		}
//
//		public PreparedStatement prepareStatement(String sql,
//				int resultSetType,
//				int resultSetConcurrency,
//				int resultSetHoldability)
//		throws SQLException {
//			return unitTestConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
//		}
//
//		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
//		throws SQLException {
//			return unitTestConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
//		}
//
//		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
//		throws SQLException {
//			return unitTestConnection.prepareStatement(sql, autoGeneratedKeys);
//		}
//
//		public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
//		throws SQLException {
//			return unitTestConnection.prepareStatement(sql, columnIndexes);
//		}
//
//		public PreparedStatement prepareStatement(String sql, String[] columnNames)
//		throws SQLException {
//			return unitTestConnection.prepareStatement(sql, columnNames);
//		}
//
//		public PreparedStatement prepareStatement(String sql)
//		throws SQLException {
//			return unitTestConnection.prepareStatement(sql);
//		}
//
//		public void releaseSavepoint(Savepoint savepoint)
//		throws SQLException {
//			unitTestConnection.releaseSavepoint(savepoint);
//		}
//
//		public void rollback()
//		throws SQLException {
//			unitTestConnection.rollback();
//		}
//
//		public void rollback(Savepoint savepoint)
//		throws SQLException {
//			unitTestConnection.releaseSavepoint(savepoint);
//		}
//
//		public void setAutoCommit(boolean autoCommit)
//		throws SQLException {
//			unitTestConnection.setAutoCommit(autoCommit);
//		}
//
//		public void setCatalog(String catalog)
//		throws SQLException {
//			unitTestConnection.setCatalog(catalog);
//		}
//
//		public void setHoldability(int holdability)
//		throws SQLException {
//			unitTestConnection.setHoldability(holdability);
//		}
//
//		public void setReadOnly(boolean readOnly)
//		throws SQLException {
//			unitTestConnection.setReadOnly(readOnly);
//		}
//
//		public Savepoint setSavepoint()
//		throws SQLException {
//			return unitTestConnection.setSavepoint();
//		}
//
//		public Savepoint setSavepoint(String name)
//		throws SQLException {
//			return unitTestConnection.setSavepoint(name);
//		}
//
//		public void setTransactionIsolation(int level)
//		throws SQLException {
//			unitTestConnection.setTransactionIsolation(level);
//		}
//
//		public void setTypeMap(Map map)
//		throws SQLException {
//			unitTestConnection.setTypeMap(map);
//		}
//	}
//}
