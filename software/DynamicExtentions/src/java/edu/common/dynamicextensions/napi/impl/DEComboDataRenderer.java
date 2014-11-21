
package edu.common.dynamicextensions.napi.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.common.dynamicextensions.domain.nui.PermissibleValue;
import edu.common.dynamicextensions.domain.nui.SelectControl;
import edu.common.dynamicextensions.napi.ControlValue;
import edu.common.dynamicextensions.napi.FormData;
import edu.common.dynamicextensions.ui.util.Constants;
import edu.common.dynamicextensions.ui.util.ControlsUtility;
import edu.common.dynamicextensions.ui.webui.util.CacheManager;
import edu.common.dynamicextensions.util.global.DEConstants;
import edu.wustl.common.util.global.CommonServiceLocator;

/**
 * @author kunal_kamble
 * This action is called in the auto complete dropdown combo box
 *
 */
public class DEComboDataRenderer {

	private static final String ROW = "row";
	private static final String FIELD = "field";
	private static final String ID = "id";
	private static final String TOTAL_COUNT = "totalCount";
	private static final String START = "start";
	private static final String LIMIT = "limit";
	private static final String CONTROL_NAME = "controlName";
	private static final String QUERY = "query";
	private static final Logger logger = Logger.getLogger(DEComboDataRenderer.class);

	public String render(HttpServletRequest request) throws ServletException {
		String query = request.getParameter(QUERY);

		Integer limitFetch = Integer.parseInt(request.getParameter(LIMIT));
		Integer startFetch = Integer.parseInt(request.getParameter(START));

		JSONArray jsonArray = new JSONArray();
		JSONObject mainJsonObject = new JSONObject();

		List<PermissibleValue> pvList = getPVList(request);
		List<PermissibleValue> filteredList  = filterList(pvList, query);

		Integer total = limitFetch + startFetch;
		try {
			mainJsonObject.put(TOTAL_COUNT, pvList.size());

			for (int i = startFetch; i < total && i < filteredList.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(ID, filteredList.get(i).getValue());
				jsonObject.put(FIELD, filteredList.get(i).getValue());
				jsonArray.put(jsonObject);
			}

			mainJsonObject.put(ROW, jsonArray);
		} catch (JSONException exception) {
			logger.error(exception);
		}
		return mainJsonObject.toString();
	}

	private List<PermissibleValue> filterList(List<PermissibleValue> pvList, String query) {
		boolean isShowAll = (query == null || query.length() == 0 || query.equals("*"));
		Locale locale = CommonServiceLocator.getInstance().getDefaultLocale();
		List<PermissibleValue> fileterdList = new ArrayList<PermissibleValue>();
		for (PermissibleValue pv : pvList) {
			if (isShowAll || pv.getValue().toLowerCase(locale).contains(query.toLowerCase(locale))) {
				fileterdList.add(pv);
			}
		}
		return fileterdList;
	}

	private List<PermissibleValue> getPVList(HttpServletRequest request) {

		Stack<FormData> formDataStack = (Stack<FormData>) CacheManager.getObjectFromCache(request,
				DEConstants.FORM_DATA_STACK);
		ControlValue controlValue = formDataStack.peek().getFieldValueByHTMLName(request.getParameter(CONTROL_NAME));
		Date encounterDate = ControlsUtility.getFormattedDate(request.getParameter(Constants.ENCOUNTER_DATE));
		SelectControl control = (SelectControl) controlValue.getControl();
		List<PermissibleValue> permissibleValues = control.getPVList(encounterDate, controlValue);

		return permissibleValues;
	}

}
