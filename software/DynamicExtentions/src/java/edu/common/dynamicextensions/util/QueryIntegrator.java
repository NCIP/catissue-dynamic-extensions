package edu.common.dynamicextensions.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.xmi.AnnotationUtil;
import edu.common.dynamicextensions.xmi.PathObject;
import edu.common.dynamicextensions.xmi.XMIUtilities;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.exception.DAOException;

public class QueryIntegrator {

	private static final Logger LOGGER = Logger.getCommonLogger(QueryIntegrator.class);
	/**
	 * It will add the Query paths for all the Entities & if addQueryPahs argument was
	 * true then it will add the currated paths from clinicalStudyRegistration to mainConatainers.
	 * @param hookEntityName2
	 * @param hibernatedao dao used for retrieving the ids of entities.
	 * @param jdbcdao dao used to insert the path.
	 * @param isEntGrpSysGented specifies weather the entityGroup is system generated or not.
	 * @param mainContainerList list of main containers for which the paths to be added.
	 * @throws DynamicExtensionsSystemException exception
	 * @throws DynamicExtensionsApplicationException exception
	 * @throws BizLogicException exception
	 * @throws DAOException exception
	 */
	public void addQueryPaths(Boolean addQueryPaths , String hookEntityName, HibernateDAO hibernatedao, JDBCDAO jdbcdao,
			boolean isEntGrpSysGented, List<ContainerInterface> mainContainerList)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			BizLogicException, DAOException
	{
		if (addQueryPaths)
		{
				LOGGER.info("Now Adding Query Paths ....");
				if (hookEntityName.equalsIgnoreCase("None"))
				{
					LOGGER.info("Main Container list size " + mainContainerList.size());
					Set<PathObject> processedPathList = new HashSet<PathObject>();
					AnnotationUtil.setHookEntityId(null);
					addQueryPathsWithoutHookEntity(jdbcdao, isEntGrpSysGented, mainContainerList,
							processedPathList);

				}
				else
				{
					addQueryPathsWithHookEntity(hookEntityName, hibernatedao,
							jdbcdao, mainContainerList);

				}
/*
				LOGGER.info("Now adding CSR query paths for entities....");
				List<AssociationInterface> associationList = getAssociationListForCurratedPath(hibernatedao);
				UpdateCSRToEntityPath.addCuratedPathsFromToAllEntities(associationList,XMIUtilities.getXMIConfigurationObject()
						.getNewEntitiesIds());
*/			}
	}

	/**
	 * @param hookEntityName
	 * @param hibernatedao
	 * @param jdbcdao
	 * @param mainContainerList
	 * @throws DAOException
	 * @throws DynamicExtensionsApplicationException
	 * @throws DynamicExtensionsSystemException
	 * @throws BizLogicException
	 */
	private void addQueryPathsWithHookEntity(String hookEntityName,
			HibernateDAO hibernatedao, JDBCDAO jdbcdao,
			List<ContainerInterface> mainContainerList) throws DAOException,
			DynamicExtensionsApplicationException,
			DynamicExtensionsSystemException, BizLogicException {
		EntityInterface staticEntity = XMIUtilities.getStaticEntity(hookEntityName,
				hibernatedao);
		for (ContainerInterface mainContainer : mainContainerList)
		{
			AnnotationUtil.addNewPathsForExistingMainContainers(staticEntity,
					((EntityInterface) mainContainer.getAbstractEntity()), true, jdbcdao,
					staticEntity);
		}
	}

	/**
	 * It will add the Query paths for which no hook entity is specified.
	 * @param jdbcdao dao used to insert the path.
	 * @param isEntGrpSysGented specifies weather the entityGroup is system generated or not.
	 * @param mainContainerList list of main containers for which the paths to be added.
	 * @param processedPathList list of the paths which are already added.
	 * @throws BizLogicException exception
	 * @throws DynamicExtensionsSystemException exception.
	 */
	private void addQueryPathsWithoutHookEntity(JDBCDAO jdbcdao, boolean isEntGrpSysGented,
			List<ContainerInterface> mainContainerList, Set<PathObject> processedPathList)
			throws BizLogicException, DynamicExtensionsSystemException
	{
		for (ContainerInterface mainContainer : mainContainerList)
		{
			AnnotationUtil.addQueryPathsForAllAssociatedEntities(((EntityInterface) mainContainer
					.getAbstractEntity()), null, null, processedPathList, isEntGrpSysGented,
					jdbcdao);
		}

		// Following will add Parent Entity's association paths to child Entity also.
		for (ContainerInterface mainContainer : mainContainerList)
		{
			AnnotationUtil.addInheritancePathforSystemGenerated(((EntityInterface) mainContainer
					.getAbstractEntity()));
		}
	}
/*	protected List<AssociationInterface> getAssociationListForCurratedPath(
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
	}*/
}
