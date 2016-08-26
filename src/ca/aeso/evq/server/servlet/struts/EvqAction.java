package ca.aeso.evq.server.servlet.struts;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.*;
import java.text.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.*;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import org.springframework.web.context.WebApplicationContext;

import ca.aeso.evq.rpc.Query;
import ca.aeso.evq.server.service.QueryService;

public class EvqAction extends BaseLookupDispatchAction {
	protected Log logger = LogFactory.getLog(EvqAction.class);

	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
	private static SimpleDateFormat df2 = new SimpleDateFormat("yyyy/mm/dd");
	
	private static List regionBeans;
	private static List planningAreaBeans;
	private static List substationBeans;
	private static List mpBeans;
	private static List granularityBeans;
	private static List coincidenceBeans;
	private static List categoryBeans;
	private static List poiCategoryBeans;
	private static List timeIntervalBeans;

	protected Map getKeyMethodMap() {
		Map map = new HashMap();
		map.put("button.gethistory", "getHistory");
		map.put("button.download", "downloadQueryResults");
		map.put("button.logout", "logout");
		map.put("button.getquery", "getQuery");
		map.put("button.reset", "resetQuery");
		map.put("button.submit", "submitQuery");
		map.put("button.date", "changeDateQueryType");
		map.put("button.geog", "changeGeogQueryType");
		map.put("button.gran", "changeGranularity");
		map.put("button.poi", "changePoiQueryType");
		map.put("button.filter", "searchAndfilterGeog");

		return map;
	}

	public ActionForward getHistory(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		logger.debug("getHistory: starting");

		EvqActionForm userForm = (EvqActionForm) form;

		userForm.setShowHistory(true);

		WebApplicationContext ctx = getWebApplicationContext();

		QueryService service = (QueryService) ctx.getBean("service");
		logger.debug("getHistory: got service");

		String user = request.getRemoteUser();
		if (user == null) {
			user = new String("dev");
		}
		logger.debug("getHistory: got user=" + user);

		userForm.setUser(user);

		List queryHistoryList = service.getQueryHistory(user);

		logger.debug("getHistory: got history list");
		logger.debug("getHistory: size=" + queryHistoryList.size());

		// get history from service
		userForm.setQueryHistory(queryHistoryList);

		userForm.setCodes(service.getCodes());

		// return it to the history tab

		logger.debug("getHistory: done");

		return mapping.findForward("history");
	}

	public ActionForward downloadQueryResults(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("downloadQueryResults: starting");

		logger.debug("downloadQueryResults: done");
		return mapping.findForward("download");
	}

	public ActionForward getQuery(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("getQuery: starting");
		EvqActionForm userForm = (EvqActionForm) form;
		String queryId = request.getParameter("selectedQueryId");

		userForm.setShowHistory(false);

		//		boolean newQuery = true;

		WebApplicationContext ctx = getWebApplicationContext();

		QueryService service = (QueryService) ctx.getBean("service");

		Query aQuery;

		if (queryId == null) {
			logger.debug("getQuery: creating new query");
			aQuery = new Query();
			aQuery.setDateQueryType("SP");
			aQuery.setGeogQueryType("ES");
			aQuery.setPoiQueryType("PE");
			aQuery.setGranularity("IL");
			aQuery.setPlanningAreas(new ArrayList());
			aQuery.setSubstations(new ArrayList());
			aQuery.setMeasurementPoints(new ArrayList());

		} else {
			//			newQuery=false;
			logger.debug("getQuery: getting #" + queryId);
			aQuery = service.getQuery(new Long(queryId).longValue());

			logger.debug("getQuery: got #" + queryId);

			String geog = aQuery.getGeogQueryType();

			if (geog.equals("PA")) {
				List areas = aQuery.getPlanningAreas();
				if (areas != null && areas.size() > 0) {
					String[] selectedAreas = new String[areas.size()];

					logger.debug("getQuery: popping areas");

					int i = 0;
					for (Iterator iterator = areas.iterator(); iterator.hasNext();) {
						selectedAreas[i] = (String) iterator.next();
						i++;
					}

					logger.debug("getQuery: got " + selectedAreas.length + " areas for the query");
					userForm.setSelectedAreas(selectedAreas);
				}

			} else if (geog.equals("SB")) {
				List subs = aQuery.getSubstations();
				if (subs != null && subs.size() > 0) {
					String[] selectedSubs = new String[subs.size()];

					logger.debug("getQuery: popping subs");

					int i = 0;
					for (Iterator iterator = subs.iterator(); iterator.hasNext();) {
						selectedSubs[i] = (String) iterator.next();
						i++;
					}

					logger.debug("getQuery: got " + selectedSubs.length + " subs for the query");
					userForm.setSelectedSubs(selectedSubs);
				}

			} else if (geog.equals("MP")) {
				List mps = aQuery.getMeasurementPoints();
				if (mps != null && mps.size() > 0) {
					String[] selectedMps = new String[mps.size()];

					logger.debug("getQuery: popping mps");

					int i = 0;
					for (Iterator iterator = mps.iterator(); iterator.hasNext();) {
						selectedMps[i] = (String) iterator.next();
						i++;
					}

					logger.debug("getQuery: got " + selectedMps.length + " mps for the query");
					userForm.setSelectedMps(selectedMps);
				}

			}

			logger.debug("getQuery: form populated");
		}

		userForm.setQuery(aQuery);

		// load regions into a static list
		if (regionBeans == null) {
			List regions = service.getRegions();
			regionBeans = new ArrayList();

			for (Iterator iterator = regions.iterator(); iterator.hasNext();) {
				String[] region = (String[]) iterator.next();
				LabelValueBean regionBean = new LabelValueBean(region[0], region[1]);
				regionBeans.add(regionBean);
			}
		}

		userForm.setRegions(regionBeans);

		// load areas into a static list
		if (planningAreaBeans == null) {
			List areas = service.getPlanningAreas();
			planningAreaBeans = new ArrayList();

			for (Iterator iterator = areas.iterator(); iterator.hasNext();) {
				String[] area = (String[]) iterator.next();
				LabelValueBean areaBean = new LabelValueBean(area[0], area[1]);
				planningAreaBeans.add(areaBean);
			}
		}

		userForm.setPlanningAreas(planningAreaBeans);

		// load subs into a static list
		if (substationBeans == null) {
			List subs = service.getSubstations();
			substationBeans = new ArrayList();

			for (Iterator iterator = subs.iterator(); iterator.hasNext();) {
				String[] sub = (String[]) iterator.next();
				LabelValueBean subBean = new LabelValueBean(sub[0], sub[1]);
				substationBeans.add(subBean);
			}
		}

		userForm.setSubstations(substationBeans);

		// load mps into a static list
		if (mpBeans == null) {
			List mps = service.getMeasurementPoints();
			mpBeans = new ArrayList();

			for (Iterator iterator = mps.iterator(); iterator.hasNext();) {
				String[] mp = (String[]) iterator.next();
				LabelValueBean mpBean = new LabelValueBean(mp[0], mp[1]);
				mpBeans.add(mpBean);
			}
		}

		userForm.setMeasurementPoints(mpBeans);

		// load granularities into a static list
		if (granularityBeans == null) {
			List grans = service.getGranularities(aQuery.getGeogQueryType());
			granularityBeans = new ArrayList();

			for (Iterator iterator = grans.iterator(); iterator.hasNext();) {
				String[] gran = (String[]) iterator.next();
				LabelValueBean granBean = new LabelValueBean(gran[0], gran[1]);
				granularityBeans.add(granBean);
			}
		}

		userForm.setGranularities(granularityBeans);

		// load categories into a static list
		if (categoryBeans == null) {
			List cats = service.getCategories();
			categoryBeans = new ArrayList();

			for (Iterator iterator = cats.iterator(); iterator.hasNext();) {
				String[] cat = (String[]) iterator.next();
				LabelValueBean catBean = new LabelValueBean(cat[0], cat[1]);
				categoryBeans.add(catBean);
			}
		}

		userForm.setCategories(categoryBeans);

		// load coincidences into a static list
		if (coincidenceBeans == null) {
			List coincs = service.getCoincidences(aQuery.getGeogQueryType(), aQuery.getGranularity());
			coincidenceBeans = new ArrayList();

			for (Iterator iterator = coincs.iterator(); iterator.hasNext();) {
				String[] coinc = (String[]) iterator.next();
				LabelValueBean coincBean = new LabelValueBean(coinc[0], coinc[1]);
				coincidenceBeans.add(coincBean);
			}
		}

		userForm.setCoincidences(coincidenceBeans);

		// load poiCategories into a static list
		if (poiCategoryBeans == null) {
			List poiCats = service.getPoiCategories();
			poiCategoryBeans = new ArrayList();

			for (Iterator iterator = poiCats.iterator(); iterator.hasNext();) {
				String[] poiCat = (String[]) iterator.next();
				LabelValueBean poiCatBean = new LabelValueBean(poiCat[0], poiCat[1]);
				poiCategoryBeans.add(poiCatBean);
			}
		}

		userForm.setPoiCategories(poiCategoryBeans);

		// load time intervals into a static list
		if (timeIntervalBeans == null) {
			List ints = service.getTimeIntervals();
			timeIntervalBeans = new ArrayList();

			for (Iterator iterator = ints.iterator(); iterator.hasNext();) {
				String[] interval = (String[]) iterator.next();
				LabelValueBean intBean = new LabelValueBean(interval[0], interval[1]);
				timeIntervalBeans.add(intBean);
			}
		}

		userForm.setTimeIntervals(timeIntervalBeans);

		logger.debug("getQuery: done");

		return mapping.findForward("detail");
	}

	public ActionForward changeDateQueryType(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("changeDateQueryType: starting");

		EvqActionForm userForm = (EvqActionForm) form;
		String dateType = request.getParameter("dateType");
		Query aQuery = userForm.getQuery();
		aQuery.setDateQueryType(dateType);

		// reset date fields
		aQuery.setBeginDate(null);
		aQuery.setEndDate(null);

		aQuery.setCalBeginYear(new Integer(0));
		aQuery.setCalEndYear(new Integer(0));

		aQuery.setTwoSeasBeginYear(new Integer(0));
		aQuery.setTwoSeasEndYear(new Integer(0));
		aQuery.setTwoSeasonSummerSelected(false);
		aQuery.setTwoSeasonWinterSelected(false);

		aQuery.setFourSeasBeginYear(new Integer(0));
		aQuery.setFourSeasEndYear(new Integer(0));
		aQuery.setFourSeasonSpringSelected(false);
		aQuery.setFourSeasonSummerSelected(false);
		aQuery.setFourSeasonFallSelected(false);
		aQuery.setFourSeasonWinterSelected(false);

		logger.debug("changeDateQueryType: done");
		return mapping.findForward("detail");
	}

	public ActionForward changeGeogQueryType(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("changeGeogQueryType: starting");

		EvqActionForm userForm = (EvqActionForm) form;
		String geogType = request.getParameter("geogType");
		Query aQuery = userForm.getQuery();
		aQuery.setGeogQueryType(geogType);

		WebApplicationContext ctx = getWebApplicationContext();
		QueryService service = (QueryService) ctx.getBean("service");

		// clear selected values
		userForm.setSelectedAreas(new String[] {});
		userForm.setSelectedSubs(new String[] {});
		userForm.setSelectedMps(new String[] {});

		// update granularities
		List grans = service.getGranularities(geogType);
		List granBeans = new ArrayList();
		String firstGran = null;

		for (Iterator iterator = grans.iterator(); iterator.hasNext();) {
			String[] gran = (String[]) iterator.next();
			LabelValueBean granBean = new LabelValueBean(gran[0], gran[1]);
			if (firstGran == null)
				firstGran = gran[1];

			granBeans.add(granBean);
		}

		userForm.setGranularities(granBeans);

		// update coincidences
		List coincs = service.getCoincidences(geogType, firstGran);
		List coincBeans = new ArrayList();

		for (Iterator iterator = coincs.iterator(); iterator.hasNext();) {
			String[] coinc = (String[]) iterator.next();
			LabelValueBean coincBean = new LabelValueBean(coinc[0], coinc[1]);
			coincBeans.add(coincBean);
		}

		userForm.setCoincidences(coincBeans);

		logger.debug("changeGeogQueryType: done");
		return mapping.findForward("detail");
	}

	public ActionForward changePoiQueryType(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("changePoiQueryType: starting");

		EvqActionForm userForm = (EvqActionForm) form;
		String poiType = request.getParameter("poiType");
		Query aQuery = userForm.getQuery();
		aQuery.setPoiQueryType(poiType);

		logger.debug("changePoiQueryType: done");
		return mapping.findForward("detail");
	}

	public ActionForward changeGranularity(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("changeGranularity: starting");

		EvqActionForm userForm = (EvqActionForm) form;
		String granularity = request.getParameter("gran");
		logger.debug("changeGranularity: changed to " + granularity);

		Query aQuery = userForm.getQuery();
		aQuery.setGranularity(granularity);
		String geogType = aQuery.getGeogQueryType();

		WebApplicationContext ctx = getWebApplicationContext();
		QueryService service = (QueryService) ctx.getBean("service");

		// update coincidences
		List coincs = service.getCoincidences(geogType, granularity);

		// make a String representation where each option is separated by '||' and a value and a label by '|'
		StringBuffer buf1 = new StringBuffer();

		for (Iterator iterator = coincs.iterator(); iterator.hasNext();) {
			String[] coinc = (String[]) iterator.next();
			//			LabelValueBean coincBean = new LabelValueBean(coinc[0], coinc[1]);
			buf1.append(coinc[0]);
			buf1.append("|");
			buf1.append(coinc[1]);
			if (iterator.hasNext())
				buf1.append("||");
		}

		String outline = buf1.toString();

		logger.debug("changeGranularity: output=" + outline);
		logger.debug("changeGranularity: done");

		PrintWriter out = response.getWriter();
		out.print(outline);

		return null;
	}

	public ActionForward searchAndfilterGeog(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("searchAndfilterGeog: starting");

		//		EvqActionForm userForm = (EvqActionForm)form;
		String geog = request.getParameter("geog");
		String search = request.getParameter("search");
		String filter = request.getParameter("filter");

		logger.debug("searchAndfilterGeog: geog=" + geog);
		logger.debug("searchAndfilterGeog: search=" + search);
		logger.debug("searchAndfilterGeog: filter=" + filter);

		//		Query aQuery = userForm.getQuery();
		//		String geogType = aQuery.getGeogQueryType();

		WebApplicationContext ctx = getWebApplicationContext();
		QueryService service = (QueryService) ctx.getBean("service");

		// filter the appropriate geog list 
		List geogs = null;

		if (geog.equals("SB")) {
			geogs = service.getSubstations();
		} else if (geog.equals("MP")) {
			geogs = service.getMeasurementPoints();
		}

		String outline = null;

		if (geogs != null) {
			outline = searchGeog(search, filter, geogs);
		}

		if (outline != null) {
			logger.debug("searchAndfilterGeog: output=" + outline);
			logger.debug("searchAndfilterGeog: done");

			PrintWriter out = response.getWriter();
			out.print(outline);
		}

		return null;
	}

	private String searchGeog(String search, String filter, List geog) {

		boolean first = true;
		StringBuffer buf1 = new StringBuffer();

		for (Iterator iterator = geog.iterator(); iterator.hasNext();) {
			String[] entry = (String[]) iterator.next();

			if (matches(entry[0], search, filter)) {
				if (!first) {
					buf1.append("||");
				}

				first = false;

				buf1.append(entry[0]);
				buf1.append("|");
				buf1.append(entry[1]);
			}
		}

		return buf1.toString();
	}

	private boolean matches(String item, String query, String filter) {

		// apply filter first
		if (filter != null && !filter.equals("") && !filter.equals("null") && filter.length() > 0) {
			if (contains(item.toLowerCase(), filter.toLowerCase())) {
				// passes filter
			} else {
				return false;
			}
		} else {
			// no filter provided, passes filter stage
		}

		if (query != null && query.length() > 0) {
			if (item.toLowerCase().startsWith(query.toLowerCase())) {
				return true;
			}
		} else {
			return true;
		}

		return false;
	}

	private boolean contains(String original, String filter) {
		if (filter.length() == 0) {
			return true;
		}
		if (filter.length() > original.length()) {
			return false;
		}
		for (int i = 0; i < original.length() - filter.length() + 1; i++) {
			if (original.charAt(i) == filter.charAt(0)) {
				boolean matches = true;
				for (int j = 0; j < filter.length(); j++) {
					if (original.charAt(i + j) != filter.charAt(j)) {
						matches = false;
						break;
					}
				}
				if (matches) {
					return true;
				}
			}
		}
		return false;
	}

	public ActionForward resetQuery(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("resetQuery: starting");

		EvqActionForm userForm = (EvqActionForm) form;

		Query aQuery = userForm.getQuery();

		aQuery.setQueryName(null);
		aQuery.setDateQueryType("SP");
		aQuery.setBeginDate(null);
		aQuery.setEndDate(null);

		aQuery.setGeogQueryType("ES");

		aQuery.setLoadTypeSelected(false);
		aQuery.setGenerationTypeSelected(false);

		aQuery.setPoiQueryType("PE");
		aQuery.setPoiPeakSelected(false);
		aQuery.setPoiMedianSelected(false);
		aQuery.setPoiLightSelected(false);

		userForm.setQuery(aQuery);

		userForm.setSelectedAreas(new String[] {});
		userForm.setSelectedSubs(new String[] {});
		userForm.setSelectedMps(new String[] {});

		return mapping.findForward("detail");
	}

	public ActionForward submitQuery(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		logger.debug("submitQuery: starting");

		EvqActionForm userForm = (EvqActionForm) form;

		Query aQuery = userForm.getQuery();

		/*
		 * Validate the query
		 */
		ActionMessages errors = validate(aQuery);
		if (!errors.isEmpty()) {
			saveMessages(request, errors);
			logger.debug("submitQuery: errors, returning to detail");
			return mapping.findForward("detail");
		}

		// valid, so submit
		WebApplicationContext ctx = getWebApplicationContext();
		QueryService service = (QueryService) ctx.getBean("service");

		// launch a separate thread to execute the query
		logger.debug("submitQuery: starting a query thread");
		QueryThread qThread = new QueryThread(service, aQuery);
		qThread.start();

		userForm.setShowHistory(true);
		logger.debug("submitQuery: submit successful, forwarding to history");
		return getHistory(mapping, form, request, response);

	}

	private ActionMessages validate(Query aQuery) {

		logger.debug("validate: starting");

		ActionMessages errors = new ActionMessages();

		aQuery.getQueryName();

		// date validation
		if (aQuery.getDateQueryType().equals("SP")) {

			if (aQuery.getBeginDate() == null || aQuery.getBeginDate().length() == 0) {
				errors.add("error", new ActionMessage("error.begin.date.required"));
			}
			if (aQuery.getEndDate() == null || aQuery.getEndDate().length() == 0) {
				errors.add("error", new ActionMessage("error.end.date.required"));
			}
			if (aQuery.getBeginDate() != null & aQuery.getEndDate() != null) {
				Date begin = null;
				Date end = null;
				try {
					begin = df.parse(aQuery.getBeginDate());
				} catch (ParseException pe) {
					try {
						begin = df2.parse(aQuery.getBeginDate());
					} catch (ParseException pe2) {
						errors.add("error", new ActionMessage("error.begin.date.format"));
					}
				}
				try {
					end = df.parse(aQuery.getEndDate());
				} catch (ParseException pe) {
					try {
						end = df2.parse(aQuery.getEndDate());
					} catch (ParseException pe2) {
						errors.add("error", new ActionMessage("error.end.date.format"));
					}
				}
				if (begin!=null & end!=null) {
					if (begin.compareTo(end)>0) {
						errors.add("error", new ActionMessage("error.begin.end.date"));
					}
				}
			}

		} else if (aQuery.getDateQueryType().equals("CA")) {
			
			Integer begin = aQuery.getCalBeginYear();
			Integer end = aQuery.getCalEndYear();
			if (begin.equals(new Integer(0))) {
				errors.add("error", new ActionMessage("error.cal.begin.year"));
			}
			if (end.equals(new Integer(0))) {
				errors.add("error", new ActionMessage("error.cal.end.year"));
			}
			if (begin.compareTo(end)>0) {
				errors.add("error", new ActionMessage("error.cal.begin.end"));
			}
			
		} else if (aQuery.getDateQueryType().equals("TW")) {
			
			Integer begin = aQuery.getTwoSeasBeginYear();
			Integer end = aQuery.getTwoSeasEndYear();
			if (begin.equals(new Integer(0))) {
				errors.add("error", new ActionMessage("error.twoseas.begin.year"));
			}
			if (end.equals(new Integer(0))) {
				errors.add("error", new ActionMessage("error.twoseas.end.year"));
			}
			if (begin.compareTo(end)>0) {
				errors.add("error", new ActionMessage("error.twoseas.begin.end"));
			}
			if (!aQuery.isTwoSeasonSummerSelected() & !aQuery.isTwoSeasonWinterSelected()) {
				errors.add("error", new ActionMessage("error.twoseas.noseas"));
			}
				
		} else if (aQuery.getDateQueryType().equals("FO")) {
			
			Integer begin = aQuery.getFourSeasBeginYear();
			Integer end = aQuery.getFourSeasEndYear();
			if (begin.equals(new Integer(0))) {
				errors.add("error", new ActionMessage("error.fourseas.begin.year"));
			}
			if (end.equals(new Integer(0))) {
				errors.add("error", new ActionMessage("error.fourseas.end.year"));
			}
			if (begin.compareTo(end)>0) {
				errors.add("error", new ActionMessage("error.fourseas.begin.end"));
			}
			if (!aQuery.isFourSeasonSpringSelected() & !aQuery.isFourSeasonSummerSelected() & !aQuery.isFourSeasonFallSelected() & !aQuery.isFourSeasonWinterSelected()) {
				errors.add("error", new ActionMessage("error.fourseas.noseas"));
			}

		}

		// geographic validation
		if (aQuery.getGeogQueryType().equals("ES")) {
			errors.add("error", new ActionMessage("error.es.not.implemented"));
		} else if (aQuery.getGeogQueryType().equals("RG")) {
			errors.add("error", new ActionMessage("error.rg.not.implemented"));
		} else if (aQuery.getGeogQueryType().equals("PA")) {
			if (aQuery.getPlanningAreas()==null || aQuery.getPlanningAreas().size()==0) {
				errors.add("error", new ActionMessage("error.areas.noselection"));
			}
		} else if (aQuery.getGeogQueryType().equals("SB")) {
			if (aQuery.getSubstations()==null || aQuery.getSubstations().size()==0) {
				errors.add("error", new ActionMessage("error.subs.noselection"));
			}
		} else if (aQuery.getGeogQueryType().equals("MP")) {
			if (aQuery.getMeasurementPoints()==null || aQuery.getMeasurementPoints().size()==0) {
				errors.add("error", new ActionMessage("error.mps.noselection"));
			}
		} else if (aQuery.getGeogQueryType().equals("IE")) {
			errors.add("error", new ActionMessage("error.ie.not.implemented"));
		}

		// volumes validation
		if (aQuery.getGeogQueryType().equals("PA") || aQuery.getGeogQueryType().equals("SB")) {
			if (!aQuery.isLoadTypeSelected() &! aQuery.isGenerationTypeSelected()) {
				errors.add("error", new ActionMessage("error.volume.type.noselection"));
			}
		}
		
		// point of interest validation
		if (aQuery.getPoiQueryType().equals("PE")) {
			if (!aQuery.isPoiPeakSelected() & !aQuery.isPoiMedianSelected() & !aQuery.isPoiLightSelected()) {
				errors.add("error", new ActionMessage("error.poi.point.noselection"));
			}
		}

		logger.debug("validate: done");

		return errors;

	}

	public ActionForward logout(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		request.getSession().invalidate();

		return mapping.findForward("logout");
	}

	class QueryThread extends Thread {
		Query aQuery;
		QueryService service;

		QueryThread(QueryService service, Query aQuery) {
			this.service = service;
			this.aQuery = aQuery;
		}

		public void run() {
			logger.debug("QueryThread.run: starting");
			try {
				service.submitQuery(aQuery);
			} catch (Exception e) {

			}
			logger.debug("QueryThread.run: done");
		}
	}

}
