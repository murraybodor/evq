package ca.aeso.evq.server.service;

import java.util.ArrayList;
import java.util.List;

import ca.aeso.evq.rpc.Query;
import ca.aeso.evq.server.util.Constants;

public class HourlySqlQueryBuilder extends SqlQueryBuilder {

	public HourlySqlQueryBuilder(Query userQuery) {
		this.userQuery = userQuery;
	}

    public void createNewQuery()
    { 
		String tablePrefixT1 = "t1";
		sqlQuery = new SqlQuery(tablePrefixT1);
		

		String granularity = userQuery.getGranularity();

		// dates
		boolean groupByDate = true;
		boolean groupByGeog = true;
		if (granularity.equals("MP")) {
			groupByDate = false;
			groupByGeog = false;
			
		} else if (granularity.equals("TM")) {
			groupByDate = true;
			groupByGeog = false;
			
		}
		sqlQuery.addColumnSelects(getHourlyDateColumns(), groupByDate, false);
		sqlQuery.addColumnSelect(Constants.CAL_DAY_DATE, groupByDate, true);
		sqlQuery.addColumnSelect(Constants.CAL_HOUR_ENDING, groupByDate, true);

		// geog
		sqlQuery.addColumnSelects(getGeogColumnsByGranularity(granularity), groupByGeog, true);	
		
		// other cols and vols
		if (granularity.equals("MP")) {
			sqlQuery.addColumnSelect(Constants.CONNECTION_TYPE, false, false);
			sqlQuery.addColumnSelect(Constants.INCL_IN_POD_LSB, false, false);
			sqlQuery.addColumnSelect(Constants.MEAS_POINT_TYPE_CODE, false, true);	// ob
			sqlQuery.addColumnSelect(Constants.CATEGORY, false, true);				// ob
			sqlQuery.addColumnSelect(Constants.LOAD_MW, false, false);
			sqlQuery.addColumnSelect(Constants.LOAD_MVAR, false, false);
			sqlQuery.addColumnSelect(Constants.MAX_MWH, false, false);
			sqlQuery.addColumnSelect(Constants.MAX_MVAR, false, false);
			sqlQuery.addColumnSelect(Constants.MIN_MWH, false, false);
			sqlQuery.addColumnSelect(Constants.MIN_MVAR, false, false);
		} else {
			sqlQuery.addColumnSelect(Constants.CONNECTION_TYPE, true, false);		// gb
			sqlQuery.addColumnSelect(Constants.INCL_IN_POD_LSB, true, false);		// gb
			sqlQuery.addColumnSelect(Constants.MEAS_POINT_TYPE_CODE, true, true);	// gb + ob
			sqlQuery.addColumnSelect(Constants.CATEGORY, true, true);				// gb + ob
			sqlQuery.addOtherSelects(getLoadSumColumns());
			
			if (userQuery.isLoadTypeSelected() || userQuery.isGenerationTypeSelected()) { // could be neither!
				sqlQuery.addWhere(buildQ1000MPTCVolumeWhere(userQuery.isLoadTypeSelected(), userQuery.isGenerationTypeSelected(), tablePrefixT1));
			}
		}
		
		sqlQuery.setTableName(getTableNameByGranOrCoinc(granularity));
		sqlQuery.addWhere(buildDateWhere(userQuery, tablePrefixT1));
		sqlQuery.addWhere(buildGeogWhere(userQuery, tablePrefixT1));
		sqlQuery.addWhere("t1.incl_in_pod_lsb = 'Y'"); 		
		
    }
    
    public String getResults() {
    	return sqlQuery.toString();
    }
    
	private static List getHourlyDateColumns() {
		List columns = new ArrayList();
		
		columns.add(Constants.CAL_YEAR);
		columns.add(Constants.CAL_MONTH_NUMBER);
		columns.add(Constants.CAL_MONTH_SHORT_NAME);
		columns.add(Constants.TWO_SEASON_YEAR);
		columns.add(Constants.TWO_SEASON_NAME);
		columns.add(Constants.FOUR_SEASON_YEAR);
		columns.add(Constants.FOUR_SEASON_NAME);
		
		return columns;
	}
    
	private static List getLoadSumColumns() {
		
		List otherSelects = new ArrayList();
		
		otherSelects.add(new String[] {Constants.SUM_OF_LOAD_MW, Constants.LOAD_MW});
		otherSelects.add(new String[] {Constants.SUM_OF_LOAD_MVAR, Constants.LOAD_MVAR});
		otherSelects.add(new String[] {Constants.SUM_OF_MAX_MWH, Constants.MAX_MWH});
		otherSelects.add(new String[] {Constants.SUM_OF_MAX_MVAR, Constants.MAX_MVAR});
		otherSelects.add(new String[] {Constants.SUM_OF_MIN_MWH, Constants.MIN_MWH});
		otherSelects.add(new String[] {Constants.SUM_OF_MIN_MVAR, Constants.MIN_MVAR});
		
		return otherSelects;
		
	}

	
}
