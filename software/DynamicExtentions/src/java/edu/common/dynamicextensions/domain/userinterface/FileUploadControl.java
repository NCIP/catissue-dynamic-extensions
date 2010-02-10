/*
 * Created on Nov 3, 2006
 * @author
 *
 */

package edu.common.dynamicextensions.domain.userinterface;

import java.util.List;

import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.FileUploadInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.common.util.global.ApplicationProperties;

/**
 * @author preeti_munot
 * @hibernate.joined-subclass table="DYEXTN_FILE_UPLOAD"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public class FileUploadControl extends Control implements FileUploadInterface
{

	/**
	 *
	 */
	private static final long serialVersionUID = 3211268406984504475L;
	private Integer columns = null;

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	protected String generateEditModeHTML(ContainerInterface container) throws DynamicExtensionsSystemException
	{
		String htmlString = "";
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<div id='" + getHTMLComponentName() + "_div' name='"
					+ getHTMLComponentName() + "_div'>";
		}
		ApplicationProperties.initBundle("ApplicationResources");
		if (value != null)
		{
			htmlString = "<A onclick='appendRecordId(this);' href='/dynamicExtensions/DownloadFileAction?attributeIdentifier="
				+ baseAbstractAttribute.getId() + "'>" + value + "</A>";

		}
		htmlString = htmlString + "<input onchange='isDataChanged();' type=\"file\" " + "name=\"value("
				+ getHTMLComponentName() + ")\" " + "id=\"" + getHTMLComponentName() + "\"/>";
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ getHTMLComponentName() + "_div' />";
			htmlString += "</div>";
		}
		return htmlString;
	}

	/**
	 * @hibernate.property name="columns" type="integer" column="NO_OF_COLUMNS"
	 * @return Returns the columns.
	 */
	public Integer getColumns()
	{
		return columns;
	}

	/**
	 *
	 */
	public void setColumns(Integer columns)
	{
		this.columns = columns;
	}

	protected String generateViewModeHTML(ContainerInterface container) throws DynamicExtensionsSystemException
	{
//		FileAttributeRecordValue fileAttributeRecordValue = (FileAttributeRecordValue) this.value;
		String htmlString = "&nbsp;";

		if (value != null)
		{
			String fileName = value.toString();
			htmlString = "<span class = '" + cssClass + "'> " + fileName + "</span>";
		}
		return htmlString;
	}

	/**
	 *
	 */
	public List<String> getValueAsStrings()
	{
		return null;
	}

	/**
	 *
	 */
	public void setValueAsStrings(List<String> listOfValues)
	{
		// TODO Auto-generated method stub

	}
	/**
	 *
	 */
	public boolean getIsEnumeratedControl()
	{
		return false;
	}
}