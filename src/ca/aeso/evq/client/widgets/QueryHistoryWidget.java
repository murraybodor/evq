package ca.aeso.evq.client.widgets;

import java.util.List;

import ca.aeso.evq.client.ElectricalVolumesQuery;
import ca.aeso.evq.common.QueryStatusType;
import ca.aeso.evq.rpc.EvqServiceAsync;
import ca.aeso.evq.rpc.Query;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * QueryHistoryWidget
 * A composite that displays a list of previous user queries
 * 
 * @author mbodor
 */
public class QueryHistoryWidget extends Composite implements TableListener, ClickListener {

    private EvqServiceAsync serviceEndpoint;
	private FlexTable historyTable = new FlexTable();
	private List queries = null;
	private int selectedRow = -1;
	private int numExecutingQueries = 0;
	
	/**
	 * Constructor. Initialize the widget
	 */
	public QueryHistoryWidget() {
	    historyTable.setCellSpacing(0);
	    historyTable.setCellPadding(0);
	    historyTable.setWidth("1000px");
	    historyTable.addTableListener(this);
	    
	    startRefreshTimer();
	    initWidget(historyTable);
	    setStyleName("queryHistoryWidget");
	    initTable();
	    update();
	}

	/**
	 * refresh the history list every 10 minutes
	 */
	private void startRefreshTimer() {
		Timer t = new Timer() {
			public void run() {
				loadQueryHistory();
			}
		};

		t.scheduleRepeating(600000); 		
	}
	
	/**
	 * When a cell is clicked, highlight the row
	 */
	public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
		// Select the row that was clicked (-1 to account for header row).
		if (row > 0) {
			selectRow(row - 1);
		}
	}

	/**
	 * Handle table click events
	 */
	public void onClick(Widget sender) {
	}

	private void initTable() {
		// Create the header row
		historyTable.setText(0, 0, "Query Date");
		historyTable.setText(0, 1, "Status");
		historyTable.setText(0, 2, "Duration");
		historyTable.setText(0, 3, "Name");
		historyTable.setText(0, 4, "Date Parameters");
		historyTable.setText(0, 5, "Geog");
		historyTable.setText(0, 6, "Volumes");
		historyTable.setText(0, 7, "Interest Point");
    
		historyTable.getRowFormatter().setStyleName(0, "queryHistoryWidgetHeader");
	}

	private void selectRow(int row) {
		Query item = (Query) queries.get(row);
		if (item == null) {
			return;
		}

		styleRow(selectedRow, false);
		styleRow(row, true);
		selectedRow = row;
	}

	private void styleRow(int row, boolean selected) {
		if (row != -1) {
			if (selected) {
				historyTable.getRowFormatter().addStyleName(row + 1, "queryHistoryWidgetSelectedRow");
			} else {
				historyTable.getRowFormatter().removeStyleName(row + 1, "queryHistoryWidgetSelectedRow");
			}
		}
	}

	private void loadQueries(List inQueries) {
	  
		this.queries = inQueries;
		int rows = historyTable.getRowCount();
		
		if (rows>1) {
			for (int i = 2; i <= rows; i++) {
				historyTable.removeRow(1);
			}
		}

		numExecutingQueries = 0;
		
		try {
		    for (int i=0; i < this.queries.size(); ++i) {
			    Query aQuery = (Query)queries.get(i);
			    
			    if (aQuery.getUserId().equals("COMMON")) {
					historyTable.getRowFormatter().addStyleName(i + 1, "queryHistoryWidgetCommonRow");
			    }
			    
			    historyTable.setWidget(i + 1, 0, formatQueryDetailUrl(aQuery));
			    
			    String status = aQuery.getQueryStatus();
			    
			    if (status==null ) {
				    historyTable.setHTML(i + 1, 1, "&nbsp;");
			    } else if (QueryStatusType.STATUS_EXECUTING.equals(status)) {
				    historyTable.setHTML(i + 1, 1, "Executing");
				    numExecutingQueries++;
			    } else if (QueryStatusType.STATUS_REMOVED.equals(status)) {
				    historyTable.setHTML(i + 1, 1, "Removed");
			    } else if (QueryStatusType.STATUS_COMPLETE.equals(status)) {
				    historyTable.setHTML(i + 1, 1, "<a class=\"link\" href=\"" + GWT.getModuleBaseURL() + "evq.download?queryId=" + aQuery.getQueryId() + "\">Complete</a>");
			    } else if (QueryStatusType.STATUS_FAILED.equals(status)) {
			    	historyTable.setWidget(i + 1, 1, formatFailedWidget(aQuery));
			    }
		 	    historyTable.setHTML(i + 1, 2, formatCellHTML(aQuery.getRuntimeStr()));
		 	    historyTable.setHTML(i + 1, 3, formatCellHTML(aQuery.getNameStr()));
			    historyTable.setHTML(i + 1, 4, formatCellHTML(aQuery.getDateParametersStr()));
			    historyTable.setHTML(i + 1, 5, formatCellHTML(aQuery.getGeogParametersStr()));
			    historyTable.setHTML(i + 1, 6, formatCellHTML(aQuery.getVolumeParametersStr()));
			    historyTable.setHTML(i + 1, 7, formatCellHTML(aQuery.getInterestPointStr()));
			    
		    }
		} catch (Exception e) {}
	}

	private Widget formatQueryDetailUrl(Query item) {
		Hyperlink queryDetail = new Hyperlink();
		queryDetail.setStyleName("link");
	    queryDetail.setText(item.getQueryDateStr());
	    queryDetail.setTitle(new Long(item.getQueryId()).toString());
	    queryDetail.addClickListener(new ClickListener() {
	    		public void onClick(Widget sender) {
	    			long queryId = new Long(sender.getTitle()).longValue();
	    			ElectricalVolumesQuery.get().loadQuery(queryId);
	    		}
	    	}
		);

	    return queryDetail;
	}
  
	private String formatCellHTML(String text) {
		if (text==null || text.equals(""))
			return "&nbsp;";
		else
			return text;
	}

	private Widget formatFailedWidget(Query aQuery) {

		StringBuffer errors = new StringBuffer("");
		errors.append(aQuery.getQueryErrors());
		errors.append("\r\n\r\n");
	  
		final QueryDialog dialog = new QueryDialog("Query Errors", errors.toString());
	  
		Hyperlink errorLink = new Hyperlink();
		errorLink.setStyleName("link");
		errorLink.setText("Failed");
		errorLink.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				dialog.center();
			}
		});
	  
		return errorLink;
	}

//	private String formatRuntime(Query item) {
//
//		StringBuffer buf = new StringBuffer("");
//		long runtime = item.getQueryRuntime();
//
//		long runtimeSecs = (runtime/1000);
//		
//		if (runtimeSecs<60) {
//			return Long.toString(runtimeSecs) + " s";
//		} else {
//			long runtimeMins = runtimeSecs/60;
//			if (runtimeMins<60) {
//				return Long.toString(runtimeMins) + " min";
//			} else {
//				long runtimeHours = runtimeMins/60;
//				runtimeMins = runtimeMins - (runtimeHours*60);
//				
//				return Long.toString(runtimeHours) + "hrs " + Long.toString(runtimeMins) + " min";
//			}
//		}
//	}

//	private String formatName(Query item) {
//
//		String queryName = item.getQueryName();
//
//		if (queryName!=null && queryName.length()>50) {
//			return queryName.substring(0, 50); 	 
//		} else {
//			return queryName;
//		}
//	}
	
//	private String formatDateParameters(Query item) {
//		StringBuffer buf = new StringBuffer();
//
//		try {
//
//			String type = item.getDateQueryType();
//
//			if (DateQueryType.DATE_QUERY_TYPE_SPECIFIC.equals(type)) {
//				buf.append(item.getBeginDate());
//				buf.append(" to ");
//				buf.append(item.getEndDate());
//			} else if (DateQueryType.DATE_QUERY_TYPE_CALENDAR.equals(type)) { // months removed for now
//				buf.append(item.getCalBeginYear());
////				buf.append("/");
////				buf.append(QueryDetailWidget.MONTHS[item.getCalBeginMonth().intValue()-1]);
//				buf.append(" to ");
//				buf.append(item.getCalEndYear());
////				buf.append("/");
////				buf.append(QueryDetailWidget.MONTHS[item.getCalEndMonth().intValue()-1]);
//			} else if (DateQueryType.DATE_QUERY_TYPE_TWO_SEASONS.equals(type)) {
//				buf.append(item.getTwoSeasBeginYear());
//				buf.append(" to ");
//				buf.append(item.getTwoSeasEndYear());
//				buf.append(": ");
//				buf.append(item.isTwoSeasonSummerSelected()?"Summer ":"");
//				buf.append(item.isTwoSeasonWinterSelected()?"Winter":"");
//			} else if (DateQueryType.DATE_QUERY_TYPE_FOUR_SEASONS.equals(type)) {
//				buf.append(item.getFourSeasBeginYear());
//				buf.append(" to ");
//				buf.append(item.getFourSeasEndYear());
//				buf.append(": ");
//				buf.append(item.isFourSeasonSpringSelected()?"Spring ":"");
//				buf.append(item.isFourSeasonSummerSelected()?"Summer ":"");
//				buf.append(item.isFourSeasonFallSelected()?"Fall ":"");
//				buf.append(item.isFourSeasonWinterSelected()?"Winter":"");
//			}
//
//		} catch (Exception e) {
//			return null;
//		}
//
//		return buf.toString();
//	}
  
//	private String formatGeogParameters(Query item) {
//		StringBuffer buf = new StringBuffer();
//		try {
//			buf.append(GeogQueryType.getDescription(item.getGeogQueryType()));
//		} catch(Exception e) {
//			return null;
//		}
//
//		return buf.toString();
//	}

//	private String formatVolumeParameters(Query item) {
//		StringBuffer buf = new StringBuffer();
//		try {
//			buf.append(CodesUtil.getCodeDesc(item.getGranularity()));
//			buf.append(": ");
//			
//			if (item.isLoadTypeSelected()) {
//				buf.append("Load");
//				if (item.isGenerationTypeSelected()) {
//					buf.append("+");
//				}
//			} 
//			
//			if (item.isGenerationTypeSelected()) {
//				buf.append("Gen");
//			}
//			
//		} catch (Exception e) {
//			return null;
//		}
//		return buf.toString();
//	}
  
//	private String formatInterestPoint(Query item) {
//		StringBuffer buf = new StringBuffer();
//		try {
//			String poiType = item.getPoiQueryType();
//			if (PoiQueryType.POI_QUERY_TYPE_HOURLY.equals(poiType)) {
//				buf.append("Hourly");
//			} else {
//
//				// coincidence
//				String coincidence = CodesUtil.getCodeDesc(item.getPoiCoincidence()); 
//				buf.append(coincidence);
//				buf.append(" ");
//				
//				if (item.isPoiLoadSelected()) {
//					buf.append("Load");
//					if (item.isPoiGenerationSelected()) {
//						buf.append("+");
//					}
//				} 
//				
//				if (item.isPoiGenerationSelected()) {
//					buf.append("Gen");
//				}
//				
//				buf.append(": ");
//
//				
//				if (item.isPoiPeakSelected()) {
//					buf.append("Peak");
//					if (item.isPoiMedianSelected() || item.isPoiLightSelected())
//						buf.append("+");
//				}
//
//				if (item.isPoiMedianSelected()) {
//					buf.append("Med");
//					if (item.isPoiLightSelected())
//						buf.append("+");
//				}
//				
//				if (item.isPoiLightSelected()) {
//					buf.append("Light");
//				}
//
//			}
//		} catch (Exception e) {
//			buf.append("&nbsp;");
//		}
//
//		return buf.toString();
//	}

	private void update() {
	}

	public int getNumExecutingQueries() {
		return numExecutingQueries;
	}
	
	/**
	 * Populate the widget
	 * @param serviceEndpoint
	 */
	public void populate(EvqServiceAsync serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
		loadQueryHistory();
	}
  
	/**
	 * Load the query history records
	 */
	public void loadQueryHistory() {
		  GWT.log("loadQueryHistory()", null);

		  AsyncCallback callback = new AsyncCallback() {
	          public void onSuccess(Object result) {
	        	  queries = (List)result;
	              loadQueries(queries);
	          }
	          public void onFailure(Throwable caught) {
	        	  queries = null;
	        	  Window.alert("Failed to get query history: " + caught.getMessage());
	          }
	      };
	      serviceEndpoint.getQueryHistory(callback);
	  }
}
