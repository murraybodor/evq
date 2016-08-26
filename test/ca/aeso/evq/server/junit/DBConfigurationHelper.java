//package ca.aeso.evq.server.junit;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.DatabaseMetaData;
//import java.sql.SQLException;
//import java.util.Hashtable;
//import java.util.Properties;
//import java.util.concurrent.ConcurrentHashMap;
//
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.sql.DataSource;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
///**
// * Helper class for unit testing against a datasource
// * 
// * @author mbodor
// *
// */
//public class DBConfigurationHelper {
//
//  protected Log logger = LogFactory.getLog(DBConfigurationHelper.class);
//
//  public static final String CONFIG_PROPERTIES_FILENAME = "evqtest.properties";
//  public static final String PROP_DEFAULT_DRIVER = "jdbc.driver";
//  public static final String PROP_DEFAULT_USER = "junit.username";
//  public static final String PROP_DEFAULT_PASSWORD = "junit.password";
//  public static final String PROP_DEFAULT_URL = "junit.url";
//  public static final String PROP_DEFAULT_SCHEMA = "junit.schema";
//
//  public static final String PROP_KEY_SCHEMA = "schema";
//  public static final String PROP_KEY_PASSWORD = "password";
//  public static final String PROP_KEY_URL = "url";
//  public static final String PROP_KEY_USER = "user";
//
//  protected Properties dbProperties = new Properties();
//  protected InitialContext context = null;
//
//  protected static ConcurrentHashMap propertyMapping;
//
//  static {
//    propertyMapping = new ConcurrentHashMap();
//
//    propertyMapping.put(PROP_KEY_SCHEMA, PROP_DEFAULT_SCHEMA);
//    propertyMapping.put(PROP_KEY_PASSWORD, PROP_DEFAULT_PASSWORD);
//    propertyMapping.put(PROP_KEY_URL, PROP_DEFAULT_URL);
//    propertyMapping.put(PROP_KEY_USER, PROP_DEFAULT_USER);
//  }
//
//  public DBConfigurationHelper() {
//  }
//
//  public void tearDown() {
//    try {
//      if (context != null) {
//        context.unbind(getPoolJndiName());
//        context = null;
//      }
//    } catch (Exception e) {
//      logger.error("Error cleaning up resources in DBConfigurationHelper.", e);
//    }
//  }
//
//  public void setUp(String fileName) {
//	  
//		InputStream in = null;
//		Properties props;
//		try {
//			props = new Properties();
//			in = ClassLoader.getSystemResourceAsStream(fileName);
//			if (in == null) {
//				throw new IllegalStateException(fileName
//						+ " not found on classpath");
//			}
//			props.load(in);
//			logger.debug("props loaded");
//			setDbProperties(props);
//		    createTestContext();
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		} finally {
//			try {
//				in.close();
//			} catch (Exception e) {
//				;
//			}
//		}
//	  
//  }
//
//  public void setDbProperties(Properties dbProperties) {
//    this.dbProperties = dbProperties;
//  }
//
//  public Properties getDbProperties() {
//    return this.dbProperties;
//  }
//
//  public void addMapping(String key, String value) {
//    propertyMapping.put(key, value);
//  }
//
//  public Context getInitialContext() {
//    return context;
//  }
//
//  public Connection getConnection(boolean autocommit) throws Exception {
//	  if (context == null) {
//		  context = createTestContext();
//	  }
//
//      DataSource dataSource = (DataSource) context.lookup(getPoolJndiName());
//	  
//      Connection con = dataSource.getConnection();
//	  con.setAutoCommit(autocommit);
//
//	  return con;
//  }
//
//  public static String getJdbcVersion(Connection con) {
//	    try {
//	      DatabaseMetaData meta = con.getMetaData();
//	      return meta.getDriverVersion();
//	    } catch (SQLException ex) {
//	      return "Can not get JDBC version due to SQLException";
//	    }
//  }
//  
//  public String getPassword() {
//    return dbProperties.getProperty((String)propertyMapping.get(PROP_KEY_PASSWORD));
//  }
//
//  public String getSchema() {
//    return dbProperties.getProperty((String)propertyMapping.get(PROP_KEY_SCHEMA)).toUpperCase();
//  }
//
//  public String getUrl() {
//    return dbProperties.getProperty((String)propertyMapping.get(PROP_KEY_URL));
//  }
//
//  public String getUser() {
//    return dbProperties.getProperty((String)propertyMapping.get(PROP_KEY_USER));
//  }
//
//  public String getDriverClassName() {
//    return dbProperties.getProperty(PROP_DEFAULT_DRIVER);
//  }
//
//  public String getTestContextFactory() {
//    return dbProperties.getProperty("java.naming.factory.initial.dbtest");
//  }
//
//  public String getPoolJndiName() {
//    return "jdbc/mhqtdb";
//  }
//
//  public InitialContext createTestContext() {
//    try {
//      Hashtable properties = new Hashtable();
//      properties.put(Context.INITIAL_CONTEXT_FACTORY, getTestContextFactory());
//      InitialContext ctx = new InitialContext(properties);
//      String poolName = getPoolJndiName();
//	  logger.debug("created test context");
//      
//      if (poolName != null) {
//        ctx.bind(getPoolJndiName(), getDataSource());
//        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, getTestContextFactory());
//        return ctx;
//      }
//    } catch (Exception e) {
//      throw new RuntimeException("Can't create context for pooled test connection.", e);
//    }
//    return null;
//  }
//
//  private DataSource getDataSource() {
//    UnitTestDataSource source = createDataSource();
//    source.dbDriver = getDriverClassName();
//    source.dbServer = getUrl();
//    source.dbLogin = getUser();
//    source.dbPassword = getPassword();
//
//    return source;
//  }
//
//  protected UnitTestDataSource createDataSource() {
//    return new UnitTestDataSource();
//  }
//}
