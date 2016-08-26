package ca.aeso.evq.rpc;


import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * EvqServiceAsync
 * Async Interface for RPCs
 *  
 * @author mbodor
 */
public interface EvqServiceAsync {

	public void getUser(AsyncCallback callback);
	public void getQueryHistory(AsyncCallback callback);
	public void getCodes(AsyncCallback callback);
	public void getQuery(long queryId, AsyncCallback callback);
    public void getRegions(AsyncCallback callback);
    public void getPlanningAreas(AsyncCallback callback);
	public void getSubstations(AsyncCallback callback);
	public void getMeasurementPoints(AsyncCallback callback);
	public void getGranularities(String geogQueryType, AsyncCallback callback);
	public void getCategories(AsyncCallback callback);
	public void getCoincidences(String geogQueryType, String granularity, AsyncCallback callback);
	public void getPoiCategories(AsyncCallback callback);
	public void getTimeIntervals(AsyncCallback callback);
	public void submitQuery(Query query, AsyncCallback callback);
	public void logoutUser(AsyncCallback callback);
} 