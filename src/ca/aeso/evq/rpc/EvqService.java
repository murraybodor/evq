package ca.aeso.evq.rpc;

import java.util.HashMap;
import java.util.List;


import com.google.gwt.user.client.rpc.RemoteService;

/**
 * EvqService
 * RemoteService Interface for RPCs
 * 
 * @author mbodor
 */
public interface EvqService extends RemoteService{

	public String getUser() throws QueryException;
    /**
     * @gwt.typeArgs <ca.aeso.evq.rpc.Query>
     */
	public List getQueryHistory() throws QueryException;

    /**
     * @gwt.typeArgs <java.lang.String,java.lang.String>
     */
	public HashMap getCodes() throws QueryException;

	public Query getQuery(long queryId) throws QueryException;
	
    /**
      * @gwt.typeArgs <java.lang.String[]>
      */
	public List getRegions() throws QueryException;
	
    /**
      * @gwt.typeArgs <java.lang.String[]>
      */
	public List getPlanningAreas() throws QueryException;
	
    /**
      * @gwt.typeArgs <java.lang.String[]>
      */
	public List getSubstations() throws QueryException;

    /**
      * @gwt.typeArgs <java.lang.String[]>
      */
	public List getMeasurementPoints() throws QueryException;
	
    /**
      * @gwt.typeArgs <java.lang.String[]>
      */
	public List getGranularities(String geogQueryType) throws QueryException;
	
    /**
      * @gwt.typeArgs <java.lang.String[]>
      */
	public List getCategories() throws QueryException;

	/**
      * @gwt.typeArgs <java.lang.String[]>
      */
	public List getCoincidences(String geogQueryType, String granularity) throws QueryException;

	/**
      * @gwt.typeArgs <java.lang.String[]>
      */
	public List getPoiCategories() throws QueryException;
	
    /**
      * @gwt.typeArgs <java.lang.String[]>
      */
	public List getTimeIntervals() throws QueryException;

	public long submitQuery(Query query) throws QueryException;

	public void logoutUser() throws QueryException;
	
} 