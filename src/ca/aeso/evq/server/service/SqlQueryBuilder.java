package ca.aeso.evq.server.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.aeso.evq.common.DateQueryType;
import ca.aeso.evq.common.GeogQueryType;
import ca.aeso.evq.rpc.Query;
import ca.aeso.evq.server.util.*;

public abstract class SqlQueryBuilder {

	protected SqlQuery sqlQuery;
	protected Query userQuery;
	 
    public void createNewQuery()
    { 
    	sqlQuery = new SqlQuery("t1"); 
    }

    public abstract String getResults();
    
	protected static String buildDateWhere(Query query, String tablePrefix) {

		// date
		String queryType = query.getDateQueryType();
		if (queryType!=null) {
			if (DateQueryType.DATE_QUERY_TYPE_SPECIFIC.equals(queryType)) {
				return buildSpecificDateWhere(query, tablePrefix);
			} else if (DateQueryType.DATE_QUERY_TYPE_CALENDAR.equals(queryType)) {
				return buildCalendarDateWhere(query, tablePrefix);
			} else if (DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.equals(queryType)) {
				return buildTwoSeasonDateWhere(query, tablePrefix);
			} else if (DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.equals(queryType)) {
				return buildFourSeasonDateWhere(query, tablePrefix);
			} else
				return null;
		} else
			return null;
	}

	protected static String buildFourSeasonDateWhere(Query query, String tablePrefix) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" " + tablePrefix + ".four_season_year BETWEEN " + query.getFourSeasBeginYear() + " AND " + query.getFourSeasEndYear());
		
		if (query.isFourSeasonSpringSelected() & query.isFourSeasonSummerSelected() & query.isFourSeasonFallSelected() & query.isFourSeasonWinterSelected()) {
			// no AND needed, get records for all 4 seasons
		} else {
			sql.append(" AND ");
			sql.append(" " + tablePrefix + ".four_season_name IN (");

			boolean firstIn = true;

			if (query.isFourSeasonSpringSelected()) {
				sql.append("'SPRING'");
				firstIn = false;
			}
			
			if (query.isFourSeasonSummerSelected()) {
				if (!firstIn) sql.append(", ");
				sql.append("'SUMMER'");
				firstIn = false;
			}
			
			if (query.isFourSeasonFallSelected()) {
				if (!firstIn) sql.append(", ");
				sql.append("'FALL'");
				firstIn = false;
			}

			if (query.isFourSeasonWinterSelected()) {
				if (!firstIn) sql.append(", ");
				sql.append("'WINTER'");
			}
			
			sql.append(" ) ");
		}
		
		return sql.toString();
	}

	protected static String buildTwoSeasonDateWhere(Query query, String tablePrefix) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" " + tablePrefix + ".two_season_year BETWEEN " + query.getTwoSeasBeginYear() + " AND " + query.getTwoSeasEndYear());
		
		if (query.isTwoSeasonSummerSelected() & query.isTwoSeasonWinterSelected()) {
			// no AND clause needed, get all records
		} else {
			sql.append(" AND ");
			sql.append(" " + tablePrefix + ".two_season_name = ");
			
			if (query.isTwoSeasonSummerSelected()) {
				sql.append("'SUMMER'");
			} 
			if (query.isTwoSeasonWinterSelected()) {
				sql.append("'WINTER'");
			}
		}
		
		return sql.toString();
	}

	protected static String buildCalendarDateWhere(Query query, String tablePrefix) {

		StringBuffer sql = new StringBuffer();

		sql.append(tablePrefix + ".cal_year BETWEEN " + query.getCalBeginYear() + " AND "  + query.getCalEndYear());
		
//		sql.append(" ( ");
//
//		if (query.getCalEndYear().compareTo(query.getCalBeginYear())==0) { 
//			// begin and end year are the same
//			sql.append(" ( ");
//			sql.append(" " + tablePrefix + ".cal_year = " + query.getCalBeginYear());
//			sql.append(" AND ");
//			sql.append(" " + tablePrefix + ".cal_month_number BETWEEN " + query.getCalBeginMonth() + " AND " + query.getCalEndMonth());
//			sql.append(" ) ");
//			
//		} else { 
//			// end year is greater than begin year
//			sql.append(" ( ");
//			sql.append(" " + tablePrefix + ".cal_year = " + query.getCalBeginYear());
//			sql.append(" AND ");
//			sql.append(" " + tablePrefix + ".cal_month_number >= " + query.getCalBeginMonth());
//			sql.append(" ) ");
//
//			sql.append(" OR ");
//
//			sql.append(" ( ");
//			sql.append(" " + tablePrefix + ".cal_year > " + query.getCalBeginYear());
//			sql.append(" AND ");
//			sql.append(" " + tablePrefix + ".cal_year < " + query.getCalEndYear());
//			sql.append(" ) ");
//			
//			sql.append(" OR ");
//
//			sql.append(" ( ");
//			sql.append(" " + tablePrefix + ".cal_year = " + query.getCalEndYear());
//			sql.append(" AND ");
//			sql.append(" " + tablePrefix + ".cal_month_number <= " + query.getCalEndMonth());
//			sql.append(" ) ");
//		}
//		
//		sql.append(" ) ");
		
		return sql.toString();
	}
	
	
	protected static String buildSpecificDateWhere(Query query, String tablePrefix) {
		return (" " + tablePrefix + ".CAL_DAY_DATE between TO_DATE('" + query.getBeginDate() + "', 'YYYY/MM/DD') AND TO_DATE('" + query.getEndDate() + "', 'YYYY/MM/DD')");
	}
	
	protected static String buildGeogWhere(Query query, String tablePrefix) {

		// geographic area
		String geogQueryType = query.getGeogQueryType();
		if (geogQueryType!=null) {
			if (GeogQueryType.GEOG_QUERY_TYPE_ENTIRE_SYSTEM.equals(geogQueryType)) {
				return null;
			} else if (GeogQueryType.GEOG_QUERY_TYPE_REGION.equals(geogQueryType)) {
				return buildRegionWhere(query, tablePrefix);
			} else if (GeogQueryType.GEOG_QUERY_TYPE_AREA.equals(geogQueryType)) {
				return buildAreaCodeWhere(query, tablePrefix);
			} else if (GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.equals(geogQueryType)) {
				return buildSubstationWhere(query, tablePrefix);
			} else if (GeogQueryType.GEOG_QUERY_TYPE_MP.equals(geogQueryType)) {
				return buildMpWhere(query, tablePrefix);
			} else if (GeogQueryType.GEOG_QUERY_TYPE_IMPORTS_EXPORTS.equals(geogQueryType)) {
				return null;
			} else
				return null;
		} else 
			return null;
	}

	protected static String buildRegionWhere(Query query, String tablePrefix) {
		StringBuffer sql = new StringBuffer();
		
		List regions = query.getRegions();
		if (regions!=null) {
			sql.append(" " + tablePrefix + ".region IN ('");
			for (Iterator iterator = regions.iterator(); iterator.hasNext();) {
				sql.append((String) iterator.next());
				if (iterator.hasNext()) {
					sql.append("', '");
				}
			}
			sql.append("')");
		}
		
		return sql.toString();
	}
	
	protected static String buildAreaCodeWhere(Query query, String tablePrefix) {
		StringBuffer sql = new StringBuffer();
		List areas = query.getPlanningAreas();

		if (areas!=null && areas.size()>0) {
			sql.append(" " + tablePrefix + ".area_code IN ('");
			for (Iterator iterator = areas.iterator(); iterator.hasNext();) {
				sql.append((String) iterator.next());
				if (iterator.hasNext()) {
					sql.append("', '");
				}
			}
			sql.append("')");
		}
		
		return sql.toString();
	}	

	protected static String buildSubstationWhere(Query query, String tablePrefix) {
		StringBuffer sql = new StringBuffer();
		List subs = query.getSubstations();

		if (subs!=null && subs.size()>0) {
			// use facility_code
			sql.append(" " + tablePrefix + ".facility_code IN ('");
			for (Iterator iterator = subs.iterator(); iterator.hasNext();) {
				sql.append((String) iterator.next());
				if (iterator.hasNext()) {
					sql.append("', '");
				}
			}
			sql.append("')");
		}
		return sql.toString();
	}	

	protected static String buildMpWhere(Query query, String tablePrefix) {
		StringBuffer sql = new StringBuffer();
		
		List mps = query.getMeasurementPoints();
		if (mps!=null) {
			sql.append(" " + tablePrefix + ".mp_id IN ('");
			for (Iterator iterator = mps.iterator(); iterator.hasNext();) {
				sql.append((String) iterator.next());
				if (iterator.hasNext()) {
					sql.append("', '");
				}
			}
			sql.append("')");
		}
		
		return sql.toString();
	}	
	
	protected static List getGeogColumnsByGranularity(String granularity) {
		
		List columns = new ArrayList();
		
		if (granularity.equals("BU")) {
			columns.add(Constants.AREA_CODE);
			columns.add(Constants.FACILITY_CODE);
			columns.add(Constants.SUBSTATION_NAME); // added
			columns.add(Constants.BUS_ID);
			columns.add(Constants.BUS_NAME); // added
		} else if (granularity.equals("SB")) {
			columns.add(Constants.AREA_CODE);
			columns.add(Constants.FACILITY_CODE);
			columns.add(Constants.SUBSTATION_NAME); // added
		} else if (granularity.equals("PA")) {
			columns.add(Constants.AREA_CODE);
		} else if (granularity.equals("MP")) {
			columns.add(Constants.AREA_CODE);
			columns.add(Constants.MP_ID);
		}
		
		return columns;
	}
	
	protected static String getTableNameByGranOrCoinc(String granOrCoinc) {
		if (granOrCoinc.equals("TS") || granOrCoinc.equals("SB") ||	granOrCoinc.equals("BU") ) {
			return Constants.IHFC_BUS_VIEW;
		} else {
			return Constants.IHFC_LTLF_VIEW;
		}
	}

	protected static String buildQ1000MPTCVolumeWhere(boolean loadTypeSelected, boolean generationTypeSelected, String tablePrefix) {

		StringBuffer sql = new StringBuffer();
		
		sql.append(tablePrefix);
		sql.append("."); 		
		sql.append("measurement_point_type_code IN ("); 		
		
		if (loadTypeSelected) {
			sql.append("'DEM'");
			if (generationTypeSelected) {
				sql.append(", ");
			}
		} 

		if (generationTypeSelected) {
			sql.append("'SUP'");
		}

		sql.append(" ) "); 		
		return sql.toString();
	}
	
	protected static String buildQ1MPTCWhere(boolean isPoiLoadSelected, String tablePrefix) {

		StringBuffer sql = new StringBuffer();
		
		sql.append(tablePrefix);
		sql.append("."); 		
		sql.append("measurement_point_type_code = "); 		
		
		if (isPoiLoadSelected) {
			sql.append("'DEM'");
		} else {
			sql.append("'SUP'");
			
		}

		return sql.toString();
	}
	
}
