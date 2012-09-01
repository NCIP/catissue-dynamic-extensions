package edu.common.dynamicextensions.util;

import edu.common.dynamicextensions.processor.ProcessorConstants;


public class DHTMLXDateFormatHandler extends AbstractDateFormatHandler
{

	@Override
	public String getFormat(String dateFormat)
	{
		String formatSpecifier = null;
		if (dateFormat.equals(ProcessorConstants.DATE_ONLY_FORMAT))
		{
			formatSpecifier = "%m%s%d%s%Y";
		}
		else if (dateFormat.equals(ProcessorConstants.DATE_TIME_FORMAT))
		{
			formatSpecifier = "%m%s%d%s%Y %H:%i";
		}
		else if (dateFormat.equals(ProcessorConstants.MONTH_YEAR_FORMAT))
		{
			formatSpecifier = "%m%s%Y";
		}
		else if (dateFormat.equals(ProcessorConstants.YEAR_ONLY_FORMAT))
		{
			formatSpecifier = "%Y";
		}
		formatSpecifier = formatSpecifier.replace("%s", ProcessorConstants.DATE_SEPARATOR);
		return formatSpecifier;
	}

}
