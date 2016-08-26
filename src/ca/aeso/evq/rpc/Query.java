package ca.aeso.evq.rpc;

import java.util.ArrayList;
import java.util.List;

import ca.aeso.evq.common.CodesUtil;
import ca.aeso.evq.common.DateQueryType;
import ca.aeso.evq.common.GeogQueryType;
import ca.aeso.evq.common.PoiQueryType;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Query
 * Represents a user query
 * @author mbodor
 */
public class Query implements IsSerializable {

	private long queryId;
	private String userId;
	private String queryName;

	private String dateQueryType;
	private String beginDate;
	private String endDate;
	private Integer calBeginYear;
	private Integer calBeginMonth;
	private Integer calEndYear;
	private Integer calEndMonth;
	
	private Integer twoSeasBeginYear;
	private Integer twoSeasEndYear;
	private boolean twoSeasonSummerSelected;
	private boolean twoSeasonWinterSelected;

	private Integer fourSeasBeginYear;
	private Integer fourSeasEndYear;
	private boolean fourSeasonSpringSelected;
	private boolean fourSeasonSummerSelected;
	private boolean fourSeasonFallSelected;
	private boolean fourSeasonWinterSelected;
	
	private String timeInterval;
	
	private String geogQueryType;
	private List regions;
	private List planningAreas;
	private List substations;
	private List measurementPoints;

	private String granularity;
	private String category;
	private boolean loadTypeSelected;
	private boolean generationTypeSelected;
	
	private String poiQueryType;
	private boolean poiPeakSelected;
	private boolean poiMedianSelected;
	private boolean poiLightSelected;

	private boolean poiLoadSelected;
	private boolean poiGenerationSelected;

	private String poiCoincidence;
	private String poiCategory;
	
	private String sql;
	private String queryStatus;
	private String queryDateStr;
	private String queryErrors;
	private long queryRuntime;

	
	public Query() {
	}

	public long getQueryId() {
		return queryId;
	}

	public void setQueryId(long queryId) {
		this.queryId = queryId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getGeogQueryType() {
		return geogQueryType;
	}

	public void setGeogQueryType(String geogQueryType) {
		this.geogQueryType = geogQueryType;
	}

	public List getRegions() {
		return regions;
	}

	public void setRegions(List regions) {
		this.regions = regions;
	}

	public void addRegion(String newRegion) {
		if (this.regions==null) {
			this.regions = new ArrayList();
		}
		this.regions.add(newRegion);
	}

	public List getSubstations() {
		return substations;
	}

	public void setSubstations(List substations) {
		this.substations = substations;
	}

	public void addSubstation(String newSub) {
		if (this.substations==null) {
			this.substations = new ArrayList();
		}
		this.substations.add(newSub);
	}

	public List getMeasurementPoints() {
		return measurementPoints;
	}

	public void setMeasurementPoints(List measurementPoints) {
		this.measurementPoints = measurementPoints;
	}

	public void addMeasurementPoint(String newMp) {
		if (this.measurementPoints==null) {
			this.measurementPoints = new ArrayList();
		}
		this.measurementPoints.add(newMp);
	}
	
	public String getGranularity() {
		return granularity;
	}

	public void setGranularity(String granularity) {
		this.granularity = granularity;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPoiQueryType() {
		return poiQueryType;
	}

	public void setPoiQueryType(String poiQueryType) {
		this.poiQueryType = poiQueryType;
	}

	public boolean isPoiPeakSelected() {
		return poiPeakSelected;
	}

	public void setPoiPeakSelected(boolean poiPeakSelected) {
		this.poiPeakSelected = poiPeakSelected;
	}

	public boolean isPoiMedianSelected() {
		return poiMedianSelected;
	}

	public void setPoiMedianSelected(boolean poiMedianSelected) {
		this.poiMedianSelected = poiMedianSelected;
	}

	public boolean isPoiLightSelected() {
		return poiLightSelected;
	}

	public void setPoiLightSelected(boolean poiLightSelected) {
		this.poiLightSelected = poiLightSelected;
	}

	public String getPoiCoincidence() {
		return poiCoincidence;
	}

	public void setPoiCoincidence(String poiCoincidence) {
		this.poiCoincidence = poiCoincidence;
	}

	public String getPoiCategory() {
		return poiCategory;
	}

	public void setPoiCategory(String poiCategory) {
		this.poiCategory = poiCategory;
	}

	public String getDateQueryType() {
		return dateQueryType;
	}

	public void setDateQueryType(String dateQueryType) {
		this.dateQueryType = dateQueryType;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getCalBeginYear() {
		return calBeginYear;
	}

	public void setCalBeginYear(Integer calBeginYear) {
		this.calBeginYear = calBeginYear;
	}

	public Integer getCalBeginMonth() {
		return calBeginMonth;
	}

	public void setCalBeginMonth(Integer calBeginMonth) {
		this.calBeginMonth = calBeginMonth;
	}

	public Integer getCalEndYear() {
		return calEndYear;
	}

	public void setCalEndYear(Integer calEndYear) {
		this.calEndYear = calEndYear;
	}

	public Integer getCalEndMonth() {
		return calEndMonth;
	}

	public void setCalEndMonth(Integer calEndMonth) {
		this.calEndMonth = calEndMonth;
	}

	public Integer getTwoSeasBeginYear() {
		return twoSeasBeginYear;
	}

	public void setTwoSeasBeginYear(Integer twoSeasBeginYear) {
		this.twoSeasBeginYear = twoSeasBeginYear;
	}

	public Integer getTwoSeasEndYear() {
		return twoSeasEndYear;
	}

	public void setTwoSeasEndYear(Integer twoSeasEndYear) {
		this.twoSeasEndYear = twoSeasEndYear;
	}

	public boolean isTwoSeasonSummerSelected() {
		return twoSeasonSummerSelected;
	}

	public void setTwoSeasonSummerSelected(boolean twoSeasonSummer) {
		this.twoSeasonSummerSelected = twoSeasonSummer;
	}

	public boolean isTwoSeasonWinterSelected() {
		return twoSeasonWinterSelected;
	}

	public void setTwoSeasonWinterSelected(boolean twoSeasonWinter) {
		this.twoSeasonWinterSelected = twoSeasonWinter;
	}

	public Integer getFourSeasBeginYear() {
		return fourSeasBeginYear;
	}

	public void setFourSeasBeginYear(Integer fourSeasBeginYear) {
		this.fourSeasBeginYear = fourSeasBeginYear;
	}

	public Integer getFourSeasEndYear() {
		return fourSeasEndYear;
	}

	public void setFourSeasEndYear(Integer fourSeasEndYear) {
		this.fourSeasEndYear = fourSeasEndYear;
	}

	public boolean isFourSeasonSpringSelected() {
		return fourSeasonSpringSelected;
	}

	public void setFourSeasonSpringSelected(boolean fourSeasonSpringFlag) {
		this.fourSeasonSpringSelected = fourSeasonSpringFlag;
	}

	public boolean isFourSeasonSummerSelected() {
		return fourSeasonSummerSelected;
	}

	public void setFourSeasonSummerSelected(boolean fourSeasonSummerFlag) {
		this.fourSeasonSummerSelected = fourSeasonSummerFlag;
	}

	public boolean isFourSeasonFallSelected() {
		return fourSeasonFallSelected;
	}

	public void setFourSeasonFallSelected(boolean fourSeasonFallFlag) {
		this.fourSeasonFallSelected = fourSeasonFallFlag;
	}

	public boolean isFourSeasonWinterSelected() {
		return fourSeasonWinterSelected;
	}

	public void setFourSeasonWinterSelected(boolean fourSeasonWinterFlag) {
		this.fourSeasonWinterSelected = fourSeasonWinterFlag;
	}

	public String getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(String timeInterval) {
		this.timeInterval = timeInterval;
	}

	public boolean isLoadTypeSelected() {
		return loadTypeSelected;
	}

	public void setLoadTypeSelected(boolean loadTypeSelected) {
		this.loadTypeSelected = loadTypeSelected;
	}

	public boolean isGenerationTypeSelected() {
		return generationTypeSelected;
	}

	public void setGenerationTypeSelected(boolean generationTypeSelected) {
		this.generationTypeSelected = generationTypeSelected;
	}

	public List getGeographicParms() {

		List parms = new ArrayList();

		if (GeogQueryType.GEOG_QUERY_TYPE_ENTIRE_SYSTEM.equals(geogQueryType)) {
			parms.add(GeogQueryType.GEOG_QUERY_TYPE_ENTIRE_SYSTEM.getValue());
			return parms;
		} else if (GeogQueryType.GEOG_QUERY_TYPE_REGION.equals(geogQueryType)) {
			return regions;
		} else if (GeogQueryType.GEOG_QUERY_TYPE_AREA.equals(geogQueryType)) {
			return planningAreas;
		} else if (GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.equals(geogQueryType)) {
			return substations;
		} else if (GeogQueryType.GEOG_QUERY_TYPE_MP.equals(geogQueryType)) {
			return measurementPoints;
		} else if (GeogQueryType.GEOG_QUERY_TYPE_IMPORTS_EXPORTS.equals(geogQueryType)) {
			parms.add(GeogQueryType.GEOG_QUERY_TYPE_IMPORTS_EXPORTS.getValue());
			return parms;
		} else {
			return null;
		}
		
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getQueryStatus() {
		return queryStatus;
	}

	public void setQueryStatus(String queryStatus) {
		this.queryStatus = queryStatus;
	}

	public List getPlanningAreas() {
		return planningAreas;
	}

	public void setPlanningAreas(List planningAreas) {
		this.planningAreas = planningAreas;
	}

	public void addPlanningArea(String newArea) {
		if (this.planningAreas==null) {
			this.planningAreas = new ArrayList();
		}
		this.planningAreas.add(newArea);
	}

	public String getQueryDateStr() {
		return queryDateStr;
	}

	public void setQueryDateStr(String queryDateStr) {
		this.queryDateStr = queryDateStr;
	}

	public String getQueryErrors() {
		return queryErrors;
	}

	public void setQueryErrors(String queryErrors) {
		this.queryErrors = queryErrors;
	}

	public boolean isPoiLoadSelected() {
		return poiLoadSelected;
	}

	public void setPoiLoadSelected(boolean poiLoadSelected) {
		this.poiLoadSelected = poiLoadSelected;
	}

	public boolean isPoiGenerationSelected() {
		return poiGenerationSelected;
	}

	public void setPoiGenerationSelected(boolean poiGenerationSelected) {
		this.poiGenerationSelected = poiGenerationSelected;
	}

	public long getQueryRuntime() {
		return queryRuntime;
	}

	public void setQueryRuntime(Long queryRuntime) {
		this.queryRuntime = queryRuntime.longValue();
	}

	public String getRuntimeStr() {

		long runtime = this.getQueryRuntime();

		long runtimeSecs = (runtime/1000);
		
		if (runtimeSecs<60) {
			return Long.toString(runtimeSecs) + " s";
		} else {
			long runtimeMins = runtimeSecs/60;
			if (runtimeMins<60) {
				return Long.toString(runtimeMins) + " min";
			} else {
				long runtimeHours = runtimeMins/60;
				runtimeMins = runtimeMins - (runtimeHours*60);
				
				return Long.toString(runtimeHours) + "hrs " + Long.toString(runtimeMins) + " min";
			}
		}
	}

	public String getNameStr() {

		String queryName = this.getQueryName();

		if (queryName!=null && queryName.length()>50) {
			return queryName.substring(0, 50); 	 
		} else {
			return queryName;
		}
	}
	
	public String getDateParametersStr() {
		StringBuffer buf = new StringBuffer();

		try {

			String type = this.getDateQueryType();

			if (DateQueryType.DATE_QUERY_TYPE_SPECIFIC.equals(type)) {
				buf.append(this.getBeginDate());
				buf.append(" to ");
				buf.append(this.getEndDate());
			} else if (DateQueryType.DATE_QUERY_TYPE_CALENDAR.equals(type)) { // months removed for now
				buf.append(this.getCalBeginYear());
//				buf.append("/");
//				buf.append(QueryDetailWidget.MONTHS[item.getCalBeginMonth().intValue()-1]);
				buf.append(" to ");
				buf.append(this.getCalEndYear());
//				buf.append("/");
//				buf.append(QueryDetailWidget.MONTHS[item.getCalEndMonth().intValue()-1]);
			} else if (DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.equals(type)) {
				buf.append(this.getTwoSeasBeginYear());
				buf.append(" to ");
				buf.append(this.getTwoSeasEndYear());
				buf.append(": ");
				buf.append(this.isTwoSeasonSummerSelected()?"Summer ":"");
				buf.append(this.isTwoSeasonWinterSelected()?"Winter":"");
			} else if (DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.equals(type)) {
				buf.append(this.getFourSeasBeginYear());
				buf.append(" to ");
				buf.append(this.getFourSeasEndYear());
				buf.append(": ");
				buf.append(this.isFourSeasonSpringSelected()?"Spring ":"");
				buf.append(this.isFourSeasonSummerSelected()?"Summer ":"");
				buf.append(this.isFourSeasonFallSelected()?"Fall ":"");
				buf.append(this.isFourSeasonWinterSelected()?"Winter":"");
			}

		} catch (Exception e) {
			return null;
		}

		return buf.toString();
	}
	
	public String getGeogParametersStr() {
		StringBuffer buf = new StringBuffer();
		try {
			buf.append(GeogQueryType.getDescription(this.getGeogQueryType()));
		} catch(Exception e) {
			return null;
		}

		return buf.toString();
	}

	public String getVolumeParametersStr() {
		StringBuffer buf = new StringBuffer();
		try {
			buf.append(CodesUtil.getCodeDesc(this.getGranularity()));
			buf.append(": ");
			
			if (this.isLoadTypeSelected()) {
				buf.append("Load");
				if (this.isGenerationTypeSelected()) {
					buf.append("+");
				}
			} 
			
			if (this.isGenerationTypeSelected()) {
				buf.append("Gen");
			}
			
		} catch (Exception e) {
			return null;
		}
		return buf.toString();
	}
	
	public String getInterestPointStr() {
		StringBuffer buf = new StringBuffer();
		try {
			String poiType = this.getPoiQueryType();
			if (PoiQueryType.POI_QUERY_TYPE_HOURLY.equals(poiType)) {
				buf.append("Hourly");
			} else {

				// coincidence
				String coincidence = CodesUtil.getCodeDesc(this.getPoiCoincidence()); 
				buf.append(coincidence);
				buf.append(" ");
				
				if (this.isPoiLoadSelected()) {
					buf.append("Load");
					if (this.isPoiGenerationSelected()) {
						buf.append("+");
					}
				} 
				
				if (this.isPoiGenerationSelected()) {
					buf.append("Gen");
				}
				
				buf.append(": ");

				
				if (this.isPoiPeakSelected()) {
					buf.append("Peak");
					if (this.isPoiMedianSelected() || this.isPoiLightSelected())
						buf.append("+");
				}

				if (this.isPoiMedianSelected()) {
					buf.append("Med");
					if (this.isPoiLightSelected())
						buf.append("+");
				}
				
				if (this.isPoiLightSelected()) {
					buf.append("Light");
				}

			}
		} catch (Exception e) {
			buf.append("&nbsp;");
		}

		return buf.toString();
	}
	
}
