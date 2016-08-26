package ca.aeso.evq.server.servlet.struts;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.PlugInConfig;


/**
 *
 */
public class BasePlugIn implements PlugIn
{

    /**
     * Default constructor
     *
     */
    public BasePlugIn()
    {
    }

    /**
     * initialize this
     *
     * @param servlet - the action servlet to initialize
     * @param moduleConfig - the plugin module configuration
     * @exception ServletException if we are unable to initialize the plugin
     */
    public void init (ActionServlet servlet, ModuleConfig moduleConfig)
    throws ServletException
    {
        this.servlet = servlet;
        this.moduleConfig = moduleConfig;

        initConfig();
//        initLogger();
        initSecurityFeatures();
//        initAccessLevels();
        initServlet();

        this.pluginClass = this.getClass().toString();

        logger.info("*************************************");
        logger.info(this.pluginClass + " - application started.");
        logger.info("*************************************");
    }

    public void destroy()
    {
        logger.info("*************************************");
        logger.info(this.pluginClass + " shutting down.");
        logger.info("*************************************");
    }

    protected void initConfig() throws ServletException
    {
        this.config = findPlugInConfigProperties();

		if (logger.isDebugEnabled()) {
			logger.debug("initConfig: config[" + this.config + "]");
		}
    }

    protected void initServlet() throws ServletException
    {
		if (logger.isDebugEnabled()) {
			logger.debug("initServlet: starting");
		}

        // application wide initialization dependent on the servlet
        // context goes here

    }

    protected void initSecurityFeatures() throws ServletException
    {
    }

    /** Initialize the Log4j system.
     * The Log4J configuration resource name taken from the Plugins
     * Log4j parameter name.
     *
     * The parameter can contain a string path to the resource or a
     * class path to the resource.
     *
     * The method tries to load the resource as listed in the parameter.
     * If that files it tries to load a resource with a
     * &quot;.&lt;arch&gto;.xml&quot; suffix, where &lt;arch&gt is &quot;win&quot;
     * on Windows platforms other wise its &quot;unix&quot;.
     * If the previous attempt fails, the method tries to load a resource
     * with a only a &quot;.xml&quot; suffix.
     * If the previous attempt fails, the method tries to load the resource
     * using the <b>{@link ResourceLoader}</b>.
     * Finally, if this fails, the method uses the inbuild defaults.
     * @throws ServletException If an error occues.
     */    
//    protected void initLogger() throws ServletException
//    {
//        ServletContext context = servlet.getServletContext();
//        
//        Properties defaultLog4jProperties = new Properties();
//        defaultLog4jProperties.setProperty("log4j.rootLogger", "INFO, A1");
//        defaultLog4jProperties.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
//        defaultLog4jProperties.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
//        defaultLog4jProperties.setProperty("log4j.appender.A1.layout.ConversionPattern", "%8r: %d{ISO8601} [%t] %-5p %c %x - %m%n");
//        defaultLog4jProperties.setProperty("log4j.logger.ca.aeso.ets", "INFO");
//        defaultLog4jProperties.setProperty("log4j.logger.com.apache", "ERROR");
//        
//        String hostArch = "sparc";
//        String fileSep = "/";
//        try
//        {
//            hostArch = System.getProperty("os.arch", "sparc");
//            fileSep = System.getProperty("file.separator", "/");
//        }
//        catch (Exception e)
//        {
//            // no-op
//        }
//        String arch = "unix";
//        if ("x86".equalsIgnoreCase(hostArch) && "\\".equals(fileSep))
//        {
//            arch = "win";
//        }
//        
//        String log4jConfigFileName = (String) config.get(Constants.LOG4J_LOGGER_CONFIG_PARAMETER);
//        servlet.log(this.getClass().getName() + ".initLoggger: hostArch[" + hostArch + "], arch[" + arch + "], fileSep[" + fileSep + "], log4jConfigFileName[" + log4jConfigFileName + "]");
//        
//        if (log4jConfigFileName == null)
//        {
//            servlet.log(this.getClass().getName() + ".initLoggger: no Log4j config file found in for parameter[" + Constants.LOG4J_LOGGER_CONFIG_PARAMETER + "], using defaults!");
//            PropertyConfigurator.configure(defaultLog4jProperties);
//            logger.debug("initLogger: logging service started.");
//            context.setAttribute(Constants.LOG4J_CONFIGURATION, log4jConfigFileName);
//            return;
//        }
//
//        URL configFileResource = this.getClass().getResource(log4jConfigFileName);
//        if (configFileResource == null)
//        {
//            configFileResource = this.getClass().getResource(log4jConfigFileName + "." + arch + ".xml");
//        }
//        if (configFileResource == null)
//        {
//            configFileResource = this.getClass().getResource(log4jConfigFileName + ".xml");
//        }
//
//        if (configFileResource == null)
//        {
//            servlet.log(this.getClass().getName() + ".initLogger: XML config file found based on resource config name \"" + log4jConfigFileName + "\"");
//            Properties log4j = ResourceLoader.getResourceAsProperties(log4jConfigFileName);
//            if (log4j.isEmpty())
//            {
//                log4j = defaultLog4jProperties;
//                servlet.log(this.getClass().getName() + ".initLogger: No Log4J properties found. Using the following in-built defaults: [" +
//                            log4j + "]");
//            }
//            else
//            {
//                servlet.log(this.getClass().getName() + ".initLoggger: \"" + Constants.LOG4J_LOGGER_CONFIG_PARAMETER +
//                            "\" properties[" + log4j + "]");
//            }
//            PropertyConfigurator.configure(log4j);
//            logger.debug("initLogger: logging service started.");
//            context.setAttribute(Constants.LOG4J_CONFIGURATION, log4j);
//        }
//        else
//        {
//            servlet.log(this.getClass().getName() + ".initLogger: XML config file found. URL: \"" + configFileResource.toString() + "\"");
//            boolean configured = false;
//            try
//            {
//                DOMConfigurator.configureAndWatch(configFileResource.getFile());
//                logger.debug("initLogger: logging service started.");
//                configured = true;
//            }
//            catch (Exception e)
//            {
//                servlet.log(this.getClass().getName() + ".initLoggger: error loading Log4j XML configuration file: \"" + configFileResource.toString() + "\"", e);
//            }
//            if (!configured)
//            {
//                // manually configure log4j if we couldn't find the XML config file
//                PropertyConfigurator.configure(defaultLog4jProperties);
//                context.setAttribute(Constants.LOG4J_CONFIGURATION, defaultLog4jProperties);
//            }
//        }
//    }

    /**
     * Find original properties set in the struts PlugInConfig object.
     * First, need to find the index of this plugin. Then retrieve array of configs
     * and then the object for this plugin.
     *
     * @return Map - the configuration properties
     * @exception ServletException if this <code>PlugIn</code> cannot
     *  be successfully initialized
     */
    protected Map findPlugInConfigProperties() throws ServletException
    {
		if (logger.isDebugEnabled()) {
			logger.debug("findPlugInConfigProperties: starting");
		}

        PlugIn plugIns[] = (PlugIn[])servlet.getServletContext().
            getAttribute(Globals.PLUG_INS_KEY + moduleConfig.getPrefix() );
        int index=0;
        while(index < plugIns.length && plugIns[index] != this)
        {
            index++;
        }

        if(plugIns[index] != this)
        {
            String msg = "Can't initialize" + this.getClass().getName() +
                ": plugin configuration object not found.";
            System.out.println(msg);
            servlet.log(msg);
            throw new ServletException(msg);
        }

        PlugInConfig plugInConfig = moduleConfig.findPlugInConfigs()[index];
        return plugInConfig.getProperties();
    }

    /**
     * Load the legacy access level codes into maps for use by the security infrastructure
     *
     */
//    protected void initAccessLevels() throws ServletException
//    {
//		if (logger.isDebugEnabled()) {
//			logger.debug("initAccessLevels: starting");
//		}
//
//        ServletContext context = servlet.getServletContext();
//
//        AccessLevels al = AccessLevels.getInstance();
//
//        try {
//            al.initialize();
//        } catch (IllegalAccessException iae)
//        {
//            throw new ServletException("initAccessLevels: AccessLevels initialization failed", iae);
//        }
//    }

    protected ActionServlet servlet = null;
    protected ModuleConfig moduleConfig = null;
    protected Log logger = LogFactory.getLog(BasePlugIn.class);
    protected Map config = new HashMap();
    protected List ndc = null;
    protected String pluginClass = new String("");
}
