
package edu.common.dynamicextensions.xmi.importer;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.xmi.XMIConfiguration;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;

public class XMIImporter extends AbstractXMIImporter
{

	/**
	 * main method
	 * @param args commond line arguments
	 */
	public static void main(String[] args)
	{

		args[3]="edu.wustl.clinportal.domain.ParticipantRecordEntry";
		XMIImporter xmiImporter = new XMIImporter();
		xmiImporter.importXMI(args);
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.xmi.importer.AbstractXMIImporter#getXMIConfigurationObject()
	 */
	protected XMIConfiguration getXMIConfigurationObject()
	{
		XMIConfiguration xmiConfiguration = XMIConfiguration.getInstance();
		xmiConfiguration.setEntityGroupSystemGenerated(false);
		xmiConfiguration.setCreateTable(true);
		xmiConfiguration.setAddIdAttr(true);
		xmiConfiguration.setAddColumnForInherianceInChild(false);
		xmiConfiguration.setAddInheritedAttribute(false);
		return xmiConfiguration;
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.xmi.importer.AbstractXMIImporter#getAssociationListForCurratedPath(edu.wustl.dao.HibernateDAO)
	 */
	protected List<AssociationInterface> getAssociationListForCurratedPath(HibernateDAO hibernatedao)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{

		return new ArrayList<AssociationInterface>();
	}

	/* (non-Javadoc)
	 * @see edu.common.dynamicextensions.xmi.importer.AbstractXMIImporter#postProcess(boolean, java.lang.String, java.util.List, java.lang.String)
	 */
	protected void postProcess(boolean isEditedXmi, String coRecObjCsvFName,
			List<ContainerInterface> mainContainerList, String domainModelName)
			throws BizLogicException, DAOException, DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub

	}
}
