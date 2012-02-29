
package edu.wustl.dynamicextensions.caching.metadata.impl.hbm;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.persister.entity.AbstractEntityPersister;

import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dynamicextensions.caching.metadata.ClassMetadata;
import edu.wustl.dynamicextensions.caching.metadata.ClassMetadataMap;
import edu.wustl.dynamicextensions.caching.metadata.PropertyMetadata;

/**
 *
 * @author Vinayak Pawar (vinayak.pawar@krishagni.com)
 */
public class ClassMetadataMapImpl implements ClassMetadataMap
{

	private static final Logger LOGGER = Logger.getCommonLogger(ClassMetadataMapImpl.class);
	
	private Map<String, ClassMetadata> classMetadataMap = new HashMap<String, ClassMetadata>();

	private Set<String> tableNames = new HashSet<String>();

	private ClassMetadataMapImpl(SessionFactory factory)
	{
		initialize(factory);
	}

	public ClassMetadataMapImpl(SessionFactory factory, Collection<String> classNames) throws DynamicExtensionsApplicationException
	{
		initialize(factory, classNames);
	}

	private void initialize(SessionFactory factory, Collection<String> classNames)
	{

		for (String className : classNames)
		{
			AbstractEntityPersister persister = (AbstractEntityPersister) factory
					.getClassMetadata(className);
			if(persister == null)
			{
				LOGGER.warn("Missing metadata for class" + className);
				continue;
			}
			updateClassMetadataMap(persister);
		}
	}

	public static ClassMetadataMap createClassMetadataMap(SessionFactory factory)
	{
		return new ClassMetadataMapImpl(factory);
	}

	public static ClassMetadataMap createClassMetadataMap(SessionFactory factory,
			Collection<String> classNames) throws DynamicExtensionsApplicationException
	{
		return new ClassMetadataMapImpl(factory, classNames);
	}

	public String[] getClassNames()
	{
		return classMetadataMap.keySet().toArray(new String[0]);
	}

	public String[] getTableNames()
	{
		return tableNames.toArray(new String[0]);
	}

	public ClassMetadata getClassMetadata(String className)
	{
		return classMetadataMap.get(className);
	}

	public int getClassCount()
	{
		return classMetadataMap.size();
	}

	public int getTableCount()
	{
		return tableNames.size();
	}

	public void clear()
	{
		classMetadataMap.clear();
		tableNames.clear();
	}

	private void initialize(SessionFactory factory)
	{
		Map hbClassMetadataMap = factory.getAllClassMetadata();
		for (Object persisterObj : hbClassMetadataMap.values())
		{
			AbstractEntityPersister persister = (AbstractEntityPersister) persisterObj;
			updateClassMetadataMap(persister);
		}
	}

	private void updateClassMetadataMap(AbstractEntityPersister persister)
	{
		ClassMetadata classMetadata = new ClassMetadataImpl(persister);
		this.classMetadataMap.put(classMetadata.getClassName(), classMetadata);

		tableNames.add(classMetadata.getTableName());
		for (PropertyMetadata propertyMetadata : classMetadata.getPropertiesMetadata())
		{
			if (propertyMetadata.isAssociation() || propertyMetadata.isCustomType())
			{
				String joinTableName = propertyMetadata.getJoinTableName();
				if (joinTableName != null)
				{
					tableNames.add(joinTableName);
				}
			}
		}
	}
}