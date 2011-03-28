package edu.common.dynamicextensions.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.global.Variables;
import edu.common.dynamicextensions.xmi.XMIConfiguration;
import edu.common.dynamicextensions.xmi.importer.AbstractXMIImporter;
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
			if (args.length > 3 && !"none".equalsIgnoreCase(args[3]))
			{
				args[3]="edu.wustl.catissuecore.domain.RecordEntry";
			}
			Variables.serverUrl=args[7];
			XMIImporter xmiImporter = new XMIImporter();
			xmiImporter.importXMI(args);
		}



		@Override
		protected XMIConfiguration getXMIConfigurationObject()
		{
			XMIConfiguration xmiConfiguration = XMIConfiguration.getInstance();
			xmiConfiguration.setEntityGroupSystemGenerated(false);
			xmiConfiguration.setCreateTable(true);
			xmiConfiguration.setAddIdAttr(true);
			xmiConfiguration.setAddActivityStatusAttribute(true);
			xmiConfiguration.setAddColumnForInherianceInChild(false);
			xmiConfiguration.setAddInheritedAttribute(false);
			xmiConfiguration.setValidateXMI(true);
			return xmiConfiguration;
		}

		@Override
		protected List<AssociationInterface> getAssociationListForCurratedPath(
				HibernateDAO hibernatedao) throws DynamicExtensionsSystemException,
				DynamicExtensionsApplicationException
		{

			Collection<AssociationInterface> associations = null;
			EntityManagerInterface entityManager = EntityManager.getInstance();
			Long recordEtryEntity = entityManager.getEntityId("edu.wustl.catissuecore.domain.RecordEntry");
			Long eventVisitEntry = entityManager.getEntityId("edu.wustl.catissuecore.domain.EventVisit");
			if (hibernatedao == null)
			{
				associations = entityManager.getAssociations(eventVisitEntry,recordEtryEntity);
			}
			else
			{
				associations = entityManager.getAssociations(eventVisitEntry, recordEtryEntity, hibernatedao);
			}

			ArrayList<AssociationInterface> associationList = new ArrayList<AssociationInterface>();
			associationList.addAll(associations);
			return associationList;
		}

		@Override
		protected void postProcess(boolean isEditedXmi, String coRecObjCsvFName,
				List<ContainerInterface> mainContainerList, String domainModelName)
				throws BizLogicException, DAOException, DynamicExtensionsApplicationException
		{
			// TODO Auto-generated method stub

		}
}
