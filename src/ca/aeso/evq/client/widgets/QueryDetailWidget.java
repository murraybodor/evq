package ca.aeso.evq.client.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ca.aeso.evq.client.ElectricalVolumesQuery;
import ca.aeso.evq.client.QueryValidationException;
import ca.aeso.evq.common.DateQueryType;
import ca.aeso.evq.common.GeogQueryType;
import ca.aeso.evq.common.PoiQueryType;
import ca.aeso.evq.rpc.Query;
import ca.aeso.evq.rpc.EvqServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

;/**
 * QueryDetailWidget
 * A composite widget that allows query detail entry
 * 
 * @author mbodor
 */
public class QueryDetailWidget extends Composite { 

	private EvqServiceAsync serviceEndpoint;

	protected static final String[] DAYS = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
			"Friday", "Saturday" };
	protected static final String[] MONTHS_TEXT = new String[] { "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December" };
	protected static final String[] MONTHS = new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG",
			"SEP", "OCT", "NOV", "DEC" };
	protected static final String[] YEARS = new String[] { "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008" };
	private static final String SPACE = "&nbsp;";
	
	private DateTimeFormat dateFmtSlash = DateTimeFormat.getFormat("yyyy/MM/dd");
	private DateTimeFormat dateFmtDash = DateTimeFormat.getFormat("yyyy-MM-dd");
	private DateTimeFormat dateFmtNopunc = DateTimeFormat.getFormat("yyyyMMdd");
	private List regions;
	private List planningAreas;
	private List substations;
	private List measurementPoints;
	private List granularities;
	private List categories;
	private List coincidences;
	private List poiCategories;
	private List timeIntervals;
	private String selectedGeogQueryType;
	private String savedGeogQueryType;
	private String savedGranularity;
	private String savedCoincidence;
	
	// UI controls
	TabPanel dateTabPanel = new TabPanel();
	TabPanel geogTabPanel = new TabPanel();
	TabPanel poiTabPanel = new TabPanel();

	TextBox specificBeginDate = new TextBox();
	TextBox specificEndDate = new TextBox();
	Button specificBeginDateButton = new Button("");
	Button specificEndDateButton = new Button("");

	DatePicker beginDatePicker = new DatePicker();
	DatePicker endDatePicker = new DatePicker();
	
	ListBox calendarBeginYear = new ListBox();
	ListBox calendarBeginMonth = new ListBox();
	ListBox calendarEndYear = new ListBox();
	ListBox calendarEndMonth = new ListBox();
	ListBox twoSeasonsBeginYear = new ListBox();
	ListBox twoSeasonsEndYear = new ListBox();
	CheckBox twoSeasonsSummer = new CheckBox("Summer");
	CheckBox twoSeasonsWinter = new CheckBox("Winter");
	ListBox fourSeasonsBeginYear = new ListBox();
	ListBox fourSeasonsEndYear = new ListBox();
	CheckBox fourSeasonsSpring = new CheckBox("Spring");
	CheckBox fourSeasonsSummer = new CheckBox("Summer");
	CheckBox fourSeasonsFall = new CheckBox("Fall");
	CheckBox fourSeasonsWinter = new CheckBox("Winter");
	ListBox timeIntervalListBox = new ListBox();
	TextBox queryName = new TextBox();
	ListBox regionListBox = new ListBox();

	// area
	ListBox planningAreaListBox = new ListBox();
	final Button addAreaButton = new Button("Add-->");
	final Button removeAreaButton = new Button("Remove");
	ListBox selectedAreaListBox = new ListBox();

	
	// substation
	ListBox areaSubListBox = new ListBox();
	StartsWithSuggestOracle subSuggestOracle = new StartsWithSuggestOracle();
	ListBox subListBox = new ListBox();
	EvqSuggestBox subSuggestBox = new EvqSuggestBox(subSuggestOracle, subListBox);
	final Button addSubButton = new Button("Add-->");
	final Button removeSubButton = new Button("Remove");
	ListBox selectedSubListBox = new ListBox();
	
	// mp
	ListBox areaMpListBox = new ListBox();
	StartsWithSuggestOracle mpSuggestOracle = new StartsWithSuggestOracle();
	ListBox mpListBox = new ListBox();
	EvqSuggestBox mpSuggestBox = new EvqSuggestBox(mpSuggestOracle, mpListBox);
	final Button addMpButton = new Button("Add-->");
	final Button removeMpButton = new Button("Remove");
	ListBox selectedMpListBox = new ListBox();

	
	ListBox granularityListBox = new ListBox();
	ListBox categoryListBox = new ListBox();
	CheckBox volumesLoadCb = new CheckBox("Load");
	CheckBox volumesGenerationCb = new CheckBox("Generation");
	RadioButton poiLoadRb = new RadioButton("poiRbGroup", "Load");
	RadioButton poiGenerationRb = new RadioButton("poiRbGroup", "Generation");
	CheckBox poiPeakCb = new CheckBox("Peak");
	CheckBox poiMedianCb = new CheckBox("Median");
	CheckBox poiLightCb = new CheckBox("Light");
	ListBox poiCoincidenceListBox = new ListBox();
	ListBox poiCategoryListBox = new ListBox();

	final Button submitButton = new Button("Submit");
	final Button resetButton = new Button("Reset");

	/**
	 *  a tablistener for the date tab behaviour
	 */
	TabListener dateTabClickListener = new TabListener() {
		public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
			return true;
		}

		public void onTabSelected(SourcesTabEvents sender, int index) {
			switch (index) {
			case 0:
				resetDatePanel();
				specificBeginDateButton.setEnabled(true);
				specificEndDateButton.setEnabled(true);
				break;
			case 1:
				resetDatePanel();
				calendarBeginYear.setEnabled(true);
//				calendarBeginMonth.setEnabled(true);
				calendarEndYear.setEnabled(true);
//				calendarEndMonth.setEnabled(true);
				break;
			case 2:
				resetDatePanel();
				twoSeasonsBeginYear.setEnabled(true);
				twoSeasonsEndYear.setEnabled(true);
				twoSeasonsSummer.setEnabled(true);
				twoSeasonsWinter.setEnabled(true);
				break;
			case 3:
				resetDatePanel();
				fourSeasonsBeginYear.setEnabled(true);
				fourSeasonsEndYear.setEnabled(true);
				fourSeasonsSpring.setEnabled(true);
				fourSeasonsSummer.setEnabled(true);
				fourSeasonsFall.setEnabled(true);
				fourSeasonsWinter.setEnabled(true);
				break;
			}
		}
	};

	/**
	 *  a clicklistener for the date picker button behaviour
	 */
	ClickListener datePickerButtonClickListener = new ClickListener() {
		public void onClick(Widget sender) {
			if (sender == specificBeginDateButton) {
				Date specBegin = parseDate(specificBeginDate.getText());
				if (specBegin!=null) 	
					beginDatePicker.setFullDate(specBegin);
				beginDatePicker.setPopupPosition(specificBeginDateButton.getAbsoluteLeft(), specificBeginDateButton.getAbsoluteTop());
				beginDatePicker.show();
			} else if (sender == specificEndDateButton) {
				Date specEnd = parseDate(specificEndDate.getText());
				if (specEnd!=null) 	
					endDatePicker.setFullDate(specEnd);
				endDatePicker.setPopupPosition(specificEndDateButton.getAbsoluteLeft(), specificEndDateButton.getAbsoluteTop());
				endDatePicker.show();
			} else {
				return;
			}
		}
	};

	/**
	 *  a changelistener for the begin date popup calendar behaviour
	 */
	ChangeListener beginDateChangeListener = new ChangeListener() {
		public void onChange(Widget sender) {
			if (sender instanceof DatePickerCell) {
				specificBeginDate.setText(dateFmtDash.format(beginDatePicker.selectedDate()));
				beginDatePicker.hide();
			}
		}
	};

	/**
	 *  a changelistener for the end date popup calendar behaviour
	 */
	ChangeListener endDateChangeListener = new ChangeListener() {
		public void onChange(Widget sender) {
			if (sender instanceof DatePickerCell) {
				specificEndDate.setText(dateFmtDash.format(endDatePicker.selectedDate()));
				endDatePicker.hide();
			}
		}
	};

	/**
	 *  a tablistener for the geographic tab behaviour
	 */
	TabListener geogTabClickListener = new TabListener() {
		public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
			return true;
		}

		public void onTabSelected(SourcesTabEvents sender, int index) {
			switch (index) {
			case 0:
				resetGeogPanel();
				selectedGeogQueryType = GeogQueryType.GEOG_QUERY_TYPE_ENTIRE_SYSTEM.getValue();
				resetVolumesPanel();
				loadGranularities();
				break;
			case 1:
				resetGeogPanel();
				selectedGeogQueryType = GeogQueryType.GEOG_QUERY_TYPE_REGION.getValue();
//				regionListBox.setEnabled(true); // regions disabled for now
				regionListBox.setEnabled(false);
				resetVolumesPanel();
				loadGranularities();
				break;
			case 2:
				resetGeogPanel();
				selectedGeogQueryType = GeogQueryType.GEOG_QUERY_TYPE_AREA.getValue();
				planningAreaListBox.setEnabled(true);
				resetVolumesPanel();
				loadGranularities();
				break;
			case 3:
				resetGeogPanel();
				selectedGeogQueryType = GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.getValue();
				subSuggestBox.setFocus(true);
				resetVolumesPanel();
				loadGranularities();
				break;
			case 4:
				resetGeogPanel();
				selectedGeogQueryType = GeogQueryType.GEOG_QUERY_TYPE_MP.getValue();
				mpSuggestBox.setFilter(null);
				mpSuggestBox.setFocus(true);
				resetVolumesPanel();
				volumesLoadCb.setEnabled(false);
				volumesGenerationCb.setEnabled(false);
				volumesLoadCb.setVisible(false);
				volumesGenerationCb.setVisible(false);
				loadGranularities();
				break;
			case 5:
				resetGeogPanel();
				selectedGeogQueryType = GeogQueryType.GEOG_QUERY_TYPE_IMPORTS_EXPORTS.getValue();
				resetVolumesPanel();
				loadGranularities();
				break;
			}
		}
	};

	
	/**
	 *  a tablistener for the poi tab behaviour
	 */
	TabListener poiTabClickListener = new TabListener() {
		public boolean onBeforeTabSelected(SourcesTabEvents sender, int index) {
			return true;
		}

		public void onTabSelected(SourcesTabEvents sender, int index) {
			switch (index) {
			case 0:
				poiPeakCb.setEnabled(true);
				poiMedianCb.setEnabled(true);
				poiLightCb.setEnabled(true);
				poiPeakCb.setChecked(false);
				poiMedianCb.setChecked(false);
				poiLightCb.setChecked(false);
				poiLoadRb.setChecked(true);
				poiCoincidenceListBox.setEnabled(true);
				poiCategoryListBox.setEnabled(true);

				break;
			case 1:
				break;
			}
		}
	};

	/**
	 *  a changelistener for the granularity/coincidence behaviour
	 */
	ChangeListener granularityChangeListener = new ChangeListener() {
		public void onChange(final Widget sender) {
			int idx = granularityListBox.getSelectedIndex();
			String[] gran = (String[])granularities.get(idx);
			loadCoincidences(gran[1]);
		} 
	};
	
	/**
	 *  a changelistener for the coincidence listbox
	 */
	ChangeListener coincidenceChangeListener = new ChangeListener() {
		public void onChange(Widget sender) {
				int idx = poiCoincidenceListBox.getSelectedIndex();
				String[] coinc = (String[])coincidences.get(idx);
				if (coinc[1].equals("SU")) {
					poiGenerationRb.setChecked(true);
					poiLoadRb.setEnabled(false);
					poiGenerationRb.setEnabled(false);
				} else if (coinc[1].equals("DE")) {
					poiLoadRb.setChecked(true);
					poiLoadRb.setEnabled(false);
					poiGenerationRb.setEnabled(false);
				} else if (coinc[1].equals("MP")) {
					poiLoadRb.setChecked(true);
					poiLoadRb.setEnabled(false);
					poiGenerationRb.setEnabled(false);
				} else {
					poiLoadRb.setEnabled(true);
					poiGenerationRb.setEnabled(true);
				}
		}
	};
	
	/**
	 *  a clicklistener for the form buttons
	 */
	ClickListener formButtonClickListener = new ClickListener() {
		public void onClick(Widget sender) {
			if (sender == resetButton) {
				reset();
			} else if (sender == submitButton) {
				submit();
			}
		}
	};

	/**
	 * Constructor
	 */
	public QueryDetailWidget() {
		VerticalPanel widgetPanel = drawWidget();
		initWidget(widgetPanel);
		setStyleName("queryDetailWidget");
	}

	/**
	 * Reset the UI to a blank state
	 */
	public void reset() {
		queryName.setText("");
		savedGeogQueryType = null;
		savedGranularity = null;
		dateTabPanel.selectTab(0);
		geogTabPanel.selectTab(0);
		resetVolumesPanel();
		poiTabPanel.selectTab(0);
	}

	/**
	 * Display the details of the selected query 
	 * @param aQuery
	 */
	public void displayQuery(final Query aQuery) {

		// name
		queryName.setText(aQuery.getQueryName());

		
		// date panel
		String dateQueryType = aQuery.getDateQueryType();

		if (DateQueryType.DATE_QUERY_TYPE_SPECIFIC.equals(dateQueryType)) {
			dateTabPanel.selectTab(0);
			specificBeginDate.setText(aQuery.getBeginDate());
			specificEndDate.setText(aQuery.getEndDate());
		} else if (DateQueryType.DATE_QUERY_TYPE_CALENDAR.equals(dateQueryType)) {
			dateTabPanel.selectTab(1);
			
			String beginYearStr = aQuery.getCalBeginYear().toString();
			for (int i = 0; i < YEARS.length; i++) {
				if (beginYearStr.equals(YEARS[i])) {
					calendarBeginYear.setItemSelected(i+1, true);
					break;
				}
			}

			String endYearStr = aQuery.getCalEndYear().toString();
			for (int i = 0; i < YEARS.length; i++) {
				if (endYearStr.equals(YEARS[i])) {
					calendarEndYear.setItemSelected(i+1, true);
					break;
				}
			}

//			int beginMonth = aQuery.getCalBeginMonth().intValue();
//			calendarBeginMonth.setItemSelected(beginMonth, true);
//			
//			int endMonth = aQuery.getCalEndMonth().intValue();
//			calendarEndMonth.setItemSelected(endMonth, true);

		} else if (DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.equals(dateQueryType)) {
			dateTabPanel.selectTab(2);

			String beginYearStr = aQuery.getTwoSeasBeginYear().toString();
			for (int i = 0; i < YEARS.length; i++) {
				if (beginYearStr.equals(YEARS[i])) {
					twoSeasonsBeginYear.setItemSelected(i+1, true);
					break;
				}
			}
			String endYearStr = aQuery.getTwoSeasEndYear().toString();
			for (int i = 0; i < YEARS.length; i++) {
				if (endYearStr.equals(YEARS[i])) {
					twoSeasonsEndYear.setItemSelected(i+1, true);
					break;
				}
			}
			
			twoSeasonsSummer.setChecked(aQuery.isTwoSeasonSummerSelected());
			twoSeasonsWinter.setChecked(aQuery.isTwoSeasonWinterSelected());
			
		} else { // four seasons
			dateTabPanel.selectTab(3);
			
			String beginYearStr = aQuery.getFourSeasBeginYear().toString();
			for (int i = 0; i < YEARS.length; i++) {
				if (beginYearStr.equals(YEARS[i])) {
					fourSeasonsBeginYear.setItemSelected(i+1, true);
					break;
				}
			}
			String endYearStr = aQuery.getFourSeasEndYear().toString();
			for (int i = 0; i < YEARS.length; i++) {
				if (endYearStr.equals(YEARS[i])) {
					fourSeasonsEndYear.setItemSelected(i+1, true);
					break;
				}
			}
			
			fourSeasonsSpring.setChecked(aQuery.isFourSeasonSpringSelected());
			fourSeasonsSummer.setChecked(aQuery.isFourSeasonSummerSelected());
			fourSeasonsFall.setChecked(aQuery.isFourSeasonFallSelected());
			fourSeasonsWinter.setChecked(aQuery.isFourSeasonWinterSelected());
		}
		
		// geog panel
		savedGeogQueryType = aQuery.getGeogQueryType();
		savedGranularity = aQuery.getGranularity();
		savedCoincidence = aQuery.getPoiCoincidence();
		
		if (GeogQueryType.GEOG_QUERY_TYPE_ENTIRE_SYSTEM.equals(savedGeogQueryType)) {
			geogTabPanel.selectTab(0);
		} else if (GeogQueryType.GEOG_QUERY_TYPE_REGION.equals(savedGeogQueryType)) {
			geogTabPanel.selectTab(1);
			
			// loop through all selected regions
			List selectedRegions = aQuery.getRegions();
			int rgIdx = 0;

			if (selectedRegions!=null) {
				for (int i = 0; i < selectedRegions.size(); i++) {
					String selectedRegion = (String)selectedRegions.get(i);
						
					// loop through the list of stored regions
					for (int j = rgIdx; j < regions.size(); j++) {
						String[] region = (String[])regions.get(j);
						if (region[1].equals(selectedRegion)) {
							regionListBox.setItemSelected(j, true);
							rgIdx = j;
							break;
						}
					}
				}
			}
		} else if (GeogQueryType.GEOG_QUERY_TYPE_AREA.equals(savedGeogQueryType)) {
			geogTabPanel.selectTab(2);

			DeferredCommand.addCommand(new Command() {
				public void execute() {

					selectedAreaListBox.clear();
					
					// loop through all selected planning areas
					List selectedAreas = aQuery.getPlanningAreas();

					if (selectedAreas!=null) {
						for (int i = 0; i < selectedAreas.size(); i++) {
							String selectedArea = (String)selectedAreas.get(i);
							selectedAreaListBox.addItem(selectedArea);
						}
					}

				}
			});
			
		} else if (GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.equals(savedGeogQueryType)) {
			geogTabPanel.selectTab(3);
			
			DeferredCommand.addCommand(new Command() {
				public void execute() {

					subSuggestBox.setText("");
					selectedSubListBox.clear();

					// loop through all selected substations
					List selectedSubs = aQuery.getSubstations();

					if (selectedSubs!=null) {
						for (int i = 0; i < selectedSubs.size(); i++) {
							String selectedSub = (String)selectedSubs.get(i);
							selectedSubListBox.addItem(selectedSub);
						}
					}
				}
			});
			
		} else if (GeogQueryType.GEOG_QUERY_TYPE_MP.equals(savedGeogQueryType)) {
			geogTabPanel.selectTab(4);

			DeferredCommand.addCommand(new Command() {
				public void execute() {

					mpSuggestBox.setText("");
					selectedMpListBox.clear();

					// loop through all selected mps
					List selectedMps = aQuery.getMeasurementPoints();
					
					if (selectedMps!=null) {
						for (int i = 0; i < selectedMps.size(); i++) {
							String selectedMp = (String)selectedMps.get(i);
							selectedMpListBox.addItem(selectedMp);
						}
					}
				}
			});
		} else if (GeogQueryType.GEOG_QUERY_TYPE_IMPORTS_EXPORTS.equals(savedGeogQueryType)) {
			geogTabPanel.selectTab(5);
		}

		// volumes panel
		for (int i = 0; i < granularities.size(); i++) {
			String[] gran = (String[])granularities.get(i);

			if (gran[1].equals(savedGranularity)) {
				granularityListBox.setItemSelected(i, true);
				break;
			}
		}
		
		
		categoryListBox.setItemSelected(0, true);
		volumesLoadCb.setChecked(aQuery.isLoadTypeSelected());
		volumesGenerationCb.setChecked(aQuery.isGenerationTypeSelected());

		
		// interest point panel
		String poiQueryType = aQuery.getPoiQueryType();

		if (PoiQueryType.POI_QUERY_TYPE_PERCENTILE.equals(poiQueryType)) {
			poiTabPanel.selectTab(0);

			// coincidence
			for (int i = 0; i < coincidences.size(); i++) {
				String[] coinc = (String[])coincidences.get(i);
				
				if (coinc[1].equals(savedCoincidence)) {
					poiCoincidenceListBox.setItemSelected(i, true);
					break;
				}
			}
			
			poiCategoryListBox.setItemSelected(0, true);

			poiLoadRb.setChecked(aQuery.isPoiLoadSelected());
			poiGenerationRb.setChecked(aQuery.isPoiGenerationSelected());

			String queryInterval = aQuery.getTimeInterval();
			for (int i = 0; i < timeIntervals.size(); i++) {
				String[] intervals = (String[])timeIntervals.get(i);
				if (intervals[1].equals(queryInterval)) {
					timeIntervalListBox.setItemSelected(i, true);
				}
			}
			
			poiPeakCb.setChecked(aQuery.isPoiPeakSelected());
			poiMedianCb.setChecked(aQuery.isPoiMedianSelected());
			poiLightCb.setChecked(aQuery.isPoiLightSelected());

		} else { // hourly
			poiTabPanel.selectTab(1);
		}	
		
	}

	/**
	 * Populate the drop-downs etc using the provided endpoint
	 * 
	 * @param serviceEndpoint
	 */
	public void populate(EvqServiceAsync serviceEndpoint) {

		this.serviceEndpoint = serviceEndpoint;

		loadDates();
		loadTimeIntervals();
		loadRegions();
		loadPlanningAreas();
		loadSubstations();
		loadMeasurementPoints();
		selectedGeogQueryType = GeogQueryType.GEOG_QUERY_TYPE_ENTIRE_SYSTEM.getValue();
		loadCategories();
		loadPoiCategories();
	}

	private VerticalPanel drawWidget() {

		VerticalPanel queryDetailPanel = new VerticalPanel();

		// name panel
		queryDetailPanel.add(drawNamePanel());

		// left panel
		VerticalPanel queryDetailLeftPanel = new VerticalPanel();
		queryDetailLeftPanel.setStyleName("queryDetailLeftPanel");
		queryDetailLeftPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		queryDetailLeftPanel.add(drawDatePanel());
		queryDetailLeftPanel.add(drawGeographicPanel());

		// right panel
		VerticalPanel queryDetailRightPanel = new VerticalPanel();
		queryDetailRightPanel.setStyleName("queryDetailRightPanel");
		queryDetailRightPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		queryDetailRightPanel.add(drawVolumesPanel());
		queryDetailRightPanel.add(drawPoiPanel());

		// left-right
		HorizontalPanel queryDetailLeftRightPanel = new HorizontalPanel();
		queryDetailLeftRightPanel.setStyleName("queryDetailLeftRightPanel");
		queryDetailLeftRightPanel.add(queryDetailLeftPanel);
		queryDetailLeftRightPanel.add(queryDetailRightPanel);

		queryDetailPanel.add(queryDetailLeftRightPanel);

		// buttons
		queryDetailPanel.add(drawButtonPanel());

		return queryDetailPanel;
	}

	private HorizontalPanel drawNamePanel() {

		HorizontalPanel namePanel = new HorizontalPanel();

		namePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		namePanel.setStyleName("namePanel");
		Label nameLabel = new Label("Enter a query name (optional):");
		nameLabel.setStyleName("boldlabel");
		namePanel.add(nameLabel);
		queryName.setStyleName("queryName");
		namePanel.add(queryName);

		return namePanel;
	}

	private VerticalPanel drawDatePanel() {

		VerticalPanel datePanel = new VerticalPanel();
		datePanel.setStyleName("datePanel");

		// label
		Label dateLabel = new Label("Select Date");
		dateLabel.setStyleName("titlelabel");
		datePanel.add(dateLabel);

		// tabs
		dateTabPanel.setStyleName("dateTabPanel");
		dateTabPanel.add(drawSpecificDateTable(), "Specific Dates");
		dateTabPanel.add(drawCalendarDateTable(), "Calendar Years");
		dateTabPanel.add(drawTwoSeasonsDateTable(), "Two Season Years");
		dateTabPanel.add(drawFourSeasonsDateTable(), "Four Season Years");
		dateTabPanel.addTabListener(dateTabClickListener);
		datePanel.add(dateTabPanel);
		
		return datePanel;
	}

	private FlexTable drawSpecificDateTable() {

		FlexTable specificDateTable = new FlexTable();
		specificDateTable.setStyleName("specificDateTable");

		Label dateFormatLabel = new Label("YYYY-MM-DD");
		dateFormatLabel.setStyleName("smallLabel");

		Label specificBeginDateLabel = new Label("Begin Date:");
		specificBeginDateLabel.setStyleName("label");

		Label specificEndDateLabel = new Label("End Date:");
		specificEndDateLabel.setStyleName("label");

		specificBeginDate.setWidth("100px");
		specificEndDate.setWidth("100px");

		specificBeginDateButton.addStyleName("dateButton");
		specificBeginDateButton.addClickListener(datePickerButtonClickListener);
		specificEndDateButton.addStyleName("dateButton");
		specificEndDateButton.addClickListener(datePickerButtonClickListener);

		CellFormatter cf = specificDateTable.getCellFormatter();
		cf.setWidth(0, 0, "5px");
		cf.setWidth(0, 1, "75px");
		cf.setWidth(0, 2, "100px");
		cf.setWidth(0, 3, "5px");
		cf.setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER);
		
		specificDateTable.setHTML(0, 0, SPACE);
		specificDateTable.setWidget(0, 1, specificBeginDateLabel);
		specificDateTable.setWidget(0, 2, specificBeginDate);
		specificDateTable.setWidget(0, 3, specificBeginDateButton);
		specificDateTable.setWidget(0, 4, dateFormatLabel);
		
		specificDateTable.setHTML(1, 0, SPACE);
		specificDateTable.setWidget(1, 1, specificEndDateLabel);
		specificDateTable.setWidget(1, 2, specificEndDate);
		specificDateTable.setWidget(1, 3, specificEndDateButton);
		specificDateTable.setHTML(1, 4, SPACE);

		beginDatePicker.showYearMonthListing(true);
		beginDatePicker.addChangeListener(beginDateChangeListener);
		endDatePicker.showYearMonthListing(true);
		endDatePicker.addChangeListener(endDateChangeListener);
		
		return specificDateTable;
	}

	private FlexTable drawCalendarDateTable() {

		FlexTable calendarDateTable = new FlexTable();
		calendarDateTable.setStyleName("calendarDateTable");
		
		Label calendarBeginLabel = new Label("Begin Year:");
		calendarBeginLabel.setStyleName("label");

		Label calendarEndLabel = new Label("End Year:");
		calendarEndLabel.setStyleName("label");

		CellFormatter cf = calendarDateTable.getCellFormatter();

		cf.setWidth(0, 0, "5px");
		cf.setWidth(0, 1, "70px");
		cf.setWidth(0, 2, "50px");
		cf.setWidth(0, 3, "50px");
		cf.setWidth(0, 4, "125px");

		calendarDateTable.setHTML(0, 0, SPACE);
		calendarDateTable.setWidget(0, 1, calendarBeginLabel);
		calendarDateTable.setWidget(0, 2, calendarBeginYear);
//		calendarDateTable.setWidget(0, 3, calendarBeginMonth);
		calendarDateTable.setHTML(0, 4, SPACE);

		calendarDateTable.setHTML(1, 0, SPACE);
		calendarDateTable.setWidget(1, 1, calendarEndLabel);
		calendarDateTable.setWidget(1, 2, calendarEndYear);
//		calendarDateTable.setWidget(1, 3, calendarEndMonth);
		calendarDateTable.setHTML(1, 4, SPACE);

		return calendarDateTable;
	}

	private FlexTable drawTwoSeasonsDateTable() {

		FlexTable twoSeasonsDateTable = new FlexTable();
		twoSeasonsDateTable.setStyleName("twoSeasonsDateTable");

		Label twoSeasonsBeginYearLabel = new Label("Begin Year:");
		twoSeasonsBeginYearLabel.setStyleName("label");

		Label twoSeasonsEndYearLabel = new Label("End Year:");
		twoSeasonsEndYearLabel.setStyleName("label");

		twoSeasonsBeginYear.setWidth("70px");
		twoSeasonsEndYear.setWidth("70px");

		twoSeasonsSummer.setStyleName("checkBox");
		twoSeasonsWinter.setStyleName("checkBox");

		CellFormatter cf = twoSeasonsDateTable.getCellFormatter();

		cf.setWidth(0, 0, "5px");
		cf.setWidth(0, 1, "70px");
		cf.setWidth(0, 2, "70px");
		cf.setWidth(0, 3, "70px");
		cf.setWidth(0, 4, "85px");

		twoSeasonsDateTable.setHTML(0, 0, SPACE);
		twoSeasonsDateTable.setWidget(0, 1, twoSeasonsBeginYearLabel);
		twoSeasonsDateTable.setWidget(0, 2, twoSeasonsBeginYear);
		twoSeasonsDateTable.setWidget(0, 3, twoSeasonsSummer);
		twoSeasonsDateTable.setHTML(0, 4, SPACE);
		
		twoSeasonsDateTable.setHTML(1, 0, SPACE);
		twoSeasonsDateTable.setWidget(1, 1, twoSeasonsEndYearLabel);
		twoSeasonsDateTable.setWidget(1, 2, twoSeasonsEndYear);
		twoSeasonsDateTable.setWidget(1, 3, twoSeasonsWinter);
		twoSeasonsDateTable.setHTML(1, 4, SPACE);

		return twoSeasonsDateTable;
	}

	private FlexTable drawFourSeasonsDateTable() {

		// four season date panel
		FlexTable fourSeasonsDateTable = new FlexTable();
		fourSeasonsDateTable.setStyleName("fourSeasonsDateTable");

		Label fourSeasonsBeginYearLabel = new Label("Begin Year:");
		fourSeasonsBeginYearLabel.setStyleName("label");

		Label fourSeasonsEndYearLabel = new Label("End Year:");
		fourSeasonsEndYearLabel.setStyleName("label");

		fourSeasonsBeginYear.setWidth("70px");
		fourSeasonsEndYear.setWidth("70px");
		fourSeasonsSpring.setStyleName("checkBox");
		fourSeasonsSummer.setStyleName("checkBox");
		fourSeasonsFall.setStyleName("checkBox");
		fourSeasonsWinter.setStyleName("checkBox");

		CellFormatter cf = fourSeasonsDateTable.getCellFormatter();

		cf.setWidth(0, 0, "5px");
		cf.setWidth(0, 1, "70px"); 
		cf.setWidth(0, 2, "70px");
		cf.setWidth(0, 3, "70px");
		cf.setWidth(0, 4, "70px");
		cf.setWidth(0, 5, "15px");

		fourSeasonsDateTable.setHTML(0, 0, SPACE);
		fourSeasonsDateTable.setWidget(0, 1, fourSeasonsBeginYearLabel);
		fourSeasonsDateTable.setWidget(0, 2, fourSeasonsBeginYear);
		fourSeasonsDateTable.setWidget(0, 3, fourSeasonsSpring);
		fourSeasonsDateTable.setWidget(0, 4, fourSeasonsFall);
		fourSeasonsDateTable.setHTML(0, 5, SPACE);

		fourSeasonsDateTable.setHTML(1, 0, SPACE);
		fourSeasonsDateTable.setWidget(1, 1, fourSeasonsEndYearLabel);
		fourSeasonsDateTable.setWidget(1, 2, fourSeasonsEndYear);
		fourSeasonsDateTable.setWidget(1, 3, fourSeasonsSummer);
		fourSeasonsDateTable.setWidget(1, 4, fourSeasonsWinter);
		fourSeasonsDateTable.setHTML(1, 5, SPACE);

		return fourSeasonsDateTable;
	}

	private VerticalPanel drawGeographicPanel() {

		VerticalPanel geogPanel = new VerticalPanel();
		geogPanel.setStyleName("geogPanel");
		geogPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

		// add label
		Label geogLabel = new Label("Select Geographic Area");
		geogLabel.setStyleName("titlelabel");

		geogPanel.add(geogLabel);

		// add tabs
		geogTabPanel.setStyleName("geogTabPanel");

		// entire system
		geogTabPanel.add(drawEntireSystemTable(), "Entire System");
		geogTabPanel.add(drawRegionTable(), "Region");
		geogTabPanel.add(drawAreaTable(), "Planning Area");
		geogTabPanel.add(drawSubstationTable(), "Substation");
		geogTabPanel.add(drawMpTable(), "Meas Point");
		geogTabPanel.add(drawImportExportTable(), "Import/Export");

		// add tab listener
		geogTabPanel.addTabListener(geogTabClickListener);

		geogPanel.add(geogTabPanel);

		return geogPanel;
	}

	private FlexTable drawEntireSystemTable() {
		FlexTable entireSystemGeogTable = new FlexTable();
		entireSystemGeogTable.setStyleName("entireSystemGeogTable");

		CellFormatter sdpfe = entireSystemGeogTable.getCellFormatter();
		sdpfe.setWidth(0, 0, "5px");

//		Label entireSystemLabel = new Label("Entire System will be queried for selected load");
		Label entireSystemLabel = new Label("ENTIRE SYSTEM QUERY NOT AVAILABLE");
		entireSystemLabel.setStyleName("label");

		entireSystemGeogTable.setHTML(0, 0, SPACE);
		entireSystemGeogTable.setWidget(0, 1, entireSystemLabel);
		return entireSystemGeogTable;
	}

	private FlexTable drawRegionTable() {
		
		FlexTable regionGeogTable = new FlexTable();
		regionGeogTable.setStyleName("regionGeogTable");
		
		CellFormatter cf = regionGeogTable.getCellFormatter();
		cf.setWidth(0, 0, "5px");
		cf.setAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_BOTTOM);
		cf.setAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);

		Label regionLabel = new Label("REGION QUERY CURRENTLY NOT AVAILABLE");
//		Label regionLabel = new Label("Select one or more regions:");
		regionLabel.setStyleName("label");

		regionListBox.setMultipleSelect(true);
		regionListBox.setVisibleItemCount(9);

		regionGeogTable.setHTML(0, 0, SPACE);
		regionGeogTable.setWidget(0, 1, regionLabel);
		regionGeogTable.setWidget(1, 1, regionListBox);
		
		return regionGeogTable;
	}

	private FlexTable drawAreaTable() {
		
		FlexTable areaGeogTable = new FlexTable();
		areaGeogTable.setStyleName("areaGeogTable");
		
		FlexCellFormatter cf = areaGeogTable.getFlexCellFormatter();
		cf.setWidth(0, 0, "5px");
		cf.setAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_BOTTOM);
		cf.setAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
		cf.setAlignment(1, 3, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
		cf.setAlignment(2, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);

		areaGeogTable.setHTML(0, 0, SPACE);

		Label areaLabel = new Label("Select one or more planning areas:");
		areaLabel.setStyleName("label");

		areaGeogTable.setWidget(0, 1, areaLabel);

		Label selectedAreaLabel = new Label("Currently Selected:");
		selectedAreaLabel.setStyleName("label");
		areaGeogTable.setWidget(0, 3, selectedAreaLabel);

		selectedAreaListBox.setMultipleSelect(true);
		selectedAreaListBox.setVisibleItemCount(12);
		selectedAreaListBox.setWidth("120px");
		
		cf.setRowSpan(1, 3, 5);
		areaGeogTable.setWidget(1, 3, selectedAreaListBox);
		
		planningAreaListBox.setMultipleSelect(true);
		planningAreaListBox.setVisibleItemCount(12);

		cf.setRowSpan(1, 1, 5);
		areaGeogTable.setWidget(1, 1, planningAreaListBox);
		
		
		addAreaButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				for (int i = 0; i < planningAreaListBox.getItemCount(); i++) {
					if (planningAreaListBox.isItemSelected(i)) {
						selectedAreaListBox.addItem(planningAreaListBox.getItemText(i).substring((planningAreaListBox.getItemText(i).indexOf("(") + 1), (planningAreaListBox.getItemText(i).indexOf(")"))));
					}
				}
			}
		});

		removeAreaButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				for (int i = 0; i < selectedAreaListBox.getItemCount(); i++) {
					if (selectedAreaListBox.isItemSelected(i)) {
						selectedAreaListBox.removeItem(i);
						i--;
					}
				}
			}
		});
		
		areaGeogTable.setWidget(1, 2, addAreaButton);
		areaGeogTable.setWidget(2, 1, removeAreaButton);
		
		return areaGeogTable;
	}
	
	private FlexTable drawSubstationTable() {
		
		FlexTable substationGeogTable = new FlexTable();
		substationGeogTable.setStyleName("substationGeogTable");
		
		FlexCellFormatter cf = substationGeogTable.getFlexCellFormatter();
		cf.setWidth(0, 0, "5px");
		cf.setAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_BOTTOM);
		cf.setAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
		cf.setAlignment(2, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
		cf.setAlignment(3, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
		cf.setAlignment(4, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);

		
		Label areaFilterLabel = new Label("Select area filter (optional):");
		areaFilterLabel.setStyleName("label");
		
		substationGeogTable.setHTML(0, 0, SPACE);
		substationGeogTable.setWidget(0, 1, areaFilterLabel);

		Label selectedSubLabel = new Label("Currently Selected:");
		selectedSubLabel.setStyleName("label");
		substationGeogTable.setWidget(0, 3, selectedSubLabel);
		
		selectedSubListBox.setMultipleSelect(true);
		selectedSubListBox.setVisibleItemCount(15);
		selectedSubListBox.setWidth("120px");
		
		cf.setRowSpan(1, 3, 7);
		substationGeogTable.setWidget(1, 3, selectedSubListBox);

		areaSubListBox.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				String fullAreaStr = areaSubListBox.getItemText(areaSubListBox.getSelectedIndex());
				if (fullAreaStr!=null && fullAreaStr.length()>0) {
					String area = fullAreaStr.substring(fullAreaStr.indexOf("("), fullAreaStr.indexOf(")")+1);
					subSuggestBox.setFilter(area);
				} else {
					subSuggestBox.setFilter(null);
				}
				subSuggestBox.setFocus(true);
			}
		});
		
		areaSubListBox.setStyleName("listbox");
		areaSubListBox.setWidth("250px");
		substationGeogTable.setWidget(1, 1, areaSubListBox);

		Label mpLabel = new Label("Search for substations:");
		mpLabel.setStyleName("label");
		substationGeogTable.setWidget(2, 1, mpLabel);

		subSuggestBox.setWidth("120px");
		substationGeogTable.setWidget(3, 1, subSuggestBox);

		subListBox.setMultipleSelect(true);
		subListBox.setVisibleItemCount(10);
		subListBox.setWidth("275px");
		
		cf.setRowSpan(4, 1, 3);
		substationGeogTable.setWidget(4, 1, subListBox);
		
		addSubButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				for (int i = 0; i < subListBox.getItemCount(); i++) {
					if (subListBox.isItemSelected(i)) {
						selectedSubListBox.addItem(subListBox.getItemText(i).substring(0, (subListBox.getItemText(i).indexOf(" -"))));
					}
				}
			}
		});

		removeSubButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				for (int i = 0; i < selectedSubListBox.getItemCount(); i++) {
					if (selectedSubListBox.isItemSelected(i)) {
						selectedSubListBox.removeItem(i);
						i--;
					}
				}
			}
		});
		
		substationGeogTable.setWidget(4, 2, addSubButton);
		substationGeogTable.setWidget(5, 1, removeSubButton);

		
		return substationGeogTable;
	}

	private FlexTable drawMpTable() {
		
		FlexTable mpGeogTable = new FlexTable();
		mpGeogTable.setStyleName("mpGeogTable");
		
		FlexCellFormatter cf = mpGeogTable.getFlexCellFormatter();
		cf.setWidth(0, 0, "5px");
		cf.setAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_BOTTOM);
		cf.setAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
		cf.setAlignment(2, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
		cf.setAlignment(3, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
		cf.setAlignment(4, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);

		Label areaFilterLabel = new Label("Select area filter (optional):");
		areaFilterLabel.setStyleName("label");
		
		mpGeogTable.setHTML(0, 0, SPACE);
		mpGeogTable.setWidget(0, 1, areaFilterLabel);

		Label selectedMpLabel = new Label("Currently Selected:");
		selectedMpLabel.setStyleName("label");
		mpGeogTable.setWidget(0, 3, selectedMpLabel);
		
		selectedMpListBox.setMultipleSelect(true);
		selectedMpListBox.setVisibleItemCount(15);
		selectedMpListBox.setWidth("120px");
		
		cf.setRowSpan(1, 3, 7);
		mpGeogTable.setWidget(1, 3, selectedMpListBox);
		
		areaMpListBox.addChangeListener(new ChangeListener() {
			public void onChange(Widget sender) {
				String fullAreaStr = areaMpListBox.getItemText(areaMpListBox.getSelectedIndex());
				if (fullAreaStr!=null && fullAreaStr.length()>0) {
					String area = fullAreaStr.substring(fullAreaStr.indexOf("("), fullAreaStr.indexOf(")")+1);
					mpSuggestBox.setFilter(area);
				} else {
					mpSuggestBox.setFilter(null);
				}
				mpSuggestBox.setFocus(true);
			}
		});
		
		areaMpListBox.setStyleName("listbox");
		areaMpListBox.setWidth("250px");
		mpGeogTable.setWidget(1, 1, areaMpListBox);

		Label mpLabel = new Label("Search for measurement points:");
		mpLabel.setStyleName("label");
		mpGeogTable.setWidget(2, 1, mpLabel);

		mpSuggestBox.setWidth("120px");
		mpGeogTable.setWidget(3, 1, mpSuggestBox);

		mpListBox.setMultipleSelect(true);
		mpListBox.setVisibleItemCount(10);
		mpListBox.setWidth("275px");
		
		cf.setRowSpan(4, 1, 3);
		mpGeogTable.setWidget(4, 1, mpListBox);
		
		addMpButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				for (int i = 0; i < mpListBox.getItemCount(); i++) {
					if (mpListBox.isItemSelected(i)) {
						selectedMpListBox.addItem(mpListBox.getItemText(i).substring(0, (mpListBox.getItemText(i).indexOf("(")-1) ));
					}
					
				}
			}
		});

		removeMpButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				for (int i = 0; i < selectedMpListBox.getItemCount(); i++) {
					if (selectedMpListBox.isItemSelected(i)) {
						selectedMpListBox.removeItem(i);
						i--;
					}
				}
			}
		});
		
		mpGeogTable.setWidget(4, 2, addMpButton);
		mpGeogTable.setWidget(5, 1, removeMpButton);

		
		return mpGeogTable;
	}

	private FlexTable drawImportExportTable() {
		FlexTable importExportGeogTable = new FlexTable();
		importExportGeogTable.setStyleName("importExportGeogTable");
		CellFormatter sdpfi = importExportGeogTable.getCellFormatter();
		sdpfi.setWidth(0, 0, "5px");
		sdpfi.setWidth(0, 1, "300px");
		sdpfi.setAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);

//		Label impExpLabel = new Label("Query will only select import and export values");
		Label impExpLabel = new Label("IMPORT/EXPORT QUERY NOT AVAILABLE");
		impExpLabel.setStyleName("label");

		importExportGeogTable.setHTML(0, 0, SPACE);
		importExportGeogTable.setWidget(0, 1, impExpLabel);
		return importExportGeogTable;
	}

	private VerticalPanel drawVolumesPanel() {

		VerticalPanel volumesPanel = new VerticalPanel();
		VerticalPanel volumesSubPanel = new VerticalPanel();
		volumesPanel.setStyleName("volumesPanel");
		volumesSubPanel.setStyleName("volumesSubPanel");

		// label
		Label loadsLabel = new Label("Select Volume");
		loadsLabel.setStyleName("titlelabel");

		volumesSubPanel.add(loadsLabel);

		FlexTable volumesTable = new FlexTable();
		volumesTable.setStyleName("volumesTable");
		volumesTable.setCellPadding(0);
		volumesTable.setCellSpacing(0);
		
		CellFormatter cf = volumesTable.getCellFormatter();
		cf.setWidth(0, 0, "5px");
		cf.setWidth(0, 1, "150px");

		Label granularityLabel = new Label("Granularity:");
		granularityLabel.setStyleName("label");

		Label categoryLabel = new Label("Category:");
		categoryLabel.setStyleName("label");

		Label typeLabel = new Label("Type:");
		typeLabel.setStyleName("label");
		
		granularityListBox.setWidth("150px");
		categoryListBox.setWidth("150px");

		granularityListBox.addChangeListener(granularityChangeListener);
		
		volumesLoadCb.setStyleName("checkBox");
		volumesGenerationCb.setStyleName("checkBox");

		volumesTable.setHTML(0, 0, SPACE);
		volumesTable.setWidget(0, 1, granularityLabel);
		volumesTable.setWidget(0, 2, granularityListBox);
		volumesTable.setHTML(0, 3, SPACE);
		volumesTable.setHTML(0, 4, SPACE);

		volumesTable.setHTML(1, 0, SPACE);
		volumesTable.setWidget(1, 1, categoryLabel);
		volumesTable.setWidget(1, 2, categoryListBox);
		volumesTable.setHTML(1, 3, SPACE);
		volumesTable.setHTML(1, 4, SPACE);

		volumesTable.setHTML(2, 0, SPACE);
		volumesTable.setWidget(2, 1, typeLabel);
		
		HorizontalPanel volCb = new HorizontalPanel();
		volCb.add(volumesLoadCb);
		volCb.add(volumesGenerationCb);
		
		FlexCellFormatter fcf = volumesTable.getFlexCellFormatter();
		fcf.setColSpan(2, 2, 2);
		volumesTable.setWidget(2, 2, volCb);

		volumesTable.setHTML(2, 4, SPACE);
		
		volumesSubPanel.add(volumesTable);

		volumesPanel.add(volumesSubPanel);

		return volumesPanel;
	}

	private VerticalPanel drawPoiPanel() {

		VerticalPanel poiPanel = new VerticalPanel();
		poiPanel.setStyleName("poiPanel");

		// add label
		Label poiLabel = new Label("Select Interest Point");
		poiLabel.setStyleName("titlelabel");

		poiPanel.add(poiLabel);

		// add tabs
		poiTabPanel.setStyleName("poiTabPanel");
		poiTabPanel.add(drawPoiPercentileTable(), "Interest Point");
		poiTabPanel.add(drawPoiHourlyTable(), "Hourly");

		// add tab listener
		poiTabPanel.addTabListener(poiTabClickListener);

		poiPanel.add(poiTabPanel);

		return poiPanel;
	}

	private FlexTable drawPoiPercentileTable() {
		
		FlexTable poiPercentileTable = new FlexTable();
		poiPercentileTable.setStyleName("poiPercentileTable");
		poiPercentileTable.setCellPadding(0);
		poiPercentileTable.setCellSpacing(0);
		
		CellFormatter cf = poiPercentileTable.getCellFormatter();
		cf.setWidth(0, 0, "5px");
		cf.setWidth(0, 1, "156px"); 
		cf.setWidth(0, 2, "150px"); 
		
		Label poiCoincLabel = new Label("Coincident with:");
		poiCoincLabel.setStyleName("label");

		Label poiCategoryLabel = new Label("Category:");
		poiCategoryLabel.setStyleName("label");

		Label poiTypeLabel = new Label("Type:");
		poiTypeLabel.setStyleName("label");
		
		Label intervalLabel = new Label("Time Interval:");
		intervalLabel.setStyleName("label");

		final Label medianPerformanceLabel = new Label("NOTE: query duration may increase substantially for MEDIAN point");
		medianPerformanceLabel.setStyleName("smalllabel");
		medianPerformanceLabel.setVisible(false);
		
		poiCoincidenceListBox.addChangeListener(coincidenceChangeListener);
		
		poiCoincidenceListBox.setWidth("150px");
		poiCategoryListBox.setWidth("150px");
		timeIntervalListBox.setWidth("150px");
		
		poiCoincidenceListBox.setStyleName("listbox");
		poiCategoryListBox.setStyleName("listbox");
		timeIntervalListBox.setStyleName("listbox");

		poiPeakCb.setStyleName("checkBox");
		poiMedianCb.setStyleName("checkBox");
		
		poiMedianCb.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				if (poiMedianCb.isChecked()) {
					medianPerformanceLabel.setVisible(true);
				} else {
					medianPerformanceLabel.setVisible(false);
				}
			}
		});
		
		poiLightCb.setStyleName("checkBox");

		poiLoadRb.setStyleName("radioButton");
		poiGenerationRb.setStyleName("radioButton");

		// row 0
		poiPercentileTable.setHTML(0, 0, SPACE);
		poiPercentileTable.setWidget(0, 1, poiCoincLabel);
		poiPercentileTable.setWidget(0, 2, poiCoincidenceListBox);
		poiPercentileTable.setHTML(0, 3, SPACE);
		poiPercentileTable.setHTML(0, 4, SPACE);

		// row 1
		poiPercentileTable.setHTML(1, 0, SPACE);
		poiPercentileTable.setWidget(1, 1, poiCategoryLabel);
		poiPercentileTable.setWidget(1, 2, poiCategoryListBox);
		poiPercentileTable.setHTML(1, 3, SPACE);
		poiPercentileTable.setHTML(1, 4, SPACE);

		// row 2
		poiPercentileTable.setHTML(2, 0, SPACE);
		poiPercentileTable.setWidget(2, 1, poiTypeLabel);

		HorizontalPanel poiRb = new HorizontalPanel();
		poiRb.add(poiLoadRb);
		poiRb.add(poiGenerationRb);
		
		FlexCellFormatter fcf = poiPercentileTable.getFlexCellFormatter();
		fcf.setColSpan(2, 2, 2);
		poiPercentileTable.setWidget(2, 2, poiRb);

		poiPercentileTable.setHTML(2, 4, SPACE);

		// row 3
		poiPercentileTable.setHTML(3, 0, SPACE);
		poiPercentileTable.setWidget(3, 1, intervalLabel);
		poiPercentileTable.setWidget(3, 2, timeIntervalListBox);
		poiPercentileTable.setHTML(3, 3, SPACE);
		poiPercentileTable.setHTML(3, 4, SPACE);
		
		// row 4
		poiPercentileTable.setHTML(4, 0, SPACE);
		poiPercentileTable.setHTML(4, 1, SPACE);
		
		HorizontalPanel poiCb = new HorizontalPanel();
		poiCb.add(poiPeakCb);
		poiCb.add(poiMedianCb);
		poiCb.add(poiLightCb);
		
		fcf.setColSpan(4, 2, 2);
		poiPercentileTable.setWidget(4, 2, poiCb);
		poiPercentileTable.setHTML(4, 4, SPACE);

		// row 5
		fcf.setColSpan(5, 1, 4);
		poiPercentileTable.setHTML(5, 0, SPACE);
		poiPercentileTable.setWidget(5, 1, medianPerformanceLabel);
		
		return poiPercentileTable;
	}

	private FlexTable drawPoiHourlyTable() {
		FlexTable poiHourlyTable = new FlexTable();
		poiHourlyTable.setStyleName("poiHourlyTable");
		
		poiHourlyTable.setCellPadding(0);
		poiHourlyTable.setCellSpacing(0);
		CellFormatter cf = poiHourlyTable.getCellFormatter();
		cf.setWidth(0, 0, "5px");
		
		Label poiHourlyLabel = new Label("Query will return hourly values");
		poiHourlyLabel.setStyleName("label");

		poiHourlyTable.setHTML(0, 0, SPACE);
		poiHourlyTable.setWidget(0, 1, poiHourlyLabel);
		return poiHourlyTable;
	}

	private HorizontalPanel drawButtonPanel() {

		HorizontalPanel buttonPanel = new HorizontalPanel();

		resetButton.addClickListener(formButtonClickListener);
		submitButton.addClickListener(formButtonClickListener);

		FlexTable buttonTable = new FlexTable();
		buttonTable.setStyleName("buttonTable");

		CellFormatter cf = buttonTable.getCellFormatter();
		cf.setWidth(0, 0, "50px");
		cf.setWidth(0, 1, "50px");

		buttonTable.setWidget(0, 0, resetButton);
		buttonTable.setWidget(0, 1, submitButton);

		buttonPanel.add(buttonTable);
		buttonPanel.setCellHorizontalAlignment(buttonTable, HasHorizontalAlignment.ALIGN_CENTER);
		buttonPanel.setStyleName("buttonPanel");

		return buttonPanel;
	}

	private void resetDatePanel() {
		specificBeginDate.setText("");
		specificBeginDateButton.setEnabled(false);
		specificEndDate.setText("");
		specificEndDateButton.setEnabled(false);
		calendarBeginYear.setItemSelected(0, true);
		calendarBeginYear.setEnabled(false);
		calendarBeginMonth.setEnabled(false);
		calendarBeginMonth.setItemSelected(0, true);
		calendarEndYear.setEnabled(false);
		calendarEndYear.setItemSelected(0, true);
		calendarEndMonth.setEnabled(false);
		calendarEndMonth.setItemSelected(0, true);
		twoSeasonsBeginYear.setEnabled(false);
		twoSeasonsBeginYear.setItemSelected(0, true);
		twoSeasonsEndYear.setEnabled(false);
		twoSeasonsEndYear.setItemSelected(0, true);
		twoSeasonsSummer.setChecked(false);
		twoSeasonsSummer.setEnabled(false);
		twoSeasonsWinter.setChecked(false);
		twoSeasonsWinter.setEnabled(false);
		fourSeasonsBeginYear.setEnabled(false);
		fourSeasonsBeginYear.setItemSelected(0, true);
		fourSeasonsEndYear.setEnabled(false);
		fourSeasonsEndYear.setItemSelected(0, true);
		fourSeasonsSpring.setChecked(false);
		fourSeasonsSpring.setEnabled(false);
		fourSeasonsSummer.setChecked(false);
		fourSeasonsSummer.setEnabled(false);
		fourSeasonsFall.setChecked(false);
		fourSeasonsFall.setEnabled(false);
		fourSeasonsWinter.setChecked(false);
		fourSeasonsWinter.setEnabled(false);
	}

	private void resetGeogPanel() {
		regionListBox.setSelectedIndex(-1);
		planningAreaListBox.setSelectedIndex(-1);
		selectedAreaListBox.clear();
		areaSubListBox.setSelectedIndex(0);
		subSuggestBox.setText("");
		subSuggestBox.setFilter(null);
		selectedSubListBox.clear();
		areaMpListBox.setSelectedIndex(0);
		mpSuggestBox.setText("");
		mpSuggestBox.setFilter(null);
		selectedMpListBox.clear();
	}

	private void resetVolumesPanel() {
		volumesLoadCb.setChecked(false);
		volumesGenerationCb.setChecked(false);
		volumesLoadCb.setEnabled(true);
		volumesLoadCb.setVisible(true);
		volumesGenerationCb.setEnabled(true);
		volumesGenerationCb.setVisible(true);
	}

	private void loadDates() {
		GWT.log("loadDates()", null);
		calendarBeginYear.addItem("");
		calendarBeginMonth.addItem("");
		calendarEndYear.addItem("");
		calendarEndMonth.addItem("");
		twoSeasonsBeginYear.addItem("");
		twoSeasonsEndYear.addItem("");
		fourSeasonsBeginYear.addItem("");
		fourSeasonsEndYear.addItem("");

		for (int i = 0; i < YEARS.length; i++) {
			calendarBeginYear.addItem(YEARS[i]);
			calendarEndYear.addItem(YEARS[i]);
			twoSeasonsBeginYear.addItem(YEARS[i]);
			twoSeasonsEndYear.addItem(YEARS[i]);
			fourSeasonsBeginYear.addItem(YEARS[i]);
			fourSeasonsEndYear.addItem(YEARS[i]);
		}

		for (int j = 0; j < MONTHS.length; j++) {
			calendarBeginMonth.addItem(MONTHS[j]);
			calendarEndMonth.addItem(MONTHS[j]);
		}
	}

	private void loadTimeIntervals() {
		GWT.log("loadTimeIntervals()", null);

		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object result) {
				timeIntervals = (List) result;
				if (timeIntervals != null) {
					for (Iterator i = timeIntervals.iterator(); i.hasNext();) {
						String s[] = (String[]) i.next();
						timeIntervalListBox.addItem(s[0]);
					}
				}
			}

			public void onFailure(Throwable caught) {
				timeIntervals = null;
			}
		};

		serviceEndpoint.getTimeIntervals(callback);
	}

	private void loadRegions() {
		GWT.log("loadRegions()", null);

		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object result) {
				regions = (List) result;
				if (regions != null) {
					for (Iterator i = regions.iterator(); i.hasNext();) {
						String s[] = (String[]) i.next();
						regionListBox.addItem(s[0]);
					}
				}
			}

			public void onFailure(Throwable caught) {
				regions = null;
			}
		};

		serviceEndpoint.getRegions(callback);
	}

	private void loadPlanningAreas() {
		GWT.log("loadPlanningAreas()", null);

		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object result) {
				planningAreas = (List) result;
				if (planningAreas != null) {
					areaSubListBox.addItem("");
					areaMpListBox.addItem("");
					for (Iterator i = planningAreas.iterator(); i.hasNext();) {
						String s[] = (String[]) i.next();
						planningAreaListBox.addItem(s[0]);
						areaSubListBox.addItem(s[0]);
						areaMpListBox.addItem(s[0]);
					}
				}
			}

			public void onFailure(Throwable caught) {
				planningAreas = null;
			}
		};

		serviceEndpoint.getPlanningAreas(callback);
	}

	private void loadSubstations() {
		GWT.log("loadSubstations()", null);

		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object result) {
				substations = (List) result;
				if (substations != null) {
					for (Iterator i = substations.iterator(); i.hasNext();) {
						String s[] = (String[]) i.next();
						subSuggestOracle.add(s[0]);
					}
				}
			}

			public void onFailure(Throwable caught) {
				substations = null;
			}
		};

		serviceEndpoint.getSubstations(callback);
	}

	private void loadMeasurementPoints() {
		GWT.log("loadMeasurementPoints()", null);

		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object result) {
				measurementPoints = (List) result;
				if (measurementPoints != null) {
					for (Iterator i = measurementPoints.iterator(); i.hasNext();) {
						String s[] = (String[]) i.next();
						mpSuggestOracle.add(s[0]);
					}
				}
			}

			public void onFailure(Throwable caught) {
				measurementPoints = null;
			}
		};

		serviceEndpoint.getMeasurementPoints(callback);
	}

	private void loadGranularities() {
		GWT.log("loadGranularities()", null);

		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object result) {
				granularities = (List) result;
				
				if (granularities != null) {
					granularityListBox.clear();

					for (int i = 0; i < granularities.size(); i++) {
						String[] gran = (String[])granularities.get(i);
						granularityListBox.addItem(gran[0]);
						
						if (savedGranularity!=null && savedGranularity.equals(gran[1])) {
							granularityListBox.setSelectedIndex(i);
						}
					}
					
					if (savedGranularity==null) {
						loadCoincidences(((String[])granularities.get(0))[1]);
					} else {
						loadCoincidences(savedGranularity);
						savedGranularity=null; // used, so throw it away now 
					}
				} 
			}

			public void onFailure(Throwable caught) {
				granularities = null;
			}
		};

		serviceEndpoint.getGranularities(selectedGeogQueryType, callback);
	}

	private void loadCategories() {
		GWT.log("loadCategories()", null);

		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object result) {
				categories = (List) result;
				if (categories != null) {
					categoryListBox.clear();
					for (Iterator i = categories.iterator(); i.hasNext();) {
						String s[] = (String[]) i.next();
						categoryListBox.addItem(s[0]);
					}
				}
			}

			public void onFailure(Throwable caught) {
				categories = null;
			}
		};

		serviceEndpoint.getCategories(callback);
	}

	private void loadCoincidences(final String granularity) {
		GWT.log("loadCoincidences()", null);

		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object result) {
				coincidences = (List) result;
				if (coincidences != null) {
					poiCoincidenceListBox.clear();

					for (int i = 0; i < coincidences.size(); i++) {
						String[] coinc = (String[])coincidences.get(i);
						poiCoincidenceListBox.addItem(coinc[0]);
						if (savedCoincidence!=null && savedCoincidence.equals(coinc[1])) {
							poiCoincidenceListBox.setSelectedIndex(i);
							savedCoincidence=null;
						}
					}
				}
				DeferredCommand.addCommand(new Command() {
					public void execute() {
						coincidenceChangeListener.onChange(null);
					}
				});
				
			}

			public void onFailure(Throwable caught) {
				coincidences = null;
			}
		};

		serviceEndpoint.getCoincidences(selectedGeogQueryType, granularity, callback);
	}

	private void loadPoiCategories() {
		GWT.log("loadPoiCategories()", null);

		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object result) {
				poiCategories = (List) result;
				if (poiCategories != null) {
					poiCategoryListBox.clear();
					for (Iterator i = poiCategories.iterator(); i.hasNext();) {
						String s[] = (String[]) i.next();
						poiCategoryListBox.addItem(s[0]);
					}
				}
			}

			public void onFailure(Throwable caught) {
				poiCategories = null;
			}
		};

		serviceEndpoint.getPoiCategories(callback);
	}

	public void loadQuery(long queryId) {
		GWT.log("loadQuery()", null);

		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object result) {
				displayQuery((Query)result);
			}

			public void onFailure(Throwable caught) {
			}
		};

		serviceEndpoint.getQuery(queryId, callback);
	}
	
	/**
	 * Submit the query for processing
	 */
	private void submit() {

		GWT.log("submit() starting", null);

		// validate the entry
		Query newQuery;
		
		try {
			newQuery = assembleQuery();
		} catch (QueryValidationException ve) {
			Window.alert(ve.getMessage());
			return;
		}

		AsyncCallback callback = new AsyncCallback() {
			public void onSuccess(Object result) {
				ElectricalVolumesQuery.get().reset();
			}

			public void onFailure(Throwable caught) {
				GWT.log("submit() failed!", caught);
				ElectricalVolumesQuery.get().reset();
			}
		};

		serviceEndpoint.submitQuery(newQuery, callback);
		
		Window.alert("Query submitted successfully. Please allow sufficient processing time");

		reset();
		ElectricalVolumesQuery.get().reset();
	}

	/**
	 * Collect the UI parameters, validate them and return a new Query object
	 * 
	 * @return a new validated Query object
	 * @throws QueryValidationException containing any validation errors
	 */
	private Query assembleQuery() throws QueryValidationException {

		List errors = new ArrayList();
		Query newQuery = new Query();
		Date specBegin = null;
		Date specEnd = null;

		newQuery.setQueryName(queryName.getText());

		// get date parms
		int selectedDateTab = dateTabPanel.getTabBar().getSelectedTab();

		switch (selectedDateTab) {

		case 0: // specific
			newQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_SPECIFIC.getValue());

			String specBeginStr = specificBeginDate.getText();
			specBegin = parseDate(specBeginStr);
			if (specBegin!=null) 
				newQuery.setBeginDate(specBeginStr);
			else
				errors.add("Not a valid specific begin date.");

			String specEndStr = specificEndDate.getText();
			specEnd = parseDate(specEndStr);
			if (specEnd!=null)
				newQuery.setEndDate(specEndStr);
			else
				errors.add("Not a valid specific end date.");

			if (specEnd != null && specBegin != null) {
				if (specEnd.compareTo(specBegin) < 0) {
					errors.add("Specific end date must not be earlier than specific begin date.");
				}
			}

			break;

		case 1: // calendar
			newQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_CALENDAR.getValue());

			Integer beginYearInt = new Integer(0);
			Integer endYearInt = new Integer(0);

			String beginYearStr = calendarBeginYear.getItemText(calendarBeginYear.getSelectedIndex());
			if (beginYearStr == null || beginYearStr.length() == 0) {
				errors.add("Begin year is required.");
			} else {
				beginYearInt = new Integer(beginYearStr);
				newQuery.setCalBeginYear(beginYearInt);
			}

			String endYearStr = calendarEndYear.getItemText(calendarEndYear.getSelectedIndex());
			if (endYearStr == null || endYearStr.length() == 0) {
				errors.add("End year is required.");
			} else {
				endYearInt = new Integer(endYearStr);
				newQuery.setCalEndYear(endYearInt);
			}

			// calendar months removed for now
			
//			int beginMonth = calendarBeginMonth.getSelectedIndex();
//			if (beginMonth == 0) {
//				errors.add("Begin month is required.");
//			} else {
//				newQuery.setCalBeginMonth(new Integer(beginMonth));
//			}
//
//			int endMonth = calendarEndMonth.getSelectedIndex();
//			if (endMonth == 0) {
//				errors.add("End month is required.");
//			} else {
//				newQuery.setCalEndMonth(new Integer(endMonth));
//			}
//
//			if (beginYearInt.intValue() > 0 && endYearInt.intValue() > 0 && beginMonth > 0 && endMonth > 0) {
//				if ((endYearInt.intValue() < beginYearInt.intValue())
//						|| ((endYearInt.intValue() == beginYearInt.intValue()) && (endMonth < beginMonth))) {
//					errors.add("Calendar end year/month must not be earlier than calendar begin year/month.");
//				}
//			}

			if (endYearInt.intValue() < beginYearInt.intValue()) 
				errors.add("Calendar end year must not be earlier than calendar begin year.");
			
			break;

		case 2: // two season
			newQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.getValue());

			Integer beginTSYearInt = new Integer(0);
			Integer endTSYearInt = new Integer(0);

			String beginTSStr = twoSeasonsBeginYear.getItemText(twoSeasonsBeginYear.getSelectedIndex());
			if (beginTSStr == null || beginTSStr.length() == 0) {
				errors.add("Begin year is required.");
			} else {
				beginTSYearInt = new Integer(beginTSStr);
				newQuery.setTwoSeasBeginYear(beginTSYearInt);
			}

			String endTSStr = twoSeasonsEndYear.getItemText(twoSeasonsEndYear.getSelectedIndex());
			if (endTSStr == null || endTSStr.length() == 0) {
				errors.add("End year is required.");

			} else {
				endTSYearInt = new Integer(endTSStr);
				newQuery.setTwoSeasEndYear(endTSYearInt);
			}

			if (beginTSYearInt.intValue() > 0 && endTSYearInt.intValue() > 0
					&& endTSYearInt.intValue() < beginTSYearInt.intValue()) {
				errors.add("Two season end year must not be earlier than two season begin year.");
			}

			if (!twoSeasonsSummer.isChecked() && !twoSeasonsWinter.isChecked()) {
				errors.add("You must select at least one season.");
			} else {
				newQuery.setTwoSeasonSummerSelected(twoSeasonsSummer.isChecked());
				newQuery.setTwoSeasonWinterSelected(twoSeasonsWinter.isChecked());
			}

			break;

		case 3: // four season
			newQuery.setDateQueryType(DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.getValue());

			Integer beginFSYearInt = new Integer(0);
			Integer endFSYearInt = new Integer(0);

			String beginFSStr = fourSeasonsBeginYear.getItemText(fourSeasonsBeginYear.getSelectedIndex());
			if (beginFSStr == null || beginFSStr.length() == 0) {
				errors.add("Begin year is required.");
			} else {
				beginFSYearInt = new Integer(beginFSStr);
				newQuery.setFourSeasBeginYear(beginFSYearInt);
			}

			String endFSStr = fourSeasonsEndYear.getItemText(fourSeasonsEndYear.getSelectedIndex());
			if (endFSStr == null || endFSStr.length() == 0) {
				errors.add("End year is required.");
			} else {
				endFSYearInt = new Integer(endFSStr);
				newQuery.setFourSeasEndYear(endFSYearInt);
			}

			if (beginFSYearInt.intValue() > 0 && endFSYearInt.intValue() > 0
					&& endFSYearInt.intValue() < beginFSYearInt.intValue()) {
				errors.add("Four season end year must not be earlier than four season begin year.");
			}

			if (!fourSeasonsSpring.isChecked() && !fourSeasonsSummer.isChecked() && !fourSeasonsFall.isChecked()
					&& !fourSeasonsWinter.isChecked()) {
				errors.add("You must select at least one season.");
			} else {
				newQuery.setFourSeasonSpringSelected(fourSeasonsSpring.isChecked());
				newQuery.setFourSeasonSummerSelected(fourSeasonsSummer.isChecked());
				newQuery.setFourSeasonFallSelected(fourSeasonsFall.isChecked());
				newQuery.setFourSeasonWinterSelected(fourSeasonsWinter.isChecked());
			}
			break;
		}

		// get geographic parms
		int selectedGeogTab = geogTabPanel.getTabBar().getSelectedTab();

		switch (selectedGeogTab) {

		case 0: // entire system - removed for now
//			newQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_ENTIRE_SYSTEM.getValue());
			errors.add("Entire System selection not available at this time.");
			break;

		case 1: // region
			if (regionListBox.getSelectedIndex() == -1) {
				errors.add("You must select at least one region.");
			} else {
				newQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_REGION.getValue());
				for (int i = 0; i < regionListBox.getItemCount(); i++) {
					if (regionListBox.isItemSelected(i)) {
						String[] region = (String[]) regions.get(i);
						newQuery.addRegion(region[1]);
					}
				}
			}
			break;

		case 2: // area
			if (selectedAreaListBox.getItemCount() == 0) {
				errors.add("You must select at least one planning area.");
			} else {
				newQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_AREA.getValue());
				for (int i = 0; i < selectedAreaListBox.getItemCount(); i++) {
					newQuery.addPlanningArea(selectedAreaListBox.getItemText(i));
				}
			}
			break;
			
		case 3: // substation
			if (selectedSubListBox.getItemCount() == 0) {
				errors.add("You must select at least one substation.");
			} else {
				newQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_SUBSTATION.getValue());
				for (int i = 0; i < selectedSubListBox.getItemCount(); i++) {
					newQuery.addSubstation(selectedSubListBox.getItemText(i));
				}
			}
			break;
			
		case 4: // MP
			if (selectedMpListBox.getItemCount() == 0) {
				errors.add("You must select at least one measurement point.");
			} else {
				newQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_MP.getValue());
				for (int i = 0; i < selectedMpListBox.getItemCount(); i++) {
					newQuery.addMeasurementPoint(selectedMpListBox.getItemText(i));
				}
			}
			break;

		case 5: // import/export - removed for now
//			newQuery.setGeogQueryType(GeogQueryType.GEOG_QUERY_TYPE_IMPORTS_EXPORTS.getValue());
			errors.add("Import/export selection not available at this time.");
			break;
		}

		// get load parms
		if (GeogQueryType.GEOG_QUERY_TYPE_MP.equals(newQuery.getGeogQueryType())) {
			newQuery.setLoadTypeSelected(false);
			newQuery.setGenerationTypeSelected(false);
		} else {
			if (!volumesLoadCb.isChecked() && !volumesGenerationCb.isChecked()) {
				errors.add("You must select at least one type of volume (load or generation).");
			} else {
				newQuery.setLoadTypeSelected(volumesLoadCb.isChecked());
				newQuery.setGenerationTypeSelected(volumesGenerationCb.isChecked());
			}			
		}

		String[] selectedLoad = (String[]) granularities.get(granularityListBox.getSelectedIndex());
		newQuery.setGranularity(selectedLoad[1]);

		String[] selectedCategory = (String[]) categories.get(categoryListBox.getSelectedIndex());
		newQuery.setCategory(selectedCategory[1]);


		// Interest Point
		int selectedPoiTab = poiTabPanel.getTabBar().getSelectedTab();

		switch (selectedPoiTab) {

		case 0: // percentile
			newQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_PERCENTILE.getValue());

			if (!poiPeakCb.isChecked() && !poiMedianCb.isChecked() && !poiLightCb.isChecked()) {
				errors.add("You must select at least one interest point (peak, median, light).");
			} else {
				newQuery.setPoiPeakSelected(poiPeakCb.isChecked());
				newQuery.setPoiMedianSelected(poiMedianCb.isChecked());
				newQuery.setPoiLightSelected(poiLightCb.isChecked());
			}

			newQuery.setPoiLoadSelected(poiLoadRb.isChecked());
			newQuery.setPoiGenerationSelected(poiGenerationRb.isChecked());

			String[] selectedCoincidentLoad = (String[]) coincidences.get(poiCoincidenceListBox.getSelectedIndex());
			newQuery.setPoiCoincidence(selectedCoincidentLoad[1]);
			
			if (newQuery.getPoiCoincidence().equals("SU") && poiLoadRb.isChecked()) {
				errors.add("You must select 'Generation' type if Total of Supply coincidence is selected.");
			}

			if (newQuery.getPoiCoincidence().equals("DE") && poiGenerationRb.isChecked()) {
				errors.add("You must select 'Load' type if Total of Demand coincidence is selected.");
			}
			
			String[] selectedPoiCategory = (String[]) poiCategories.get(poiCategoryListBox.getSelectedIndex());
			newQuery.setPoiCategory(selectedPoiCategory[1]);

			int selectedIntervalIdx = timeIntervalListBox.getSelectedIndex();
			if (selectedIntervalIdx < 0)
				selectedIntervalIdx = 1;

			String[] timeIntervalArr = (String[]) timeIntervals.get(selectedIntervalIdx);
			newQuery.setTimeInterval(timeIntervalArr[1]);
			
			// validate time interval against date parameters
			if (DateQueryType.DATE_QUERY_TYPE_SPECIFIC.equals(newQuery.getDateQueryType())) {
				if (newQuery.getTimeInterval().equals("YR") || newQuery.getTimeInterval().equals("SE") ) {
					errors.add("Yearly and Seasonal time interval not allowed for specific date selection.");
				}				
			} else if (DateQueryType.DATE_QUERY_TYPE_CALENDAR.equals(newQuery.getDateQueryType())) {
				if (newQuery.getTimeInterval().equals("SE") ) {
					errors.add("Seasonal time interval not allowed for calendar date selection.");
				}				
			} else if (DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.equals(newQuery.getDateQueryType())) {
				if (newQuery.getTimeInterval().equals("YR") ) {
					if (newQuery.isTwoSeasonSummerSelected() && newQuery.isTwoSeasonWinterSelected()) {
						
					} else
						errors.add("All seasons must be selected to use Yearly time interval.");
				}				
				
			} else if (DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.equals(newQuery.getDateQueryType())) {
				if (newQuery.getTimeInterval().equals("YR") ) {
					if (newQuery.isFourSeasonSpringSelected() && newQuery.isFourSeasonSummerSelected() && newQuery.isFourSeasonFallSelected() && newQuery.isFourSeasonWinterSelected()) {
						
					} else
						errors.add("All seasons must be selected to use Yearly time interval.");
				}
			}
					
			break;

		case 1: // hourly
			newQuery.setPoiQueryType(PoiQueryType.POI_QUERY_TYPE_HOURLY.getValue());
			if (DateQueryType.DATE_QUERY_TYPE_SPECIFIC.equals(newQuery.getDateQueryType())) {
				if (specEnd != null && specBegin != null) {
					long dayDiff = ((specEnd.getTime() - specBegin.getTime()) /86400000);
					if (dayDiff > 366) {
						errors.add("Hourly query not allowed for a date range of more than 1 year.");
					}
				}
			}
			
			break;
		}

		
		// final validation
		if (GeogQueryType.GEOG_QUERY_TYPE_AREA.equals(newQuery.getGeogQueryType())) {
			if (newQuery.getGranularity().equals("SB") || newQuery.getGranularity().equals("BU")) {
				if (PoiQueryType.POI_QUERY_TYPE_HOURLY.equals(newQuery.getPoiQueryType())) {
					errors.add("Hourly query not available for Planning Areas with Substation or Bus granularity.");
				}
			}
		}

		
		int currentlyExecuting = ElectricalVolumesQuery.get().getNumExecutingQueries();

		if (currentlyExecuting>0)
			errors.add("Only one query may be executing at a time. Please wait until the previous query is complete before submitting this query.");
		
		if (errors.size() > 0)
			throw new QueryValidationException(errors);
		else
			return newQuery;
	}

	
	private Date parseDate(String dateStr) {
		Date aDate = null;
		
		try {
			aDate = dateFmtSlash.parse(dateStr);
		} catch (IllegalArgumentException iae) {
			try {
				aDate = dateFmtDash.parse(dateStr);
			} catch (IllegalArgumentException iae2) {
				try {
					aDate = dateFmtNopunc.parse(dateStr);
				} catch (IllegalArgumentException iae3) {}
				
			}
		}
		return aDate;
	}

}
