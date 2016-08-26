package ca.aeso.evq.server.servlet.struts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.web.struts.LookupDispatchActionSupport;

/**
 * 
 */
public abstract class BaseLookupDispatchAction extends LookupDispatchActionSupport {
	protected Log logger = LogFactory.getLog(BaseLookupDispatchAction.class);

	public final ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ActionForward af = null;
		try {

			logger.debug("execute: starting");

			// figure out whether this action's execution is from a submit
			// button
			// (check the method property in the request parameter
			
			String parameter = mapping.getParameter();
			if (parameter == null)
				logger.debug("execute: mapping parameter is null");
			else {
				logger.debug("execute: mapping parameter=" + parameter);
				String name = request.getParameter(parameter);
				if (name == null)
					logger.debug("execute: request parameter value is null");
				else
					logger.debug("execute: request parameter value=" + name);	
			}


			messages = getResources(request);

			// go ahead and do our dispatch mapping and action execution
			logger.debug("execute: executing forward");	
			af = super.execute(mapping, form, request, response);

		} catch (Exception e) {
			logger.debug("Error: " + e.getMessage());
			e.printStackTrace();
		}
		return af;

	}
	
	protected abstract Map getKeyMethodMap();

}