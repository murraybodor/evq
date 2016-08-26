package ca.aeso.evq.server.dao;

import java.io.OutputStream;
import java.util.List;

import ca.aeso.evq.rpc.Query;
import ca.aeso.evq.server.service.ExcelTransformer;

/**
 * QueryDao Interface.
 * Electrical Volumes Query DAO layer
 * @author mbodor
 */
public interface QueryDao {

	/**
	 * Get a List of historical queries for a userId 
	 * @param userId
	 * @return list of Query objects
	 * @throws Exception
	 */
	public List getQueryHistory(String userId) throws Exception;
	
	/**
	 * Get a specific query
	 * @param queryId
	 * @return Query object
	 * @throws Exception
	 */
	public Query getQuery(long queryId) throws Exception;
	
	/**
	 * Get a list of database codes 
	 * @return
	 * @throws Exception
	 */
	public List getCodes() throws Exception;
	
	/**
	 * Gets a list of regions
	 * @return string array: [0]=desc, [1]=value, [2]=attrib
	 * @throws Exception
	 */
	public List getRegions() throws Exception;

	/**
	 * Gets a list of areas
	 * @return string array: [0]=desc, [1]=value, [2]=attrib
	 * @throws Exception
	 */
	public List getPlanningAreas() throws Exception;

	/**
	 * Gets a list of substations
	 * @return string array: [0]=desc, [1]=value, [2]=attrib
	 * @throws Exception
	 */
	public List getSubstations() throws Exception;

	/**
	 * Gets a list of measurement points
	 * @return string array: [0]=desc, [1]=value, [2]=attrib
	 * @throws Exception
	 */
	public List getMeasurementPoints() throws Exception;
	
	/**
	 * Gets a list of allowable granularities for a geographic type
	 * @param geogQueryType
	 * @return string array: [0]=desc, [1]=value, [2]=attrib
	 * @throws Exception
	 */
	public List getGranularities(String geogQueryType) throws Exception;

	/**
	 * Gets a list of volume types
	 * @return string array: [0]=desc, [1]=value, [2]=attrib
	 * @throws Exception
	 */
	public List getCategories() throws Exception;

	/**
	 * Gets a list of coincidences for a geographic type and granularity
	 * @param geogQueryType
	 * @param granularity
	 * @return string array: [0]=desc, [1]=value, [2]=attrib
	 * @throws Exception
	 */
	public List getCoincidences(String geogQueryType, String granularity) throws Exception;

	/**
	 * Gets a list of categories
	 * @return string array: [0]=desc, [1]=value, [2]=attrib
	 * @throws Exception
	 */
	public List getPoiCategories() throws Exception;

	/**
	 * Gets a list of time intervals
	 * @return string array: [0]=desc, [1]=value, [2]=attrib
	 * @throws Exception
	 */
	public List getTimeIntervals() throws Exception;

	/**
	 * Execute a SQL string 
	 * @param sql
	 * @return a list of results of the query
	 * @throws Exception
	 */
	public List executeQuery(String sql) throws Exception;

	/**
	 * Save the results of a query
	 * @param qr
	 * @return
	 * @throws Exception
	 */
	public int saveQueryResults(ExcelTransformer qr, long runTime) throws Exception;
	
	/**
	 * Get the results of a query
	 * @param queryId
	 * @param contentStream
	 * @throws Exception
	 */
	public void getQueryResults(long queryId, OutputStream contentStream) throws Exception;
	
	/**
	 * Get the next available query sequence number
	 * @return
	 * @throws Exception
	 */
	public long getNextQuerySeq() throws Exception;
	
	/**
	 * Update the status of a query
	 * @param queryId
	 * @param status
	 * @param failDesc
	 * @return
	 * @throws Exception
	 */
	public int updateQueryStatus(long queryId, long runTime, String status, String failDesc) throws Exception;
	
	/**
	 * Insert a query into the database
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public int insertQuery(Object[] args) throws Exception;
	
	/**
	 * Insert query parameters into the database
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public int insertQueryParm(Object[] args) throws Exception;
	
	/**
	 * Insert the status of a query into the database
	 * @param queryId
	 * @param status
	 * @param failDesc
	 * @return
	 * @throws Exception
	 */
	public int insertQueryStatus(long queryId, String status, String failDesc) throws Exception;
	
	/**
	 * Reap BLOBs that are older than the number of expiry days in CODES_TABLE
	 * @return
	 * @throws Exception
	 */
	public int reapBlobs() throws Exception;

	/**
	 * Reset the status of queries that have failed and still show as Executing
	 * @return
	 * @throws Exception
	 */
	public int resetStatuses() throws Exception;
	
}