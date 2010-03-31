/*
 * Created on Nov 3, 2006
 * @author
 *
 */

package edu.common.dynamicextensions.domain.userinterface;

import java.util.List;

import edu.common.dynamicextensions.domain.CategoryEntityRecord;
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

	private static final long serialVersionUID = 3211268406984504475L;

	private Integer columns = null;

	/**
	 * @return
	 * @throws DynamicExtensionsSystemException
	 */
	protected String generateEditModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		String htmlString = "";
		String controlname = getHTMLComponentName();

		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<div id='" + controlname + "_div' name='" + controlname + "_div'>";
		}

		ApplicationProperties.initBundle("ApplicationResources");
		Container parentContainer = getParentContainer();
		final CategoryEntityRecord entityRecord = new CategoryEntityRecord(parentContainer
				.getAbstractEntity().getId(), parentContainer.getAbstractEntity().getName());

		htmlString = htmlString + "<span id='" + controlname + "_button'>";
		if (value != null)
		{
			Long recordId = (Long) parentContainer.getContainerValueMap().get(entityRecord);
			htmlString = htmlString + "<input type='text' disabled name='" + controlname + "'_1 id='" + controlname + "_1' value='" + value + "'/>&nbsp;&nbsp;";
			htmlString = htmlString + "<img src='/images/de/download.bmp' />&nbsp;&nbsp;";
			htmlString = htmlString + "<img src='/images/de/deleteIcon.jpg' style='cursor:pointer' onClick='updateFileControl(" +controlname+ ");' />";
			/*htmlString = "<A onclick='appendRecordId(this);' href='/dynamicExtensions/DownloadFileAction?attributeIdentifier="
					+ baseAbstractAttribute.getId()
					+ "&recordIdentifier="
					+ recordId
					+ "'>"
					+ value + "</A>";*/
		}
		else
		{
			htmlString = htmlString + "<input onchange='isDataChanged();' type=\"file\" "
					+ "name='" + controlname + "' " + "id=\"" + controlname + "\"'/>";
		}
		htmlString = htmlString + "</span>";
		if (getIsSkipLogicTargetControl())
		{
			htmlString += "<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '"
					+ controlname + "_div' />";
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

	protected String generateViewModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
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