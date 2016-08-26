package ca.aeso.evq.client;

import java.util.HashMap;

import ca.aeso.evq.client.widgets.QueryDetailWidget;
import ca.aeso.evq.client.widgets.QueryDialog;
import ca.aeso.evq.client.widgets.QueryHistoryWidget;
import ca.aeso.evq.common.CodesUtil;
import ca.aeso.evq.rpc.EvqService;
import ca.aeso.evq.rpc.EvqServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ElectricalVolumesQuery
 * Entry point to application.
 * @author mbodor
 */
public class ElectricalVolumesQuery implements EntryPoint { 
	//, HistoryListener {

    // local variables
	private static ElectricalVolumesQuery singleton;
	private static EvqServiceAsync serviceEndpoint = (EvqServiceAsync) GWT.create(EvqService.class);
	public static HashMap codes;
	
	static {
		((ServiceDefTarget) serviceEndpoint).setServiceEntryPoint(GWT.getModuleBaseURL()	+ "/evq.service");
	}

	// UI controls
	EvqImageBundle evqImageBundle = (EvqImageBundle) GWT.create(EvqImageBundle.class);
	Label userId = new Label();
	Hyperlink logout = new Hyperlink("Logout", "main");
	VerticalPanel mainPanel = new VerticalPanel();
	TabPanel mainTabPanel = new TabPanel();
	QueryHistoryWidget queryHistoryWidget = new QueryHistoryWidget();
	QueryDetailWidget queryDetailWidget = new QueryDetailWidget();

	
	/**
	 * handler for uncaught exceptions
	 */
	GWT.UncaughtExceptionHandler evqHandler = new GWT.UncaughtExceptionHandler() {
		public void onUncaughtException(Throwable throwable) {
			String text = "Uncaught exception: ";
			while (throwable != null) {
				StackTraceElement[] stackTraceElements = throwable.getStackTrace();
				text += new String(throwable.toString() + "\r\n");
				for (int i = 0; i < stackTraceElements.length; i++) {
					text += "    at " + stackTraceElements[i] + "\r\n";
				}
				throwable = throwable.getCause();
				if (throwable != null) {
					text += "\r\nCaused by: ";
				}
			}
			
//			text = text.replaceAll(" ", "&nbsp;");
			final QueryDialog dialog = new QueryDialog("Uncaught Exception", text);
			dialog.center();
		}
	};
	
	
	/**
	 * Clicklistener for the logout link behaviour
	 */
	ClickListener logoutClickListener = new ClickListener() {
		public void onClick(Widget sender) {
			if (sender == logout) {
				logoutUser();
			} else {
				return;
			}
		}
	};

    /**
	 * Gets the singleton ElectricalVolumesQuery instance.
	 */
    public static ElectricalVolumesQuery get() {
      return singleton;
    }

    /**
     * Loads the specified query into the detail widget
     */
    public void loadQuery(long queryId) {
		mainTabPanel.selectTab(1);
    	queryDetailWidget.loadQuery(queryId);
    }
    
    /**
	 * Entry point
	 */
	public void onModuleLoad() {

		// set the uncaught exception handler 
		GWT.setUncaughtExceptionHandler(evqHandler);
		
//		History.addHistoryListener(this);
		
		// use a deferred command to catch initialization exceptions
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				loadModule();
			}
		});
	}
    
    private void loadModule() {

		singleton = this;

		prePopulate();

		mainPanel.add(drawHeaderPanel());
		mainPanel.add(drawSeparatorLine());

		mainTabPanel.add(drawHistoryPanel(), "Query History");
		mainTabPanel.add(drawDetailPanel(), "Query Detail");
		mainPanel.add(mainTabPanel);

		mainPanel.add(drawSeparatorLine());
		mainPanel.add(drawFooterPanel());

		RootPanel.get("mainPanel").add(mainPanel);

		reset();
	}

    /**
     * Reset the state
     */
    public void reset() {
		mainTabPanel.selectTab(0);
		queryHistoryWidget.populate(serviceEndpoint);
    }
    
	private HorizontalPanel drawSeparatorLine() {
		HorizontalPanel separator = new HorizontalPanel();
		separator.setStyleName("separator");
		Label blank = new Label("");
		blank.setStyleName("smalllabel");
		separator.add(blank);
		return separator;
	}

    private HorizontalPanel drawHeaderPanel() {

		HorizontalPanel headerPanel = new HorizontalPanel();
		headerPanel.setStyleName("headerPanel");

		AbstractImagePrototype logoImgPrototype = evqImageBundle.logo();
		Image logo = logoImgPrototype.createImage();
		logo.setStyleName("logo");
		headerPanel.add(logo);

		Label title = new Label("Electrical Volumes Query");
		title.setStyleName("title");

		headerPanel.add(title);

		userId.setStyleName("label");
		headerPanel.add(userId);

		logout.setStyleName("logout");
		logout.addClickListener(logoutClickListener);
		headerPanel.add(logout);

		headerPanel.setCellVerticalAlignment(title, HasVerticalAlignment.ALIGN_MIDDLE);
		headerPanel.setCellHorizontalAlignment(userId, HasHorizontalAlignment.ALIGN_RIGHT);
		headerPanel.setCellVerticalAlignment(userId, HasVerticalAlignment.ALIGN_BOTTOM);
		headerPanel.setCellHorizontalAlignment(logout, HasHorizontalAlignment.ALIGN_RIGHT);
		headerPanel.setCellVerticalAlignment(logout, HasVerticalAlignment.ALIGN_BOTTOM);

		return headerPanel;

	}
    
    private VerticalPanel drawDetailPanel() {

		queryDetailWidget.populate(serviceEndpoint);
		queryDetailWidget.reset();
		VerticalPanel queryDetailPanel = new VerticalPanel();
		queryDetailPanel.add(queryDetailWidget);
		queryDetailPanel.setStyleName("queryDetailPanel");

		return queryDetailPanel;
	}

	private VerticalPanel drawHistoryPanel() {

//		ScrollPanel historyScrollPanel = new ScrollPanel(queryHistoryWidget);
//		historyScrollPanel.setStyleName("historyScrollPanel");

		VerticalPanel queryHistoryPanel = new VerticalPanel();
//		queryHistoryPanel.add(historyScrollPanel);
		queryHistoryPanel.add(queryHistoryWidget);
		queryHistoryPanel.setStyleName("queryHistoryPanel");

		return queryHistoryPanel;
	}

    private HorizontalPanel drawFooterPanel() {

		HorizontalPanel footerPanel = new HorizontalPanel();
		footerPanel.setStyleName("footerPanel");
		String lastUpdatedStr = "Release #: 1.0.23 Built: Wed Mar 4 2008 14:30";
		Label lastUpdated = new Label(lastUpdatedStr);
		lastUpdated.setStyleName("smallLabel");
		footerPanel.add(lastUpdated);
		footerPanel.setCellHorizontalAlignment(lastUpdated, HasHorizontalAlignment.ALIGN_CENTER);

		return footerPanel;
	}

	private void prePopulate() {
		getUser();
		getCodes();
	}

	public void onHistoryChanged(String historyToken)
	{
		
    }
	
	public int getNumExecutingQueries() {
		return queryHistoryWidget.getNumExecutingQueries();
	}
	
	/**
	 * Retrieve the user information
	 */
	private void getUser() {
	  GWT.log("getUser()", null);

	  AsyncCallback callback = new AsyncCallback() {
          public void onSuccess(Object result) {
        	  String user = (String)result;
        	  userId.setText("User: " + user);
        	  
          }
          public void onFailure(Throwable caught) {
        	  sendToLogin(GWT.getModuleBaseURL() + "index.html");
          }
      };
	  
      serviceEndpoint.getUser(callback);
	}

	/**
	 * Retrieve codes
	 */
	private void getCodes() {
		  GWT.log("getCodes()", null);

		  AsyncCallback callback = new AsyncCallback() {
	          public void onSuccess(Object result) {
	        	  CodesUtil.setCodeMap((HashMap)result);
	          }
	          public void onFailure(Throwable caught) {
	          }
	      };
		  
	      serviceEndpoint.getCodes(callback);
	}

	/**
	 * Log out the user
	 */
	private void logoutUser() {
	  GWT.log("logoutUser()", null);

	  AsyncCallback callback = new AsyncCallback() {
          public void onSuccess(Object result) {
        	  sendToLogin(GWT.getModuleBaseURL() + "index.html");
          }
          public void onFailure(Throwable caught) {
        	  sendToLogin(GWT.getModuleBaseURL() + "index.html");
          }
      };
	  
      serviceEndpoint.logoutUser(callback);
	}

	/**
	 * Redirect the user to the login page
	 * @param url
	 */
	public native void sendToLogin(String url) /*-{
  		$doc.location = url;
	}-*/; 
  
}
