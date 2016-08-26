package ca.aeso.evq.server.servlet;

import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ca.aeso.evq.server.dao.QueryDao;

/**
 * EvqDownloadServlet
 * Standalone servlet to stream back an xls file to the users' browser.
 *  
 * @author mbodor
 */
public class EvqDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 2L;
	protected Log logger = LogFactory.getLog(EvqDownloadServlet.class);
	private QueryDao dao;

	/**
	 * Initialize the Spring WebApplicationContext
	 */
	public void init() {
		logger.info("EvqDownloadServlet init");
		ServletContext ser = this.getServletContext();
		WebApplicationContext appCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(ser) ;
		dao = (QueryDao)appCtx.getBean("dao");
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		logger.debug("EvqDownloadServlet doPost");
		processReq(req, resp);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		logger.debug("EvqDownloadServlet doGet");
		processReq(req, resp);
	}

	private void processReq(HttpServletRequest req, HttpServletResponse resp) {
		logger.debug("EvqDownloadServlet processReq");
		long start = System.currentTimeMillis();
		
		OutputStream os = null;
		String xlsFileName = null;

		String queryIdStr = req.getParameter("queryId");
		if (queryIdStr!=null) {
			xlsFileName = queryIdStr + ".xls";
		}
		long queryId = new Long(queryIdStr).longValue();

		resp.setContentType("application/vnd.ms-excel");
		resp.setHeader("Content-disposition", "attachment; filename=\"" + xlsFileName + "\"");

		try {
			os = resp.getOutputStream();
			logger.debug("EvqDownloadServlet queryId= " + queryIdStr);

			dao.getQueryResults(queryId, os);
			logger.debug("EvqDownloadServlet - got query results into outputstream ");
			os.flush();

		} catch (Exception ioe) {
			logger.error("EvqDownloadServlet caught Exception: " + ioe.getMessage() + ", " + ioe.getLocalizedMessage());
			ioe.printStackTrace();
		}
		finally
		{
			try {
				os.close();
			} catch (Exception e) {}
		}

		long end = System.currentTimeMillis();
    	logger.info("EvqDownloadServlet.processReq() #" + queryId + " duration=" + (end-start) + "ms");
		
	}
} 