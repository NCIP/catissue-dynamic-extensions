package edu.common.dynamicextensions.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.xmi.AnnotationUtil;
import edu.common.dynamicextensions.xmi.PathObject;
import edu.common.dynamicextensions.xmi.UpdateCSRToEntityPath;
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

				LOGGER.info("Now adding CSR query paths for entities....");
				List<AssociationInterface> associationList = getAssociationListForCurratedPath(hookEntityName,
						hibernatedao);
				UpdateCSRToEntityPath.addCuratedPathsFromToAllEntities(associationList,XMIUtilities.getXMIConfigurationObject()
						.getNewEntitiesIds());
			}
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
	protected List<AssociationInterface> getAssociationListForCurratedPath(
			String hookEntityName, HibernateDAO hibernatedao) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException
	{

		List<AssociationInterface> associationList = new ArrayList<AssociationInterface>();
		EntityManagerInterface entityManager = EntityManager.getInstance();


		String srcEntityName= null;

		if(hookEntityName.equals("edu.wustl.catissuecore.domain.deintegration.ParticipantRecordEntry")){
			srcEntityName="edu.wustl.catissuecore.domain.Participant";
		}
		else if(hookEntityName.equals("edu.wustl.catissuecore.domain.deintegration.SCGRecordEntry")){
			srcEntityName="edu.wustl.catissuecore.domain.SpecimenCollectionGroup";
		}
		else {
			srcEntityName="edu.wustl.catissuecore.domain.Specimen";
		}
		long srcEntityId = entityManager.getEntityId(srcEntityName);
		long hookEntityId = entityManager.getEntityId(hookEntityName);
		AssociationInterface recordEntryAssociation = getAssociationByTargetRole(srcEntityId,hookEntityId,
				 "recordEntryCollection", hibernatedao);
		if (recordEntryAssociation == null)
		{
			throw new DynamicExtensionsApplicationException(
					"Associations For Currated Path Not Found");
		}
		associationList.add(recordEntryAssociation);
		return associationList;
	}

	/**
	 * It will search the DE association whose source & target entity is as given in parameters &
	 * target role of the association also as given. else will return null
	 * @param srcEntityId source entity id of association
	 * @param tgtEntityId target entity id of association
	 * @param targetRoleName target role of association
	 * @param hibernatedao dao used for retrieving the association.
	 * @return the found association
	 * @throws DynamicExtensionsSystemException exception
	 * @throws DynamicExtensionsApplicationException exception
	 */
	private static AssociationInterface getAssociationByTargetRole(Long srcEntityId,
			Long tgtEntityId, String targetRoleName, HibernateDAO hibernatedao)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		AssociationInterface deAsssociation = null;
		Collection<AssociationInterface> associations = null;
		EntityManagerInterface entityManager = EntityManager.getInstance();

		if (hibernatedao == null)
		{
			associations = entityManager.getAssociations(srcEntityId, tgtEntityId);
		}
		else
		{
			associations = entityManager.getAssociations(srcEntityId, tgtEntityId, hibernatedao);
		}

		for (AssociationInterface association : associations)
		{
			if (association.getTargetRole().getName().equalsIgnoreCase(targetRoleName))
			{
				deAsssociation = association;
				break;
			}
		}
		return deAsssociation;
	}
}
