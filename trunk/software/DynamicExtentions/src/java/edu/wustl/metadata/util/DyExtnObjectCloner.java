
package edu.wustl.metadata.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.common.querysuite.querableobject.QueryableObjectUtility;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableAttributeInterface;
import edu.wustl.common.querysuite.querableobjectinterface.QueryableObjectInterface;
import edu.wustl.common.util.ObjectCloner;

/**
 * Used for cloning objects that contain references to Dynamic Extensions (DE)
 * entity, association or attribute. This class customizes the serialization
 * process of <tt>ObjectCloner</tt> to only write out the id of the DE object.
 * It correspondingly customizes the deserialization process to obtain the DE
 * object, based on its id, from <tt>AbstractEntityCache</tt>.
 * 
 * @author srinath_k
 * 
 */
public class DyExtnObjectCloner extends ObjectCloner
{

	@Override
	protected CloneInputStream createObjectInputStream() throws IOException
	{
		return new DyExtnResolveObjectInputStream(createInputStream());
	}

	@Override
	protected CloneOutputStream createObjectOutputStream() throws IOException
	{
		return new DyExtnReplaceObjectOutputStream(createOutputStream());
	}

	private static class DyExtnResolveObjectInputStream extends CloneInputStream
	{

		DyExtnResolveObjectInputStream(InputStream inputStream) throws IOException
		{
			super(inputStream);
			enableResolveObject(true);
		}

		@Override
		protected Object resolveObject(Object obj) throws IOException
		{
			if (obj instanceof Replacement)
			{
				return ((Replacement) obj).getOrigObject();
			}
			return super.resolveObject(obj);
		}
	}

	private static class DyExtnReplaceObjectOutputStream extends CloneOutputStream
	{

		public DyExtnReplaceObjectOutputStream(OutputStream out) throws IOException
		{
			super(out);
			enableReplaceObject(true);
		}

		@Override
		protected Object replaceObject(Object obj) throws IOException
		{
			//commented cause of  QueryableObject & QueryableAttributes Introduction. Now hose will be 
			//replaced with id & then retrieved from cache.
			if (obj instanceof QueryableAttributeInterface)
			{
				return new ReplaceForQueryableAttribute((QueryableAttributeInterface) obj);
			}
			if (obj instanceof QueryableObjectInterface)
			{
				return new ReplaceForQueryableObject((QueryableObjectInterface) obj);
			}
			if (obj instanceof EntityInterface)
			{
				return new ReplacementForEntity((EntityInterface) obj);
			}
			if (obj instanceof AttributeInterface)
			{
				return new ReplacementForAttribute((AttributeInterface) obj);
			}
			if (obj instanceof AssociationInterface)
			{
				return new ReplacementForAssociation((AssociationInterface) obj);
			}
			return super.replaceObject(obj);
		}
	}

	private interface Replacement<D>
	{

		Long getId();

		D getOrigObject();
	}

	private static class ReplaceForQueryableObject
			implements
				Serializable,
				Replacement<QueryableObjectInterface>
	{

		private static final long serialVersionUID = -1324920205387970975L;

		private final Long identifier;

		ReplaceForQueryableObject(QueryableObjectInterface entity)
		{
			this.identifier = entity.getId();
		}

		public Long getId()
		{
			return identifier;
		}

		public QueryableObjectInterface getOrigObject()
		{
			return QueryableObjectUtility.getQueryableObjectFromCache(identifier);
		}
	};

	private static class ReplaceForQueryableAttribute
			implements
				Serializable,
				Replacement<QueryableAttributeInterface>
	{

		private static final long serialVersionUID = -1324920205387970975L;

		private final Long identifier;

		ReplaceForQueryableAttribute(QueryableAttributeInterface attribute)
		{
			this.identifier = attribute.getId();
		}

		public Long getId()
		{
			return identifier;
		}

		public QueryableAttributeInterface getOrigObject()
		{
			return QueryableObjectUtility.getQueryableAttributeFromCache(identifier);
		}
	};

	private static class ReplacementForEntity implements Serializable, Replacement<EntityInterface>
	{

		private static final long serialVersionUID = -1324920205387970975L;

		private final Long identifier;

		ReplacementForEntity(EntityInterface entity)
		{
			this.identifier = entity.getId();
		}

		public Long getId()
		{
			return identifier;
		}

		public EntityInterface getOrigObject()
		{
			return EntityCache.getInstance().getEntityById(identifier);
		}
	};

	private static class ReplacementForAttribute
			implements
				Serializable,
				Replacement<AttributeInterface>
	{

		private static final long serialVersionUID = 9194062618296956803L;

		private final Long identifier;

		ReplacementForAttribute(AttributeInterface attr)
		{
			this.identifier = attr.getId();
		}

		public Long getId()
		{
			return identifier;
		}

		public AttributeInterface getOrigObject()
		{
			return EntityCache.getInstance().getAttributeById(identifier);
		}

	};

	private static class ReplacementForAssociation
			implements
				Serializable,
				Replacement<AssociationInterface>
	{

		private static final long serialVersionUID = 6784553723374640799L;

		private final Long identifier;

		ReplacementForAssociation(AssociationInterface assoc)
		{
			this.identifier = assoc.getId();
		}

		public Long getId()
		{
			return identifier;
		}

		public AssociationInterface getOrigObject()
		{
			return EntityCache.getInstance().getAssociationById(identifier);
		}
	};

}
