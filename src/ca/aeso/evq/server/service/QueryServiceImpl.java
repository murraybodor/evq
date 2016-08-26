package ca.aeso.evq.server.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.aeso.evq.common.CodesUtil;
import ca.aeso.evq.common.QueryStatusType;
import ca.aeso.evq.rpc.Query;
import ca.aeso.evq.server.dao.QueryDao;

/**
 * QueryServiceImpl
 * Service class to support query operations
 * 
 * @author mbodor
 */
public class QueryServiceImpl implements QueryService {

	protected Log logger = LogFactory.getLog(QueryServiceImpl.class);
	private QueryDao dao;
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static List regions;
	private static List areas;
	private static List substations;
	private static Map subMap;
	private static List mps;
	private static List intervals;
	private static List categories;
	
	public QueryServiceImpl() {
	}
	
	public void setDao(QueryDao dao) {
		this.dao = dao;
	}
	
	public List getQueryHistory(String userId) throws Exception {
		return dao.getQueryHistory(userId);
	}	
	
	public Query getQuery(long queryId) throws Exception {
		return dao.getQuery(queryId);
	}	
	
	public List getRegions() throws Exception {
    	if (regions==null) {
    		regions = dao.getRegions(); 
    	}
    	return regions;
	}

	public List getPlanningAreas() throws Exception {
    	if (areas==null) {
    		areas = dao.getPlanningAreas();
    	}
    	return areas; 
	}

	public List getSubstations() throws Exception {
    	if (substations==null) {
    		substations = dao.getSubstations();
    		
    		// also store the substations in a map, we will need them later for lookup
    		subMap = new HashMap();
    		for (Iterator iterator = substations.iterator(); iterator.hasNext();) {
    			String entry[] = (String[]) iterator.next();
		    	subMap.put(entry[1], entry[2]);
			}
    	}
    	return substations;
	}
	
	public Map getSubMap() throws Exception {
		if (subMap==null) {
			getSubstations();
		}
		return subMap;
	}

	public List getMeasurementPoints() throws Exception {
    	if (mps==null) {
        	mps = dao.getMeasurementPoints(); 
    	}
    	return mps;
	}

	public List getCodes() throws Exception {
    	if (CodesUtil.getCodeList()==null) {
    		CodesUtil.setCodes(dao.getCodes()); 
    	}
    	return CodesUtil.getCodeList();
	}

	public HashMap getCodeMap() throws Exception {
    	if (CodesUtil.getCodeMap()==null || CodesUtil.getCodeMap().size()==0) {
    		CodesUtil.setCodes(dao.getCodes()); 
    	}
    	return CodesUtil.getCodeMap();
	}

	public String getCodeDesc(String value) {
    	if (CodesUtil.getCodeMap()==null || CodesUtil.getCodeMap().size()==0) {
    		try {
        		CodesUtil.setCodes(dao.getCodes()); 
    		} catch (Exception e) {
    	    	logger.error("QueryServiceImpl.getCodeDesc() threw exception: " + e);
    		}
    	}
    	return CodesUtil.getCodeDesc(value);
	}
	
	public List getGranularities(String geogQueryType) throws Exception {
    	return dao.getGranularities(geogQueryType); 
	}

	public List getCategories() throws Exception {
    	if (categories==null) {
    		categories = dao.getCategories(); 
    	}
    	return categories; 
	}
	
	public List getCoincidences(String geogQueryType, String granularity) throws Exception {
    	return dao.getCoincidences(geogQueryType, granularity); 
	}

	public List getPoiCategories() throws Exception {
    	if (categories==null) {
    		categories = dao.getCategories(); 
    	}
    	return categories; 
	}

	public List getTimeIntervals() throws Exception {
    	if (intervals==null) {
    		intervals = dao.getTimeIntervals(); 
    	}
    	return intervals;
	}
	
	
  	/**
	 * Submit a query object for processing
	 * 
	 * @param query
	 * @return
	 */
	public long submitQuery(Query query) throws Exception {
    	logger.debug("QueryServiceImpl.submitQuery() starting");
    	long submitStart = System.currentTimeMillis();
    	long queryStart = 0;
    	long queryEnd = 0;
    	long queryRunTime = 0;
    	
    	// assign a queryId
    	long queryId = 0;
    	try {
        	queryId = dao.getNextQuerySeq();
        	query.setQueryId(queryId);
    	} catch (Exception e) {
    		String exceptionMsg = "QueryServiceImpl.submitQuery() error getting next query sequence number"; 
        	logger.error(exceptionMsg, e);
        	throw new Exception(exceptionMsg, e);
    	}
    	
    	// generate the sql based on the parameters
    	SqlQueryDirector director = new SqlQueryDirector(query);
    	director.constructQuery();
    	String sql = director.getResults();
    	
    	if (sql==null || sql.length()==0) {
    		String exceptionMsg = "QueryServiceImpl.submitQuery() error generating sql!"; 
        	logger.error(exceptionMsg);
        	throw new Exception(exceptionMsg);
    	} else {
        	query.setSql(sql);
    	}

    	Calendar cal = Calendar.getInstance();
    	String nowStr = format.format(cal.getTime());
    	query.setQueryDateStr(nowStr);
    	
    	// save the query
    	int result = saveQuery(query);

    	// execute the query
    	if (result>0) {

    		ExcelTransformer transformer = null;
			List results = null;
	
			try {
				// execute the sql and get the results
		    	queryStart = System.currentTimeMillis();
				
				results = dao.executeQuery(sql);
				
		    	queryEnd = System.currentTimeMillis();
		    	
		    	queryRunTime = queryEnd - queryStart;
		    	
		    	logger.info("****JdbcQueryDAOImpl.executeQuery() #" + queryId + " duration=" + queryRunTime + " ms (" + queryRunTime/1000/60 + " minutes)");

				if (results==null || results.size()==0) {
					try {
			    		String exceptionMsg = "QueryServiceImpl.submitQuery() - query #" + queryId + " returned no results"; 
			        	logger.error(exceptionMsg);
			            dao.updateQueryStatus(queryId, queryRunTime, QueryStatusType.STATUS_FAILED.getValue(), "Query returned no results");
					} catch (Exception sqle) {
			    		String exceptionMsg = "QueryServiceImpl.submitQuery() - error updating query status: " + sqle.getMessage(); 
			        	logger.error(exceptionMsg, sqle);
			        	throw new Exception(exceptionMsg, sqle);
					}
				} else if (results.size() > 65000) {
					try {
			        	logger.error("QueryServiceImpl.submitQuery() - query #" + queryId + " returned >65000 rows");
			        	dao.updateQueryStatus(queryId, queryRunTime, QueryStatusType.STATUS_FAILED.getValue(), "Query returned >65000 rows, please refine your query");
					} catch (Exception sqle) {
						logger.error("QueryServiceImpl.submitQuery() - error updating query status: ", sqle);
						sqle.printStackTrace();
					}
				} else {
		        	logger.info("QueryServiceImpl.submitQuery() query complete, size=" + results.size());
					
		    		transformer = new ExcelTransformer(query, results);
		    		transformer.setService(this);
		    		
					// transform results into an Excel workbook
		        	int numRowsTransformed = 0;
					try {
						numRowsTransformed = transformer.transform();
					} catch (Exception e) {
			    		String exceptionMsg = "QueryServiceImpl.submitQuery() - exception transforming results to Excel: " + e.getMessage(); 
			        	logger.error(exceptionMsg, e);
						e.printStackTrace();
						try {
							dao.updateQueryStatus(queryId, queryRunTime, QueryStatusType.STATUS_FAILED.getValue(), "Error transforming query results");
						} catch (Exception sqle) {
				    		exceptionMsg = "QueryServiceImpl.submitQuery() - error updating query status: " + sqle.getMessage(); 
				        	logger.error(exceptionMsg, sqle);
							sqle.printStackTrace();
				        	throw new Exception(exceptionMsg, sqle);
						}
					}
						
					// save into database blob
					if (numRowsTransformed>0) {
						try {
							dao.saveQueryResults(transformer, queryRunTime);
						} catch (Exception e) {
				    		String exceptionMsg = "QueryServiceImpl.submitQuery() - exception saving blob to database: " + e.getMessage(); 
				        	logger.error(exceptionMsg, e);
							e.printStackTrace();
							try {
								dao.updateQueryStatus(queryId, queryRunTime, QueryStatusType.STATUS_FAILED.getValue(), "Error saving query results to database");
							} catch (Exception sqle) {
					    		exceptionMsg = "QueryServiceImpl.submitQuery() - error updating query status: " + sqle.getMessage(); 
					        	logger.error(exceptionMsg, sqle);
								sqle.printStackTrace();
					        	throw new Exception(exceptionMsg, sqle);
							}
						}
					}
	
				}
				
			} catch (Exception dae) {
				try {
		    		String exceptionMsg = "QueryServiceImpl.submitQuery() - error executing query sql" + dae.getMessage(); 
		        	logger.error(exceptionMsg, dae);
		        	dae.printStackTrace();
					dao.updateQueryStatus(queryId, queryRunTime, QueryStatusType.STATUS_FAILED.getValue(), "Error executing query: " + dae.getMessage());
				} catch (Exception sqle) {
		    		String exceptionMsg = "QueryServiceImpl.submitQuery() - error updating query status: " + sqle.getMessage(); 
		        	logger.error(exceptionMsg, sqle);
		        	sqle.printStackTrace();
		        	throw new Exception(exceptionMsg, sqle);
				}
			} finally {
				sql = null;
				transformer = null;
				results = null;
			}

    	} else {
    		// failed to save the query
			try {
				logger.error("QueryServiceImpl.submitQuery() - error saving the query object for #" + queryId);
				dao.updateQueryStatus(queryId, queryRunTime, QueryStatusType.STATUS_FAILED.getValue(), "Error saving query parameters");
			} catch (Exception sqle) {
	    		String exceptionMsg = "QueryServiceImpl.submitQuery() - error updating query status: " + sqle.getMessage(); 
	        	logger.error(exceptionMsg, sqle);
	        	sqle.printStackTrace();
	        	throw new Exception(exceptionMsg, sqle);
			} finally {
				sql = null;
			}
    		
			queryId = -1;
    	}
    	
    	long end = System.currentTimeMillis();
		logger.debug("QueryServiceImpl.submitQuery() done");
    	logger.info("QueryServiceImpl.submitQuery(): #" + queryId + " duration=" + (end-submitStart) + "ms");

    	return queryId;
    	
	}
	
	/**
	 * Save the query to the database
	 * @param query
	 * @return
	 */
	private int saveQuery(Query query)  {
    	long start = System.currentTimeMillis();
		logger.debug("QueryServiceImpl.saveQuery() starting");
		long queryId = query.getQueryId();
		
		int result = -1;
		
    	// save the query and parameters
    	Object args[] = new Object[] {
    			new Long(queryId),
    			query.getUserId(),
    			query.getQueryName(),
    			query.getDateQueryType(),
    			query.getBeginDate(),
    			query.getEndDate(),
    			query.getCalBeginYear(),
    			query.getCalBeginMonth(),
    			query.getCalEndYear(),
    			query.getCalEndMonth(),
    			query.getTwoSeasBeginYear(),
    			query.getTwoSeasEndYear(),
    			query.isTwoSeasonSummerSelected()?"Y":"N",
    			query.isTwoSeasonWinterSelected()?"Y":"N",
    			query.getFourSeasBeginYear(),
    	    	query.getFourSeasEndYear(),
    	    	query.isFourSeasonSpringSelected()?"Y":"N",
 	    	    query.isFourSeasonSummerSelected()?"Y":"N",
    	    	query.isFourSeasonFallSelected()?"Y":"N",
    	    	query.isFourSeasonWinterSelected()?"Y":"N",
    	    	query.getGeogQueryType(),
    	    	query.getGranularity(),
    	    	query.getCategory(),
    	    	query.isLoadTypeSelected()?"Y":"N",
    	    	query.isGenerationTypeSelected()?"Y":"N",
    	    	query.getPoiQueryType(),
    	    	query.getPoiCoincidence(),
    	    	query.getPoiCategory(),
    	    	query.isPoiLoadSelected()?"Y":"N",
    	    	query.isPoiGenerationSelected()?"Y":"N",
    	    	query.getTimeInterval(),
    	    	query.isPoiPeakSelected()?"Y":"N",
    	    	query.isPoiMedianSelected()?"Y":"N",
    	    	query.isPoiLightSelected()?"Y":"N",
    	    	query.getSql(),
    	    	query.getQueryDateStr()
    	};

    	try {
    		result = dao.insertQuery(args);
    	
	    	List parmList = query.getGeographicParms();
	    	if (parmList!=null) {
	        	for (int i = 0; i < parmList.size(); i++) {
	        		String geogParm = (String)parmList.get(i);
	
	            	Object parms[] = new Object[] {
	            			new Long(query.getQueryId()),
	            			new Integer(i+1), // parm id starts at 1
	            			query.getGeogQueryType(),
	            			geogParm,
	            	};
	
	            	dao.insertQueryParm(parms);
	        	}
	    	}
	
	    	dao.insertQueryStatus(query.getQueryId(), QueryStatusType.STATUS_EXECUTING.getValue(), "");
	    	
    	} catch (Exception dae) {
        	logger.error("QueryServiceImpl.saveQuery() error saving query! ", dae);

    		try {
    			result = -1;
    			dao.updateQueryStatus(query.getQueryId(), 0, QueryStatusType.STATUS_FAILED.getValue(), dae.getMessage());
        	} catch (Exception sqle2) {
        		sqle2.printStackTrace();
        	}
    	}
    	
    	args = null;

    	long end = System.currentTimeMillis();
		logger.debug("QueryServiceImpl.saveQuery() done");
    	logger.info("QueryServiceImpl.saveQuery(): #" + queryId + " duration=" + (end-start) + "ms");
    	
    	return result;
	}
	
	public int reapBlobs() throws Exception {
		return dao.reapBlobs();

	}
    
	public int resetStatuses() throws Exception {
		return dao.resetStatuses();
	}
	
}
