package edu.common.dynamicextensions.util;

import java.util.ArrayList;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.xmi.XMIConfiguration;
import edu.common.dynamicextensions.xmi.importer.AbstractXMIImporter;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.exception.DAOException;


public class CiderXMIImporter extends AbstractXMIImporter
{
	/**
	 * main method
	 * @param args commond line arguments
	 */
	public static void main(String[] args)
	{
		CiderXMIImporter xmiImporter = new CiderXMIImporter();
		xmiImporter.importXMI(args);
	}



	@Override
	protected XMIConfiguration getXMIConfigurationObject()
	{
		XMIConfiguration xmiConfiguration = XMIConfiguration.getInstance();
		xmiConfiguration.setEntityGroupSystemGenerated(false);
		xmiConfiguration.setDefaultPackagePrefix("");
		xmiConfiguration.setCreateTable(false);
		xmiConfiguration.setAddIdAttr(false);
		xmiConfiguration.setAddColumnForInherianceInChild(true);
		xmiConfiguration.setAddInheritedAttribute(true);
		// Not to validate XMI for caCore errors
		xmiConfiguration.setValidateXMI(false);
		return xmiConfiguration;
	}

	@Override
	protected List<AssociationInterface> getAssociationListForCurratedPath(
			HibernateDAO hibernatedao) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub
		return new ArrayList<AssociationInterface>();
	}



	@Override
	protected void postProcess(boolean isEditedXmi, String coRecObjCsvFName,
			List<ContainerInterface> mainContainerList, String domainModelName)
			throws BizLogicException, DAOException, DynamicExtensionsApplicationException
	{
		// TODO Auto-generated method stub

	}

}
