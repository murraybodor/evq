package ca.aeso.evq.server.servlet.struts;

/**
 * <p>Title: BaseExceptionhandler</p>
 * <p>Description: This class extends the Struts Exception handler to do additional logging when exceptions occur</p>
 * <p>Copyright: Copyright (c) 2003 Alberta Electric System Operator</p>
 * @Version $Id: BaseExceptionHandler.java,v 1.2 2008/02/20 16:46:15 mbodor Exp $
 *
 */

import java.net.InetAddress;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ExceptionHandler;
import org.apache.struts.config.ExceptionConfig;

public class BaseExceptionHandler extends ExceptionHandler
{

    public BaseExceptionHandler()
    {
    }

    /**
     * execute
     * Here we want to store the key details of the exception in the errors object and give the user a graceful error page
     * @param ex The exception that was thrown by an action
     * @param ae The global-exceptions configuration
     * @param mapping
     * @param formInstance
     * @param request
     * @param response
     * @return the error page tiles layout
     * @throws ServletException
     */
    public ActionForward execute(Exception ex,
                                 ExceptionConfig ae,
                                 ActionMapping mapping,
                                 ActionForm formInstance,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws ServletException
    {
        if (logger.isDebugEnabled()) {
            logger.debug("execute: starting");
        }

        ActionForward forward = null;
		String exceptionType = null;
		String message = null;
		String machineName = null;
		String userId = null;
        String referenceCode = null;

        // Use the UID from the server as the identification code
		try {
			machineName = InetAddress.getLocalHost().getHostName() + " - " + InetAddress.getLocalHost().getHostAddress();
		} catch (Throwable t) 
		{
			machineName = "Unknown";
		}

		// log the fatal error. We will make sure the log4j config uses the appropriate appender
		logger.fatal("------------------------------------------------------");
		logger.fatal("User Id: " + userId);
		logger.fatal("Date: " + (new Date()).toString());
		logger.fatal("Unique Server Reference Code: " + referenceCode);
		logger.fatal("Machine Name: " + machineName);
		logger.fatal("Exception Message: " + message);
		logger.fatal("Action Mapping Path: " + mapping.getPath());
		logger.fatal("Action Form Name: " + mapping.getName());
		logger.fatal("Requested URL: " + request.getRequestURI()+"?"+ request.getQueryString());
		logger.fatal("Exception Type: " + exceptionType);
		logger.fatal("Exception Details: ", ex);

		// repeat the fatal log into the exception details for the user to view
		StringBuffer exceptionDetail = new StringBuffer("");
		exceptionDetail.append("<b>Exception Detail Report</b>");
		exceptionDetail.append("<BR>");
		exceptionDetail.append("<b>------------------------------------------------------</b>");
		exceptionDetail.append("<BR>");
		exceptionDetail.append("<b>User Id:</b> " + userId);
		exceptionDetail.append("<BR>");
		exceptionDetail.append("<b>Date:</b> " + (new Date()).toString());
		exceptionDetail.append("<BR>");
		exceptionDetail.append("<b>Unique Server Reference Code:</b> " + referenceCode);
		exceptionDetail.append("<BR>");
		exceptionDetail.append("<b>Machine Name:</b> " + machineName);
		exceptionDetail.append("<BR>");
		exceptionDetail.append("<b>Exception Message:</b> " + message);
		exceptionDetail.append("<BR>");
		exceptionDetail.append("<b>Action Mapping Path:</b> " + mapping.getPath());
		exceptionDetail.append("<BR>");
		exceptionDetail.append("<b>Action Form Name:</b> " + mapping.getName());
		exceptionDetail.append("<BR>");
		exceptionDetail.append("<b>Requested URL:</b> " + request.getRequestURI()+"?"+ request.getQueryString());
		exceptionDetail.append("<BR>");
		exceptionDetail.append("<b>Exception Type:</b> " + exceptionType);
		exceptionDetail.append("<BR>");


        // Build the forward from the exception mapping if it exists
        // or from the form input
        if (ae.getPath() != null) {
            forward = new ActionForward(ae.getPath());
        } else {
            forward = mapping.getInputForward();
        }

		// create the new errors we want to display
		ActionMessage error = new ActionMessage(ae.getKey(), "An internal error has occurred. Reference Code: " + referenceCode + "<BR>");
		ActionMessage error2 = new ActionMessage(ae.getKey(), "Reason: " + message);

		ActionMessages errors  = new ActionMessages();
		errors.add(ae.getKey(), error);
		errors.add(ae.getKey(), error2);

		// format the items for notifying the administrator 
//		MessageResources messages = MessageResources.getMessageResources(Constants.RESOURCE_COMMONRESOURCES);
//		String administrator = messages.getMessage(Constants.GLOBAL_ADMINISTRATOR);		
//		String subject = "Exception in " + request.getRequestURI() + " Reference code: " + referenceCode;

//
//		if (logger.isDebugEnabled()) {
//			logger.debug("execute: administrator email=" + administrator);
//		}

//		// set the items in the request
		request.setAttribute(Globals.ERROR_KEY, errors);
//		request.setAttribute(Constants.GLOBAL_EXCEPTION_NOTIFY, Constants.HTML_MAILTO + administrator + Constants.HTML_SUBJECT + subject);

		// set the exception detail in the session (details are retrieved by a separate page)
		request.getSession().setAttribute("global.exception.detail", exceptionDetail.toString());

        if (logger.isDebugEnabled()) {
            logger.debug("execute: done, forwarding to " + forward.toString());
        }

        return forward;

    }

    protected Log logger = LogFactory.getLog(BaseExceptionHandler.class);
}
