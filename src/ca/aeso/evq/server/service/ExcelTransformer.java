package ca.aeso.evq.server.service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ca.aeso.evq.common.DateQueryType;
import ca.aeso.evq.common.GeogQueryType;
import ca.aeso.evq.common.PoiQueryType;
import ca.aeso.evq.rpc.Query;
import ca.aeso.evq.server.util.Constants;
import ca.aeso.evq.server.util.POISupport;

/**
 * ExcelTransformer
 * A class to manipulate the results of the query
 * Implements Transformable
 * 
 * @author mbodor
 */
public class ExcelTransformer implements Transformable {

	protected Log logger = LogFactory.getLog(ExcelTransformer.class);
	
	private Query query;
	private QueryService service;
	private HSSFWorkbook wb;
	private List results;
	HSSFCellStyle titleStyle;
	HSSFCellStyle headerStyle;
	HSSFCellStyle leftStringStyle;
	HSSFCellStyle centerStringStyle;
	HSSFCellStyle rightStringStyle;
    HSSFCellStyle rightDecimalStyle;
    HSSFCellStyle rightNumberStyle;

    /**
     * Constructor
     * @param aQuery
     * @param results
     */
	public ExcelTransformer(Query aQuery, List results) {
		this.query = aQuery;
		this.results = results;
		wb = new HSSFWorkbook();
		titleStyle = POISupport.createHeaderStyle(wb, false) ;
		headerStyle = POISupport.createHeaderStyle(wb, true) ;
		leftStringStyle = POISupport.createDetailStyle(wb, HSSFCellStyle.ALIGN_LEFT, null, null);
		centerStringStyle = POISupport.createDetailStyle(wb, HSSFCellStyle.ALIGN_CENTER, null, null);
		rightStringStyle = POISupport.createDetailStyle(wb, HSSFCellStyle.ALIGN_RIGHT, null, null);
	    rightDecimalStyle = POISupport.createDetailStyle(wb, HSSFCellStyle.ALIGN_RIGHT, "########0.0000", null);
	    rightNumberStyle = POISupport.createDetailStyle(wb, HSSFCellStyle.ALIGN_RIGHT, "#########0", null);
	}

	public void setService(QueryService service) {
		this.service = service;
	}
	
	/**
	 * Transform the query results into a spreadsheet
	 * @return the number of result rows transformed
	 */
    public int transform() {
    	logger.debug("ExcelTransformer.transform(): starting");
    	
    	if (results==null)
    		return 0;

    	long start = System.currentTimeMillis();
		buildQuerySheet();
		int numRows = buildResultsSheet(); 

		long end = System.currentTimeMillis();
    	logger.debug("ExcelTransformer.transform(): done");
    	logger.info("ExcelTransformer.transform(): duration=" + (end-start) + "ms");
		
        return numRows;
    }

	private void buildQuerySheet() {
		logger.debug("buildQuerySheet() starting");
    	long start = System.currentTimeMillis();
		
		int rownum = 0;
		short col1 = (short)1;
		short col2 = (short)2;
		short col3 = (short)3;
		short col5 = (short)5;
    	
		HSSFSheet querySheet = wb.createSheet(Constants.QUERY);

		HSSFRow titleRow1 = querySheet.createRow(rownum);
	    POISupport.createCell(wb, titleStyle, titleRow1, col2, Constants.AESO);
	    rownum++;
		
		HSSFRow titleRow2 = querySheet.createRow(rownum);
	    POISupport.createCell(wb, titleStyle, titleRow2, col2, Constants.EVQ);
	    rownum++;
		
		querySheet.createRow(rownum); // blank
	    rownum++;

	    HSSFRow qrDate = querySheet.createRow(rownum);
	    POISupport.createCell(wb, leftStringStyle, qrDate, col1, Constants.QUERY_DATE_LABEL);
	    POISupport.createCell(wb, leftStringStyle, qrDate, col3, query.getQueryDateStr());
	    rownum++;

	    HSSFRow qrName = querySheet.createRow(rownum);
	    POISupport.createCell(wb, leftStringStyle, qrName, col1, Constants.QUERY_NAME_LABEL);
	    POISupport.createCell(wb, leftStringStyle, qrName, col3, query.getQueryName());
	    rownum++;

	    HSSFRow qrNum = querySheet.createRow(rownum);
	    POISupport.createCell(wb, leftStringStyle, qrNum, col1, Constants.QUERY_NUMBER_LABEL);
	    POISupport.createCell(wb, leftStringStyle, qrNum, col3, query.getQueryId());
	    rownum++;
		

	    String dateQueryType = query.getDateQueryType();
	    if (DateQueryType.DATE_QUERY_TYPE_SPECIFIC.equals(dateQueryType)) {
		    HSSFRow qrBeginDate = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrBeginDate, col1, Constants.BEGIN_DATE_LABEL);
		    POISupport.createCell(wb, leftStringStyle, qrBeginDate, col3, query.getBeginDate());
		    rownum++;
		    HSSFRow qrEndDate = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrEndDate, col1, Constants.END_DATE_LABEL);
		    POISupport.createCell(wb, leftStringStyle, qrEndDate, col3, query.getEndDate());
		    rownum++;
	    } else if (DateQueryType.DATE_QUERY_TYPE_CALENDAR.equals(dateQueryType)) {
		    HSSFRow qrCalBegin = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrCalBegin, col1, Constants.CAL_BEGIN_YM_LABEL);
//		    POISupport.createCell(wb, leftStringStyle, qrCalBegin, col3, query.getCalBeginYear().toString() + "/" + Constants.getMonthStr(query.getCalBeginMonth().intValue()-1));
		    POISupport.createCell(wb, leftStringStyle, qrCalBegin, col3, query.getCalBeginYear().toString());
		    rownum++;
		    HSSFRow qrCalEnd = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrCalEnd, col1, Constants.CAL_END_YM_LABEL);
//		    POISupport.createCell(wb, leftStringStyle, qrCalEnd, col3, query.getCalEndYear().toString() + "/" + Constants.getMonthStr(query.getCalEndMonth().intValue()-1));
		    POISupport.createCell(wb, leftStringStyle, qrCalEnd, col3, query.getCalEndYear().toString());
		    rownum++;
	    } else if (DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.equals(dateQueryType)) {
		    HSSFRow qrTSBegin = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrTSBegin, col1, Constants.TWO_SEASON_BEGIN_YEAR_LABEL);
		    POISupport.createCell(wb, leftStringStyle, qrTSBegin, col3, query.getTwoSeasBeginYear().toString());
		    rownum++;
		    HSSFRow qrTSEnd = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrTSEnd, col1, Constants.TWO_SEASON_END_YEAR_LABEL);
		    POISupport.createCell(wb, leftStringStyle, qrTSEnd, col3, query.getTwoSeasEndYear().toString());
		    rownum++;
		    HSSFRow qrTSSeas = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrTSSeas, col1, Constants.SEASON_LABEL);
		    
		    StringBuffer seasons = new StringBuffer();
		    if (query.isTwoSeasonSummerSelected()) {
		    	seasons.append(Constants.SUMMER);
			    if (query.isTwoSeasonWinterSelected()) {
			    	seasons.append(Constants.COMMA_SPACE);
			    }
		    }
		    if (query.isTwoSeasonWinterSelected()) {
		    	seasons.append(Constants.WINTER);
		    }
		    
		    POISupport.createCell(wb, leftStringStyle, qrTSSeas, col3, seasons.toString());
		    rownum++;
	    } else if (DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.equals(dateQueryType)) {
		    HSSFRow qrFSBegin = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrFSBegin, col1, Constants.FOUR_SEASON_BEGIN_YEAR_LABEL);
		    POISupport.createCell(wb, leftStringStyle, qrFSBegin, col3, query.getFourSeasBeginYear().toString());
		    rownum++;
		    HSSFRow qrFSEnd = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrFSEnd, col1, Constants.FOUR_SEASON_END_YEAR_LABEL);
		    POISupport.createCell(wb, leftStringStyle, qrFSEnd, col3, query.getFourSeasEndYear().toString());
		    rownum++;
		    HSSFRow qrFSSeas = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrFSSeas, col1, Constants.SEASON_LABEL);
		    
		    StringBuffer seasons = new StringBuffer();
		    if (query.isFourSeasonSpringSelected()) {
		    	seasons.append(Constants.SPRING);
			    if (query.isFourSeasonSummerSelected() || query.isFourSeasonFallSelected() || query.isFourSeasonWinterSelected()) {
			    	seasons.append(Constants.COMMA_SPACE);
			    }
		    }
		    if (query.isFourSeasonSummerSelected()) {
		    	seasons.append(Constants.SUMMER);
			    if (query.isFourSeasonFallSelected() || query.isFourSeasonWinterSelected()) {
			    	seasons.append(Constants.COMMA_SPACE);
			    }
		    }
		    if (query.isFourSeasonFallSelected()) {
		    	seasons.append(Constants.FALL);
			    if (query.isFourSeasonWinterSelected()) {
			    	seasons.append(Constants.COMMA_SPACE);
			    }
		    }
		    if (query.isFourSeasonWinterSelected()) {
		    	seasons.append(Constants.WINTER);
		    }
		    
		    POISupport.createCell(wb, leftStringStyle, qrFSSeas, col3, seasons.toString());
		    rownum++;
	    }
		
	    HSSFRow qrGeog = querySheet.createRow(rownum);
	    String geogQueryType = query.getGeogQueryType();
	    POISupport.createCell(wb, leftStringStyle, qrGeog, col1, Constants.GEOG_QUERY_PARMS_LABEL);
	    POISupport.createCell(wb, leftStringStyle, qrGeog, col3, GeogQueryType.getDescription(geogQueryType));
	    
	    // list geographic parameters 
	    List parms = query.getGeographicParms();
	    if (parms!=null) {
		    StringBuffer geogParms = new StringBuffer();
		    for (Iterator iterator = parms.iterator(); iterator.hasNext();) {
				String parm = (String) iterator.next();
				if (GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.equals(geogQueryType)) {
					// this is a substation, so look up the id in the code map to get the string value
					try {
						String subsAlpha = (String)service.getSubMap().get(parm);
						if (subsAlpha==null)
							geogParms.append(parm);
						else
							geogParms.append(subsAlpha);
					} catch (Exception e) {
						geogParms.append(parm);
					}
				} else {
					geogParms.append(parm);
				}
				
				if (iterator.hasNext())
					geogParms.append(Constants.COMMA_SPACE);
			}
		    POISupport.createCell(wb, leftStringStyle, qrGeog, col5, geogParms.toString());
	    }
	    
	    rownum++;

	    querySheet.createRow(rownum); // blank
	    rownum++;

	    HSSFRow qrGranularity = querySheet.createRow(rownum);
	    POISupport.createCell(wb, leftStringStyle, qrGranularity, col1, Constants.GRANULARITY_LABEL);
	    POISupport.createCell(wb, leftStringStyle, qrGranularity, col3, service.getCodeDesc(query.getGranularity()));
	    rownum++;
	    
	    HSSFRow qrVolType = querySheet.createRow(rownum);
	    POISupport.createCell(wb, leftStringStyle, qrVolType, col1, Constants.CATEGORY_LABEL);
	    POISupport.createCell(wb, leftStringStyle, qrVolType, col3, service.getCodeDesc(query.getCategory()));
	    rownum++;
	    
	    HSSFRow qrVols = querySheet.createRow(rownum);
	    POISupport.createCell(wb, leftStringStyle, qrVols, col1, Constants.VOLUME_TYPE_LABEL);
	    
	    StringBuffer volumes = new StringBuffer();
	    if (query.isLoadTypeSelected()) {
	    	volumes.append(Constants.LOAD);
		    if (query.isGenerationTypeSelected()) {
		    	volumes.append(Constants.COMMA_SPACE);
		    }
	    }
	    if (query.isGenerationTypeSelected()) {
	    	volumes.append(Constants.GENERATION);
	    }
	    
	    POISupport.createCell(wb, leftStringStyle, qrVols, col3, volumes.toString());
	    rownum++;
	    
	    querySheet.createRow(rownum); // blank
	    rownum++;

	    String poiQueryType = query.getPoiQueryType();
	    HSSFRow qrPoi = querySheet.createRow(rownum);
	    POISupport.createCell(wb, leftStringStyle, qrPoi, col1, Constants.INTEREST_POINT_TYPE_LABEL);
	    POISupport.createCell(wb, leftStringStyle, qrPoi, col3, service.getCodeDesc(poiQueryType));
	    rownum++;


	    if (PoiQueryType.POI_QUERY_TYPE_PERCENTILE.equals(poiQueryType)) {
		    HSSFRow qrCoinc = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrCoinc, col1, Constants.COINCIDENCE_LABEL);
		    POISupport.createCell(wb, leftStringStyle, qrCoinc, col3, service.getCodeDesc(query.getPoiCoincidence()));
		    rownum++;

		    HSSFRow qrPoiType = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrPoiType, col1, Constants.POI_CATEGORY_LABEL);
		    POISupport.createCell(wb, leftStringStyle, qrPoiType, col3, service.getCodeDesc(query.getPoiCategory()));
		    rownum++;

		    HSSFRow qrPoiVols = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrPoiVols, col1, Constants.POI_VOLUME_TYPE_LABEL);
		    POISupport.createCell(wb, leftStringStyle, qrPoiVols, col3, query.isPoiLoadSelected()?Constants.LOAD:Constants.GENERATION);
		    rownum++;

		    HSSFRow qrPoiInterval = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrPoiInterval, col1, Constants.TIME_INTERVAL_LABEL);
		    POISupport.createCell(wb, leftStringStyle, qrPoiInterval, col3, service.getCodeDesc(query.getTimeInterval()));
		    rownum++;

		    HSSFRow qrPoiPoints = querySheet.createRow(rownum);
		    POISupport.createCell(wb, leftStringStyle, qrPoiPoints, col1, Constants.INTEREST_POINT_LABEL);

		    StringBuffer poiPoints = new StringBuffer();
		    if (query.isPoiPeakSelected()) {
		    	poiPoints.append(Constants.PEAK);
			    if (query.isPoiMedianSelected() || (query.isPoiLightSelected())) {
			    	poiPoints.append(Constants.COMMA_SPACE);
			    }
		    }
		    if (query.isPoiMedianSelected()) {
		    	poiPoints.append(Constants.MEDIAN);
			    if (query.isPoiLightSelected()) {
			    	poiPoints.append(Constants.COMMA_SPACE);
			    }
		    }
		    if (query.isPoiLightSelected()) {
		    	poiPoints.append(Constants.LIGHT);
		    }
		    
		    POISupport.createCell(wb, leftStringStyle, qrPoiPoints, col3, poiPoints.toString());
		    rownum++;
		    
	    }
	    
	    querySheet.createRow(rownum); // blank
	    rownum++;

	    HSSFRow qrSql = querySheet.createRow(rownum);
	    POISupport.createCell(wb, leftStringStyle, qrSql, col1, Constants.SQL_LABEL);
	    POISupport.createCell(wb, leftStringStyle, qrSql, col3, query.getSql());
	    rownum++;
	    
	    querySheet.autoSizeColumn((short)1);
	    
    	long end = System.currentTimeMillis();
		logger.debug("ExcelTransformer.buildQuerySheet() done");
    	logger.info("ExcelTransformer.buildQuerySheet(): duration=" + (end-start) + "ms");
	    
	}

	private int buildResultsSheet() {
		logger.debug("ExcelTransformer.buildResultsSheet() starting");
    	long start = System.currentTimeMillis();
		
		HSSFSheet resultsSheet = wb.createSheet(Constants.RESULTS);
    	int resultCount = results.size();
    	
    	// go through the whole result set
    	for (int i = 0; i < resultCount; i++) {
    			
        	ElectricalVolume ev = (ElectricalVolume)results.get(i);
    		short colNum = 0;

        	// get the list of ordered values for this result row
        	List valueList = ev.getValueList();

    		// if this is the first result row, create the column headers
			if (i==0) {
			    // create a spreadsheet row
				HSSFRow headerRow = resultsSheet.createRow(i);
	        	
	        	for (Iterator iterator = valueList.iterator(); iterator.hasNext();) {
					String valueName = (String) iterator.next();
//					logger.debug("Creating result sheet header column for: " + valueName + ", title=" + Constants.getColumnTitle(valueName));
				    POISupport.createCell(wb, headerStyle, headerRow, colNum, Constants.getColumnTitle(valueName));
				    int length = Constants.getColumnTitle(valueName).length();
				    if (length<15) length=15;
				    resultsSheet.setColumnWidth(colNum, ((short)(length*256)));				    
				    colNum++;
	        	}				

	        	colNum=0;
			} 
				
    		// create a detail row
			HSSFRow detailRow = resultsSheet.createRow(i+1);
				
    		// go through the ordered list of values in the row
        	for (Iterator iterator = valueList.iterator(); iterator.hasNext();) {
				String valueName = (String) iterator.next();
				
				// get the specific value and create the appropriate detail cell
				Object valueObj = ev.get(valueName);
				
				if (valueObj instanceof String) {
					String value = (String)valueObj;
	    		    POISupport.createCell(wb, centerStringStyle, 	detailRow, colNum, value);
	    		    colNum++;
				} else if (valueObj instanceof BigDecimal) {
					BigDecimal value = (BigDecimal)valueObj;
					int scale = value.scale();
					if (scale>0) {
						POISupport.createCell(wb, rightDecimalStyle, 	detailRow, colNum, value.doubleValue());
					} else {
		    		    POISupport.createCell(wb, rightNumberStyle, 	detailRow, colNum, value.longValue());
					}
	    		    colNum++;
				} else if (valueObj instanceof Date) {
					Date value = (Date)valueObj;
	    		    POISupport.createCell(wb, leftStringStyle, 	detailRow, colNum, value.toString());
	    		    colNum++;
				} else {
					logger.error("ExcelTransformer.buildResultsSheet(): found a value that isnt mapped!");
					if (valueObj==null)
						logger.error("ExcelTransformer.buildResultsSheet(): valueObj is null");
					
				}

			}
        	valueList = null;
        	ev = null;
    	}

    	long end = System.currentTimeMillis();
		logger.debug("ExcelTransformer.buildResultsSheet() done");
    	logger.info("ExcelTransformer.buildResultsSheet(): duration=" + (end-start) + "ms");
		
    	return resultCount;
	}
	
	/**
	 * Write the workbook into an outputstream
	 * @param out
	 * @throws IOException
	 */
    public void streamResult(OutputStream out) throws IOException {
        wb.write(out);    	
    }

    /**
     * Get the id of the query
     * @return
     */
	public long getTransformedIdentifier() {
		return query.getQueryId();
	}

	public String getResultType() {
		return "HSSF";
	}
}
