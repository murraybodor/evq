package ca.aeso.evq.server.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.aeso.evq.common.DateQueryType;
import ca.aeso.evq.common.GeogQueryType;
import ca.aeso.evq.common.PoiQueryType;
import ca.aeso.evq.rpc.Query;

/**
 * SqlQueryDirectorTest
 * Test all the different use cases and assert that the constructed SQL matches expected results.
 * 
 * Test method naming convention:
 *    Hourly: 				testUC# + geographic area + granularity + "Hourly"
 *    Point of Interest: 	testUC# + geographic area + granularity + coincidence + time interval + "Poi"
 *  
 * @author mbodor
 */
public class SqlQueryDirectorTest extends TestCase {

	protected Log logger = LogFactory.getLog(SqlQueryDirectorTest.class);

	public SqlQueryDirectorTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		logger.debug("setUp called");
	}

	public void tearDown() throws Exception {
		logger.debug("tearDown called");
	}

	public void testUC1_Mp_Mp_Hourly() {
		logger.debug("testUC1_Mp_Mp_Hourly starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_SPECIFIC.getValue());
		aQuery.setBeginDate("2006/01/01");
		aQuery.setEndDate("2006/01/01");
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_MP.getValue());
		List mps = new ArrayList();
		mps.add("35S001");
		mps.add("351S009"); // dem
		mps.add("RL1"); // sup
		mps.add("BCR2"); // sup
		mps.add("BCRK"); // sup
		aQuery.setMeasurementPoints(mps);

		aQuery.setGranularity("MP"); // meas point
		aQuery.setCategory("RM"); // revenue metered
		aQuery.setLoadTypeSelected(true);
		
		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_HOURLY.getValue());

		String expectedSql = "SELECT t1.CAL_YEAR as CAL_YEAR , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME as CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.AREA_CODE as AREA_CODE , t1.MP_ID as MP_ID , t1.CONNECTION_TYPE as CONNECTION_TYPE , t1.INCL_IN_POD_LSB as INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY as CATEGORY , t1.LOAD_MW as LOAD_MW , t1.LOAD_MVAR as LOAD_MVAR , t1.MAX_MWH as MAX_MWH , t1.MAX_MVAR as MAX_MVAR , t1.MIN_MWH as MIN_MWH , t1.MIN_MVAR as MIN_MVAR FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND  t1.CAL_DAY_DATE between TO_DATE('2006/01/01', 'YYYY/MM/DD') AND TO_DATE('2006/01/01', 'YYYY/MM/DD') AND  t1.mp_id IN ('35S001', '351S009', 'RL1', 'BCR2', 'BCRK') AND t1.incl_in_pod_lsb = 'Y' ORDER BY t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE , t1.MP_ID , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC1_Mp_Mp_Hourly sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}

	
	public void testUC2_Area_Sb_Pa_Tot_Poi() {
		logger.debug("testUC2_Area_Sb_Pa_Tot_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_SPECIFIC.getValue());
		aQuery.setBeginDate("2006/03/15");
		aQuery.setEndDate("2006/03/15");
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_AREA.getValue());
		List areas = new ArrayList();
		areas.add("6");
		aQuery.setPlanningAreas(areas);

		aQuery.setGranularity("SB"); // substation
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); // loads only

		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("PA"); // planning area
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiLoadSelected(true); // loads only
		aQuery.setTimeInterval("TO"); // total
		aQuery.setPoiPeakSelected(true); // peak only

		String expectedSql = "SELECT t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.FACILITY_CODE as FACILITY_CODE , t2000.SUBSTATION_NAME as SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.AREA_CODE as AREA_CODE , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.AREA_CODE as AREA_CODE , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.AREA_CODE,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, max_sum_load_mw, 'Peak' ) as POINTOFINTEREST FROM ( SELECT t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.AREA_CODE as AREA_CODE , SUM(t1.load_mw) as SUM_LOAD_MW , MAX (SUM(t1.load_mw)) over( PARTITION BY  t1.AREA_CODE) as MAX_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = 'DEM' AND t1.incl_in_pod_lsb = 'Y' AND  t1.CAL_DAY_DATE between TO_DATE('2006/03/15', 'YYYY/MM/DD') AND TO_DATE('2006/03/15', 'YYYY/MM/DD') AND  t1.area_code IN ('6') GROUP BY t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( max_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t1000.area_code = t2000.area_code AND t2000.measurement_point_type_code IN ('DEM' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.area_code IN ('6') AND  t2000.CAL_DAY_DATE between TO_DATE('2006/03/15', 'YYYY/MM/DD') AND TO_DATE('2006/03/15', 'YYYY/MM/DD') GROUP BY t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";

		SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC2_Area_Sb_Pa_Tot_Poi sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}

	public void testUC3_Area_Bu_Sb_Seasonal_Poi() {
		logger.debug("testUC3_Area_Bu_Sb_Seasonal_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.getValue());
		aQuery.setTwoSeasBeginYear(new Integer(2002));
		aQuery.setTwoSeasEndYear(new Integer(2007));
		aQuery.setTwoSeasonSummerSelected(true);
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_AREA.getValue());
		List areas = new ArrayList();
		areas.add("47");
		aQuery.setPlanningAreas(areas);
		
		aQuery.setGranularity("BU"); // bus
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); // loads

		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("SB"); // substation
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiLoadSelected(true); // loads
		aQuery.setTimeInterval("SE"); // seasonal

		aQuery.setPoiPeakSelected(true); // peak
		aQuery.setPoiMedianSelected(true); // median
		aQuery.setPoiLightSelected(true); // light

		String expectedSql = "SELECT t2000.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME as TWO_SEASON_NAME , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.FACILITY_CODE as FACILITY_CODE , t2000.SUBSTATION_NAME as SUBSTATION_NAME , t2000.BUS_ID as BUS_ID , t2000.BUS_NAME as BUS_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t100.TWO_SEASON_NAME as TWO_SEASON_NAME , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.AREA_CODE as AREA_CODE , t100.FACILITY_CODE as FACILITY_CODE , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t10.TWO_SEASON_NAME as TWO_SEASON_NAME , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.AREA_CODE as AREA_CODE , t10.FACILITY_CODE as FACILITY_CODE , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.TWO_SEASON_YEAR,  t10.TWO_SEASON_NAME,  t10.AREA_CODE,  t10.FACILITY_CODE,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, max_sum_load_mw, 'Peak' , median_sum_load_mw, 'Median' , min_sum_load_mw, 'Light' ) as POINTOFINTEREST FROM ( SELECT t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.AREA_CODE as AREA_CODE , t1.FACILITY_CODE as FACILITY_CODE , SUM(t1.load_mw) as SUM_LOAD_MW , MAX (SUM(t1.load_mw)) over( PARTITION BY  t1.TWO_SEASON_YEAR,  t1.TWO_SEASON_NAME,  t1.AREA_CODE,  t1.FACILITY_CODE) as MAX_SUM_LOAD_MW , PERCENTILE_DISC (0.5) WITHIN GROUP ( ORDER BY SUM(t1.LOAD_MW) DESC) OVER ( PARTITION BY  t1.TWO_SEASON_YEAR,  t1.TWO_SEASON_NAME,  t1.AREA_CODE,  t1.FACILITY_CODE) as MEDIAN_SUM_LOAD_MW , MIN (SUM(t1.load_mw)) over( PARTITION BY  t1.TWO_SEASON_YEAR,  t1.TWO_SEASON_NAME,  t1.AREA_CODE,  t1.FACILITY_CODE) as MIN_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_BUS_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = 'DEM' AND t1.incl_in_pod_lsb = 'Y' AND  t1.two_season_year BETWEEN 2002 AND 2007 AND  t1.two_season_name = 'SUMMER' AND  t1.area_code IN ('47') GROUP BY t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE , t1.FACILITY_CODE ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( max_sum_load_mw , median_sum_load_mw , min_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t1000.facility_code = t2000.facility_code AND t2000.measurement_point_type_code IN ('DEM' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.area_code IN ('47') AND  t2000.two_season_year BETWEEN 2002 AND 2007 AND  t2000.two_season_name = 'SUMMER' GROUP BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.BUS_ID , t2000.BUS_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.BUS_ID , t2000.BUS_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC3_Area_Bu_Sb_Seasonal_Poi 3 sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}

	public void testUC4_Area_Pa_Pa_Monthly_Poi() {
		logger.debug("testUC4_Area_Pa_Pa_Monthly_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.getValue());
		aQuery.setFourSeasBeginYear(new Integer(2006));
		aQuery.setFourSeasEndYear(new Integer(2008));
		aQuery.setFourSeasonSummerSelected(true);
		aQuery.setFourSeasonWinterSelected(true);
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_AREA.getValue());
		List areas = new ArrayList();
		areas.add("44");
		areas.add("46");
		areas.add("47");
		areas.add("48");
		aQuery.setPlanningAreas(areas);

		aQuery.setGranularity("PA"); // area
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); 
		aQuery.setGenerationTypeSelected(true); 

		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("PA"); // substation
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiLoadSelected(true); // loads
		aQuery.setTimeInterval("MO"); // monthly

		aQuery.setPoiPeakSelected(true); // peak
		aQuery.setPoiLightSelected(true); // light

		
		String expectedSql = "SELECT t2000.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t2000.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t100.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t100.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.AREA_CODE as AREA_CODE , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t10.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t10.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.AREA_CODE as AREA_CODE , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.FOUR_SEASON_YEAR,  t10.FOUR_SEASON_NAME,  t10.CAL_MONTH_NUMBER,  t10.AREA_CODE,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, max_sum_load_mw, 'Peak' , min_sum_load_mw, 'Light' ) as POINTOFINTEREST FROM ( SELECT t1.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.AREA_CODE as AREA_CODE , SUM(t1.load_mw) as SUM_LOAD_MW , MAX (SUM(t1.load_mw)) over( PARTITION BY  t1.FOUR_SEASON_YEAR,  t1.FOUR_SEASON_NAME,  t1.CAL_MONTH_NUMBER,  t1.AREA_CODE) as MAX_SUM_LOAD_MW , MIN (SUM(t1.load_mw)) over( PARTITION BY  t1.FOUR_SEASON_YEAR,  t1.FOUR_SEASON_NAME,  t1.CAL_MONTH_NUMBER,  t1.AREA_CODE) as MIN_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = 'DEM' AND t1.incl_in_pod_lsb = 'Y' AND  t1.four_season_year BETWEEN 2006 AND 2008 AND  t1.four_season_name IN ('SUMMER', 'WINTER' )  AND  t1.area_code IN ('44', '46', '47', '48') GROUP BY t1.FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME , t1.CAL_MONTH_NUMBER , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( max_sum_load_mw , min_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_LTLF_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t1000.area_code = t2000.area_code AND t2000.measurement_point_type_code IN ('DEM', 'SUP' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.area_code IN ('44', '46', '47', '48') AND  t2000.four_season_year BETWEEN 2006 AND 2008 AND  t2000.four_season_name IN ('SUMMER', 'WINTER' )  GROUP BY t2000.FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME , t2000.CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME , t2000.CAL_MONTH_NUMBER , t2000.AREA_CODE , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC4_Area_Pa_Pa_Monthly_Poi sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}

	public void testUC5_Sub_Sb_Ts_Monthly_Poi() {
		logger.debug("testUC5_Sub_Sb_Ts_Monthly_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_CALENDAR.getValue());
		aQuery.setCalBeginYear(new Integer(2005));
		aQuery.setCalBeginMonth(new Integer(1));
		
		aQuery.setCalEndYear(new Integer(2006));
		aQuery.setCalEndMonth(new Integer(6));
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.getValue());
		List subs = new ArrayList();

		subs.add("342S"); // 342S
		subs.add("346S"); // 346S
		subs.add("SS-10"); // SS-10
		subs.add("SS-6"); // SS-6
		subs.add("SS-5"); // SS-5
		subs.add("5S"); // 5S
		
		aQuery.setSubstations(subs);

		aQuery.setGranularity("SB"); // area
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); 

		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("TS"); // total of substations
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiLoadSelected(true); // loads
		aQuery.setTimeInterval("MO"); // monthly

		aQuery.setPoiMedianSelected(true); // med
		aQuery.setPoiLightSelected(true); // light
		
		String expectedSql = "SELECT t2000.CAL_YEAR as CAL_YEAR , t2000.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.FACILITY_CODE as FACILITY_CODE , t2000.SUBSTATION_NAME as SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.CAL_YEAR as CAL_YEAR , t100.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.CAL_YEAR as CAL_YEAR , t10.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.CAL_YEAR,  t10.CAL_MONTH_NUMBER,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, median_sum_load_mw, 'Median' , min_sum_load_mw, 'Light' ) as POINTOFINTEREST FROM ( SELECT t1.CAL_YEAR as CAL_YEAR , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , SUM(t1.load_mw) as SUM_LOAD_MW , PERCENTILE_DISC (0.5) WITHIN GROUP ( ORDER BY SUM(t1.LOAD_MW) DESC) OVER ( PARTITION BY  t1.CAL_YEAR,  t1.CAL_MONTH_NUMBER) as MEDIAN_SUM_LOAD_MW , MIN (SUM(t1.load_mw)) over( PARTITION BY  t1.CAL_YEAR,  t1.CAL_MONTH_NUMBER) as MIN_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_BUS_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = 'DEM' AND t1.incl_in_pod_lsb = 'Y' AND t1.cal_year BETWEEN 2005 AND 2006 AND  t1.facility_code IN ('342S', '346S', 'SS-10', 'SS-6', 'SS-5', '5S') GROUP BY t1.CAL_YEAR , t1.CAL_MONTH_NUMBER , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( median_sum_load_mw , min_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t2000.measurement_point_type_code IN ('DEM' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.facility_code IN ('342S', '346S', 'SS-10', 'SS-6', 'SS-5', '5S') AND t2000.cal_year BETWEEN 2005 AND 2006 GROUP BY t2000.CAL_YEAR , t2000.CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.CAL_YEAR , t2000.CAL_MONTH_NUMBER , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";
		SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC5_Sub_Sb_Ts_Monthly_Poi 5 sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}

	public void testUC6_Sub_Sb_Ts_Monthly_Poi() {
		logger.debug("testUC6_Sub_Sb_Ts_Monthly_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_CALENDAR.getValue());
		aQuery.setCalBeginYear(new Integer(2005));
		aQuery.setCalBeginMonth(new Integer(1));
		
		aQuery.setCalEndYear(new Integer(2005));
		aQuery.setCalEndMonth(new Integer(6));
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.getValue());
		List subs = new ArrayList();

		subs.add("342S"); // 342S
		subs.add("SS-6"); // SS-6
		subs.add("SS-5"); // SS-5
		subs.add("5S"); // 5S
		
		
		aQuery.setSubstations(subs);

		aQuery.setGranularity("SB"); // area
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); 

		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("TS"); // total of substations
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiLoadSelected(true); // loads
		aQuery.setTimeInterval("MO"); // monthly

		aQuery.setPoiMedianSelected(true); // med
		aQuery.setPoiLightSelected(true); // light
		
		String expectedSql = "SELECT t2000.CAL_YEAR as CAL_YEAR , t2000.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.FACILITY_CODE as FACILITY_CODE , t2000.SUBSTATION_NAME as SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.CAL_YEAR as CAL_YEAR , t100.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.CAL_YEAR as CAL_YEAR , t10.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.CAL_YEAR,  t10.CAL_MONTH_NUMBER,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, median_sum_load_mw, 'Median' , min_sum_load_mw, 'Light' ) as POINTOFINTEREST FROM ( SELECT t1.CAL_YEAR as CAL_YEAR , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , SUM(t1.load_mw) as SUM_LOAD_MW , PERCENTILE_DISC (0.5) WITHIN GROUP ( ORDER BY SUM(t1.LOAD_MW) DESC) OVER ( PARTITION BY  t1.CAL_YEAR,  t1.CAL_MONTH_NUMBER) as MEDIAN_SUM_LOAD_MW , MIN (SUM(t1.load_mw)) over( PARTITION BY  t1.CAL_YEAR,  t1.CAL_MONTH_NUMBER) as MIN_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_BUS_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = 'DEM' AND t1.incl_in_pod_lsb = 'Y' AND t1.cal_year BETWEEN 2005 AND 2005 AND  t1.facility_code IN ('342S', 'SS-6', 'SS-5', '5S') GROUP BY t1.CAL_YEAR , t1.CAL_MONTH_NUMBER , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( median_sum_load_mw , min_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t2000.measurement_point_type_code IN ('DEM' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.facility_code IN ('342S', 'SS-6', 'SS-5', '5S') AND t2000.cal_year BETWEEN 2005 AND 2005 GROUP BY t2000.CAL_YEAR , t2000.CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.CAL_YEAR , t2000.CAL_MONTH_NUMBER , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";

		SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC6_Sub_Sb_Ts_Monthly_Poi sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}

	public void testUC7_Sub_Sb_Ts_Monthly_Poi() {
		logger.debug("testUC7_Sub_Sb_Ts_Monthly_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.getValue());
		aQuery.setTwoSeasBeginYear(new Integer(2005));
		aQuery.setTwoSeasEndYear(new Integer(2006));
		aQuery.setTwoSeasonWinterSelected(true);
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.getValue());
		List subs = new ArrayList();
		subs.add("342S"); // 342S
		subs.add("SS-6"); // SS-6
		subs.add("SS-5"); // SS-5
		subs.add("5S"); // 5S
		
		aQuery.setSubstations(subs);

		aQuery.setGranularity("SB"); // area
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); 

		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("TS"); // total of substations
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiLoadSelected(true); // loads
		aQuery.setTimeInterval("MO"); // monthly

		aQuery.setPoiMedianSelected(true); // med
		aQuery.setPoiLightSelected(true); // light
		
		String expectedSql = "SELECT t2000.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME as TWO_SEASON_NAME , t2000.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.FACILITY_CODE as FACILITY_CODE , t2000.SUBSTATION_NAME as SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t100.TWO_SEASON_NAME as TWO_SEASON_NAME , t100.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t10.TWO_SEASON_NAME as TWO_SEASON_NAME , t10.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.TWO_SEASON_YEAR,  t10.TWO_SEASON_NAME,  t10.CAL_MONTH_NUMBER,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, median_sum_load_mw, 'Median' , min_sum_load_mw, 'Light' ) as POINTOFINTEREST FROM ( SELECT t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , SUM(t1.load_mw) as SUM_LOAD_MW , PERCENTILE_DISC (0.5) WITHIN GROUP ( ORDER BY SUM(t1.LOAD_MW) DESC) OVER ( PARTITION BY  t1.TWO_SEASON_YEAR,  t1.TWO_SEASON_NAME,  t1.CAL_MONTH_NUMBER) as MEDIAN_SUM_LOAD_MW , MIN (SUM(t1.load_mw)) over( PARTITION BY  t1.TWO_SEASON_YEAR,  t1.TWO_SEASON_NAME,  t1.CAL_MONTH_NUMBER) as MIN_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_BUS_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = 'DEM' AND t1.incl_in_pod_lsb = 'Y' AND  t1.two_season_year BETWEEN 2005 AND 2006 AND  t1.two_season_name = 'WINTER' AND  t1.facility_code IN ('342S', 'SS-6', 'SS-5', '5S') GROUP BY t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.CAL_MONTH_NUMBER , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( median_sum_load_mw , min_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t2000.measurement_point_type_code IN ('DEM' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.facility_code IN ('342S', 'SS-6', 'SS-5', '5S') AND  t2000.two_season_year BETWEEN 2005 AND 2006 AND  t2000.two_season_name = 'WINTER' GROUP BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.CAL_MONTH_NUMBER , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";

		SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC7_Sub_Sb_Ts_Monthly_Poi sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}

	public void testUC8_Sub_Sb_Sb_Monthly_Poi() {
		logger.debug("testUC8_Sub_Sb_Sb_Monthly_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.getValue());
		aQuery.setTwoSeasBeginYear(new Integer(2005));
		aQuery.setTwoSeasEndYear(new Integer(2006));
		aQuery.setTwoSeasonWinterSelected(true);
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.getValue());
		List subs = new ArrayList();
		subs.add("342S"); // 342S
		subs.add("SS-6"); // SS-6
		subs.add("SS-5"); // SS-5
		subs.add("5S"); // 5S
		
		aQuery.setSubstations(subs);

		aQuery.setGranularity("SB"); // area
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); 

		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("SB"); // total of substations
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiLoadSelected(true); // loads
		aQuery.setTimeInterval("MO"); // monthly

		aQuery.setPoiMedianSelected(true); // med
		aQuery.setPoiLightSelected(true); // light
		
		String expectedSql = "SELECT t2000.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME as TWO_SEASON_NAME , t2000.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.FACILITY_CODE as FACILITY_CODE , t2000.SUBSTATION_NAME as SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t100.TWO_SEASON_NAME as TWO_SEASON_NAME , t100.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.AREA_CODE as AREA_CODE , t100.FACILITY_CODE as FACILITY_CODE , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t10.TWO_SEASON_NAME as TWO_SEASON_NAME , t10.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.AREA_CODE as AREA_CODE , t10.FACILITY_CODE as FACILITY_CODE , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.TWO_SEASON_YEAR,  t10.TWO_SEASON_NAME,  t10.CAL_MONTH_NUMBER,  t10.AREA_CODE,  t10.FACILITY_CODE,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, median_sum_load_mw, 'Median' , min_sum_load_mw, 'Light' ) as POINTOFINTEREST FROM ( SELECT t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.AREA_CODE as AREA_CODE , t1.FACILITY_CODE as FACILITY_CODE , SUM(t1.load_mw) as SUM_LOAD_MW , PERCENTILE_DISC (0.5) WITHIN GROUP ( ORDER BY SUM(t1.LOAD_MW) DESC) OVER ( PARTITION BY  t1.TWO_SEASON_YEAR,  t1.TWO_SEASON_NAME,  t1.CAL_MONTH_NUMBER,  t1.AREA_CODE,  t1.FACILITY_CODE) as MEDIAN_SUM_LOAD_MW , MIN (SUM(t1.load_mw)) over( PARTITION BY  t1.TWO_SEASON_YEAR,  t1.TWO_SEASON_NAME,  t1.CAL_MONTH_NUMBER,  t1.AREA_CODE,  t1.FACILITY_CODE) as MIN_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_BUS_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = 'DEM' AND t1.incl_in_pod_lsb = 'Y' AND  t1.two_season_year BETWEEN 2005 AND 2006 AND  t1.two_season_name = 'WINTER' AND  t1.facility_code IN ('342S', 'SS-6', 'SS-5', '5S') GROUP BY t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.CAL_MONTH_NUMBER , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE , t1.FACILITY_CODE ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( median_sum_load_mw , min_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t1000.facility_code = t2000.facility_code AND t2000.measurement_point_type_code IN ('DEM' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.facility_code IN ('342S', 'SS-6', 'SS-5', '5S') AND  t2000.two_season_year BETWEEN 2005 AND 2006 AND  t2000.two_season_name = 'WINTER' GROUP BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.CAL_MONTH_NUMBER , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";

		SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC8_Sub_Sb_Sb_Monthly_Poi sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}

	public void testUC9_Sub_Sb_Ts_Daily_Poi() {
		logger.debug("testUC9_Sub_Sb_Ts_Daily_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.getValue());
		aQuery.setTwoSeasBeginYear(new Integer(2005));
		aQuery.setTwoSeasEndYear(new Integer(2006));
		aQuery.setTwoSeasonWinterSelected(true);
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.getValue());
		List subs = new ArrayList();
		subs.add("342S"); // 342S
		subs.add("SS-6"); // SS-6
		subs.add("SS-5"); // SS-5
		subs.add("5S"); // 5S
		
		aQuery.setSubstations(subs);

		aQuery.setGranularity("SB"); // area
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); 

		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("TS"); // total of substations
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiLoadSelected(true); // loads
		aQuery.setTimeInterval("DA"); // daily

		aQuery.setPoiMedianSelected(true); // med
		aQuery.setPoiLightSelected(true); // light
		
		String expectedSql = "SELECT t2000.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME as TWO_SEASON_NAME , t2000.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.FACILITY_CODE as FACILITY_CODE , t2000.SUBSTATION_NAME as SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t100.TWO_SEASON_NAME as TWO_SEASON_NAME , t100.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t10.TWO_SEASON_NAME as TWO_SEASON_NAME , t10.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.TWO_SEASON_YEAR,  t10.TWO_SEASON_NAME,  t10.CAL_MONTH_NUMBER,  t10.CAL_DAY_DATE,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, median_sum_load_mw, 'Median' , min_sum_load_mw, 'Light' ) as POINTOFINTEREST FROM ( SELECT t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , SUM(t1.load_mw) as SUM_LOAD_MW , PERCENTILE_DISC (0.5) WITHIN GROUP ( ORDER BY SUM(t1.LOAD_MW) DESC) OVER ( PARTITION BY  t1.TWO_SEASON_YEAR,  t1.TWO_SEASON_NAME,  t1.CAL_MONTH_NUMBER,  t1.CAL_DAY_DATE) as MEDIAN_SUM_LOAD_MW , MIN (SUM(t1.load_mw)) over( PARTITION BY  t1.TWO_SEASON_YEAR,  t1.TWO_SEASON_NAME,  t1.CAL_MONTH_NUMBER,  t1.CAL_DAY_DATE) as MIN_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_BUS_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = 'DEM' AND t1.incl_in_pod_lsb = 'Y' AND  t1.two_season_year BETWEEN 2005 AND 2006 AND  t1.two_season_name = 'WINTER' AND  t1.facility_code IN ('342S', 'SS-6', 'SS-5', '5S') GROUP BY t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.CAL_MONTH_NUMBER , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( median_sum_load_mw , min_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t2000.measurement_point_type_code IN ('DEM' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.facility_code IN ('342S', 'SS-6', 'SS-5', '5S') AND  t2000.two_season_year BETWEEN 2005 AND 2006 AND  t2000.two_season_name = 'WINTER' GROUP BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC9_Sub_Sb_Ts_Daily_Poi sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}

	public void testUC10_Sub_Bu_De_Daily_Poi() {
		logger.debug("testUC10_Sub_Bu_De_Daily_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_SPECIFIC.getValue());
		aQuery.setBeginDate("2007/02/15");
		aQuery.setEndDate("2007/02/28");
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.getValue());
		List subs = new ArrayList();
		subs.add("342S"); // 342S
		subs.add("SS-6"); // SS-6
		subs.add("SS-5"); // SS-5
		subs.add("5S"); // 5S
		
		aQuery.setSubstations(subs);

		aQuery.setGranularity("BU"); // bus
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); 

		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("DE"); // total of demand
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiLoadSelected(true); // loads
		aQuery.setTimeInterval("DA"); // daily

		aQuery.setPoiPeakSelected(true); // peak
		
		String expectedSql = "SELECT t2000.CAL_YEAR as CAL_YEAR , t2000.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.FACILITY_CODE as FACILITY_CODE , t2000.SUBSTATION_NAME as SUBSTATION_NAME , t2000.BUS_ID as BUS_ID , t2000.BUS_NAME as BUS_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.CAL_YEAR as CAL_YEAR , t100.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.CAL_YEAR as CAL_YEAR , t10.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.CAL_YEAR,  t10.CAL_MONTH_NUMBER,  t10.CAL_DAY_DATE,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, max_sum_load_mw, 'Peak' ) as POINTOFINTEREST FROM ( SELECT t1.CAL_YEAR as CAL_YEAR , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , SUM(t1.load_mw) as SUM_LOAD_MW , MAX (SUM(t1.load_mw)) over( PARTITION BY  t1.CAL_YEAR,  t1.CAL_MONTH_NUMBER,  t1.CAL_DAY_DATE) as MAX_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = 'DEM' AND t1.incl_in_pod_lsb = 'Y' AND  t1.CAL_DAY_DATE between TO_DATE('2007/02/15', 'YYYY/MM/DD') AND TO_DATE('2007/02/28', 'YYYY/MM/DD') GROUP BY t1.CAL_YEAR , t1.CAL_MONTH_NUMBER , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( max_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t2000.measurement_point_type_code IN ('DEM' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.facility_code IN ('342S', 'SS-6', 'SS-5', '5S') AND  t2000.CAL_DAY_DATE between TO_DATE('2007/02/15', 'YYYY/MM/DD') AND TO_DATE('2007/02/28', 'YYYY/MM/DD') GROUP BY t2000.CAL_YEAR , t2000.CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.BUS_ID , t2000.BUS_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.CAL_YEAR , t2000.CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.BUS_ID , t2000.BUS_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC10_Sub_Bu_De_Daily_Poi sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}
	
	public void testUC11_Sub_Bu_Hourly() {
		logger.debug("testUC11_Sub_Bu_Hourly starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_SPECIFIC.getValue());
		aQuery.setBeginDate("2006/01/01");
		aQuery.setEndDate("2006/01/01");
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.getValue());
		List subs = new ArrayList();
		
		subs.add("342S"); // 342S
		subs.add("346S"); // 346S
		subs.add("SS-10"); // SS-10
		subs.add("SS-6"); // SS-6
		subs.add("SS-5"); // SS-5
		subs.add("5S"); // 5S
		subs.add("535S"); // joffre = nasty!
		
		aQuery.setSubstations(subs);

		aQuery.setGranularity("BU");
		aQuery.setCategory("RM");
		aQuery.setLoadTypeSelected(true);
		
		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_HOURLY.getValue());

		String expectedSql = "SELECT t1.CAL_YEAR as CAL_YEAR , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME as CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.AREA_CODE as AREA_CODE , t1.FACILITY_CODE as FACILITY_CODE , t1.SUBSTATION_NAME as SUBSTATION_NAME , t1.BUS_ID as BUS_ID , t1.BUS_NAME as BUS_NAME , t1.CONNECTION_TYPE as CONNECTION_TYPE , t1.INCL_IN_POD_LSB as INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY as CATEGORY , SUM(LOAD_MW) as LOAD_MW , SUM(LOAD_MVAR) as LOAD_MVAR , SUM(MAX_MWH) as MAX_MWH , SUM(MAX_MVAR) as MAX_MVAR , SUM(MIN_MWH) as MIN_MWH , SUM(MIN_MVAR) as MIN_MVAR FROM ( IHFC_CORP_HIST_BUS_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code IN ('DEM' )  AND  t1.CAL_DAY_DATE between TO_DATE('2006/01/01', 'YYYY/MM/DD') AND TO_DATE('2006/01/01', 'YYYY/MM/DD') AND  t1.facility_code IN ('342S', '346S', 'SS-10', 'SS-6', 'SS-5', '5S', '535S') AND t1.incl_in_pod_lsb = 'Y' GROUP BY t1.CAL_YEAR , t1.CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE , t1.FACILITY_CODE , t1.SUBSTATION_NAME , t1.BUS_ID , t1.BUS_NAME , t1.CONNECTION_TYPE , t1.INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY ORDER BY t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE , t1.FACILITY_CODE , t1.SUBSTATION_NAME , t1.BUS_ID , t1.BUS_NAME , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY";

		SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC11_Sub_Bu_Hourly sql=" + sql);
		assertEquals(sql, expectedSql);
		
	}

	public void testUC12_Sub_Sb_Hourly() {
		logger.debug("testUC12_Sub_Sb_Hourly starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_SPECIFIC.getValue());
		aQuery.setBeginDate("2006/01/01");
		aQuery.setEndDate("2006/01/01");
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.getValue());
		List subs = new ArrayList();
		subs.add("342S"); // 342S
		subs.add("346S"); // 346S
		subs.add("SS-10"); // SS-10
		subs.add("SS-6"); // SS-6
		subs.add("SS-5"); // SS-5
		subs.add("5S"); // 5S
		subs.add("535S"); // joffre = nasty!
		
		aQuery.setSubstations(subs);

		aQuery.setGranularity("SB");
		aQuery.setCategory("RM");
		aQuery.setLoadTypeSelected(true);
		aQuery.setGenerationTypeSelected(true);
		
		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_HOURLY.getValue());

		String expectedSql = "SELECT t1.CAL_YEAR as CAL_YEAR , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME as CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.AREA_CODE as AREA_CODE , t1.FACILITY_CODE as FACILITY_CODE , t1.SUBSTATION_NAME as SUBSTATION_NAME , t1.CONNECTION_TYPE as CONNECTION_TYPE , t1.INCL_IN_POD_LSB as INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY as CATEGORY , SUM(LOAD_MW) as LOAD_MW , SUM(LOAD_MVAR) as LOAD_MVAR , SUM(MAX_MWH) as MAX_MWH , SUM(MAX_MVAR) as MAX_MVAR , SUM(MIN_MWH) as MIN_MWH , SUM(MIN_MVAR) as MIN_MVAR FROM ( IHFC_CORP_HIST_BUS_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code IN ('DEM', 'SUP' )  AND  t1.CAL_DAY_DATE between TO_DATE('2006/01/01', 'YYYY/MM/DD') AND TO_DATE('2006/01/01', 'YYYY/MM/DD') AND  t1.facility_code IN ('342S', '346S', 'SS-10', 'SS-6', 'SS-5', '5S', '535S') AND t1.incl_in_pod_lsb = 'Y' GROUP BY t1.CAL_YEAR , t1.CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE , t1.FACILITY_CODE , t1.SUBSTATION_NAME , t1.CONNECTION_TYPE , t1.INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY ORDER BY t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE , t1.FACILITY_CODE , t1.SUBSTATION_NAME , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC12_Sub_Sb_Hourly sql=" + sql);
		assertEquals(sql, expectedSql);
		
	}

	public void testUC13_Sub_Ts_Hourly() {
		logger.debug("testUC13_Sub_Ts_Hourly starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_SPECIFIC.getValue());
		aQuery.setBeginDate("2006/01/01");
		aQuery.setEndDate("2006/01/01");
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.getValue());
		List subs = new ArrayList();
		
		subs.add("342S"); // 342S
		subs.add("346S"); // 346S
		subs.add("SS-10"); // SS-10
		subs.add("SS-6"); // SS-6
		subs.add("SS-5"); // SS-5
		subs.add("5S"); // 5S
		subs.add("535S"); // joffre = nasty!

		aQuery.setSubstations(subs);

		aQuery.setGranularity("TS");
		aQuery.setCategory("RM");
		aQuery.setLoadTypeSelected(true);
		aQuery.setGenerationTypeSelected(true);
		
		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_HOURLY.getValue());

		String expectedSql = "SELECT t1.CAL_YEAR as CAL_YEAR , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME as CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.CONNECTION_TYPE as CONNECTION_TYPE , t1.INCL_IN_POD_LSB as INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY as CATEGORY , SUM(LOAD_MW) as LOAD_MW , SUM(LOAD_MVAR) as LOAD_MVAR , SUM(MAX_MWH) as MAX_MWH , SUM(MAX_MVAR) as MAX_MVAR , SUM(MIN_MWH) as MIN_MWH , SUM(MIN_MVAR) as MIN_MVAR FROM ( IHFC_CORP_HIST_BUS_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code IN ('DEM', 'SUP' )  AND  t1.CAL_DAY_DATE between TO_DATE('2006/01/01', 'YYYY/MM/DD') AND TO_DATE('2006/01/01', 'YYYY/MM/DD') AND  t1.facility_code IN ('342S', '346S', 'SS-10', 'SS-6', 'SS-5', '5S', '535S') AND t1.incl_in_pod_lsb = 'Y' GROUP BY t1.CAL_YEAR , t1.CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.CONNECTION_TYPE , t1.INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY ORDER BY t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC13_Sub_Ts_Hourly sql=" + sql);
		assertEquals(sql, expectedSql);
		
	}

	public void testUseCase1AreaMpQuery() {
		// client prevents
	}
	public void testUseCase1AreaBusQuery() {
		// client prevents
	}
	public void testUseCase1AreaSubstationQuery() {
		// client prevents
	}
	
	public void testUC14_Area_Pa_Hourly() {
		logger.debug("testUC14_Area_Pa_Hourly starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.getValue());
		aQuery.setFourSeasBeginYear(new Integer(2006));
		aQuery.setFourSeasEndYear(new Integer(2006));
		aQuery.setFourSeasonSummerSelected(true);
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_AREA.getValue());
		List areas = new ArrayList();
		areas.add("44");
		areas.add("46");
		areas.add("47");
		areas.add("48");
		aQuery.setPlanningAreas(areas);

		aQuery.setGranularity("PA");
		aQuery.setCategory("RM");
		aQuery.setLoadTypeSelected(true);
		aQuery.setGenerationTypeSelected(true);
		
		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_HOURLY.getValue());

		String expectedSql = "SELECT t1.CAL_YEAR as CAL_YEAR , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME as CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.AREA_CODE as AREA_CODE , t1.CONNECTION_TYPE as CONNECTION_TYPE , t1.INCL_IN_POD_LSB as INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY as CATEGORY , SUM(LOAD_MW) as LOAD_MW , SUM(LOAD_MVAR) as LOAD_MVAR , SUM(MAX_MWH) as MAX_MWH , SUM(MAX_MVAR) as MAX_MVAR , SUM(MIN_MWH) as MIN_MWH , SUM(MIN_MVAR) as MIN_MVAR FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code IN ('DEM', 'SUP' )  AND  t1.four_season_year BETWEEN 2006 AND 2006 AND  t1.four_season_name IN ('SUMMER' )  AND  t1.area_code IN ('44', '46', '47', '48') AND t1.incl_in_pod_lsb = 'Y' GROUP BY t1.CAL_YEAR , t1.CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE , t1.CONNECTION_TYPE , t1.INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY ORDER BY t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC14_Area_Pa_Hourly sql=" + sql);
		assertEquals(sql, expectedSql);
		
	}

	public void testUC15_Area_Ta_Hourly() {
		logger.debug("testUC15_Area_Ta_Hourly starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.getValue());
		aQuery.setFourSeasBeginYear(new Integer(2006));
		aQuery.setFourSeasEndYear(new Integer(2006));
		aQuery.setFourSeasonSummerSelected(true);
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_AREA.getValue());
		List areas = new ArrayList();
		areas.add("44");
		areas.add("46");
		areas.add("47");
		areas.add("48");
		aQuery.setPlanningAreas(areas);

		aQuery.setGranularity("TA");
		aQuery.setCategory("RM");
		aQuery.setLoadTypeSelected(true);
		aQuery.setGenerationTypeSelected(true);
		
		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_HOURLY.getValue());

		String expectedSql = "SELECT t1.CAL_YEAR as CAL_YEAR , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME as CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.CONNECTION_TYPE as CONNECTION_TYPE , t1.INCL_IN_POD_LSB as INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY as CATEGORY , SUM(LOAD_MW) as LOAD_MW , SUM(LOAD_MVAR) as LOAD_MVAR , SUM(MAX_MWH) as MAX_MWH , SUM(MAX_MVAR) as MAX_MVAR , SUM(MIN_MWH) as MIN_MWH , SUM(MIN_MVAR) as MIN_MVAR FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code IN ('DEM', 'SUP' )  AND  t1.four_season_year BETWEEN 2006 AND 2006 AND  t1.four_season_name IN ('SUMMER' )  AND  t1.area_code IN ('44', '46', '47', '48') AND t1.incl_in_pod_lsb = 'Y' GROUP BY t1.CAL_YEAR , t1.CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.CONNECTION_TYPE , t1.INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY ORDER BY t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC15_Area_Ta_Hourly sql=" + sql);
		assertEquals(sql, expectedSql);
		
	}

	public void testUC16_Mp_Tm_Hourly() {
		logger.debug("testUC16_Mp_Tm_Hourly starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_SPECIFIC.getValue());
		aQuery.setBeginDate("2006/01/01");
		aQuery.setEndDate("2006/01/01");
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_MP.getValue());
		List mps = new ArrayList();
		mps.add("351S001"); // dem
		mps.add("351S009"); // dem
		mps.add("RL1"); // sup
		mps.add("BCR2"); // sup
		mps.add("BCRK"); // sup
		aQuery.setMeasurementPoints(mps);

		aQuery.setGranularity("TM");
		aQuery.setCategory("RM");
//		aQuery.setLoadTypeSelected(true);
		
		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_HOURLY.getValue());

		String expectedSql = "SELECT t1.CAL_YEAR as CAL_YEAR , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME as CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.CONNECTION_TYPE as CONNECTION_TYPE , t1.INCL_IN_POD_LSB as INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY as CATEGORY , SUM(LOAD_MW) as LOAD_MW , SUM(LOAD_MVAR) as LOAD_MVAR , SUM(MAX_MWH) as MAX_MWH , SUM(MAX_MVAR) as MAX_MVAR , SUM(MIN_MWH) as MIN_MWH , SUM(MIN_MVAR) as MIN_MVAR FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND  t1.CAL_DAY_DATE between TO_DATE('2006/01/01', 'YYYY/MM/DD') AND TO_DATE('2006/01/01', 'YYYY/MM/DD') AND  t1.mp_id IN ('351S001', '351S009', 'RL1', 'BCR2', 'BCRK') AND t1.incl_in_pod_lsb = 'Y' GROUP BY t1.CAL_YEAR , t1.CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.CONNECTION_TYPE , t1.INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY ORDER BY t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC16_Mp_Tm_Hourly sql=" + sql);
		assertEquals(sql, expectedSql);
		
	}

	public void testUC17_MS_Ts_Ts_Yearly_Poi() {
		logger.debug("testUC17_MS_Ts_Ts_Yearly_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.getValue());
		aQuery.setTwoSeasBeginYear(new Integer(2006));
		aQuery.setTwoSeasEndYear(new Integer(2006));
		aQuery.setTwoSeasonSummerSelected(true);
		aQuery.setTwoSeasonWinterSelected(true);

		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.getValue());
		List subs = new ArrayList();
		subs.add("342S"); // 342S
		subs.add("SS-6"); // SS-6
		subs.add("SS-5"); // SS-5
		subs.add("5S"); // 5S

		aQuery.setSubstations(subs);

		aQuery.setGranularity("TS"); // area
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); 
		aQuery.setGenerationTypeSelected(true);
		
		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("TS"); // total of substations
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiGenerationSelected(true); // gen
		aQuery.setTimeInterval("YR"); // yearly

		aQuery.setPoiPeakSelected(true); // peak
		aQuery.setPoiLightSelected(true); // light

		
		String expectedSql = "SELECT t2000.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.TWO_SEASON_YEAR,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, max_sum_load_mw, 'Peak' , min_sum_load_mw, 'Light' ) as POINTOFINTEREST FROM ( SELECT t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , SUM(t1.load_mw) as SUM_LOAD_MW , MAX (SUM(t1.load_mw)) over( PARTITION BY  t1.TWO_SEASON_YEAR) as MAX_SUM_LOAD_MW , MIN (SUM(t1.load_mw)) over( PARTITION BY  t1.TWO_SEASON_YEAR) as MIN_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_BUS_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = 'SUP' AND t1.incl_in_pod_lsb = 'Y' AND  t1.two_season_year BETWEEN 2006 AND 2006 AND  t1.facility_code IN ('342S', 'SS-6', 'SS-5', '5S') GROUP BY t1.TWO_SEASON_YEAR , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( max_sum_load_mw , min_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t2000.measurement_point_type_code IN ('DEM', 'SUP' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.facility_code IN ('342S', 'SS-6', 'SS-5', '5S') AND  t2000.two_season_year BETWEEN 2006 AND 2006 GROUP BY t2000.TWO_SEASON_YEAR , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.TWO_SEASON_YEAR , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC17_MS_Ts_Ts_Yearly_Poi sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}

	public void testUC18_Ma_Ta_Ta_Monthly_Poi() {
		logger.debug("testUC18_Ma_Ta_Ta_Monthly_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.getValue());
		aQuery.setFourSeasBeginYear(new Integer(2006));
		aQuery.setFourSeasEndYear(new Integer(2008));
		aQuery.setFourSeasonSpringSelected(true);
		aQuery.setFourSeasonSummerSelected(true);
		aQuery.setFourSeasonFallSelected(true);
		aQuery.setFourSeasonWinterSelected(true);
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_AREA.getValue());
		List areas = new ArrayList();
		areas.add("44");
		areas.add("46");
		areas.add("47");
		areas.add("48");
		aQuery.setPlanningAreas(areas);

		aQuery.setGranularity("TA"); // area
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); 

		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("TA"); // total of areas
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiLoadSelected(true); // loads
		aQuery.setTimeInterval("MO"); // monthly

		aQuery.setPoiPeakSelected(true); // peak
		aQuery.setPoiLightSelected(true); // light

		
		String expectedSql = "SELECT t2000.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t2000.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t100.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t100.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t10.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t10.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.FOUR_SEASON_YEAR,  t10.FOUR_SEASON_NAME,  t10.CAL_MONTH_NUMBER,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, max_sum_load_mw, 'Peak' , min_sum_load_mw, 'Light' ) as POINTOFINTEREST FROM ( SELECT t1.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , SUM(t1.load_mw) as SUM_LOAD_MW , MAX (SUM(t1.load_mw)) over( PARTITION BY  t1.FOUR_SEASON_YEAR,  t1.FOUR_SEASON_NAME,  t1.CAL_MONTH_NUMBER) as MAX_SUM_LOAD_MW , MIN (SUM(t1.load_mw)) over( PARTITION BY  t1.FOUR_SEASON_YEAR,  t1.FOUR_SEASON_NAME,  t1.CAL_MONTH_NUMBER) as MIN_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = 'DEM' AND t1.incl_in_pod_lsb = 'Y' AND  t1.four_season_year BETWEEN 2006 AND 2008 AND  t1.area_code IN ('44', '46', '47', '48') GROUP BY t1.FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME , t1.CAL_MONTH_NUMBER , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( max_sum_load_mw , min_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_LTLF_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t2000.measurement_point_type_code IN ('DEM' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.area_code IN ('44', '46', '47', '48') AND  t2000.four_season_year BETWEEN 2006 AND 2008 GROUP BY t2000.FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME , t2000.CAL_MONTH_NUMBER , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME , t2000.CAL_MONTH_NUMBER , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC18_Ma_Ta_Ta_Monthly_Poi sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}

	public void testUC19_Ma_Sb_Su_Seasonal_Poi() {
		logger.debug("testUC19_Ma_Sb_Su_Seasonal_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.getValue());
		aQuery.setFourSeasBeginYear(new Integer(2006));
		aQuery.setFourSeasEndYear(new Integer(2008));
		aQuery.setFourSeasonSummerSelected(true);
		aQuery.setFourSeasonFallSelected(true);
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_AREA.getValue());
		List areas = new ArrayList();
		areas.add("44");
		areas.add("46");
		areas.add("47");
		areas.add("48");
		aQuery.setPlanningAreas(areas);

		aQuery.setGranularity("SB"); // substation
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); 
		aQuery.setGenerationTypeSelected(true);
		
		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("SU"); // total of supply
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiGenerationSelected(true); // gen
		aQuery.setTimeInterval("SE"); //seasonal

		aQuery.setPoiPeakSelected(true); // peak
		aQuery.setPoiMedianSelected(true); // med
		aQuery.setPoiLightSelected(true); // light

		String expectedSql = "SELECT t2000.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.FACILITY_CODE as FACILITY_CODE , t2000.SUBSTATION_NAME as SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t100.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t10.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.FOUR_SEASON_YEAR,  t10.FOUR_SEASON_NAME,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, max_sum_load_mw, 'Peak' , median_sum_load_mw, 'Median' , min_sum_load_mw, 'Light' ) as POINTOFINTEREST FROM ( SELECT t1.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , SUM(t1.load_mw) as SUM_LOAD_MW , MAX (SUM(t1.load_mw)) over( PARTITION BY  t1.FOUR_SEASON_YEAR,  t1.FOUR_SEASON_NAME) as MAX_SUM_LOAD_MW , PERCENTILE_DISC (0.5) WITHIN GROUP ( ORDER BY SUM(t1.LOAD_MW) DESC) OVER ( PARTITION BY  t1.FOUR_SEASON_YEAR,  t1.FOUR_SEASON_NAME) as MEDIAN_SUM_LOAD_MW , MIN (SUM(t1.load_mw)) over( PARTITION BY  t1.FOUR_SEASON_YEAR,  t1.FOUR_SEASON_NAME) as MIN_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = 'SUP' AND t1.incl_in_pod_lsb = 'Y' AND  t1.four_season_year BETWEEN 2006 AND 2008 AND  t1.four_season_name IN ('SUMMER', 'FALL' )  GROUP BY t1.FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( max_sum_load_mw , median_sum_load_mw , min_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t2000.measurement_point_type_code IN ('DEM', 'SUP' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.area_code IN ('44', '46', '47', '48') AND  t2000.four_season_year BETWEEN 2006 AND 2008 AND  t2000.four_season_name IN ('SUMMER', 'FALL' )  GROUP BY t2000.FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC19_Ma_Sb_Su_Seasonal_Poi sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}

	public void testUC20_Ma_Ta_De_Seasonal_Poi() {
		logger.debug("testUC20_Ma_Ta_De_Seasonal_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.getValue());
		aQuery.setFourSeasBeginYear(new Integer(2006));
		aQuery.setFourSeasEndYear(new Integer(2008));
		aQuery.setFourSeasonSummerSelected(true);
		aQuery.setFourSeasonFallSelected(true);
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_AREA.getValue());
		List areas = new ArrayList();
		areas.add("44");
		areas.add("46");
		areas.add("47");
		areas.add("48");
		aQuery.setPlanningAreas(areas);

		aQuery.setGranularity("TA"); // area
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); 
		aQuery.setGenerationTypeSelected(true);
		
		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("DE"); // total of demand
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiLoadSelected(true); // load
		aQuery.setTimeInterval("SE"); //seasonal

		aQuery.setPoiPeakSelected(true); // peak
		aQuery.setPoiMedianSelected(true); // med
		aQuery.setPoiLightSelected(true); // light

		String expectedSql = "SELECT t2000.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t100.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t10.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.FOUR_SEASON_YEAR,  t10.FOUR_SEASON_NAME,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, max_sum_load_mw, 'Peak' , median_sum_load_mw, 'Median' , min_sum_load_mw, 'Light' ) as POINTOFINTEREST FROM ( SELECT t1.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , SUM(t1.load_mw) as SUM_LOAD_MW , MAX (SUM(t1.load_mw)) over( PARTITION BY  t1.FOUR_SEASON_YEAR,  t1.FOUR_SEASON_NAME) as MAX_SUM_LOAD_MW , PERCENTILE_DISC (0.5) WITHIN GROUP ( ORDER BY SUM(t1.LOAD_MW) DESC) OVER ( PARTITION BY  t1.FOUR_SEASON_YEAR,  t1.FOUR_SEASON_NAME) as MEDIAN_SUM_LOAD_MW , MIN (SUM(t1.load_mw)) over( PARTITION BY  t1.FOUR_SEASON_YEAR,  t1.FOUR_SEASON_NAME) as MIN_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = 'DEM' AND t1.incl_in_pod_lsb = 'Y' AND  t1.four_season_year BETWEEN 2006 AND 2008 AND  t1.four_season_name IN ('SUMMER', 'FALL' )  GROUP BY t1.FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( max_sum_load_mw , median_sum_load_mw , min_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_LTLF_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t2000.measurement_point_type_code IN ('DEM', 'SUP' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.area_code IN ('44', '46', '47', '48') AND  t2000.four_season_year BETWEEN 2006 AND 2008 AND  t2000.four_season_name IN ('SUMMER', 'FALL' )  GROUP BY t2000.FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC20_Ma_Ta_De_Seasonal_Poi sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}

	public void testUC21_Mp_Mp_Mp_Seasonal_Poi() {
		logger.debug("testUC21_Mp_Mp_Mp_Seasonal_Poi starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.getValue());
		aQuery.setFourSeasBeginYear(new Integer(2006));
		aQuery.setFourSeasEndYear(new Integer(2008));
		aQuery.setFourSeasonSummerSelected(true);
		aQuery.setFourSeasonFallSelected(true);
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_MP.getValue());
		List mps = new ArrayList();
		mps.add("35S001");
		mps.add("351S009"); // dem
		mps.add("RL1"); // sup
		mps.add("BCR2"); // sup
		mps.add("BCRK"); // sup
		aQuery.setMeasurementPoints(mps);

		aQuery.setGranularity("MP"); // area
		aQuery.setCategory("RM"); // rev metered
		aQuery.setLoadTypeSelected(true); 
		aQuery.setGenerationTypeSelected(true);
		
		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());
		aQuery.setPoiCoincidence("MP"); // total of supply
		aQuery.setPoiCategory("RM"); // rev metered
		aQuery.setPoiLoadSelected(true); // load
		aQuery.setTimeInterval("SE"); //seasonal

		aQuery.setPoiPeakSelected(true); // peak
		aQuery.setPoiMedianSelected(true); // med
		aQuery.setPoiLightSelected(true); // light

		String expectedSql = "SELECT t2000.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.MP_ID as MP_ID , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t100.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.AREA_CODE as AREA_CODE , t100.MP_ID as MP_ID , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t10.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.AREA_CODE as AREA_CODE , t10.MP_ID as MP_ID , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.FOUR_SEASON_YEAR,  t10.FOUR_SEASON_NAME,  t10.AREA_CODE,  t10.MP_ID,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, max_sum_load_mw, 'Peak' , median_sum_load_mw, 'Median' , min_sum_load_mw, 'Light' ) as POINTOFINTEREST FROM ( SELECT t1.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.AREA_CODE as AREA_CODE , t1.MP_ID as MP_ID , SUM(t1.load_mw) as SUM_LOAD_MW , MAX (SUM(t1.load_mw)) over( PARTITION BY  t1.FOUR_SEASON_YEAR,  t1.FOUR_SEASON_NAME,  t1.AREA_CODE,  t1.MP_ID) as MAX_SUM_LOAD_MW , PERCENTILE_DISC (0.5) WITHIN GROUP ( ORDER BY SUM(t1.LOAD_MW) DESC) OVER ( PARTITION BY  t1.FOUR_SEASON_YEAR,  t1.FOUR_SEASON_NAME,  t1.AREA_CODE,  t1.MP_ID) as MEDIAN_SUM_LOAD_MW , MIN (SUM(t1.load_mw)) over( PARTITION BY  t1.FOUR_SEASON_YEAR,  t1.FOUR_SEASON_NAME,  t1.AREA_CODE,  t1.MP_ID) as MIN_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND t1.incl_in_pod_lsb = 'Y' AND  t1.four_season_year BETWEEN 2006 AND 2008 AND  t1.four_season_name IN ('SUMMER', 'FALL' )  AND  t1.mp_id IN ('35S001', '351S009', 'RL1', 'BCR2', 'BCRK') GROUP BY t1.FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE , t1.MP_ID ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( max_sum_load_mw , median_sum_load_mw , min_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_LTLF_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t1000.MP_ID = t2000.MP_ID AND t2000.measurement_point_type_code IN ('DEM', 'SUP' )  AND t2000.incl_in_pod_lsb = 'Y' AND  t2000.mp_id IN ('35S001', '351S009', 'RL1', 'BCR2', 'BCRK') AND  t2000.four_season_year BETWEEN 2006 AND 2008 AND  t2000.four_season_name IN ('SUMMER', 'FALL' )  GROUP BY t2000.FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.MP_ID , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.FOUR_SEASON_YEAR , t2000.FOUR_SEASON_NAME , t2000.AREA_CODE , t2000.MP_ID , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC21_Mp_Mp_Mp_Seasonal_Poi sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}
	
	public void testUC22_Mp_Tm_Hourly() {
		logger.debug("testUC22_Mp_Tm_Hourly starting");
		Query aQuery = new Query();
		
		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_SPECIFIC.getValue());
		aQuery.setBeginDate("2006/01/01");
		aQuery.setEndDate("2006/01/01");
		
		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_MP.getValue());
		List mps = new ArrayList();
		mps.add("35S001");
		mps.add("351S009"); // dem
		aQuery.setMeasurementPoints(mps);

		aQuery.setGranularity("TM");
		aQuery.setCategory("RM");
		aQuery.setLoadTypeSelected(true);
		
		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_HOURLY.getValue());

		String expectedSql = "SELECT t1.CAL_YEAR as CAL_YEAR , t1.CAL_MONTH_NUMBER as CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME as CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR as FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME as FOUR_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.CONNECTION_TYPE as CONNECTION_TYPE , t1.INCL_IN_POD_LSB as INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY as CATEGORY , SUM(LOAD_MW) as LOAD_MW , SUM(LOAD_MVAR) as LOAD_MVAR , SUM(MAX_MWH) as MAX_MWH , SUM(MAX_MVAR) as MAX_MVAR , SUM(MIN_MWH) as MIN_MWH , SUM(MIN_MVAR) as MIN_MVAR FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code IN ('DEM' )  AND  t1.CAL_DAY_DATE between TO_DATE('2006/01/01', 'YYYY/MM/DD') AND TO_DATE('2006/01/01', 'YYYY/MM/DD') AND  t1.mp_id IN ('35S001', '351S009') AND t1.incl_in_pod_lsb = 'Y' GROUP BY t1.CAL_YEAR , t1.CAL_MONTH_NUMBER , t1.CAL_MONTH_SHORT_NAME , t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.FOUR_SEASON_YEAR , t1.FOUR_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.CONNECTION_TYPE , t1.INCL_IN_POD_LSB , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY ORDER BY t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.MEASUREMENT_POINT_TYPE_CODE , t1.CATEGORY";
		
    	SqlQueryDirector director = new SqlQueryDirector(aQuery);
    	director.constructQuery();
    	String sql = director.getResults();
		
		System.out.println("testUC22_Mp_Tm_Hourly sql=" + sql);

		assertEquals(sql, expectedSql);
		
	}
	
}
