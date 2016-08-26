package ca.aeso.evq.server.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import oracle.jdbc.OracleResultSet;
import oracle.sql.BLOB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;

import ca.aeso.evq.common.GeogQueryType;
import ca.aeso.evq.common.QueryStatusType;
import ca.aeso.evq.rpc.Query;
import ca.aeso.evq.server.service.ElectricalVolume;
import ca.aeso.evq.server.service.ExcelTransformer;
import ca.aeso.evq.server.util.Constants;

/**
 * JdbcQueryDaoImpl
 * 
 * This DAO impl class handles all JDBC access to the database
 * 
 * @see QueryDao.java for public method javadoc
 * @author mbodor
 */
public class JdbcQueryDaoImpl extends JdbcDaoSupport implements QueryDao {

	protected static Log logger = LogFactory.getLog(JdbcQueryDaoImpl.class);
	private static SimpleDateFormat formatHms = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static SimpleDateFormat formatDay = new SimpleDateFormat("yyyy/MM/dd");
	
	private static final String QUERY_SELECT = 
		"SELECT q.* " +
		",qr.QUERY_STATUS" +
		",qr.QUERY_ERRORS" +
		",qr.QUERY_RUNTIME_MS " +
		"FROM ELEC_VOLUMES_QUERY_T q" +
		", ELEC_VOLUMES_QUERY_RESULT_T qr " +
		"WHERE q.QUERY_ID = ? " +
		"AND qr.QUERY_ID = q.QUERY_ID";

	private static final String USER_QUERY_SELECT = 
		"SELECT q.* " +
		",qr.QUERY_STATUS" +
		",qr.QUERY_ERRORS " +
		",qr.QUERY_RUNTIME_MS " +
		"FROM ELEC_VOLUMES_QUERY_T q" +
		", ELEC_VOLUMES_QUERY_RESULT_T qr " +
		"WHERE q.USER_ID = ? " +
		"AND qr.QUERY_ID = q.QUERY_ID " +
		"ORDER BY q.AUDIT_DATETIME DESC";

	private static final String QUERY_PARM_SELECT = 
		"SELECT p.PARAMETER_ID " +
		", p.PARAMETER_TYPE " +
		", p.PARAMETER_VALUE " + 
		"FROM ELEC_VOLUMES_QUERY_PARM_T p " +
		"WHERE p.QUERY_ID = ? " +
		"ORDER BY p.PARAMETER_ID";

	private static final String REGION_SELECT = 
		"SELECT t2.region_name || ' (' || t2.region_desc || ')' as \"desc\" " +
	    ", t2.region_name as \"value\" " +
	    ", t2.region_order as \"attrib\" " +
	    "FROM IHFC_REGIONS_V t2 " +
	    "WHERE t2.region_ver = ( " + 
	    	"SELECT MAX(t1.region_ver) " +
	    	"FROM IHFC_REGIONS_V t1 ) " +
	    "ORDER BY 3";		
	
	private static final String PLANNING_AREA_SELECT = 
		"select DISTINCT t1.AREA_NAME || ' (' || t1.AREA_CODE || ')' as \"desc\" " + 	
		", t1.AREA_CODE as \"value\" " +
		", 'attrib' as \"attrib\" " +
		"FROM IHFC_SYS_AREAS_V t1 " + 
		", IHFC_MEASURE_POINT_V t2 " +
		"WHERE t2.ZONE_CODE IS NOT NULL AND t2.ZONE_CODE != 'UND' " + 
		"AND t1.AREA_CODE = TO_NUMBER(t2.ZONE_CODE) " + 
		"ORDER BY t1.AREA_CODE ";

	private static final String SUBSTATION_SELECT =
		"SELECT DISTINCT f.facility_code || ' - ' || f.substation_name || ' (' || b.area_code || ')' AS \"desc\" " +
		", f.facility_code AS \"value\" " + 
		", f.facility_code AS \"attrib\" " + 
		"  FROM IHFC_FACILITY_V f " +
		"     , IHFC_BUS_V b " +
		" WHERE f.FACILITY_ID = b.FACILITY_ID " +
		"   AND f.LINE_OR_STATION = 'S' " +
		"   AND b.AREA_CODE NOT IN ('50','51') "+
		" ORDER by 1 ";	
		
	private static final String MEASUREMENT_POINT_SELECT = 
		"SELECT mp_id || ' (' || area_code || ') ' || tas_description || ' - ' || category AS \"desc\" " +
		", mp_id AS \"value\" " +
		", category as \"attrib\" " +
		"FROM IHFC_MEASURE_POINT_V " +
		"WHERE connection_type IS NOT NULL " +
		"ORDER BY AREA_CODE, MP_ID";

	private static final String CODES_SELECT = 
		"SELECT code_desc as \"desc\" " + 
		", code_value as \"value\" " +
		", order_info as \"attrib\" " +
		"FROM ELEC_VOLUMES_CODES_T ";

	private static final String CATEGORY_SELECT = 
		"SELECT code_desc as \"desc\" " + 
		", code_value as \"value\" " +
		", order_info as \"attrib\" " +
		"FROM ELEC_VOLUMES_CODES_T " +
		"WHERE CODE_TYPE='CAT'";

	private static final String TIME_INTERVAL_SELECT = 
		"SELECT code_desc as \"desc\" " + 
		", code_value as \"value\" " +
		", order_info as \"attrib\" " +
		"FROM ELEC_VOLUMES_CODES_T " +
		"WHERE CODE_TYPE='INTV'";
	
	private static final String GRANULARITY_SELECT = 
		"SELECT code_desc as \"desc\" " + 
		", code_value as \"value\" " +
		", order_info as \"attrib\" " +
		"FROM ELEC_VOLUMES_CODES_T " +
		"WHERE code_value IN " +
		"  (SELECT DISTINCT GRAN_VALUE FROM ELEC_VOLUMES_COINCIDENCE_T " +
		"  WHERE GEOG_VALUE = ?) " +
		"ORDER BY 3 ";

	private static final String COINCIDENCE_SELECT = 
		"SELECT code_desc as \"desc\" " + 
		", code_value as \"value\" " +
		", order_info as \"attrib\" " +
		"FROM ELEC_VOLUMES_CODES_T " +
		"WHERE code_value IN " +
		"  (SELECT DISTINCT COINC_VALUE FROM ELEC_VOLUMES_COINCIDENCE_T " +
		"  WHERE GEOG_VALUE = ? " +
		"  AND GRAN_VALUE = ?) " + 
		"ORDER BY 3 ";
	
	private static final String NEXT_QUERY_SEQ = 
		"SELECT ELEC_VOLUMES_QUERY_SEQ.NEXTVAL FROM DUAL";

	private static final String QUERY_INSERT = 
		"INSERT INTO ELEC_VOLUMES_QUERY_T values (?, ?, ?, ?, TO_DATE(?, 'YYYY/MM/DD'), TO_DATE(?, 'YYYY/MM/DD'), " +
		"?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
		"TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS') )";

	private static final String QUERY_PARM_INSERT = 
		"INSERT INTO ELEC_VOLUMES_QUERY_PARM_T values (?, ?, ?, ?)";

	private static final String QUERY_RESULT_INSERT = 
		"INSERT INTO ELEC_VOLUMES_QUERY_RESULT_T values (?, ?, EMPTY_BLOB(), ?, ?)";
			
	private static final String QUERY_RESULT_SELECT_FOR_UPDATE = "SELECT QUERY_RESULT " +
		"FROM ELEC_VOLUMES_QUERY_RESULT_T " +
		"WHERE QUERY_ID = ? FOR UPDATE";

	private static final String QUERY_RESULT_UPDATE = 
		"UPDATE ELEC_VOLUMES_QUERY_RESULT_T " +
		"SET QUERY_STATUS = ?, QUERY_ERRORS = ?, QUERY_RUNTIME_MS = ? " +
		"WHERE QUERY_ID = ?";
	
	private static final String QUERY_RESULT_SELECT = 
		"SELECT QUERY_STATUS, QUERY_RESULT, QUERY_ERRORS " +
		"FROM ELEC_VOLUMES_QUERY_RESULT_T " +
		"WHERE QUERY_ID = ? ";

	private static final String QUERY_RESULT_REAP = 
		"UPDATE ELEC_VOLUMES_QUERY_RESULT_T t " +
		"SET t.QUERY_RESULT = EMPTY_BLOB(), " +
		"    t.QUERY_STATUS = 'R' " +
		"WHERE t.QUERY_STATUS = 'C' " +
		"   AND t.QUERY_ID IN ( " +
		"	SELECT q.QUERY_ID " +
		"	FROM ELEC_VOLUMES_QUERY_T q " +
		"	WHERE q.AUDIT_DATETIME < " +
		"		(SELECT SYSDATE - " +
		"	   		(SELECT CODE_VALUE " +
		"			FROM ELEC_VOLUMES_CODES_T " +
		"			WHERE CODE_TYPE='REAP') " +
		"		FROM DUAL) " +
		"	) ";

	private static final String QUERY_STATUS_RESET = 
		"UPDATE ELEC_VOLUMES_QUERY_RESULT_T t " +
		"SET t.QUERY_RESULT = EMPTY_BLOB(), " +
		"    t.QUERY_STATUS = 'F'," +
		"    t.QUERY_ERRORS = 'Query exceeded maximum runtime'" +
		"WHERE t.QUERY_STATUS = 'E' " +
		"AND  t.QUERY_ID IN ( " +
		"	SELECT q.query_id " +
		"	FROM ELEC_VOLUMES_QUERY_T q " +
		"	WHERE q.AUDIT_DATETIME < " +
		"		(SELECT SYSDATE - " +
		"	   		(SELECT CODE_VALUE " +
		"			FROM ELEC_VOLUMES_CODES_T " +
		"			WHERE CODE_TYPE='RESET') " +
		"		FROM DUAL) " +
		"	) ";
	
	public JdbcQueryDaoImpl() {
	}

	public List getQueryHistory(String userId) {
	    List userQueries = jdbcTemplate.query( USER_QUERY_SELECT, new Object[]{userId}, new QueryHistoryMapper());
	    List commonQueries = jdbcTemplate.query( USER_QUERY_SELECT, new Object[]{Constants.COMMON_USER_ID}, new QueryHistoryMapper());
	    
	    userQueries.addAll(commonQueries);
	    return userQueries;
	}

	public List getQueryParms(long queryId) {
	    return jdbcTemplate.query( QUERY_PARM_SELECT, new Object[]{new Long(queryId)}, new QueryParmMapper());
	}
	
	public Query getQuery(long queryId) throws Exception {
    	logger.debug("JdbcQueryDaoImpl.getQuery() starting, query=" + queryId);
    	long start = System.currentTimeMillis();

    	// get query
	    List results = this.jdbcTemplate.query( QUERY_SELECT, new Object[]{new Long(queryId)}, new QueryHistoryMapper());
	    Query theQuery = (Query)results.get(0);

	    // get parameters and attach to query
	    List parms = getQueryParms(queryId);

	    for (Iterator iterator = parms.iterator(); iterator.hasNext();) {
			Object[] entry = (Object[]) iterator.next();
			String parmType = (String)entry[1];
			String parmValue = (String)entry[2];

	    	if (GeogQueryType.GEOG_QUERY_TYPE_REGION.equals(parmType)) {
				theQuery.addRegion(parmValue);
			} else if (GeogQueryType.GEOG_QUERY_TYPE_AREA.equals(parmType)) {
				theQuery.addPlanningArea(parmValue);
			} else if (GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.equals(parmType)) {
				theQuery.addSubstation(parmValue);
			} else if (GeogQueryType.GEOG_QUERY_TYPE_MP.equals(parmType)) {
				theQuery.addMeasurementPoint(parmValue);
			}
			
		}
	    
    	long end = System.currentTimeMillis();
		
    	logger.debug("JdbcQueryDaoImpl.getQuery() done");
    	logger.info("JdbcQueryDaoImpl.getQuery() #" + queryId + " duration=" + (end-start) + "ms");
    	
	    return theQuery;
	}

	public List getRegions() throws Exception {
		return jdbcTemplate.query(REGION_SELECT, new Object[]{}, new StringArrayMapper()); 
	}

	public List getPlanningAreas() throws Exception {
		return jdbcTemplate.query(PLANNING_AREA_SELECT, new Object[]{}, new StringArrayMapper());
	}

	public List getSubstations() throws Exception {
		return jdbcTemplate.query(SUBSTATION_SELECT, new Object[]{}, new StringArrayMapper());
	}
	
	public List getMeasurementPoints() throws Exception {
		return jdbcTemplate.query(MEASUREMENT_POINT_SELECT, new Object[]{}, new StringArrayMapper()); 
	}

	public List getCodes() throws Exception {
    	logger.debug("JdbcQueryDaoImpl.getCodes() executing");
		return jdbcTemplate.query(CODES_SELECT, new Object[]{}, new StringArrayMapper()); 
	}
	
	public List getGranularities(String geogQueryType) throws Exception {
    	return jdbcTemplate.query(GRANULARITY_SELECT, new Object[]{geogQueryType}, new StringArrayMapper()); 
	}

	public List getCategories() throws Exception {
		return jdbcTemplate.query(CATEGORY_SELECT, new Object[]{}, new StringArrayMapper()); 
	}
	
	public List getCoincidences(String geogQueryType, String granularity) throws Exception {
    	return jdbcTemplate.query(COINCIDENCE_SELECT, new Object[]{geogQueryType, granularity}, new StringArrayMapper()); 
	}

	public List getPoiCategories() throws Exception {
		return jdbcTemplate.query(CATEGORY_SELECT, new Object[]{}, new StringArrayMapper()); 
	}

	public List getTimeIntervals() throws Exception {
		return jdbcTemplate.query(TIME_INTERVAL_SELECT, new Object[]{}, new StringArrayMapper()); 
	}

	public long getNextQuerySeq() {
    	logger.debug("JdbcQueryDaoImpl.getNextQuerySeq() starting");
    	
    	List querySeqList  = jdbcTemplate.query(NEXT_QUERY_SEQ, new Object[]{}, new QuerySequenceMapper());
    	Long seq = (Long)querySeqList.get(0);
    	
    	logger.debug("JdbcQueryDaoImpl.getNextQuerySeq() done");
    	return seq.longValue();
	}

	public int insertQuery(Object[] args) {
    	logger.debug("JdbcQueryDaoImpl.insertQuery() starting");
    	long start = System.currentTimeMillis();
    	
    	int result = jdbcTemplate.update(QUERY_INSERT, args);

    	long end = System.currentTimeMillis();
		
    	logger.debug("JdbcQueryDaoImpl.insertQuery() done");
    	logger.info("JdbcQueryDaoImpl.insertQuery() duration=" + (end-start) + "ms");
    	return result;
	}
	
	public int insertQueryParm(Object[] args) {
		return jdbcTemplate.update(QUERY_PARM_INSERT, args);
	}

	public List executeQuery(String sql) {
    	logger.debug("JdbcQueryDaoImpl.executeQuery() starting");
    	
    	List results = jdbcTemplate.query(sql, new Object[]{}, new ElectricalVolumeMapper());

    	logger.debug("JdbcQueryDaoImpl.executeQuery() done");
		return results;
	}
	
	public int saveQueryResults(ExcelTransformer qr, long runTime)  {
		logger.debug("JdbcQueryDaoImpl.saveQueryResults() starting");
    	long start = System.currentTimeMillis();
		long queryId = qr.getTransformedIdentifier();
		
		int result = -1;
    	Connection con = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
     	OutputStream blobOutputStream = null;
        BLOB queryResultBlob = null;
    	
    	try {
        	con = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
        	con.setAutoCommit(true);
        	ps = con.prepareStatement(QUERY_RESULT_SELECT_FOR_UPDATE);
        	ps.setLong(1, queryId);
        	rs = ps.executeQuery();

         	boolean isQueryForUpdateValid = rs.next();
            if (!isQueryForUpdateValid) {
            	logger.error("saveQueryResults() - SELECT FOR UPDATE returned no rows to update!");
            	updateQueryStatus(queryId, runTime, QueryStatusType.STATUS_FAILED.getValue(), "SELECT FOR UPDATE returned no rows to update");
            }

            queryResultBlob = ((OracleResultSet) rs).getBLOB(1);
         	blobOutputStream = queryResultBlob.setBinaryStream(0);
            qr.streamResult(blobOutputStream);
         	blobOutputStream.close();
         	con.commit();
         	blobOutputStream = null;
         	
            // if we got this far, update query status
         	result = updateQueryStatus(queryId, runTime, QueryStatusType.STATUS_COMPLETE.getValue(), QueryStatusType.STATUS_COMPLETE.getDescription());
            
    	} catch (SQLException sqle) {
        	logger.error("saveResults() - caught SQL Exception", sqle);
    		sqle.printStackTrace();
    		
        	try {
                updateQueryStatus(queryId, runTime, QueryStatusType.STATUS_FAILED.getValue(), sqle.getMessage());
        	} catch (DataAccessException sqle2) {
        		sqle2.printStackTrace();
        	}
    		
    	} catch (IOException ioe) {
        	logger.error("saveResults() - caught IO Exception", ioe);
    		ioe.printStackTrace();

        	try {
                updateQueryStatus(queryId, runTime, QueryStatusType.STATUS_FAILED.getValue(), ioe.getMessage());
        	} catch (DataAccessException sqle2) {
        		sqle2.printStackTrace();
        	}
    	} finally {
    		queryResultBlob = null;
    		if (blobOutputStream!=null) {
        		try {
                 	blobOutputStream.close();
        		} catch (IOException ioe) {}
             	blobOutputStream=null;
    		}
    		
    		try {
                rs.close();
    		} catch (SQLException sqle) {}
    		rs = null;

    		try {
                ps.close();
    		} catch (SQLException sqle) {}
            ps = null;
            
    		try {
                con.close();
    		} catch (SQLException sqle) {}

    		
    	}

		long end = System.currentTimeMillis();
		
		logger.debug("JdbcQueryDaoImpl.saveQueryResults() done");
    	logger.info("JdbcQueryDaoImpl.saveQueryResults() #" + queryId + " duration=" + (end-start) + "ms");
    	return result;
	}

	public int updateQueryStatus(long queryId, long runTime, String status, String failDesc) throws DataAccessException {
       	logger.debug("JdbcQueryDaoImpl.updateQueryStatus() starting");
		long start = System.currentTimeMillis();

       	Object updateArgs[] = new Object[] {
    			status,
    			failDesc,
    			new Long(runTime),
    			new Long(queryId)
   			};

    	int result = jdbcTemplate.update(QUERY_RESULT_UPDATE, updateArgs);
    	
		long end = System.currentTimeMillis();
		
       	logger.debug("JdbcQueryDaoImpl.updateQueryStatus() done");
    	logger.info("JdbcQueryDaoImpl.updateQueryStatus() #" + queryId + " duration=" + (end-start) + "ms");
    	return result;
	}

	public int insertQueryStatus(long queryId, String status, String failDesc) throws DataAccessException {
       	logger.debug("JdbcQueryDaoImpl.insertQueryStatus() starting");

       	Object insertArgs[] = new Object[] {
				new Long(queryId),
				status,
				new Long(0),
				failDesc
		};
        		
		int result = jdbcTemplate.update(QUERY_RESULT_INSERT, insertArgs);

		logger.debug("JdbcQueryDaoImpl.insertQueryStatus() done");
       	return result;
	}
	
	/**
	 * getQueryResults
	 * For a specific queryId, retrieve the blob from the database and stream it back in the provided OutputStream
	 * Uses a JDBC connection directly
	 */
    public void getQueryResults(long queryId, OutputStream outStream) throws Exception {
       	logger.debug("JdbcQueryDaoImpl.getQueryResults() starting");
		long start = System.currentTimeMillis();

    	Connection con = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	InputStream is= null;
    	
    	try {
        	con = jdbcTemplate.getDataSource().getConnection();
        	ps = con.prepareStatement(QUERY_RESULT_SELECT);
        	ps.setLong(1, queryId);
        	rs = ps.executeQuery();

        	boolean valid = rs.next();

        	if (!valid)
            	logger.error("JdbcQueryDaoImpl.getQueryResults() rs.next returned false!");
    		
            is = ((OracleResultSet) rs).getBinaryStream(2);

            byte[] buffer = new byte[8192];
            int bytesRead = 0;
            
            while ((bytesRead = is.read(buffer)) != -1) {
            	outStream.write(buffer, 0, bytesRead);
            }        
            
    	} catch (SQLException sqle) {
    		throw new Exception(sqle);
    	} catch (IOException ioe) {
    		throw new Exception(ioe);
    	} finally {
    		try {
                outStream.flush();
                outStream.close();
        		is.close();
    		} catch (Exception ioe) {}

    		try {
                rs.close();
    		} catch (Exception sqle) {}
            
    		try {
                ps.close();
    		} catch (Exception sqle) {}
            
    		try {
                con.close();
    		} catch (Exception sqle) {}
    	}
    	
		long end = System.currentTimeMillis();
		
       	logger.debug("JdbcQueryDaoImpl.getQueryResults() done");
    	logger.info("JdbcQueryDaoImpl.getQueryResults() #" + queryId + " duration=" + (end-start) + "ms");
    }
   
    
	public int reapBlobs() throws DataAccessException {
       	logger.debug("JdbcQueryDaoImpl.reapBlobs() starting");

		int result = jdbcTemplate.update(QUERY_RESULT_REAP);

		logger.debug("JdbcQueryDaoImpl.reapBlobs() done");
       	return result;
	}
    
	public int resetStatuses() throws DataAccessException {
       	logger.debug("JdbcQueryDaoImpl.resetStatuses() starting");

		int result = jdbcTemplate.update(QUERY_STATUS_RESET);

		logger.debug("JdbcQueryDaoImpl.resetStatuses() done");
       	return result;
	}

	/**
     * Query History Mapper class
     */
	private static final class QueryHistoryMapper implements RowMapper {

	    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	Query aQuery = new Query();
	    	
	    	aQuery.setQueryId(getLong(rs, Constants.QUERY_ID).longValue());
	    	aQuery.setUserId(getString(rs, Constants.USER_ID));
	    	aQuery.setQueryName(getString(rs, Constants.QUERY_NAME));
	    	aQuery.setDateQueryType(getString(rs, Constants.DATE_QUERY_TYPE));
	    	
	    	Date begin = getTimestampAsDate(rs, Constants.BEGIN_DATE);
	    	if (begin!=null) {
		    	aQuery.setBeginDate(formatDay.format(begin));
	    	}
	    	
	    	Date end = getTimestampAsDate(rs, Constants.END_DATE);
	    	if (end!=null) {
		    	aQuery.setEndDate(formatDay.format(end));
	    	}
	    	aQuery.setCalBeginYear(getInt(rs, Constants.CAL_BEGIN_YEAR));
	    	aQuery.setCalBeginMonth(getInt(rs, Constants.CAL_BEGIN_MONTH));
	    	aQuery.setCalEndYear(getInt(rs, Constants.CAL_END_YEAR));
	    	aQuery.setCalEndMonth(getInt(rs, Constants.CAL_END_MONTH));
	    	aQuery.setTwoSeasBeginYear(getInt(rs, Constants.TS_BEGIN_YEAR));
	    	aQuery.setTwoSeasEndYear(getInt(rs, Constants.TS_END_YEAR));
	    	
	    	String tsSummer = getString(rs, Constants.TS_SUMMER_FLAG);
	    	if (tsSummer!=null && tsSummer.equals(Constants.YES))
	    		aQuery.setTwoSeasonSummerSelected(true);
	    	else
	    		aQuery.setTwoSeasonSummerSelected(false);
	    	
	    	String tsWinter = getString(rs, Constants.TS_WINTER_FLAG);
	    	if (tsWinter!=null && tsWinter.equals(Constants.YES))
	    		aQuery.setTwoSeasonWinterSelected(true);
	    	else
	    		aQuery.setTwoSeasonWinterSelected(false);

	    	aQuery.setFourSeasBeginYear(getInt(rs, Constants.FS_BEGIN_YEAR));
	    	aQuery.setFourSeasEndYear(getInt(rs, Constants.FS_END_YEAR));

	    	String fsSpring = getString(rs, Constants.FS_SPRING_FLAG);
	    	if (fsSpring!=null && fsSpring.equals(Constants.YES))
	    		aQuery.setFourSeasonSpringSelected(true);
	    	else
	    		aQuery.setFourSeasonSpringSelected(false);

	    	String fsSummer = getString(rs, Constants.FS_SUMMER_FLAG);
	    	if (fsSummer!=null && fsSummer.equals(Constants.YES))
	    		aQuery.setFourSeasonSummerSelected(true);
	    	else
	    		aQuery.setFourSeasonSummerSelected(false);

	    	String fsFall = getString(rs, Constants.FS_FALL_FLAG);
	    	if (fsFall!=null && fsFall.equals(Constants.YES))
	    		aQuery.setFourSeasonFallSelected(true);
	    	else
	    		aQuery.setFourSeasonFallSelected(false);
	    	
	    	String fsWinter = getString(rs, Constants.FS_WINTER_FLAG);
	    	if (fsWinter!=null && fsWinter.equals(Constants.YES))
	    		aQuery.setFourSeasonWinterSelected(true);
	    	else
	    		aQuery.setFourSeasonWinterSelected(false);
	    	
	    	aQuery.setGeogQueryType(getString(rs, Constants.GEOG_QUERY_TYPE));
	    	
	    	aQuery.setGranularity(getString(rs, Constants.GRANULARITY));
	    	aQuery.setCategory(getString(rs, Constants.CATEGORY));

	    	String loadFlag = getString(rs, Constants.LOAD_FLAG);
	    	if (loadFlag!=null && loadFlag.equals(Constants.YES))
	    		aQuery.setLoadTypeSelected(true);
	    	else
	    		aQuery.setLoadTypeSelected(false);
	    	
	    	String generationFlag = getString(rs, Constants.GENERATION_FLAG);
	    	if (generationFlag!=null && generationFlag.equals(Constants.YES))
	    		aQuery.setGenerationTypeSelected(true);
	    	else
	    		aQuery.setGenerationTypeSelected(false);
	  	  
	    	aQuery.setPoiQueryType(getString(rs, Constants.POI_QUERY_TYPE));

	    	aQuery.setPoiCoincidence(getString(rs, Constants.POI_COINCIDENCE));
	    	aQuery.setPoiCategory(getString(rs, Constants.POI_CATEGORY));
	    	
	    	String poiLoadFlag = getString(rs, Constants.POI_LOAD_FLAG);
	    	if (poiLoadFlag!=null && poiLoadFlag.equals(Constants.YES))
	    		aQuery.setPoiLoadSelected(true);
	    	else
	    		aQuery.setPoiLoadSelected(false);

	    	String poiGenerationFlag = getString(rs, Constants.POI_GENERATION_FLAG);
	    	if (poiGenerationFlag!=null && poiGenerationFlag.equals(Constants.YES))
	    		aQuery.setPoiGenerationSelected(true);
	    	else
	    		aQuery.setPoiGenerationSelected(false);
	    	

	    	aQuery.setTimeInterval(getString(rs, Constants.TIME_INTERVAL));

	    	String poiPeakFlag = getString(rs, Constants.POI_PEAK_FLAG);
	    	if (poiPeakFlag!=null && poiPeakFlag.equals(Constants.YES))
	    		aQuery.setPoiPeakSelected(true);
	    	else
	    		aQuery.setPoiPeakSelected(false);

	    	String poiMedianFlag = getString(rs, Constants.POI_MEDIAN_FLAG);
	    	if (poiMedianFlag!=null && poiMedianFlag.equals(Constants.YES))
	    		aQuery.setPoiMedianSelected(true);
	    	else
	    		aQuery.setPoiMedianSelected(false);
	    	
	    	String poiLightFlag = getString(rs, Constants.POI_LIGHT_FLAG);
	    	if (poiLightFlag!=null && poiLightFlag.equals(Constants.YES))
	    		aQuery.setPoiLightSelected(true);
	    	else
	    		aQuery.setPoiLightSelected(false);
	    	

	    	
	    	aQuery.setSql(getString(rs, Constants.QUERY_SQL_STRING));

	    	Date queryDate = getTimestampAsDate(rs, Constants.AUDIT_DATETIME);
	    	if (queryDate!=null) {
		    	aQuery.setQueryDateStr(formatHms.format(queryDate));
	    	}

	    	aQuery.setQueryStatus(getString(rs, Constants.QUERY_STATUS));
	    	aQuery.setQueryErrors(getString(rs, Constants.QUERY_ERRORS));
	    	aQuery.setQueryRuntime(getLong(rs, Constants.QUERY_RUNTIME_MS));
	    	
	    	return aQuery;
	    }
	}

	/**
	 * Query Parameter mapper class 
	 */
	private static final class QueryParmMapper implements RowMapper {
	    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	Object entry[] = new Object[]{new Object(), new Object(), new Object()};

	    	int parameterId = rs.getInt(Constants.PARAMETER_ID);
	    	String parameterType = rs.getString(Constants.PARAMETER_TYPE);
	    	String parameterValue = rs.getString(Constants.PARAMETER_VALUE);
	    	
	    	entry[0] = new Integer(parameterId);
	    	entry[1] = parameterType;
	    	entry[2] = parameterValue;
	    	
	    	return entry;
	    }
	}

	/**
	 * String Array mapper class 
	 */
	private static final class StringArrayMapper implements RowMapper {
	    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	String entry[] = new String[]{new String(), new String(), new String()};
	    	entry[0] = rs.getString(Constants.DESC);
	    	entry[1] = rs.getString(Constants.VALUE);
	    	entry[2] = rs.getString(Constants.ATTRIB);
	    	return entry;
	    }
	}
	
	/**
	 * Query sequence mapper class 
	 */
	private static final class QuerySequenceMapper implements RowMapper {
	    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	long sequence = rs.getLong(Constants.NEXTVAL);
	    	return new Long(sequence);
	    }
	}

	/**
	 * Electrical Volume mapper class
	 */
	private static final class ElectricalVolumeMapper implements RowMapper {
		
	    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	
	    	ElectricalVolume ev = new ElectricalVolume();
	    	ResultSetMetaData rsmd = rs.getMetaData();
	    	int numcols = rsmd.getColumnCount();
	    	
	    	for (int i = 0; i < numcols; i++) {
	    		String colName = rsmd.getColumnName(i+1);
	    		int colType = rsmd.getColumnType(i+1);
//    			logger.debug("ElectricalVolumeMapper.mapRow(): colname=" + colName + " coltype=" + colType);	

    			switch (colType) {

		    		case java.sql.Types.VARCHAR:
		    			ev.addValue(colName, getString(rs, colName));
		    			break;
	
		    		case java.sql.Types.CHAR:
		    			ev.addValue(colName, getString(rs, colName));
		    			break;

		    		case java.sql.Types.NUMERIC:
		    			ev.addValue(colName, getBigDecimal(rs, colName));
		    			break;
		    			
		    		case java.sql.Types.DATE:
		    			ev.addValue(colName, getDate(rs, colName));
		    			break;
	
		    		default:
		    			logger.error("ElectricalVolumeMapper.mapRow(): bad column type for " + colName + " type=" + colType);	
		    			break;
	    		}
	    	}

	    	return ev;
	    }
	}

    private static String getString(ResultSet rs, String colName) {
    	String value = null;

    	try {
	    	value = rs.getString(colName);
    	} catch (Exception e) {
    	}

    	// treat null varchar columns as empty Strings 
    	if (value==null)
    		value=new String("");
    	
    	return value;
    }
    private static Date getDate(ResultSet rs, String colName) {
    	Date value = null;
    	try {
	    	value = rs.getDate(colName);
    	} catch (Exception e) {
    	}
    	return value;
    }
    private static Date getTimestampAsDate(ResultSet rs, String colName) {
    	Date value = null;
    	Timestamp ts = null;
    	try {
	    	ts = rs.getTimestamp(colName);
	    	value = new Date(ts.getTime());
    	} catch (Exception e) {
    	}
    	return value;
    }
    private static Integer getInt(ResultSet rs, String colName) {
    	Integer value = null;
    	try {
	    	value = new Integer(rs.getInt(colName));
    	} catch (Exception e) {
    	}
    	return value;
    }
    private static Long getLong(ResultSet rs, String colName) {
    	Long value = null;
    	try {
	    	value = new Long(rs.getLong(colName));
    	} catch (Exception e) {
    	}
    	return value;
    }
    private static BigDecimal getBigDecimal(ResultSet rs, String colName) {
    	BigDecimal value = null;
    	try {
	    	value = rs.getBigDecimal(colName);
    	} catch (Exception e) {
    	}
    	return value;
    }

}
