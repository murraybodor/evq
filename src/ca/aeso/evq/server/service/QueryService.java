package ca.aeso.evq.server.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.aeso.evq.rpc.Query;
import ca.aeso.evq.server.dao.QueryDao;

public interface QueryService {

	public void setDao(QueryDao dao);
	public List getQueryHistory(String userId) throws Exception; 
	public Query getQuery(long queryId) throws Exception; 
	public List getRegions() throws Exception; 
	public List getPlanningAreas() throws Exception; 
	public List getSubstations() throws Exception; 
	public Map getSubMap() throws Exception; 
	public List getMeasurementPoints() throws Exception; 
	public List getCodes() throws Exception; 
	public String getCodeDesc(String value);  
	public HashMap getCodeMap() throws Exception; 
	public List getGranularities(String geogQueryType) throws Exception; 
	public List getCategories() throws Exception; 
	public List getCoincidences(String geogQueryType, String granularity) throws Exception; 
	public List getPoiCategories() throws Exception; 
	public List getTimeIntervals() throws Exception; 
	public long submitQuery(Query query) throws Exception; 
	public int reapBlobs() throws Exception;    
	public int resetStatuses() throws Exception;
	
}
