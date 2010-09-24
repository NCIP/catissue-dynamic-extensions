/*
 * Created on Nov 3, 2006
 * @author
 *
 */

package edu.common.dynamicextensions.domain.userinterface;

import java.util.List;

import edu.common.dynamicextensions.domain.CategoryEntityRecord;
import edu.common.dynamicextensions.domain.EntityRecord;
import edu.common.dynamicextensions.domaininterface.CategoryEntityInterface;
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
	 * This methods generates the HTML for FIle type of attribute
	 * @return HTML in string form
	 * @throws DynamicExtensionsSystemException
	 */
	protected String generateEditModeHTML(ContainerInterface container)
			throws DynamicExtensionsSystemException
	{
		StringBuffer htmlString = new StringBuffer(210);
		String controlname = getHTMLComponentName();

		if (getIsSkipLogicTargetControl())
		{
			htmlString.append("<div id='");
			htmlString.append(controlname);
			htmlString.append("_div' name='");
			htmlString.append(controlname);
			htmlString.append("_div'>");
		}

		ApplicationProperties.initBundle("ApplicationResources");
		htmlString.append("<span id='").append(controlname).append("_button'>");

		if (value == null || value.equals(""))
		{
			htmlString.append("<input onchange='isDataChanged();' type=\"file\" name='").append(
					controlname);
			htmlString.append("' id=\"").append(controlname).append("\"'/>");
		}
		else
		{
			generateHTMLForFileControl(htmlString, controlname);
		}
		htmlString.append("</span>");
		if (getIsSkipLogicTargetControl())
		{
			htmlString
					.append("<input type='hidden' name='skipLogicControl' id='skipLogicControl' value = '");
			htmlString.append(controlname).append("_div' /></div>");
		}
		return htmlString.toString();
	}

	/**
	 * This method generates HTML for file attribute
	 * @param htmlString
	 * @param controlname
	 */
	private void generateHTMLForFileControl(StringBuffer htmlString, String controlname)
	{
		Container parentContainer = getParentContainer();
		Long recordId;
		recordId = getRecordId(parentContainer);

		htmlString.append("<input type='text' disabled name='").append(controlname).append(
				"'_1 id='").append(controlname);
		htmlString.append("_1' value='").append(value).append("'/>&nbsp;&nbsp;");

		// Refer Bug # 17326
		generateHTMLBasedOnRecordId(recordId, htmlString);

		htmlString
				.append("<img src='images/de/deleteIcon.jpg' style='cursor:pointer' title='Delete File' onClick='updateFileControl(\"");
		htmlString.append(controlname).append("\");' /><input type='hidden' id='");
		htmlString.append(controlname).append("_hidden' name='");
		htmlString.append(controlname).append("_hidden' value='hidden'/>");
	}

	private Long getRecordId(Container parentContainer)
	{
		Long recordId;
		if (parentContainer.getAbstractEntity() instanceof CategoryEntityInterface)
		{
			final CategoryEntityRecord entityRecord = new CategoryEntityRecord(parentContainer
					.getAbstractEntity().getId(), parentContainer.getAbstractEntity().getName());
			recordId = (Long) parentContainer.getContainerValueMap().get(entityRecord);
		}
		else
		{
			final EntityRecord entityRecord = new EntityRecord();
			recordId = (Long) parentContainer.getContainerValueMap().get(entityRecord);
		}
		return recordId;
	}

	/**
	 * Generate html based on record id.
	 *
	 * @param recordId the record id
	 * @param htmlString the html string
	 */
	private void generateHTMLBasedOnRecordId(Long recordId, StringBuffer htmlString)
	{
		/** This is the case when file attribute is present in multiple levels in a form
		 *  In this case when file is uploaded for upper level and then sub level is traversed
		 *  then upper level form opens in edit mode. Although the control has value, since
		 *  the form is not yet submitted, recordId is null. Hence Download option is not to be
		 *  provided.
		 */
		if (recordId == null)
		{
			htmlString
					.append("<img src='images/uIEnhancementImages/error-green.gif' title='File uploaded'/>");
		}
		else
		{
			htmlString
					.append("<A onclick='appendRecordId(this);' href='/dynamicExtensions/DownloadFileAction?attributeIdentifier=");
			htmlString.append(baseAbstractAttribute.getId());
			htmlString.append("&recordIdentifier=");
			htmlString.append(recordId);
			htmlString
					.append("'><img src='images/de/download.bmp' title='Download File'/> </A>&nbsp;&nbsp;");
		}

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
	 * (non-Javadoc).
	 * @return the checks if is enumerated control
	 * @see edu.common.dynamicextensions.domain.userinterface.Control#getIsEnumeratedControl()
	 */
	public boolean getIsEnumeratedControl()
	{
		return false;
	}

	/**
	 * (non-Javadoc).
	 * @return the value as strings
	 * @see edu.common.dynamicextensions.domain.userinterface.Control#getValueAsStrings()
	 */
	@Override
	public List<String> getValueAsStrings()
	{
		return null;
	}

	/**
	 * This method is not used in this context.
	 *
	 * @param listOfValues the list of values
	 */
	public void setValueAsStrings(List<String> listOfValues)
	{
		// TODO
	}
}
