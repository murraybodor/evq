package ca.aeso.evq.server.servlet;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ca.aeso.evq.server.dao.QueryDao;
import ca.aeso.evq.server.service.QueryServiceImpl;

/**
 * EvqReaperServlet
 * Standalone servlet to reap old results and reset statuses
 *  
 * @author mbodor
 */
public class EvqReaperServlet extends HttpServlet {

	private static final long serialVersionUID = 88L;
	protected Log logger = LogFactory.getLog(EvqReaperServlet.class);
	private QueryDao dao;
	private QueryServiceImpl service;
	private static Timer timer;

	/**
	 * Initialize the Spring WebApplicationContext
	 */
	public void init() {
		logger.info("EvqReaperServlet init");
		ServletContext ser = this.getServletContext();
		WebApplicationContext appCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(ser) ;
		service = (QueryServiceImpl)appCtx.getBean("service");
		dao = (QueryDao)appCtx.getBean("dao");
		service.setDao(dao);
		
		// schedule the reap every day at 5:00 AM, starting tomorrow
		timer = new Timer();
		Calendar myCal = Calendar.getInstance();
		myCal.add(Calendar.DATE, 1);
		myCal.set(Calendar.HOUR_OF_DAY, 5);
		myCal.set(Calendar.MINUTE, 00);
		myCal.set(Calendar.SECOND, 00);
		
		TimerTask task = new TimerTask() {
		  public void run() {
		    reap();
		  }
		};
		timer.scheduleAtFixedRate(task, myCal.getTime(), 86400000);
		logger.info("EvqReaperServlet scheduled reap at 5:00AM daily");

		
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		logger.debug("EvqReaperServlet doPost");
		processReq(req, resp);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		logger.debug("EvqReaperServlet doGet");
		processReq(req, resp);
	}

	private void processReq(HttpServletRequest req, HttpServletResponse resp) {
		logger.debug("EvqReaperServlet processReq");
		reap();
	}
	
	private void reap() {
		logger.debug("EvqReaperServlet reap start");
		
		long start = System.currentTimeMillis();
		
		int reapResult = 0;
		int resetStatusResult = 0;
		
		try {
			reapResult = service.reapBlobs();
		} catch (Exception ioe) {
			logger.error("EvqReaperServlet caught Exception: " + ioe.getMessage() + ", " + ioe.getLocalizedMessage());
			ioe.printStackTrace();
		}

		try {
			resetStatusResult = service.resetStatuses();
		} catch (Exception ioe) {
			logger.error("EvqReaperServlet caught Exception: " + ioe.getMessage() + ", " + ioe.getLocalizedMessage());
			ioe.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		
		logger.info("EvqReaperServlet reaped " + reapResult + " blobs<BR>");
		logger.info("EvqReaperServlet reset " + resetStatusResult + " statuses from E to F<BR>");
		logger.info("EvqReaperServlet duration=" + (end-start) + "ms");
    	
		logger.debug("EvqReaperServlet reap end");
	}
} 