package ca.aeso.evq.server.service;

import java.util.ArrayList;
import java.util.List;

import ca.aeso.evq.common.DateQueryType;
import ca.aeso.evq.rpc.Query;
import ca.aeso.evq.server.util.Constants;

public class CoincidentSqlQueryBuilder extends SqlQueryBuilder {

	
	public CoincidentSqlQueryBuilder(Query userQuery) {
		this.userQuery = userQuery;
	}

    public void createNewQuery()
    { 
		SqlQuery q1 = getAllCoincidencePoints(userQuery, "t1");
		
		SqlQuery q10 = getFirstCoincidencePoint(q1, userQuery, "t10");

		SqlQuery q100 = getHourlyVolumeAtCoincidence(q10, userQuery, "t100");
	
		sqlQuery = sumRequestedVolumesAtPOI(q100, userQuery, "t1000", "t2000");
    	
    }

    public String getResults() {
    	return sqlQuery.toString();
    }
    
	private static SqlQuery getAllCoincidencePoints(Query aQuery, String ident) {
		
		SqlQuery q1 = new SqlQuery(ident);

		// columns
		String coincidence = aQuery.getPoiCoincidence();
		String dateQueryType = aQuery.getDateQueryType();
		String timeInterval = aQuery.getTimeInterval();
		
		q1.addColumnSelects(getDateColumnsByTimeInterval(dateQueryType, timeInterval), true, false);
		q1.addColumnSelects(getGeogColumnsByGranOrCoinc(coincidence), true, false);
		q1.addOtherSelect("SUM(" + ident + ".load_mw)", Constants.SUM_LOAD_MW);
		
		// partitions
		q1.addPartitionColumns(getDatePartitionsByTimeInterval(dateQueryType, timeInterval));

		q1.addPartitionColumns(getGeogPartitionsByCoincidence(coincidence)); 

		// others
		if (aQuery.isPoiPeakSelected()) {
			q1.addOtherSelect("MAX (SUM(" + ident + ".load_mw)) over( PARTITION BY " + q1.getPartitionColumnString() + ")", Constants.MAX_SUM_LOAD_MW);
		}
		
		if (aQuery.isPoiMedianSelected()) {
			q1.addOtherSelect("PERCENTILE_DISC (0.5) WITHIN GROUP ( ORDER BY SUM(" + ident + ".LOAD_MW) DESC) OVER ( PARTITION BY " + q1.getPartitionColumnString() + ")", Constants.MEDIAN_SUM_LOAD_MW);
		}

		if (aQuery.isPoiLightSelected()) {
			q1.addOtherSelect("MIN (SUM(" + ident + ".load_mw)) over( PARTITION BY " + q1.getPartitionColumnString() + ")", Constants.MIN_SUM_LOAD_MW);
		}

		// table
		q1.setTableName(getTableNameByGranOrCoinc(coincidence));
		
		// wheres
		if (!coincidence.equals("MP")) {
			q1.addWhere(buildQ1MPTCWhere(aQuery.isPoiLoadSelected(), q1.getTableIdentifier()));
		}
		q1.addWhere(ident + ".incl_in_pod_lsb = 'Y'"); 		
		q1.addWhere(buildDateWhere(aQuery, ident)); 	
		
		if (coincidence.equals("DE") || coincidence.equals("SU")) {
			
		} else {
			q1.addWhere(buildGeogWhere(aQuery, ident)); 		
		}
		
		return q1;
	}

	/**
	 * build a query for the first (min) coincidence point
	 */
	private static SqlQuery getFirstCoincidencePoint(SqlQuery qt1, Query aQuery, String ident) {
		
		SqlQuery q10 = new SqlQuery(ident);
		q10.setInnerQuery(qt1);

		String coincidence = aQuery.getPoiCoincidence();
		String dateQueryType = aQuery.getDateQueryType();
		String timeInterval = aQuery.getTimeInterval();
		boolean peakSelected = aQuery.isPoiPeakSelected();
		boolean medianSelected = aQuery.isPoiMedianSelected();
		boolean lightSelected = aQuery.isPoiLightSelected();
		
		// columns
		q10.addColumnSelects(getDateColumnsByTimeInterval(dateQueryType, timeInterval), false, false);
		q10.addColumnSelects(getGeogColumnsByGranOrCoinc(coincidence), false, false);
		q10.addColumnSelect(Constants.SUM_LOAD_MW, false, false);

		// partitions
		q10.addPartitionColumns(getDatePartitionsByTimeInterval(dateQueryType, timeInterval));
		q10.addPartitionColumns(getGeogPartitionsByCoincidence(coincidence));
		q10.addPartitionColumn(Constants.SUM_LOAD_MW);

		// extras
		q10.addOtherSelect("MIN (" + ident + ".CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY " + q10.getPartitionColumnString() + ")", "POI_date_HE");
		q10.addOtherSelect(getPOIDecode(peakSelected, medianSelected, lightSelected), Constants.POINTOFINTEREST);

		// wheres
		q10.addWhere(getPOISumIn(peakSelected, medianSelected, lightSelected));
		
		return q10;
	}

	/**
	 * build a query for the hourly volume at a coincidence point
	 */
	private static SqlQuery getHourlyVolumeAtCoincidence(SqlQuery qt1, Query aQuery, String ident) {
		
		SqlQuery q100 = new SqlQuery(ident);
		q100.setInnerQuery(qt1);

		String coincidence = aQuery.getPoiCoincidence();
		String dateQueryType = aQuery.getDateQueryType();
		String timeInterval = aQuery.getTimeInterval();
		
		// columns
		q100.addColumnSelects(getDateColumnsByTimeInterval(dateQueryType, timeInterval), false, false);
		q100.addColumnSelects(getGeogColumnsByGranOrCoinc(coincidence), false, false);
		q100.addColumnSelect(Constants.POINTOFINTEREST, false, false);

		// wheres
		q100.addWhere(ident + ".CAL_DAY_DATE || '-' || " + ident + ".CAL_HOUR_ENDING = " + ident + ".POI_date_HE ");
		
		return q100;
	}
	
	/**
	 * build a query for the sum of requested volumes at a coincidence point 
	 */
	private static SqlQuery sumRequestedVolumesAtPOI(SqlQuery qt1, Query aQuery, String ident, String joinIdent) {
		
		SqlQuery q1000 = new SqlQuery(ident);
		q1000.setInnerQuery(qt1);
		
		String coincidence = aQuery.getPoiCoincidence();
		String granularity = aQuery.getGranularity();

		q1000.setJoinTableName(getTableNameByGranOrCoinc(granularity));
		
		q1000.setJoinTableIdentifier(joinIdent);

		q1000.addJoinColumnsSelect(getDateColumnsByTimeInterval(aQuery.getDateQueryType(), aQuery.getTimeInterval()), aQuery.getTimeInterval(), true, true);
		
		List columns = getGeogColumnsByGranularity(granularity);
		q1000.addJoinColumnsSelect(columns, null, true, true);
		
		q1000.addJoinColumnSelect(Constants.MEAS_POINT_TYPE_CODE, true, true);
		q1000.addColumnSelect(Constants.POINTOFINTEREST, true, true);
		q1000.addOtherSelect("SUM(" + joinIdent + ".load_mw)", Constants.MW);
		q1000.addOtherSelect("SUM(" + joinIdent + ".load_mvar)", Constants.MVAR);

		q1000.addWhere(ident + ".cal_day_date = " + joinIdent + ".cal_day_date");
		q1000.addWhere(ident + ".cal_hour_ending = " + joinIdent + ".cal_hour_ending");
		
		if (coincidence.equals("MP")) {
			q1000.addWhere(ident + ".MP_ID = " + joinIdent + ".MP_ID");
		} else if (coincidence.equals("SB")) {
			q1000.addWhere(ident + ".facility_code = " + joinIdent + ".facility_code");
		} else if (coincidence.equals("PA")) {
			q1000.addWhere(ident + ".area_code = " + joinIdent + ".area_code");
		}

		if (aQuery.isLoadTypeSelected() || aQuery.isGenerationTypeSelected()) { // could be neither!
			q1000.addWhere(buildQ1000MPTCVolumeWhere(aQuery.isLoadTypeSelected(), aQuery.isGenerationTypeSelected(), joinIdent));
		}
		
		q1000.addWhere(joinIdent + ".incl_in_pod_lsb = 'Y'"); 		
		q1000.addWhere(buildGeogWhere(aQuery, joinIdent));
		q1000.addWhere(buildDateWhere(aQuery, joinIdent));
		
		return q1000;
	}
    
	private static List getDateColumnsByTimeInterval(String dateQueryType, String timeInterval) {
		
		List columns = new ArrayList();
		
		if (timeInterval.equals("TO")) {
			// no date columns get selected for TOTAL time interval
		} else {
			// add date column selects depending on query type and time interval
			if (DateQueryType.DATE_QUERY_TYPE_SPECIFIC.equals(dateQueryType)) {
				//
				if (timeInterval.equals("DA")) {
					columns.add(Constants.CAL_YEAR);
					columns.add(Constants.CAL_MONTH_NUMBER);
				} else if (timeInterval.equals("HR")) {
					columns.add(Constants.CAL_YEAR);
					columns.add(Constants.CAL_MONTH_NUMBER);
				}				
			} else if (DateQueryType.DATE_QUERY_TYPE_CALENDAR.equals(dateQueryType)) {
				if (timeInterval.equals("YR")) {
					columns.add(Constants.CAL_YEAR);
				} else if (timeInterval.equals("MO")) {
					columns.add(Constants.CAL_YEAR);
					columns.add(Constants.CAL_MONTH_NUMBER);
				} else if (timeInterval.equals("DA")) {
					columns.add(Constants.CAL_YEAR);
					columns.add(Constants.CAL_MONTH_NUMBER);
				} else if (timeInterval.equals("HR")) {
					columns.add(Constants.CAL_YEAR);
					columns.add(Constants.CAL_MONTH_NUMBER);
				}				
			} else if (DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.equals(dateQueryType)) {
				if (timeInterval.equals("YR")) {
					columns.add(Constants.TWO_SEASON_YEAR);
				} else if (timeInterval.equals("SE")) {
					columns.add(Constants.TWO_SEASON_YEAR);
					columns.add(Constants.TWO_SEASON_NAME);
				} else if (timeInterval.equals("MO")) {
					columns.add(Constants.TWO_SEASON_YEAR);
					columns.add(Constants.TWO_SEASON_NAME);
					columns.add(Constants.CAL_MONTH_NUMBER);
				} else if (timeInterval.equals("DA")) {
					columns.add(Constants.TWO_SEASON_YEAR);
					columns.add(Constants.TWO_SEASON_NAME);
					columns.add(Constants.CAL_MONTH_NUMBER);
				} else if (timeInterval.equals("HR")) {
					columns.add(Constants.TWO_SEASON_YEAR);
					columns.add(Constants.TWO_SEASON_NAME);
					columns.add(Constants.CAL_MONTH_NUMBER);
				}				

			} else if (DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.equals(dateQueryType)) {
				if (timeInterval.equals("YR")) {
					columns.add(Constants.FOUR_SEASON_YEAR);
				} else if (timeInterval.equals("SE")) {
					columns.add(Constants.FOUR_SEASON_YEAR);
					columns.add(Constants.FOUR_SEASON_NAME);
				} else if (timeInterval.equals("MO")) {
					columns.add(Constants.FOUR_SEASON_YEAR);
					columns.add(Constants.FOUR_SEASON_NAME);
					columns.add(Constants.CAL_MONTH_NUMBER);
				} else if (timeInterval.equals("DA")) {
					columns.add(Constants.FOUR_SEASON_YEAR);
					columns.add(Constants.FOUR_SEASON_NAME);
					columns.add(Constants.CAL_MONTH_NUMBER);
				} else if (timeInterval.equals("HR")) {
					columns.add(Constants.FOUR_SEASON_YEAR);
					columns.add(Constants.FOUR_SEASON_NAME);
					columns.add(Constants.CAL_MONTH_NUMBER);
				}				
			}
		}
		
		// always add these 2 columns to the 'select' clause
		columns.add(Constants.CAL_DAY_DATE);
		columns.add(Constants.CAL_HOUR_ENDING);
		
		return columns;
	}

	private static List getGeogColumnsByGranOrCoinc(String granOrCoinc) {
		
		// NB: does not contain region column! 
		
		List columns = new ArrayList();
		
		if (granOrCoinc.equals(Constants.COINCIDENCE_MP)) {
			columns.add(Constants.AREA_CODE);
			columns.add(Constants.MP_ID);
		} else if (granOrCoinc.equals(Constants.COINCIDENCE_SB)) {
			columns.add(Constants.AREA_CODE);
			columns.add(Constants.FACILITY_CODE);
		} else if (granOrCoinc.equals(Constants.COINCIDENCE_PA)) {
			columns.add(Constants.AREA_CODE);
		}

		return columns;
		
	}
	
	private static List getDatePartitionsByTimeInterval(String dateQueryType, String timeInterval) {
		
		List partitions = new ArrayList();
		
		if (timeInterval.equals("TO")) {
			// no date partitioning
		} else {
			// Date columns to partition
			if (DateQueryType.DATE_QUERY_TYPE_SPECIFIC.equals(dateQueryType)) {
				//
				if (timeInterval.equals("DA")) {
					partitions.add(Constants.CAL_YEAR);
					partitions.add(Constants.CAL_MONTH_NUMBER);
					partitions.add(Constants.CAL_DAY_DATE);
				} else if (timeInterval.equals("HR")) {
					partitions.add(Constants.CAL_YEAR);
					partitions.add(Constants.CAL_MONTH_NUMBER);
					partitions.add(Constants.CAL_DAY_DATE);
					partitions.add(Constants.CAL_HOUR_ENDING);
				}				
			} else if (DateQueryType.DATE_QUERY_TYPE_CALENDAR.equals(dateQueryType)) {
				if (timeInterval.equals("YR")) {
					partitions.add(Constants.CAL_YEAR);
				} else if (timeInterval.equals("MO")) {
					partitions.add(Constants.CAL_YEAR);
					partitions.add(Constants.CAL_MONTH_NUMBER);
				} else if (timeInterval.equals("DA")) {
					partitions.add(Constants.CAL_YEAR);
					partitions.add(Constants.CAL_MONTH_NUMBER);
					partitions.add(Constants.CAL_DAY_DATE);
				} else if (timeInterval.equals("HR")) {
					partitions.add(Constants.CAL_YEAR);
					partitions.add(Constants.CAL_MONTH_NUMBER);
					partitions.add(Constants.CAL_DAY_DATE);
					partitions.add(Constants.CAL_HOUR_ENDING);
				}				
			} else if (DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.equals(dateQueryType)) {
				if (timeInterval.equals("YR")) {
					partitions.add(Constants.TWO_SEASON_YEAR);
				} else if (timeInterval.equals("SE")) {
					partitions.add(Constants.TWO_SEASON_YEAR);
					partitions.add(Constants.TWO_SEASON_NAME);
				} else if (timeInterval.equals("MO")) {
					partitions.add(Constants.TWO_SEASON_YEAR);
					partitions.add(Constants.TWO_SEASON_NAME);
					partitions.add(Constants.CAL_MONTH_NUMBER);
				} else if (timeInterval.equals("DA")) {
					partitions.add(Constants.TWO_SEASON_YEAR);
					partitions.add(Constants.TWO_SEASON_NAME);
					partitions.add(Constants.CAL_MONTH_NUMBER);
					partitions.add(Constants.CAL_DAY_DATE);
				} else if (timeInterval.equals("HR")) {
					partitions.add(Constants.TWO_SEASON_YEAR);
					partitions.add(Constants.TWO_SEASON_NAME);
					partitions.add(Constants.CAL_MONTH_NUMBER);
					partitions.add(Constants.CAL_DAY_DATE);
					partitions.add(Constants.CAL_HOUR_ENDING);
				}				

			} else if (DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.equals(dateQueryType)) {
				if (timeInterval.equals("YR")) {
					partitions.add(Constants.FOUR_SEASON_YEAR);
				} else if (timeInterval.equals("SE")) {
					partitions.add(Constants.FOUR_SEASON_YEAR);
					partitions.add(Constants.FOUR_SEASON_NAME);
				} else if (timeInterval.equals("MO")) {
					partitions.add(Constants.FOUR_SEASON_YEAR);
					partitions.add(Constants.FOUR_SEASON_NAME);
					partitions.add(Constants.CAL_MONTH_NUMBER);
				} else if (timeInterval.equals("DA")) {
					partitions.add(Constants.FOUR_SEASON_YEAR);
					partitions.add(Constants.FOUR_SEASON_NAME);
					partitions.add(Constants.CAL_MONTH_NUMBER);
					partitions.add(Constants.CAL_DAY_DATE);
				} else if (timeInterval.equals("HR")) {
					partitions.add(Constants.FOUR_SEASON_YEAR);
					partitions.add(Constants.FOUR_SEASON_NAME);
					partitions.add(Constants.CAL_MONTH_NUMBER);
					partitions.add(Constants.CAL_DAY_DATE);
					partitions.add(Constants.CAL_HOUR_ENDING);
				}				
			}
		}
		
		return partitions;
	}
	
	private static List getGeogPartitionsByCoincidence(String coincidence) {

		List partitions = new ArrayList();

		// if selected coincidence is a Total (TA, TS, TM, DE, SU) then no geographic partitioning
		
		if (coincidence.equals(Constants.COINCIDENCE_MP)) {
			partitions.add(Constants.AREA_CODE);
			partitions.add(Constants.MP_ID);
		} else if (coincidence.equals(Constants.COINCIDENCE_SB)) {
			partitions.add(Constants.AREA_CODE);
			partitions.add(Constants.FACILITY_CODE);
		} else if (coincidence.equals(Constants.COINCIDENCE_PA)) {
			partitions.add(Constants.AREA_CODE);
		}
		
		return partitions;
	}
	
	private static String getPOIDecode(boolean peakSelected, boolean medianSelected, boolean lightSelected) {

		StringBuffer sql = new StringBuffer();
		
		sql.append("DECODE (sum_load_mw, ");

		if (peakSelected) {
			sql.append("max_sum_load_mw, 'Peak'");
			if (medianSelected || lightSelected)
				sql.append(" , ");
		}
		if (medianSelected) {
			sql.append("median_sum_load_mw, 'Median'");
			if (lightSelected)
				sql.append(" , ");
		}
		if (lightSelected) {
			sql.append("min_sum_load_mw, 'Light'");
		}

		sql.append(" )");
		
		return sql.toString();
	}
	
	private static String getPOISumIn(boolean peakSelected, boolean medianSelected, boolean lightSelected) {

		StringBuffer sql = new StringBuffer();

		sql.append("t10.sum_load_mw IN ( ");

		if (peakSelected) {
			sql.append("max_sum_load_mw");
			if (medianSelected || lightSelected)
				sql.append(" , ");
		}
		if (medianSelected) {
			sql.append("median_sum_load_mw");
			if (lightSelected)
				sql.append(" , ");
		}
		if (lightSelected) {
			sql.append("min_sum_load_mw");
		}

		sql.append(" )");
		
		return sql.toString();
	}
	
}
