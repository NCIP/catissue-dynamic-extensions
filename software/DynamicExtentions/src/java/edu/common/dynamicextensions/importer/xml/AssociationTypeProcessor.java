
package edu.common.dynamicextensions.importer.xml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.common.dynamicextensions.domain.DomainObjectFactory;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.domaininterface.RoleInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.importer.jaxb.Association;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationDirection;
import edu.common.dynamicextensions.util.global.DEConstants.AssociationType;
import edu.common.dynamicextensions.util.global.DEConstants.Cardinality;
import edu.wustl.dynamicextensions.caching.metadata.ClassMetadata;
import edu.wustl.dynamicextensions.caching.metadata.ClassMetadataMap;
import edu.wustl.dynamicextensions.caching.metadata.PropertyMetadata;

public class AssociationTypeProcessor
{

	private EntityGroupInterface entityGroup;
	private ClassMetadataMap classMetadataMap;

	public AssociationTypeProcessor(ClassMetadataMap classMetadataMap,
			EntityGroupInterface entityGroup)
	{
		this.entityGroup = entityGroup;
		this.classMetadataMap = classMetadataMap;
	}

	public List<AssociationInterface> process(List<Association> associationsList) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{

		List<AssociationInterface> processedAssociations = new ArrayList<AssociationInterface>();
		for (Association association : associationsList)
		{
			// Step 1: Get the require objects
			ClassMetadata classMetadataImpl = classMetadataMap.getClassMetadata(association
					.getSourceEntityName());
			if(classMetadataImpl == null)
			{
				throw new DynamicExtensionsApplicationException("Missing association form "+association
						.getSourceEntityName()+" to "+association
						.getTargetEntityName()+", check the hbm files.");
			}
			
			PropertyMetadata associationMetadata = classMetadataImpl.getAssociation(association
					.getTargetEntityName());
			if(associationMetadata == null)
			{
				throw new DynamicExtensionsApplicationException("Missing association form "+association
						.getSourceEntityName()+" to "+association
						.getTargetEntityName()+", check the hbm files.");
			}

			//Step 2: Create association object
			AssociationInterface associationInterface = getAssocition(association);

			processedAssociations.add(associationInterface);
			if(associationInterface.getId() == null)
			{
				//Step 3: populate association
				associationInterface.setName(associationMetadata.getPropertyName());
				processDirection(association, associationMetadata, associationInterface);

				ConstraintPropertiesInterface constraintProperties = DynamicExtensionsUtility
						.getConstraintPropertiesForAssociation(associationInterface);
				associationInterface.setConstraintProperties(constraintProperties);
				setDefault(associationInterface);

			}
		}
		return processedAssociations;
	}

	private AssociationInterface getAssocition(Association association)
	{
		for(AssociationInterface associationInterface: entityGroup.getEntityByName(association.getSourceEntityName()).getAssociationCollection())
		{
			if(associationInterface.getTargetEntity().equals(entityGroup.getEntityByName(association.getTargetEntityName())))
			{
				return associationInterface;
			}
		}
		return DomainObjectFactory.getInstance()
		.createAssociation();
	}

	private RoleInterface getRole(AssociationType associationType, String name,
			Cardinality minCard, Cardinality maxCard)
	{
		RoleInterface role = DomainObjectFactory.getInstance().createRole();
		role.setAssociationsType(associationType);
		role.setName(name);
		role.setMinimumCardinality(minCard);
		role.setMaximumCardinality(maxCard);
		return role;
	}



	private void processDirection(Association association, PropertyMetadata associationMetadata,
			AssociationInterface associationInterface) throws DynamicExtensionsSystemException
	{
		entityGroup.getEntityByName(association
				.getSourceEntityName()).addAssociation(associationInterface);
		associationInterface.setEntity(entityGroup.getEntityByName(association
				.getSourceEntityName()));
		associationInterface.setTargetEntity(entityGroup.getEntityByName(association
				.getTargetEntityName()));

		if ("ManyToOne".equals(associationMetadata.getJoinType()))
		{
			associationInterface.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			associationInterface.setSourceRole(getRole(AssociationType.CONTAINTMENT,
					associationMetadata.getPropertyName(), Cardinality.ONE, Cardinality.MANY));
			associationInterface.setTargetRole(getRole(AssociationType.CONTAINTMENT,
					associationMetadata.getPropertyName(), Cardinality.ONE, Cardinality.ONE));

		}else if("OneToMany".equals(associationMetadata.getJoinType()))
		{
			associationInterface.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			associationInterface.setSourceRole(getRole(AssociationType.CONTAINTMENT,
					associationMetadata.getPropertyName(), Cardinality.ONE, Cardinality.ONE));
			associationInterface.setTargetRole(getRole(AssociationType.CONTAINTMENT,
					associationMetadata.getPropertyName(), Cardinality.ONE, Cardinality.MANY));
		}if("OneToOne".equals(associationMetadata.getJoinType()))
		{
			associationInterface.setAssociationDirection(AssociationDirection.SRC_DESTINATION);

			associationInterface.setSourceRole(getRole(AssociationType.CONTAINTMENT,
					associationMetadata.getPropertyName(), Cardinality.ONE, Cardinality.ONE));

			associationInterface.setTargetRole(getRole(AssociationType.CONTAINTMENT,
					associationMetadata.getRHSUniqueKeyPropertyName(), Cardinality.ONE, Cardinality.ONE));
		}
		
	}

	private void setDefault(AssociationInterface associationInterface)
	{

		/*associationInterface.setActivityStatus(Constants.ACTIVE);*/
		associationInterface.setIsSystemGenerated(false);
		associationInterface.setCreatedDate(new Date());
	}

}
