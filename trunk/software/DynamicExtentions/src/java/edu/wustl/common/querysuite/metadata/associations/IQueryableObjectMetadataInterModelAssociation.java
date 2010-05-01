package edu.wustl.common.querysuite.metadata.associations;

import edu.wustl.common.querysuite.querableobjectinterface.QueryableAttributeInterface;

/**
 * This class is used in the Query which is supporting the QueryableObject(i.e the
 * Query with the Category ). it is the replacement interface for 'IMetadataInterModelAssociation'
 * Interface for the Query project.
 * @author pavan_kalantri
 *
 */

public interface IQueryableObjectMetadataInterModelAssociation
{
	/**
     * @return the QueryaleAttribute from source entity.
     */
    QueryableAttributeInterface getSourceAttribute();

    /**
     * 
     * @return the QueryableAttribute from target entity.
     */
    QueryableAttributeInterface getTargetAttribute();
}
