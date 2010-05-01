
package edu.wustl.common.querysuite.querableobjectinterface;

import java.io.Serializable;
import java.util.Collection;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.TaggedValueInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.TablePropertiesInterface;
import edu.common.dynamicextensions.util.global.DEConstants.InheritanceStrategy;

/**
 * This interface is designed to create a wrapper on the Category & Entity .
 * This Interface will provide the methods which are required to create IQUERY object from
 * the Entity as well as Category. In case of category it will internally do the processing
 * and return the results in the format which Entity returns.   
 * @author pavan_kalantri
 *
 */
public interface QueryableObjectInterface extends Serializable
{

	/**
	 * In case of Entity it will return collection of QueryableAttributeInterface which 
	 * contain QueryableAttribute for each Attribute of the Entity including its inherited
	 * local attributes as well as the Attributes of its parent & so on.
	 *
	 * In case of Category it will return the Collection of QueryableAttributeInterface which
	 * contains QueryableAttribute for Each CategoryAttribute in that Category .
	 * @return collection of QueryableAttributeInterface.
	 */
	Collection<QueryableAttributeInterface> getQueryableAttributeColl();

	/**
	 * In case of Entity it will return the Collection of QueryableObjectInterface which contains
	 * same Object on which we have called this method.
	 * 
	 * In case of Category it will return the Collection of QueryableObjectInterface which contains
	 * Queryable object for Each Entity from which the categoryEntity's are created & added in the
	 * Category. 
	 * @return collection of QueryableObjectInterface.
	 */
	Collection<QueryableObjectInterface> getEntityCollection();

	/**
	 * In case of Entity it will return the Collection of QueryableAttributeInterface which 
	 * contains the QueryableAttributes for each attribute present in that Entity , its parent Entity 
	 * and so on excluding its inherited local attribute.
	 * 
	 * In case of Category it will return the Collection of QueryableAttributeInterface which
	 * contains QueryableAttribute for Each CategoryAttribute in that Category .
	 * @return collection of QueryableAttributeInterface.
	 */
	Collection<QueryableAttributeInterface> getAllAttributes();

	/**
	 * In case of Entity it will search the QueryableAttribute which is 
	 * created from the Attribute(Attributes which are of the same Entity of its parent & are also  
	 * inherited ) having id same as given identfier. 
	 * 
	 * In case of Category it will search QueryableAttribute which is created from the 
	 * all the categoryAttributes in that category and the categoryAttribute having Id same as
	 * given identifier.
	 * @param identifier 
	 * @return QueryableAttributeInterface whose attribute or categoryAttribute has id same as identifier.
	 */
	QueryableAttributeInterface getAttributeByIdentifier(Long identifier);

	/**
	 * In case of Entity it will search the QueryableAttribute which is 
	 * created from the Attribute(of entity as well as its parent entity but not inherited
	 * local attributes of that entity ) having name same as given attributeName. 
	 * 
	 * In case of Category it will search QueryableAttribute which is created from the 
	 * all the categoryAttributes in that category and the categoryAttribute having name same as
	 * given attributeName.
	 * @param attributeName
	 * @return QueryableAttributeInterface whose attribute or categoryAttribute has name same as attributeName.
	 */
	QueryableAttributeInterface getAttributeByName(String attributeName);

	/**
	 * In case of Entity it will return the Collection of QueryableAttributeInterface which 
	 * contains the QueryableAttributes for each attribute present in that Entity 
	 * excluding its inherited local attribute.
	 * 
	 * In case of Category it will return the Collection of QueryableAttributeInterface which
	 * contains QueryableAttribute for Each CategoryAttribute in that Category .
	 * @return collection of QueryableAttributeInterface.
	 */
	Collection<QueryableAttributeInterface> getAttributeCollection();

	/**
	 * In case of Entity it will search the QueryableAttribute which is 
	 * created from the Attribute(of entity including inherited
	 * local attributes of that entity but not of its parent entity & so on) 
	 * having name same as given attributeName. 
	 * 
	 * In case of Category it will search QueryableAttribute which is created from the 
	 * all the categoryAttributes in that category and the categoryAttribute having name same as
	 * given attributeName.
	 * @param attributeName
	 * @return QueryableAttributeInterface whose attribute or categoryAttribute has name same as attributeName.
	 */
	QueryableAttributeInterface getEntityAttributeByName(String attributeName);

	/**
	 * In case of Entity it will search the QueryableAttribute which is 
	 * created from the Attribute(of entity including attributes of
	 * its parent entity & so on but not the inherited local attributes of that entity). 
	 *  
	 * In case of Category it will search QueryableAttribute which is created from the 
	 * all the categoryAttributes in that category 
	 * @return collection QueryableAttributeInterface.
	 */
	Collection<QueryableAttributeInterface> getEntityAttributesForQuery();

	/**
	 * In case of Entity it will search the QueryableAttribute which is 
	 * created from the Attribute(of entity including inherited
	 * local attributes of that entity but not of its parent entity & so on) 
	 * having name same as given attributeName. 
	 * 
	 * In case of Category it will search QueryableAttribute which is created from the 
	 * all the categoryAttributes in that category and the categoryAttribute having name same as
	 * given attributeName.
	 * If found will return true
	 * @param attributeName
	 * @return true if attribute or categoryAttribute is found with name as attributeName else false.
	 */
	boolean isAttributePresent(String attributeName);

	/**
	 * It will return the Description of the Entity or the Category from which this
	 * QueryableObject is created.
	 * @return description
	 */
	String getDescription();

	/**
	 * It will return the Id of the Entity or the Category from which this object is created.
	 * @return id
	 */
	Long getId();

	/**
	 * It will return the name of the Entity or the Category from which this object is created. 
	 * @return
	 */
	String getName();

	/**
	 * It checks weather tag with the given key is present in the Entity or Category.
	 * @param key
	 * @return
	 */
	boolean isTagPresent(String key);

	/**
	 * It will return the TaggedValueCollection of the Entity or category from 
	 * which this object is created 
	 * @return collection of taggedvalues
	 */
	Collection<TaggedValueInterface> getTaggedValueCollection();

	/**
	 * In case of QueryableEntity it will return the Entity from which this QueryableObject was created.
	 * In case of QueryableCategory it will Exception. 
	 * @return
	 */
	EntityInterface getEntity();

	/**
	 * In case of QueryableEntity it will return the QueryableObject which is created from the 
	 * parent entity of the Entity from which this object is created.
	 * In case of QueryableCategory it will Exception. 
	 * @return
	 */
	QueryableObjectInterface getParentEntity();

	/**
	 * In case of QueryableEntity it will return the inheritance strategy of the Entity from 
	 * which this QueryableObject was created.
	 * In case of QueryableCategory it will Exception. 
	 * @return
	 */
	InheritanceStrategy getInheritanceStrategy();

	/**
	 * In case of QueryableEntity it will return the tableProperties of the Entity from 
	 * which this QueryableObject was created.
	 * In case of QueryableCategory it will Exception. 
	 * @return
	 */
	TablePropertiesInterface getTableProperties();

	/**
	 * In case of QueryableEntity it will return the Entity from which this QueryableObject was create
	 * In case of QueryableCategory it will Exception. 
	 * @return
	 */
	String getTaggedValue(String key);

	/**
	 * In case of QueryableEntity it will return the tagged value of the tag having the key as given
	 * parameter on the Entity from which this QueryableObject was created.
	 * In case of QueryableCategory it will return the tagged value of the tag having the key as given
	 * parameter on the category from which this QueryableObject was created. 
	 * @return
	 */
	String getDiscriminatorColumn();

	/**
	 * In case of QueryableEntity it will return the Discriminator value of the Entity from 
	 * which this QueryableObject was created.
	 * In case of QueryableCategory it will Exception. 
	 * @return
	 */
	String getDiscriminatorValue();

	/**
	 * In case of QueryableEntity it will return the weather the Entity from 
	 * which this QueryableObject was created is abstract or not.
	 * In case of QueryableCategory it will Exception. 
	 * @return
	 */
	boolean isAbstract();

	/**
	 * It will check weather the given QueryableObject is Derived on the basis of category.
	 * If object is derived from caegory then it will return true else false.
	 * @return
	 */
	boolean isCategoryObject();

	/**
	 * 
	 * @return
	 */
	QueryableObjectInterface getRootQueryableObject();

}
