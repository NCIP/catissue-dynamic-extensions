
package edu.wustl.metadata.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsCacheException;
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

	/**
	 * Creates the object input stream.
	 *
	 * @return the clone input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @see edu.wustl.common.util.ObjectCloner#createObjectInputStream()
	 */
	@Override
	protected CloneInputStream createObjectInputStream() throws IOException
	{
		return new DyExtnResolveObjectInputStream(createInputStream());
	}

	/**
	 * Creates the object output stream.
	 *
	 * @return the clone output stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @see edu.wustl.common.util.ObjectCloner#createObjectOutputStream()
	 */
	@Override
	protected CloneOutputStream createObjectOutputStream() throws IOException
	{
		return new DyExtnReplaceObjectOutputStream(createOutputStream());
	}

	/**
	 * The Private Class DyExtnResolveObjectInputStream.
	 */
	private static class DyExtnResolveObjectInputStream extends CloneInputStream
	{

		/**
		 * Instantiates a new dy extn resolve object input stream.
		 *
		 * @param inputStream the input stream
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		DyExtnResolveObjectInputStream(final InputStream inputStream) throws IOException
		{
			super(inputStream);
			enableResolveObject(true);
		}

		/**
		 * Resolve object.
		 *
		 * @param obj the obj
		 * @return the object
		 * @throws IOException Signals that an I/O exception has occurred.
		 * @see java.io.ObjectInputStream#resolveObject(java.lang.Object)
		 */
		@Override
		protected Object resolveObject(final Object obj) throws IOException
		{
			Object resolveObj;
			if (obj instanceof Replacement)
			{
				try
				{
					resolveObj = ((Replacement) obj).getOrigObject();
				}
				catch (DynamicExtensionsCacheException e)
				{
					resolveObj = null;
				}
			}
			else
			{
				resolveObj = super.resolveObject(obj);
			}
			return resolveObj;
		}
	}

	/**
	 * The Inner Class DyExtnReplaceObjectOutputStream.
	 */
	private static class DyExtnReplaceObjectOutputStream extends CloneOutputStream
	{

		/**
		 * Instantiates a new dy extn replace object output stream.
		 *
		 * @param out
		 *            the out
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		public DyExtnReplaceObjectOutputStream(final OutputStream out) throws IOException
		{
			super(out);
			enableReplaceObject(true);
		}

		/**
		 * Replace object.
		 *
		 * @param obj
		 *            the obj
		 * @return the object
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 * @see java.io.ObjectOutputStream#replaceObject(java.lang.Object)
		 */
		@Override
		protected Object replaceObject(final Object obj) throws IOException
		{
			// commented cause of QueryableObject & QueryableAttributes
			// Introduction. Now hose will be
			// replaced with id & then retrieved from cache.
			Object replaceObj;
			if (obj instanceof QueryableAttributeInterface)
			{
				replaceObj = new ReplaceForQueryableAttribute((QueryableAttributeInterface) obj);
			}
			else if (obj instanceof QueryableObjectInterface)
			{
				replaceObj = new ReplaceForQueryableObject((QueryableObjectInterface) obj);
			}
			else if (obj instanceof EntityInterface)
			{
				replaceObj = new ReplacementForEntity((EntityInterface) obj);
			}
			else if (obj instanceof AttributeInterface)
			{
				replaceObj = new ReplacementForAttribute((AttributeInterface) obj);
			}
			else if (obj instanceof AssociationInterface)
			{
				replaceObj = new ReplacementForAssociation((AssociationInterface) obj);
			}
			else
			{
				replaceObj = super.replaceObject(obj);
			}
			return replaceObj;
		}
	}

	/**
	 * The Private Interface Replacement.
	 *
	 * @param <D> the generic type
	 */
	private interface Replacement<D>
	{

		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		Long getId();

		/**
		 * Gets the orig object.
		 *
		 * @return the orig object
		 * @throws DynamicExtensionsCacheException
		 */
		D getOrigObject() throws DynamicExtensionsCacheException;
	}

	/**
	 * The Class ReplaceForQueryableObject.
	 */
	private static class ReplaceForQueryableObject
			implements
				Serializable,
				Replacement<QueryableObjectInterface>
	{

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -1324920205387970975L;

		/** The identifier. */
		private final Long identifier;

		/**
		 * Instantiates a new replace for queryable object.
		 *
		 * @param entity the entity
		 */
		ReplaceForQueryableObject(final QueryableObjectInterface entity)
		{
			identifier = entity.getId();
		}

		/**
		 * Gets the id.
		 *
		 * @return the id
		 * @see edu.wustl.metadata.util.DyExtnObjectCloner.Replacement#getId()
		 */
		public Long getId()
		{
			return identifier;
		}

		/** (non-Javadoc)
		 * @see edu.wustl.metadata.util.DyExtnObjectCloner.Replacement#getOrigObject()
		 */
		public QueryableObjectInterface getOrigObject()
		{
			return QueryableObjectUtility.getQueryableObjectFromCache(identifier);
		}
	};

	/**
	 * The Class ReplaceForQueryableAttribute.
	 */
	private static class ReplaceForQueryableAttribute
			implements
				Serializable,
				Replacement<QueryableAttributeInterface>
	{

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -1324920205387970975L;

		/** The identifier. */
		private final Long identifier;

		/**
		 * Instantiates a new replace for queryable attribute.
		 *
		 * @param attribute the attribute
		 */
		ReplaceForQueryableAttribute(final QueryableAttributeInterface attribute)
		{
			identifier = attribute.getId();
		}

		/**
		 * Gets the id.
		 *
		 * @return the id
		 * @see edu.wustl.metadata.util.DyExtnObjectCloner.Replacement#getId()
		 */
		public Long getId()
		{
			return identifier;
		}

		/**
		 * Gets the orig object.
		 *
		 * @return the orig object
		 * @see edu.wustl.metadata.util.DyExtnObjectCloner.Replacement#getOrigObject()
		 */
		public QueryableAttributeInterface getOrigObject()
		{
			return QueryableObjectUtility.getQueryableAttributeFromCache(identifier);
		}
	};

	/**
	 * The Class ReplacementForEntity.
	 */
	private static class ReplacementForEntity implements Serializable, Replacement<EntityInterface>
	{

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -1324920205387970975L;

		/** The identifier. */
		private final Long identifier;

		/**
		 * Instantiates a new replacement for entity.
		 *
		 * @param entity the entity
		 */
		ReplacementForEntity(final EntityInterface entity)
		{
			identifier = entity.getId();
		}

		/**
		 * Gets the id.
		 *
		 * @return the id
		 * @see edu.wustl.metadata.util.DyExtnObjectCloner.Replacement#getId()
		 */
		public Long getId()
		{
			return identifier;
		}

		/**
		 * Gets the orig object.
		 *
		 * @return the orig object
		 * @throws DynamicExtensionsCacheException
		 * @see edu.wustl.metadata.util.DyExtnObjectCloner.Replacement#getOrigObject()
		 */
		public EntityInterface getOrigObject() throws DynamicExtensionsCacheException
		{
			return EntityCache.getInstance().getEntityByIdForCacheUpdate(identifier);
		}
	};

	/**
	 * The Class ReplacementForAttribute.
	 */
	private static class ReplacementForAttribute
			implements
				Serializable,
				Replacement<AttributeInterface>
	{

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 9194062618296956803L;

		/** The identifier. */
		private final Long identifier;

		/**
		 * Instantiates a new replacement for attribute.
		 *
		 * @param attr the attr
		 */
		ReplacementForAttribute(final AttributeInterface attr)
		{
			identifier = attr.getId();
		}

		/**
		 * Gets the id.
		 *
		 * @return the id
		 * @see edu.wustl.metadata.util.DyExtnObjectCloner.Replacement#getId()
		 */
		public Long getId()
		{
			return identifier;
		}

		/**
		 * Gets the orig object.
		 *
		 * @return the orig object
		 * @see edu.wustl.metadata.util.DyExtnObjectCloner.Replacement#getOrigObject()
		 */
		public AttributeInterface getOrigObject()
		{
			return EntityCache.getInstance().getAttributeById(identifier);
		}

	};

	/**
	 * The Class ReplacementForAssociation.
	 */
	private static class ReplacementForAssociation
			implements
				Serializable,
				Replacement<AssociationInterface>
	{

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 6784553723374640799L;

		/** The identifier. */
		private final Long identifier;

		/**
		 * Instantiates a new replacement for association.
		 *
		 * @param assoc the assoc
		 */
		ReplacementForAssociation(final AssociationInterface assoc)
		{
			identifier = assoc.getId();
		}

		/**
		 * Gets the id.
		 *
		 * @return the id
		 * @see edu.wustl.metadata.util.DyExtnObjectCloner.Replacement#getId()
		 */
		public Long getId()
		{
			return identifier;
		}

		/**
		 * Gets the orig object.
		 *
		 * @return the orig object
		 * @see edu.wustl.metadata.util.DyExtnObjectCloner.Replacement#getOrigObject()
		 */
		public AssociationInterface getOrigObject()
		{
			return EntityCache.getInstance().getAssociationById(identifier);
		}
	};
}
