
package edu.common.dynamicextensions.summary;

import java.util.Map;

import edu.common.dynamicextensions.domaininterface.userinterface.ControlInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;

public class DefaultSummaryDataManager extends AbstractSummaryDataManager
{

	@Override
	protected void populateHeaderList()
	{
		headerList.add(new ColumnFormatter(SR_NO, ""));
		ColumnFormatter question = new ColumnFormatter(QUESTION, "Question");
		question.getDataFormatter().put(ALIGN, RIGHT);
		headerList.add(question);

		headerList.add(new ColumnFormatter(RESPONSE, "Response"));

	}

	@Override
	protected void populateRow(ControlInterface control, Map<ControlInterface, Object> map,
			Map<String, String> data) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		data.put(QUESTION, control.getCaption());
		data.put(RESPONSE, getValueAsString(control, map.get(control)));

	}

}
