//package ca.aeso.evq.server.service;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.util.ArrayList;
//import java.util.*;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import ca.aeso.evq.common.DateQueryType;
//import ca.aeso.evq.common.GeogQueryType;
//import ca.aeso.evq.common.PoiQueryType;
//import ca.aeso.evq.rpc.Query;
//import ca.aeso.evq.server.junit.DBConfigurationHelper;
//import ca.aeso.evq.server.junit.DBTestCase;
//import ca.aeso.evq.server.service.QueryServiceImpl;
//import ca.aeso.evq.server.dao.*;
//
///** 
// * Tests JdbcQueryDAO
// * 
// * @author mbodor
// */
//public class QueryServiceTest extends DBTestCase {
//
//	protected static Log logger = LogFactory.getLog(QueryServiceTest.class);
//	protected DBConfigurationHelper dbConfigHelper = new DBConfigurationHelper();
//	protected static ApplicationContext springContext = new ClassPathXmlApplicationContext("applicationContext.xml");
//	JdbcQueryDaoImpl dao;
//
//
//	public QueryServiceTest(String name) {
//		super(name);
//		dao = (JdbcQueryDaoImpl) springContext.getBean("dao");
//	}
//
//	public void setUp() throws Exception {
//		logger.debug("JdbcQueryDAOTest: setUp called");
//		super.setUp();
////		clearTable("ELEC_VOLUMES_QUERY_PARM_T");
////		clearTable("ELEC_VOLUMES_QUERY_T");
//	}
//
//	public void tearDown() throws Exception {
//		logger.debug("JdbcQueryDAOTest: tearDown called");
//		super.tearDown();
//		dbConfigHelper.tearDown();
//	}
//
//	public void testGetRegions() throws Exception {
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		List regions = service.getRegions();
//		assertEquals(6, regions.size());
//	}
//
//	public void testGetPlanningAreas() throws Exception {
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		List areas = service.getPlanningAreas();
//		assertEquals(43, areas.size());
//	}
//
//	public void testGetSubstations() throws Exception {
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		List subs = service.getSubstations();
//		assertEquals(683, subs.size());
//	}
//	
//	public void testGetSubMap() throws Exception {
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		Map subMap = service.getSubMap();
//		assertEquals(683, subMap.size());
//	}
//
//	public void testGetMeasurementPoints() throws Exception {
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		List mps = service.getMeasurementPoints();
//		assertEquals(994, mps.size());
//	}
//
//	public void testGetCodes() throws Exception {
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		List codes = service.getCodes();
//		assertEquals(29, codes.size());
//	}
//
//	public void testGetCodeDesc() throws Exception {
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		String desc = service.getCodeDesc("TO");
//		assertEquals("Total", desc);
//	}
//
//	public void testGetCodeMap() throws Exception {
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		HashMap codeMap = service.getCodeMap();
//		assertEquals(29, codeMap.size());
//	}
//
//	public void testGetGranularities() throws Exception {
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		List areaGranularities = service.getGranularities("PA");
//		assertEquals(5, areaGranularities.size());
//	}
//
//	public void testGetCategories() throws Exception {
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		List categories = service.getCategories();
//		assertEquals(categories.size(), 1);
//		assertEquals("RM", ((String[])categories.get(0))[1]);
//		
//	}
//
//	public void testGetCoincidences() throws Exception {
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		List areaSubCoincidences = service.getCoincidences("PA", "SB");
//		assertEquals(4, areaSubCoincidences.size());
//	}
//	
//	public void testGetPoiCategories() throws Exception {
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		List categories = service.getPoiCategories();
//		assertEquals(categories.size(), 1);
//		assertEquals("RM", ((String[])categories.get(0))[1]);
//		
//	}
//
//	public void testGetTimeIntervals() throws Exception {
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		List intervals = service.getTimeIntervals();
//		assertEquals(intervals.size(), 6);
//		assertEquals("TO", ((String[])intervals.get(0))[1]);
//		
//	}
//	
//	public void testSubmitQuery() throws Exception {
//
//		Query aQuery = new Query();
//		aQuery.setUserId("dev");
//		aQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.getValue());
//		aQuery.setFourSeasBeginYear(new Integer(2006));
//		aQuery.setFourSeasEndYear(new Integer(2006));
//		aQuery.setFourSeasonSummerSelected(true);
//		
//		aQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_AREA.getValue());
//		List areas = new ArrayList();
//		areas.add("44");
//		areas.add("48");
//		aQuery.setPlanningAreas(areas);
//
//		aQuery.setGranularity("PA");
//		aQuery.setCategory("RM");
//		aQuery.setLoadTypeSelected(true);
//		aQuery.setGenerationTypeSelected(true);
//		
//		aQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_HOURLY.getValue());
//		
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//
//		long queryId = service.submitQuery(aQuery);
//
//		assertRecordCount("ELEC_VOLUMES_QUERY_T", "QUERY_ID = " + queryId, 1);
//		assertRecordCount("ELEC_VOLUMES_QUERY_RESULT_T", "QUERY_ID = " + queryId, 1);
//		assertRecordCount("ELEC_VOLUMES_QUERY_PARM_T", "QUERY_ID = " + queryId, 2);
//	}
//
//	public void testGetQuery() throws Exception {
//
//		Query newQuery = new Query();
//		newQuery.setUserId("DEV");
//		newQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.getValue());
//		newQuery.setFourSeasBeginYear(new Integer(1998));
//		newQuery.setFourSeasEndYear(new Integer(1999));
//		newQuery.setFourSeasonSpringSelected(true);
//		newQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_MP.getValue());
//		List mps = new ArrayList();
//		String testMp = new String("0000002002");
//		mps.add(testMp);
//		newQuery.setMeasurementPoints(mps);
//		newQuery.setGranularity("MP");
//		newQuery.setPoiQueryType("HO");
//		
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		
//		long queryId = service.submitQuery(newQuery);
//		Query aQuery = dao.getQuery(queryId);
//
//		assertEquals(aQuery.getQueryId(), queryId);
//	}		
//
//	public void testGetQueryHistory() throws Exception {
//
//		Query newQuery = new Query();
//		newQuery.setUserId("DEV");
//		newQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.getValue());
//		newQuery.setFourSeasBeginYear(new Integer(1998));
//		newQuery.setFourSeasEndYear(new Integer(1999));
//		newQuery.setFourSeasonSpringSelected(true);
//		newQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_MP.getValue());
//		List mps = new ArrayList();
//		String testMp = new String("0000002001");
//		mps.add(testMp);
//		newQuery.setMeasurementPoints(mps);
//		newQuery.setGranularity("MP");
//		newQuery.setPoiQueryType("HO");
//
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		
//		long queryId = service.submitQuery(newQuery);
//		assertTrue(queryId>0);
//		
//		Query newQuery2 = new Query();
//		newQuery2.setUserId("DEV");
//		newQuery2.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.getValue());
//		newQuery2.setFourSeasBeginYear(new Integer(1998));
//		newQuery2.setFourSeasEndYear(new Integer(1999));
//		newQuery2.setFourSeasonSpringSelected(true);
//		newQuery2.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_MP.getValue());
//		List mps2 = new ArrayList();
//		String testMp2 = new String("0000002002");
//		mps2.add(testMp2);
//		newQuery2.setMeasurementPoints(mps2);
//		newQuery2.setGranularity("MP");
//		newQuery2.setPoiQueryType("HO");
//
//		long queryId2 = service.submitQuery(newQuery2);
//		assertTrue(queryId2>0);
//
//		List queries = dao.getQueryHistory("DEV");
//		assertTrue(queries.size()>=2);
//	}		
//
//	public void testSaveQueryAndGetResults() throws Exception {
//
//		// submit a query
//		logger.debug("jdbc version=" + DBConfigurationHelper.getJdbcVersion(con));
//		Query newQuery = new Query();
//		newQuery.setUserId("DEV");
//		newQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_SPECIFIC.getValue());
//		newQuery.setBeginDate("2000/01/01");
//		newQuery.setEndDate("2000/01/02");
//		newQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_MP.getValue());
//		List mps = new ArrayList();
//		String testMp = new String("0000002001");
//		mps.add(testMp);
//		newQuery.setMeasurementPoints(mps);
//		newQuery.setGranularity("MP");
//		newQuery.setPoiQueryType("HO");
//
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		
//		long queryId = service.submitQuery(newQuery);
//		
//		// get the query result back from the database and put into a local file
//		String fileName = "C:\\dev\\evq\\testresult" + queryId + ".xls";
//		FileOutputStream contentStream = new FileOutputStream(fileName);
//		
//		dao.getQueryResults(queryId, contentStream);
//
//		contentStream.close();
//		
//		File resultsFile = new File(fileName);
//		long length = resultsFile.length();
//		logger.debug("file length=" + length);
//		assertEquals(length, 15872);
//		
//	}
//
//	public void testReapBlobs() throws Exception {
//
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		
//		int reapedBlobs = service.reapBlobs();
//		
//		assertEquals(0, reapedBlobs);
//		
//	}
//
//	public void testResetStatuses() throws Exception {
//
//		QueryServiceImpl service = new QueryServiceImpl(); 
//		service.setDao(dao);
//		
//		int resetStatuses = service.resetStatuses();
//		
//		assertEquals(0, resetStatuses);
//		
//	}
//	
//}
