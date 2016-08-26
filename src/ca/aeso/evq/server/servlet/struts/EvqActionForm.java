package ca.aeso.evq.server.servlet.struts;

import java.util.*;

import ca.aeso.evq.rpc.Query;
import ca.aeso.evq.common.*;

public class EvqActionForm extends BaseActionForm
{
	
	protected static final String[] MONTHS = new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG",
		"SEP", "OCT", "NOV", "DEC" };
	protected static final String[] YEARS = new String[] { "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008" };

	private String user;
	private List queryHistory;
	private List codes;
	private Query query;
	private List regions;
	private List planningAreas;
	private List substations;
	private List measurementPoints;
	private List granularities;
	private List categories;
	private List coincidences;
	private List poiCategories;
	private List timeIntervals;
	private boolean showHistory;
	private String filterArea;
	private String filterMpId;
	private String filterSub;
	private List nullList;
	private String[] selectedAreas;
	private String[] selectedSubs;
	private String[] selectedMps;
	
	private GeogQueryType geogQueryType;
	
	public void reset()
	{
	}


	public List getMonths() {
		return Arrays.asList(MONTHS);
	}

	public List getYears() {
		return Arrays.asList(YEARS);
	}
	
	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public List getQueryHistory() {
		return queryHistory;
	}


	public void setQueryHistory(List queryHistory) {
		this.queryHistory = queryHistory;
	}


	public List getCodes() {
		return codes;
	}


	public void setCodes(List codes) {
		this.codes = codes;
	}


	public Query getQuery() {
		return query;
	}


	public void setQuery(Query query) {
		this.query = query;
	}


	public List getRegions() {
		return regions;
	}


	public void setRegions(List regions) {
		this.regions = regions;
	}


	public List getPlanningAreas() {
		return planningAreas;
	}


	public void setPlanningAreas(List planningAreas) {
		this.planningAreas = planningAreas;
	}


	public List getSubstations() {
		return substations;
	}


	public void setSubstations(List substations) {
		this.substations = substations;
	}


	public List getMeasurementPoints() {
		return measurementPoints;
	}


	public void setMeasurementPoints(List measurementPoints) {
		this.measurementPoints = measurementPoints;
	}


	public List getGranularities() {
		return granularities;
	}


	public void setGranularities(List granularities) {
		this.granularities = granularities;
	}


	public List getCategories() {
		return categories;
	}


	public void setCategories(List categories) {
		this.categories = categories;
	}


	public List getCoincidences() {
		return coincidences;
	}


	public void setCoincidences(List coincidences) {
		this.coincidences = coincidences;
	}


	public List getPoiCategories() {
		return poiCategories;
	}


	public void setPoiCategories(List poiCategories) {
		this.poiCategories = poiCategories;
	}


	public List getTimeIntervals() {
		return timeIntervals;
	}


	public void setTimeIntervals(List timeIntervals) {
		this.timeIntervals = timeIntervals;
	}


	public boolean isShowHistory() {
		return showHistory;
	}


	public void setShowHistory(boolean showHistory) {
		this.showHistory = showHistory;
	}


	public GeogQueryType getGeogQueryType() {
		return geogQueryType;
	}


	public void setGeogQueryType(GeogQueryType geogQueryType) {
		this.geogQueryType = geogQueryType;
	}


	public String getFilterArea() {
		return filterArea;
	}


	public void setFilterArea(String filterArea) {
		this.filterArea = filterArea;
	}


	public String getFilterMpId() {
		return filterMpId;
	}


	public void setFilterMpId(String filterMpId) {
		this.filterMpId = filterMpId;
	}


	public String getFilterSub() {
		return filterSub;
	}


	public void setFilterSub(String filterSub) {
		this.filterSub = filterSub;
	}


	public List getNullList() {
		return nullList;
	}


	public void setNullList(List nullList) {
		this.nullList = nullList;
	}


	public String[] getSelectedMps() {
		return selectedMps;
	}


	public void setSelectedMps(String[] selectedMps) {
		this.selectedMps = selectedMps;
	}


	public String[] getSelectedAreas() {
		return selectedAreas;
	}


	public void setSelectedAreas(String[] selectedAreas) {
		this.selectedAreas = selectedAreas;
	}


	public String[] getSelectedSubs() {
		return selectedSubs;
	}


	public void setSelectedSubs(String[] selectedSubs) {
		this.selectedSubs = selectedSubs;
	}
	

}
