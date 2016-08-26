package ca.aeso.evq.server.util;

import java.util.HashMap;

/**
 * Constants
 * Contains all application constants, as well as a map of column names to column labels
 * 
 * @author mbodor
 */
public class Constants {

	private static final String[] MONTHS = new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG",
		"SEP", "OCT", "NOV", "DEC" };
	
	// tables
	public static final String IHFC_LTLF_VIEW = "IHFC_CORP_HIST_LTLF_V";
	public static final String IHFC_BUS_VIEW = "IHFC_CORP_HIST_BUS_V";
	
	// result columns
	public static final String MP_ID = "MP_ID";
	public static final String MP_ID_DESC = "MP ID";
	public static final String MEASURE_POINT_ID = "MEASURE_POINT_ID";
	public static final String MEASURE_POINT_ID_DESC = "Meas. Point ID";
	public static final String AREA_CODE = "AREA_CODE";
	public static final String AREA_CODE_DESC = "Planning Area";
	public static final String REGION_NAME = "REGION_NAME";
	public static final String REGION_NAME_DESC = "Region";
	public static final String MEAS_POINT_TYPE_CODE = "MEASUREMENT_POINT_TYPE_CODE";
	public static final String MEAS_POINT_TYPE_CODE_DESC = "Meas Point Type";
	public static final String CONNECTION_TYPE = "CONNECTION_TYPE";
	public static final String CONNECTION_TYPE_DESC = "Connection Type";
	public static final String INCL_IN_POD_LSB = "INCL_IN_POD_LSB";
	public static final String INCL_IN_POD_LSB_DESC = "Incl In Pod";
	public static final String CATEGORY = "CATEGORY";
	public static final String CATEGORY_DESC = "Category";
	public static final String CAL_DAY_DATE = "CAL_DAY_DATE";
	public static final String CAL_DAY_DATE_DESC = "Cal Day Date";
	public static final String CAL_HOUR_ENDING = "CAL_HOUR_ENDING";
	public static final String CAL_HOUR_ENDING_DESC = "Cal Hour End";
	public static final String CAL_YEAR = "CAL_YEAR";
	public static final String CAL_YEAR_DESC = "Cal Year";
	public static final String CAL_MONTH_NUMBER = "CAL_MONTH_NUMBER";
	public static final String CAL_MONTH_NUMBER_DESC = "Cal Month Number";
	public static final String CAL_MONTH_SHORT_NAME = "CAL_MONTH_SHORT_NAME";
	public static final String CAL_MONTH_SHORT_NAME_DESC = "Cal Month Short Name";
	public static final String TWO_SEASON_YEAR = "TWO_SEASON_YEAR";
	public static final String TWO_SEASON_YEAR_DESC = "Two Season Year";
	public static final String TWO_SEASON_NAME = "TWO_SEASON_NAME";
	public static final String TWO_SEASON_NAME_DESC = "Two Season Name";
	public static final String FOUR_SEASON_YEAR = "FOUR_SEASON_YEAR";
	public static final String FOUR_SEASON_YEAR_DESC = "Four Season Year";
	public static final String FOUR_SEASON_NAME = "FOUR_SEASON_NAME";
	public static final String FOUR_SEASON_NAME_DESC = "Four Season Name";
	public static final String BUS_ID = "BUS_ID";
	public static final String BUS_ID_DESC = "Bus ID";
	public static final String BUS_NAME = "BUS_NAME";
	public static final String BUS_NAME_DESC = "Bus Name";
	public static final String FACILITY_CODE = "FACILITY_CODE";
	public static final String FACILITY_CODE_DESC = "Facility Code";
	public static final String FACILITY_ID = "FACILITY_ID";
	public static final String FACILITY_ID_DESC = "Facility ID";
	public static final String SUBSTATION_NAME = "SUBSTATION_NAME";
	public static final String SUBSTATION_NAME_DESC = "Substation Name";
	public static final String MW = "MW";
	public static final String MW_DESC = "MW";
	public static final String MVAR = "MVAR";
	public static final String MVAR_DESC = "MVar";
	public static final String LOAD_MW = "LOAD_MW";
	public static final String LOAD_MW_DESC = "Load MW";
	public static final String LOAD_MVAR = "LOAD_MVAR";
	public static final String LOAD_MVAR_DESC = "Load MVar";
	public static final String MAX_MWH = "MAX_MWH";
	public static final String MAX_MWH_DESC = "Max MWH";
	public static final String MAX_MVAR = "MAX_MVAR";
	public static final String MAX_MVAR_DESC = "Max MVar";
	public static final String MIN_MWH = "MIN_MWH";
	public static final String MIN_MWH_DESC = "Min MWH";
	public static final String MIN_MVAR = "MIN_MVAR";
	public static final String MIN_MVAR_DESC = "Min MVar";
	public static final String SUM_LOAD_MW = "SUM_LOAD_MW";
	public static final String SUM_LOAD_MW_DESC = "Sum Load MW";
	public static final String MAX_SUM_LOAD_MW = "MAX_SUM_LOAD_MW";
	public static final String MAX_SUM_LOAD_MW_DESC = "Max Sum Load MW";
	public static final String MEDIAN_SUM_LOAD_MW = "MEDIAN_SUM_LOAD_MW";
	public static final String MEDIAN_SUM_LOAD_MW_DESC = "Median Sum Load MW";
	public static final String MIN_SUM_LOAD_MW = "MIN_SUM_LOAD_MW";
	public static final String MIN_SUM_LOAD_MW_DESC = "Min Sum Load MW";
	public static final String SUM_OF_LOAD_MW = "SUM(LOAD_MW)";
	public static final String SUM_OF_LOAD_MVAR = "SUM(LOAD_MVAR)";
	public static final String SUM_OF_MAX_MWH = "SUM(MAX_MWH)";
	public static final String SUM_OF_MAX_MVAR = "SUM(MAX_MVAR)";
	public static final String SUM_OF_MIN_MWH = "SUM(MIN_MWH)";
	public static final String SUM_OF_MIN_MVAR = "SUM(MIN_MVAR)";
	public static final String POINTOFINTEREST = "POINTOFINTEREST";
	public static final String POINTOFINTEREST_DESC = "Interest Point";

	// query items
	public static final String QUERY_ID = "QUERY_ID";
	public static final String USER_ID = "USER_ID";
	public static final String COMMON_USER_ID = "COMMON";
	public static final String QUERY_NAME = "QUERY_NAME";
	public static final String DATE_QUERY_TYPE = "DATE_QUERY_TYPE";
	public static final String BEGIN_DATE = "BEGIN_DATE";
	public static final String END_DATE = "END_DATE";
	public static final String CAL_BEGIN_YEAR = "CAL_BEGIN_YEAR";
	public static final String CAL_BEGIN_MONTH = "CAL_BEGIN_MONTH";
	public static final String CAL_END_YEAR = "CAL_END_YEAR";
	public static final String CAL_END_MONTH = "CAL_END_MONTH";
	public static final String TS_BEGIN_YEAR = "TS_BEGIN_YEAR";
	public static final String TS_END_YEAR = "TS_END_YEAR";
	public static final String TS_SUMMER_FLAG = "TS_SUMMER_FLAG";
	public static final String TS_WINTER_FLAG = "TS_WINTER_FLAG";
	public static final String FS_BEGIN_YEAR = "FS_BEGIN_YEAR";
	public static final String FS_END_YEAR = "FS_END_YEAR";
	public static final String FS_SPRING_FLAG = "FS_SPRING_FLAG";
	public static final String FS_SUMMER_FLAG = "FS_SUMMER_FLAG";
	public static final String FS_FALL_FLAG = "FS_FALL_FLAG";
	public static final String FS_WINTER_FLAG = "FS_WINTER_FLAG";
	public static final String GEOG_QUERY_TYPE = "GEOG_QUERY_TYPE";
	public static final String GRANULARITY = "GRANULARITY";
	public static final String LOAD_FLAG = "LOAD_FLAG";
	public static final String GENERATION_FLAG = "GENERATION_FLAG";
	public static final String POI_QUERY_TYPE = "POI_QUERY_TYPE";
	public static final String POI_COINCIDENCE = "POI_COINCIDENCE";
	public static final String POI_CATEGORY = "POI_CATEGORY";
	public static final String POI_LOAD_FLAG = "POI_LOAD_FLAG";
	public static final String POI_GENERATION_FLAG = "POI_GENERATION_FLAG";
	public static final String TIME_INTERVAL = "TIME_INTERVAL";
	public static final String POI_PEAK_FLAG = "POI_PEAK_FLAG";
	public static final String POI_MEDIAN_FLAG = "POI_MEDIAN_FLAG";
	public static final String POI_LIGHT_FLAG = "POI_LIGHT_FLAG";
	public static final String QUERY_SQL_STRING = "QUERY_SQL_STRING";
	public static final String AUDIT_DATETIME = "AUDIT_DATETIME";
	public static final String QUERY_STATUS = "QUERY_STATUS";
	public static final String QUERY_ERRORS = "QUERY_ERRORS";
	public static final String QUERY_RUNTIME_MS = "QUERY_RUNTIME_MS";
	public static final String PARAMETER_ID = "PARAMETER_ID";
	public static final String PARAMETER_TYPE = "PARAMETER_TYPE";
	public static final String PARAMETER_VALUE = "PARAMETER_VALUE";
	public static final String DESC = "DESC";
	public static final String VALUE = "VALUE";
	public static final String ATTRIB = "ATTRIB";
	public static final String NEXTVAL = "NEXTVAL";
	public static final String YES = "Y";
	public static final String COMMA_SPACE = ", ";


	// labels
	public static final String AESO = "Alberta Electric System Operator";
	public static final String EVQ = "Electric Volumes Query";
	public static final String QUERY_DATE_LABEL = "Query Date: ";
	public static final String QUERY_NAME_LABEL = "Query Name: ";
	public static final String QUERY_NUMBER_LABEL = "Query Number: ";
	public static final String BEGIN_DATE_LABEL = "Begin Date: ";
	public static final String END_DATE_LABEL = "End Date: ";
	public static final String CAL_BEGIN_YM_LABEL = "Calendar Begin Year/Month: ";
	public static final String CAL_END_YM_LABEL = "Calendar End Year/Month: ";
	public static final String FOUR_SEASON_BEGIN_YEAR_LABEL = "Four season begin year: ";
	public static final String FOUR_SEASON_END_YEAR_LABEL = "Four season end year: ";
	public static final String TWO_SEASON_BEGIN_YEAR_LABEL = "Two season begin year: ";
	public static final String TWO_SEASON_END_YEAR_LABEL = "Two season end year: ";
	public static final String SEASON_LABEL = "Season: ";
	public static final String GEOG_QUERY_PARMS_LABEL = "Geog. Query Parms: ";
	public static final String GRANULARITY_LABEL = "Granularity: ";
	public static final String CATEGORY_LABEL = "Category: ";
	public static final String VOLUME_TYPE_LABEL = "Volume Type: ";
	public static final String LOAD = "Load";
	public static final String GENERATION = "Generation";
	public static final String INTEREST_POINT_TYPE_LABEL = "Interest Point Type: ";
	public static final String COINCIDENCE_LABEL = "Coincidence: ";
	public static final String POI_CATEGORY_LABEL = "POI Category: ";
	public static final String POI_VOLUME_TYPE_LABEL = "POI Volume Type: ";
	public static final String TIME_INTERVAL_LABEL = "Time Interval: ";
	public static final String INTEREST_POINT_LABEL = "Interest Point: ";
	public static final String SQL_LABEL = "SQL: ";
	public static final String QUERY = "Query";
	public static final String RESULTS = "Results";
	public static final String PEAK = "Peak";
	public static final String MEDIAN = "Median";
	public static final String LIGHT = "Light";
	public static final String SPRING = "Spring";
	public static final String SUMMER = "Summer";
	public static final String FALL = "Fall";
	public static final String WINTER = "Winter";
	
	public static final String COINCIDENCE_PA = "PA";
	public static final String COINCIDENCE_SB = "SB";
	public static final String COINCIDENCE_MP = "MP";
	
	public static HashMap columnTitles = new HashMap();

	// load a map of column names to column titles for the result set
	static {
		columnTitles.put(MP_ID, MP_ID_DESC);
		columnTitles.put(MEASURE_POINT_ID, MEASURE_POINT_ID_DESC);
		columnTitles.put(AREA_CODE, AREA_CODE_DESC);
		columnTitles.put(REGION_NAME, REGION_NAME_DESC);
		columnTitles.put(MEAS_POINT_TYPE_CODE, MEAS_POINT_TYPE_CODE_DESC);
		columnTitles.put(CONNECTION_TYPE, CONNECTION_TYPE_DESC);
		columnTitles.put(INCL_IN_POD_LSB, INCL_IN_POD_LSB_DESC);
		columnTitles.put(CATEGORY, CATEGORY_DESC);
		columnTitles.put(CAL_DAY_DATE, CAL_DAY_DATE_DESC);
		columnTitles.put(CAL_HOUR_ENDING, CAL_HOUR_ENDING_DESC);
		columnTitles.put(CAL_YEAR, CAL_YEAR_DESC);
		columnTitles.put(CAL_MONTH_NUMBER, CAL_MONTH_NUMBER_DESC);
		columnTitles.put(CAL_MONTH_SHORT_NAME, CAL_MONTH_SHORT_NAME_DESC);
		columnTitles.put(TWO_SEASON_YEAR, TWO_SEASON_YEAR_DESC);
		columnTitles.put(TWO_SEASON_NAME, TWO_SEASON_NAME_DESC);
		columnTitles.put(FOUR_SEASON_YEAR, FOUR_SEASON_YEAR_DESC);
		columnTitles.put(FOUR_SEASON_NAME, FOUR_SEASON_NAME_DESC);
		columnTitles.put(BUS_ID, BUS_ID_DESC);
		columnTitles.put(BUS_NAME, BUS_NAME_DESC);
		columnTitles.put(FACILITY_CODE, FACILITY_CODE_DESC);
		columnTitles.put(FACILITY_ID, FACILITY_ID_DESC);
		columnTitles.put(SUBSTATION_NAME, SUBSTATION_NAME_DESC);
		columnTitles.put(MW, MW_DESC);
		columnTitles.put(MVAR, MVAR_DESC);
		columnTitles.put(LOAD_MW, LOAD_MW_DESC);
		columnTitles.put(LOAD_MVAR, LOAD_MVAR_DESC);
		columnTitles.put(MAX_MWH, MAX_MWH_DESC);
		columnTitles.put(MAX_MVAR, MAX_MVAR_DESC);
		columnTitles.put(MIN_MWH, MIN_MWH_DESC);
		columnTitles.put(MIN_MVAR, MIN_MVAR_DESC);
		columnTitles.put(SUM_LOAD_MW, SUM_LOAD_MW_DESC);
		columnTitles.put(POINTOFINTEREST, POINTOFINTEREST_DESC);
	}
	
	public static String getColumnTitle(String colName) {

		Object colTitle = columnTitles.get(colName);
		if (colTitle!=null) {
			return (String)colTitle;
		} else
			return "";
	}
	
	public static String getMonthStr(int i) {
		if (i<0 || i>11) {
			return "";
		} else {
			return MONTHS[i];		
		}
	}
}
