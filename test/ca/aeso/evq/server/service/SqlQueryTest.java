package ca.aeso.evq.server.service;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.aeso.evq.server.service.SqlQuery;


public class SqlQueryTest extends TestCase {

	protected Log logger = LogFactory.getLog(SqlQueryTest.class);

	public SqlQueryTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		logger.debug("setUp called");
	}

	public void tearDown() throws Exception {
		logger.debug("tearDown called");
	}

	
	public void testBuildQl() {
		logger.debug("testBuildQl starting");
		
		SqlQuery qt1 = buildQ1();
		String sql = qt1.toString();
		logger.debug("sql returned=" + sql);
		System.out.println("ql1 sql=" + sql);
	}

	public void testBuildQll0() {
		logger.debug("testBuildQll0 starting");
		
		SqlQuery qt1 = buildQ1();
		
		SqlQuery qt10 = buildQ10(qt1);
		
		String sql = qt10.toString();
		logger.debug("sql returned=" + sql);
		System.out.println("ql10 sql=" + sql);
		
	}

	
	public void testBuildQl100() {
		logger.debug("testBuildQl100 starting");
		
		SqlQuery q1 = buildQ1();
		
		SqlQuery q10 = buildQ10(q1);

		SqlQuery q100 = buildQ100(q10);
		
		String sql = q100.toString();
		logger.debug("sql returned=" + sql);
		System.out.println("q100 sql=" + sql);
		
	}

	public void testBuildQl1000() {
		logger.debug("testBuildQl1000 starting");
		
		SqlQuery q1 = buildQ1();
		
		SqlQuery q10 = buildQ10(q1);

		SqlQuery q100 = buildQ100(q10);
		
		SqlQuery q1000 = buildQ1000(q100);

		String sql = q1000.toString();
		logger.debug("sql returned=" + sql);
		System.out.println("q1000 sql=" + sql);
		
	}
	
	private SqlQuery buildQ1() {
		
		SqlQuery q1 = new SqlQuery("t1");

		q1.setTableName("IHFC_CORP_HIST_BUS_V");

		q1.addColumnSelect("two_season_year", true, false);
		q1.addColumnSelect("two_season_name", true, false);
		q1.addColumnSelect("cal_day_date", true, false);
		q1.addColumnSelect("cal_hour_ending", true, false);
		q1.addOtherSelect("'Area 47'", "region_name");
		q1.addColumnSelect("area_code", true, false);
		q1.addColumnSelect("facility_code", true, false);
		q1.addOtherSelect("SUM(t1.load_mw)", "sum_load_mw");
		q1.addOtherSelect("MAX (SUM(t1.load_mw)) over( PARTITION BY t1.two_season_year, t1.two_season_name, t1.area_code, t1.facility_code)", "max_sum_load_mw");
		q1.addOtherSelect("PERCENTILE_DISC (0.5) WITHIN GROUP ( ORDER BY SUM(t1.LOAD_MW) DESC) OVER ( PARTITION BY t1.two_season_year, t1.two_season_name, t1.area_code, t1.facility_code)", "median_sum_load_mw");
		q1.addOtherSelect("MIN (SUM(t1.load_mw)) over( PARTITION BY t1.two_season_year, t1.two_season_name, t1.area_code, t1.facility_code)", "min_sum_load_mw");


		q1.addWhere("t1.measurement_point_type_code = 'DEM'"); 		
		q1.addWhere("t1.incl_in_pod_lsb = 'Y'"); 		
		q1.addWhere("t1.two_season_year BETWEEN 2002 AND 2007"); 		
		q1.addWhere("t1.two_season_name IN ('SUMMER')"); 		
		q1.addWhere("t1.area_code IN ('47')"); 		

		return q1;
	}
	
	private SqlQuery buildQ10(SqlQuery qt1) {
		
		SqlQuery q10 = new SqlQuery("t10");
		
		q10.setInnerQuery(qt1);

		q10.addColumnSelect("two_season_year", false, false);
		q10.addColumnSelect("two_season_name", false, false);
		q10.addColumnSelect("cal_day_date", false, false);
		q10.addColumnSelect("cal_hour_ending", false, false);
		q10.addOtherSelect("MIN (t10.CAL_DAY_DATE || '-' || t10.CAL_HOUR_ENDING) over( PARTITION BY t10.two_season_year, t10.two_season_name, t10.area_code, t10.facility_code, t10.sum_load_mw)", "POI_date_HE");
		q10.addColumnSelect("region_name", false, false);
		q10.addColumnSelect("area_code", false, false);
		q10.addColumnSelect("facility_code", false, false);
		q10.addColumnSelect("sum_load_mw", false, false);
		q10.addOtherSelect("DECODE (sum_load_mw, max_sum_load_mw, 'Peak' , median_sum_load_mw, 'Median' , min_sum_load_mw   , 'Light')", "POINTOFINTEREST");

		q10.addWhere("t10.sum_load_mw IN (max_sum_load_mw, median_sum_load_mw, min_sum_load_mw)");
		return q10;
	}

	private SqlQuery buildQ100(SqlQuery qt1) {
		
		SqlQuery q100 = new SqlQuery("t100");
		
		q100.setInnerQuery(qt1);

		q100.addColumnSelect("cal_day_date", false, false);
		q100.addColumnSelect("cal_hour_ending", false, false);
		q100.addColumnSelect("region_name", false, false);
		q100.addColumnSelect("area_code", false, false);
		q100.addColumnSelect("facility_code", false, false);
		q100.addColumnSelect("POINTOFINTEREST", false, false);
		
		q100.addWhere("t100.CAL_DAY_DATE || '-' || t100.CAL_HOUR_ENDING = t100.POI_date_HE ");
		return q100;
	}

	private SqlQuery buildQ1000(SqlQuery qt1) {
		
		SqlQuery q1000 = new SqlQuery("t1000");
		q1000.setInnerQuery(qt1);
		
		q1000.setJoinTableName("IHFC_CORP_HIST_BUS_V");
		q1000.setJoinTableIdentifier("t2000");

		q1000.addJoinColumnSelect("two_season_year", true, true);
		q1000.addJoinColumnSelect("two_season_name", true, true);
	    q1000.addJoinColumnSelect("cal_day_date", true, false);
		q1000.addJoinColumnSelect("cal_hour_ending", true, false);
		q1000.addColumnSelect("region_name", true, true);
		q1000.addJoinColumnSelect("area_code", true, true);
		q1000.addJoinColumnSelect("facility_code", true, true);
		q1000.addColumnSelect("POINTOFINTEREST", true, true);
		q1000.addJoinColumnSelect("bus_id", true, true);
		q1000.addOtherSelect("SUM(t2000.load_mw)", "bus_MW");
		q1000.addOtherSelect("SUM(t2000.load_mvar)", "bus_MVar");

		
		q1000.addWhere("t1000.cal_day_date = t2000.cal_day_date");
		q1000.addWhere("t1000.cal_hour_ending = t2000.cal_hour_ending");
		q1000.addWhere("t1000.facility_code = t2000.facility_code");
		q1000.addWhere("t1000.area_code IN ('47')");

		
		return q1000;
	}
	
}
