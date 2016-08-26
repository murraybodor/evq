///*
// * Created on Oct 27, 2003
// *
// */
//package ca.aeso.evq.server.junit;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//import junit.framework.TestCase;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
///**
// * a Test Case that knows how to get a connection. by default will look for
// * test.properties to define connection pramas jdbc.driver, jdbc.user,
// * jdbc.password and jdbc.url
// * 
// */
//public abstract class DBTestCase extends TestCase {
//
//  protected Connection con;
//  protected boolean autoCommit = false;
//  protected static Log logger = LogFactory.getLog(DBTestCase.class);
//  
//  DBConfigurationHelper dbConfigHelper;
//
//  public DBTestCase(String name) {
//    this(name, DBConfigurationHelper.CONFIG_PROPERTIES_FILENAME);
//  }
//
//  public DBTestCase(String name, String fileName) {
//    super(name);
//    initialize(fileName);
//  }
//
//  protected void initialize(String fileName) {
//    dbConfigHelper = new DBConfigurationHelper();
//    dbConfigHelper.setUp(fileName);
//  }
//
//  public void setUp()
//    throws Exception {
//    logger.debug(this.getClass().getName() + " set up called");
//
////    dbConfigHelper.setUp(fileName);
//    con = dbConfigHelper.getConnection(autoCommit);
//    logger.debug("got a connection, con=" + con.toString());
//    
//  }
//
//  public void tearDown() throws Exception {
//    logger.debug("tearDown called");
//    con.rollback();
//    dbConfigHelper.tearDown();
//  }
//
////  public void commit()throws Exception {
////    con.commit();
////  }
//
//  protected void assertRecordExists(String tableName, String whereClause)
//    throws SQLException {
//    assertRecordCount(tableName, whereClause, 1);
//  }
//
//  protected void assertRecordCount(String tableName, String whereClause, int expected)
//    throws SQLException {
//    StringBuffer sql = new StringBuffer();
//
//    sql.append("select count(*) from ");
//    sql.append(tableName);
//    sql.append(" where ");
//    sql.append(whereClause);
//
//    assertEquals(sql.toString(), expected, executeCount(sql, con));
//  }
//
//  protected void clearTable(String tableName) throws SQLException {
//    executeUpdate("delete from " + tableName, con);
//  }
//
//  protected Connection getConnection() throws SQLException {
//    return con;
//  }
//  
//  public static int executeCount(StringBuffer sql, Connection con) throws SQLException {
//	  return executeCount(sql.toString(), con);
//  }
//  
//  public static int executeCount(String sql, Connection con) throws SQLException {
//
//	    logger.debug("<SqlUtil::executeCount> " + sql);
//	    Statement stmt = null;
//	    ResultSet rs = null;
//	    try {
//	      stmt = con.createStatement();
//	      rs = stmt.executeQuery(sql);
//
//	      if (!rs.next()) {
//	        throw new SQLException(sql);
//	      }
//	      return rs.getInt(1);
//	    } finally {
//	      close(rs);
//	      close(stmt);
//	    }
//	}
//	  
//  public static int executeUpdate(StringBuffer sql, Connection con) throws SQLException {
//	  return executeUpdate(sql.toString(), con);
//  }
//
//  public static int executeUpdate(String sql, Connection con) throws SQLException {
//
//	  logger.debug("<SqlUtil::executeupdate> " + sql);
//	  Statement stmt = null;
//	  try {
//		  stmt = con.createStatement();
//		  return stmt.executeUpdate(sql);
//	  } finally {
//		  close(stmt);
//	  }
//  }
//  
//  public static void close(ResultSet rs) {
//
//	    if (rs != null) {
//	      try {
//	        rs.close();
//	      } catch (NullPointerException e) {
//	      } catch (SQLException e) {
//	        e.printStackTrace();
//	      }
//	    }
//	  }
//  
//  public static void close(Statement stmt) {
//
//	    if (stmt != null) {
//	      try {
//	        stmt.close();
//	      } catch (NullPointerException e) {
//	      } catch (SQLException e) {
//	        e.printStackTrace();
//	      }
//	    }
//	  }
//  
//}
