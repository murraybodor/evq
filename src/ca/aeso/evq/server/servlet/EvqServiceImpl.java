package ca.aeso.evq.server.servlet;

import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ca.aeso.evq.rpc.Query;
import ca.aeso.evq.rpc.QueryException;
import ca.aeso.evq.rpc.EvqService;
import ca.aeso.evq.server.dao.QueryDao;
import ca.aeso.evq.server.service.QueryService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * EvqServiceImpl
 * Implements the QueryService interface to support client requests.
 * 
 * @author mbodor
 */
public class EvqServiceImpl extends RemoteServiceServlet 
	implements EvqService {

	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(EvqServiceImpl.class);
	private WebApplicationContext appCtx;
	private QueryDao dao;
	private QueryService service;
	
	/**
	 * Initialize the Spring WebApplicationContext
	 */
	public void init() throws ServletException {
		super.init();
		ServletContext ser = this.getServletContext();
		appCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(ser) ;
		service = (QueryService)appCtx.getBean("service");
		dao = (QueryDao)appCtx.getBean("dao");
		service.setDao(dao);
	} 
	
	/**
	 * Gets the threadlocal user
	 * @return the userId
	 */
	public String getUser() throws QueryException {
    	String user = getThreadLocalRequest().getRemoteUser();
    	
    	if (user!=null)
    		logger.debug("EvqServiceImpl.getUser() remote user=" + user);
    	else {
    		logger.debug("EvqServiceImpl.getUser() remote user is null, using dev instead");
//    		user = "dev";
    		throw new QueryException("user not authenticated!");
    	}

    	return user;
	}

	/**
	 * Gets a list of queries by userid
	 * @return a List of previous queries for the threadlocal user
	 */
	public List getQueryHistory() throws QueryException {
    	logger.debug("EvqServiceImpl.getQueryHistory()");
    	try {
    		return service.getQueryHistory(getUser());
    	} catch (Exception sqle) {
    		logger.error("EvqServiceImpl.getQueryHistory(): Exception: " + sqle.getMessage());
    		sqle.printStackTrace();
    		throw new QueryException(sqle.getMessage());
    	}
    }

	/**
	 * Gets database codes
	 * @return a HashMap containing all database code_table_t entries
	 */
	public HashMap getCodes() throws QueryException {
    	logger.debug("EvqServiceImpl.getCodes()");
    	try {
        	return service.getCodeMap();
    	} catch (Exception sqle) {
    		logger.error("EvqServiceImpl.getCodes(): Exception: " + sqle.getMessage());
    		sqle.printStackTrace();
    		throw new QueryException(sqle.getMessage());
    	}
    }
	
	/**
	 * Gets a specific query by id
	 * @param queryId the ID of the query to retrieve
	 * @return a Query object
	 */
	public Query getQuery(long queryId) throws QueryException { 
    	logger.debug("EvqServiceImpl.getQuery()");
    	try {
    		return service.getQuery(queryId);
    	} catch (Exception sqle) {
    		logger.error("EvqServiceImpl.getQuery(): Exception: " + sqle.getMessage());
    		sqle.printStackTrace();
    		throw new QueryException(sqle.getMessage());
    	}
	}

	/**
	 * Gets a list of valid regions
	 * @return a List of Regions
	 */
	public List getRegions() throws QueryException {
    	logger.debug("EvqServiceImpl.getRegions()");
    	try {
    		return service.getRegions();
    	} catch (Exception sqle) {
    		logger.error("EvqServiceImpl.getRegions(): Exception: " + sqle.getMessage());
    		sqle.printStackTrace();
    		throw new QueryException(sqle.getMessage());
    	}
	}

	/**
	 * Gets a list of valid planning areas
	 * @return a List of planning areas
	 */
    public List getPlanningAreas() throws QueryException {
    	logger.debug("EvqServiceImpl.getPlanningAreas()");
    	try {
    		return service.getPlanningAreas();
    	} catch (Exception sqle) {
    		logger.error("EvqServiceImpl.getPlanningAreas(): Exception: " + sqle.getMessage());
    		sqle.printStackTrace();
    		throw new QueryException(sqle.getMessage());
    	}
    }
	
	/**
	 * Gets a list of valid substations
	 * @return a List of substations
	 */
    public List getSubstations() throws QueryException {
    	logger.debug("EvqServiceImpl.getSubstations()");
    	try {
    		return service.getSubstations();
    	} catch (Exception sqle) {
    		logger.error("EvqServiceImpl.getSubstations(): Exception: " + sqle.getMessage());
    		sqle.printStackTrace();
    		throw new QueryException(sqle.getMessage());
    	}
    }
	
	/**
	 * Gets a list of valid measurement points
	 * @return a List of measurement points
	 */
	public List getMeasurementPoints() throws QueryException {
    	logger.debug("EvqServiceImpl.getMeasurementPoints()");
    	try {
    		return service.getMeasurementPoints();
    	} catch (Exception sqle) {
    		logger.error("EvqServiceImpl.getMeasurementPoints(): Exception: " + sqle.getMessage());
    		sqle.printStackTrace();
    		throw new QueryException(sqle.getMessage());
    	}
    }

	/**
	 * Gets a list of volume granularities for a geographic query type
	 * @param geogQueryType a geographic query type
	 * @return a List of valid granularities for the geographic type
	 */
	public List getGranularities(String geogQueryType) throws QueryException {
    	logger.debug("EvqServiceImpl.getGranularities() - " + geogQueryType);
    	try {
    		return service.getGranularities(geogQueryType);
    	} catch (Exception sqle) {
    		logger.error("EvqServiceImpl.getGranularities(): Exception: " + sqle.getMessage());
    		sqle.printStackTrace();
    		throw new QueryException(sqle.getMessage());
    	}
    }

	/**
	 * Gets a list of valid categories
	 * @return a List of categories
	 */
	public List getCategories() throws QueryException {
    	logger.debug("EvqServiceImpl.getCategories()");
    	try {
    		return service.getCategories();
    	} catch (Exception sqle) {
    		logger.error("EvqServiceImpl.getCategories(): Exception: " + sqle.getMessage());
    		sqle.printStackTrace();
    		throw new QueryException(sqle.getMessage());
    	}
    }
	
	/**
	 * Gets a list of valid coincident loads
	 * @param geogQueryType the type of geographic query
	 * @param granularity the granularity of volumes being requested
	 * @return a List of valid coincidences
	 */
	public List getCoincidences(String geogQueryType, String granularity) throws QueryException {
    	logger.debug("EvqServiceImpl.getCoincidences()");
    	try {
    		return service.getCoincidences(geogQueryType, granularity);
    	} catch (Exception sqle) {
    		logger.error("EvqServiceImpl.getCoincidences(): Exception: " + sqle.getMessage());
    		sqle.printStackTrace();
    		throw new QueryException(sqle.getMessage());
    	}
    }

	/**
	 * Gets a list of valid coincident categories
	 * @return a List of categories
	 */
	public List getPoiCategories() throws QueryException {
    	logger.debug("EvqServiceImpl.getPoiCategories()");
    	try {
    		return service.getPoiCategories();
    	} catch (Exception sqle) {
    		logger.error("EvqServiceImpl.getPoiCategories(): Exception: " + sqle.getMessage());
    		sqle.printStackTrace();
    		throw new QueryException(sqle.getMessage());
    	}
    }

	/**
	 * Gets a list of time intervals
	 * @return a List of time intervals
	 */
	public List getTimeIntervals() throws QueryException {
    	logger.debug("EvqServiceImpl.getTimeIntervals()");
    	try {
    		return service.getTimeIntervals();
    	} catch (Exception sqle) {
    		logger.error("EvqServiceImpl.getTimeIntervals(): Exception: " + sqle.getMessage());
    		sqle.printStackTrace();
    		throw new QueryException(sqle.getMessage());
    	}
    }
	
	/**
	 * Submits a validated query
	 * @param aQuery a valid Query object
	 * @return the new id of the submitted query
	 */
    public long submitQuery(Query query) throws QueryException {
    	logger.debug("EvqServiceImpl.submitQuery(): starting");

		query.setUserId(getUser());
    	long queryId = 0;
    	
    	try {
    		queryId = service.submitQuery(query);
    	} catch (Exception sqle) {
    		String exceptionMsg = "EvqServiceImpl.submitQuery(): Exception submitting query: " + sqle.getMessage() + " trace: " + sqle.getStackTrace();
    		logger.error(exceptionMsg, sqle);
    		sqle.printStackTrace();
    		throw new QueryException(exceptionMsg);
    	}

    	return queryId;
    }

    /**
     * Logs out and invalidates a user session
     */
	public void logoutUser() throws QueryException {
		logger.debug("EvqServiceImpl.logoutUser() logging out user=" + getUser());

		HttpServletRequest req = getThreadLocalRequest();
		HttpSession session = req.getSession();
		session.invalidate(); 
	}
} 