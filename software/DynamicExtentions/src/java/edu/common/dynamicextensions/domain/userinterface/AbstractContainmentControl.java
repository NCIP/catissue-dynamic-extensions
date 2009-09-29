
package edu.common.dynamicextensions.domain.userinterface;

import java.util.List;
import java.util.Map;

import edu.common.dynamicextensions.domaininterface.BaseAbstractAttributeInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.AbstractContainmentControlInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.UserInterfaceiUtility;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;

/**
 * @author mandar_shidhore
 * @hibernate.joined-subclass table="DYEXTN_ABSTR_CONTAIN_CTR"
 * @hibernate.joined-subclass-key column="IDENTIFIER"
 */
public abstract class AbstractContainmentControl extends Control
		implements
			AbstractContainmentControlInterface
{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Parent container
	 */
	protected ContainerInterface container;

	public String generateEditModeHTML(Integer rowId) throws DynamicExtensionsSystemException
	{
		ContainerInterface containerInterface = this.getContainer();
		this.setIsSubControl(true);

		if (this.getParentContainer().getShowAssociationControlsAsLink())
		{
			String previousLink = containerInterface.generateLink(getParentContainer());
			String link = UserInterfaceiUtility.getControlHTMLAsARow(this, previousLink);
			return link;
		}

		String subContainerHTML = "";
		if (isCardinalityOneToMany())
		{
			List<Map<BaseAbstractAttributeInterface, Object>> valueMapList = (List<Map<BaseAbstractAttributeInterface, Object>>) value;
			subContainerHTML = containerInterface.generateControlsHTMLAsGrid(valueMapList,getDataEntryOperation());
		}
		else
		{
			if (value != null && ((List) value).size() > 0)
			{
				Map<BaseAbstractAttributeInterface, Object> displayContainerValueMap = ((List<Map<BaseAbstractAttributeInterface, Object>>) value)
						.get(0);
				containerInterface.setContainerValueMap(displayContainerValueMap);
			}
			this.getContainer().setShowAssociationControlsAsLink(true);
			subContainerHTML = containerInterface.generateControlsHTML(null,getDataEntryOperation());
		}
		return subContainerHTML;
	}

	public String generateViewModeHTML(Integer rowId) throws DynamicExtensionsSystemException
	{
		String subContainerHTML = "";
		this.setIsSubControl(true);
		if (isCardinalityOneToMany())
		{
			List<Map<BaseAbstractAttributeInterface, Object>> valueMapList = (List<Map<BaseAbstractAttributeInterface, Object>>) value;
			String oldMode = this.getContainer().getMode();
			this.getContainer().setMode(WebUIManagerConstants.VIEW_MODE);
			subContainerHTML = this.getContainer().generateControlsHTMLAsGrid(valueMapList,getDataEntryOperation());
			this.getContainer().setMode(oldMode);
		}
		else
		{
			if (value != null && ((List) value).size() > 0)
			{
				Map<BaseAbstractAttributeInterface, Object> displayContainerValueMap = ((List<Map<BaseAbstractAttributeInterface, Object>>) value)
						.get(0);
				this.getContainer().setContainerValueMap(displayContainerValueMap);
			}
			this.getContainer().setShowAssociationControlsAsLink(true);
			String oldMode = this.getContainer().getMode();
			this.getContainer().setMode(WebUIManagerConstants.VIEW_MODE);
			subContainerHTML = this.getContainer().generateControlsHTML(null,getDataEntryOperation());
			this.getContainer().setMode(oldMode);
		}
		return subContainerHTML;
	}

	/**
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.ContainmentAssociationControlInterface#generateLinkHTML()
	 */
	public String generateLinkHTML() throws DynamicExtensionsSystemException
	{
		String detailsString = "Details";
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer
				.append("<img src='images/de/ic_det.gif' alt='Details' width='12' height='12' hspace='3' border='0' align='absmiddle'><a href='#' style='cursor:hand' class='set1' onclick='showChildContainerInsertDataPage(");
		stringBuffer.append(this.getParentContainer().getIncontextContainer().getId());
		stringBuffer.append(",this)'>");
		stringBuffer.append(detailsString);
		stringBuffer.append("</a>");

		return stringBuffer.toString();
	}

	/**
	 * @return container
	 * @hibernate.many-to-one cascade="save-update" column="CONTAINER_ID" class="edu.common.dynamicextensions.domain.userinterface.Container" constrained="true"
	 */
	public ContainerInterface getContainer()
	{
		return container;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.common.dynamicextensions.domaininterface.userinterface.ContainmentAssociationControlInterface#setContainer(edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface)
	 */
	public void setContainer(ContainerInterface container)
	{
		this.container = container;
	}

}