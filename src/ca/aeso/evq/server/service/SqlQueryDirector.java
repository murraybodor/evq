package ca.aeso.evq.server.service;

import ca.aeso.evq.common.PoiQueryType;
import ca.aeso.evq.rpc.Query;

public class SqlQueryDirector {

	private SqlQueryBuilder queryBuilder;
	private Query userQuery;
	 
    public SqlQueryDirector(Query q)
    {
    	userQuery = q; 
    }
 
    public void constructQuery()
    {
		if (PoiQueryType.POI_QUERY_TYPE_HOURLY.equals(userQuery.getPoiQueryType())) {
			queryBuilder = new HourlySqlQueryBuilder(userQuery);
		} else {
			queryBuilder = new CoincidentSqlQueryBuilder(userQuery);
		}

    	queryBuilder.createNewQuery();
    }
    
    public String getResults()
    { 
        return queryBuilder.getResults(); 
    }
    
}
