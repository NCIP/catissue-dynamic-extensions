
package edu.common.dynamicextensions.importer.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.xml.sax.SAXException;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.importer.jaxb.Association;
import edu.common.dynamicextensions.importer.jaxb.Entity;
import edu.common.dynamicextensions.importer.jaxb.StaticMetaData;
import edu.common.dynamicextensions.util.DynamicExtensionsUtility;
import edu.common.dynamicextensions.util.XMLUtility;
import edu.common.dynamicextensions.xmi.AnnotationUtil;
import edu.common.dynamicextensions.xmi.PathObject;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.dao.HibernateDAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dynamicextensions.caching.metadata.ClassMetadataMap;
import edu.wustl.dynamicextensions.caching.metadata.impl.hbm.ClassMetadataMapImpl;
import edu.wustl.dynamicextensions.caching.util.HibernateDaoHelper;

public class XMLImporter
{

	protected EntityGroupInterface entityGroup;
	protected StaticMetaData staticMetaData;
	private ClassMetadataMap classMetadataMap;
	
	public static void main(String[] args) throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		validate(args);
		XMLImporter importer = new XMLImporter(args[0], args[1],args[2], null);
		importer.processXml();
	}

	private static void validate(String[] args) throws DynamicExtensionsApplicationException
	{
		if(args.length > 2)
		{
			throw new DynamicExtensionsApplicationException("Infufficient parametres");
		}
	}

	public XMLImporter(String filePath,String xmlFileName, String entityGroupName, HibernateDAO hostApplicationDAO)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		final String packageName = StaticMetaData.class.getPackage().getName();
		JAXBElement jAXBElement;
		try
		{
			jAXBElement = (JAXBElement) XMLUtility
					.getJavaObjectForXML(xmlFileName, filePath, packageName, "StaticMetaData.xsd");
			staticMetaData = (StaticMetaData) jAXBElement.getValue();
			entityGroup = EntityGroupManager.getInstance().getEntityGroupByName(entityGroupName);

			if(hostApplicationDAO == null)
			{
				hostApplicationDAO =DynamicExtensionsUtility.getHostAppHibernateDAO(null); 
			}
			classMetadataMap = ClassMetadataMapImpl.createClassMetadataMap(HibernateDaoHelper
					.getSessionFactory(hostApplicationDAO), getClassNames(staticMetaData.getEntity(),staticMetaData.getAssociation()));
		}
		catch (SAXException e)
		{
			throw new DynamicExtensionsSystemException("Error generating object using xml:"
					+ filePath, e);
		}

	}

	private Collection<String> getClassNames(List<Entity> entityCollection,List<Association> associationCollection)
	{
		List<String> list = new ArrayList<String>();
		for (Entity entity : entityCollection)
		{
			list.add(entity.getName());
		}
		for (Association association: associationCollection)
		{
			list.add(association.getSourceEntityName());
			list.add(association.getTargetEntityName());
		}
		return list;
	}

	public void processXml() throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
	
		//Step1: Process EntityList
		EntityTypeProcessor entityTypeProcessor = new EntityTypeProcessor(classMetadataMap,
				entityGroup);
		entityTypeProcessor.process(staticMetaData.getEntity());

		//Step2: Process AssociationList
		AssociationTypeProcessor associationProcessor = new AssociationTypeProcessor(
				classMetadataMap, entityGroup);
		List<AssociationInterface> associationList = associationProcessor.process(staticMetaData.getAssociation());
		
		//Step3: persist entity group
		EntityGroupManager.getInstance().persistEntityGroupMetadata(entityGroup);

		//Step4: Add query paths
		handleQueryPaths(associationList);
	}
	private void handleQueryPaths(List<AssociationInterface> associationList) throws DynamicExtensionsSystemException
	{
		JDBCDAO jdbcdao = DynamicExtensionsUtility.getJDBCDAO(null);
		Set<PathObject> processedPathList = new HashSet<PathObject>();
		for(AssociationInterface association: associationList)
		{
			try
			{
				AnnotationUtil.setHookEntityId(null);
				AnnotationUtil.addQueryPathsForAllAssociatedEntities(association.getTargetEntity(), association.getEntity(), association.getId(), processedPathList, false,
						jdbcdao);
			}
			catch (BizLogicException e)
			{
				throw new DynamicExtensionsSystemException("Error adding query paths ", e);
			}

			AnnotationUtil.addInheritancePathforSystemGenerated(association.getTargetEntity());
		}
	}
}
