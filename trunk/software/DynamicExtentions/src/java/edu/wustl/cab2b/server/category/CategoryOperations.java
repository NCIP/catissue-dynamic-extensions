package edu.wustl.cab2b.server.category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.common.errorcodes.ErrorCodeConstants;
import edu.wustl.cab2b.common.exception.RuntimeException;
import edu.wustl.cab2b.server.cache.EntityCache;
import edu.wustl.cab2b.server.path.PathFinder;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.querysuite.metadata.category.CategorialAttribute;
import edu.wustl.common.querysuite.metadata.category.CategorialClass;
import edu.wustl.common.querysuite.metadata.category.Category;
import edu.wustl.common.querysuite.metadata.path.IPath;
import edu.wustl.common.querysuite.metadata.path.Path;

/**
 * This class provides all operations needed for Category like save, retrieve
 * etc.
 * 
 * @author Chandrakant Talele
 * @author Srinath K
 */
public class CategoryOperations extends DefaultBizLogic {



    /**
     * Saves the input category to database.
     * 
     * @param category
     *            Category to save.
     */
    public void saveCategory(Category category) {
        try {
            insert(category);
        } catch (BizLogicException e) {
            throw new RuntimeException(ErrorCodeConstants.CATEGORY_SAVE_ERROR,e);
        }
    }

    /**
     * @param entityId
     *            Category Entity Id.
     * @param con
     *            Connection object reference
     * @return Category for corresponding to given category entity id.
     */
    public Category getCategoryByEntityId(Long entityId) {
        List list = null;
        try {
            list = retrieve(Category.class.getName(), "deEntityId", entityId);
        } catch (BizLogicException e) {
            throw new RuntimeException(ErrorCodeConstants.CATEGORY_RETRIEVE_ERROR,e);
        }
        Category category = getCategoryFromList(list);
        postRetrievalProcess(category, EntityCache.getInstance());
        return category;
    }

    /**
     * @param categoryId
     *            Id of the category
     * @param con
     *            Connection object reference
     * @return The Category for given id.
     */
    public Category getCategoryByCategoryId(Long categoryId) {
        List list = null;
        try {
            list = retrieve(Category.class.getName(), "id", categoryId);
        } catch (BizLogicException e) {
            throw new RuntimeException(ErrorCodeConstants.CATEGORY_RETRIEVE_ERROR,e);
        }
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new RuntimeException("Problem in code; probably db schema");
        }
        Category category = getCategoryFromList(list);
        postRetrievalProcess(category, EntityCache.getInstance());
        return category;
    }

    private Category getCategoryFromList(List categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        if (categories.size() > 1) {
            throw new RuntimeException("Problem in code; probably db schema");
        }
        return (Category) categories.get(0);
    }

    /**
     * @param category
     *            Input Category for which all source classes are to be found.
     * @return Set of all entities present in given categoty.
     */
    public Set<EntityInterface> getAllSourceClasses(Category category) {
        Set<Long> idSet = new HashSet<Long>();
        CategorialClass rootCategorialClass = category.getRootClass();
        idSet.add(rootCategorialClass.getDeEntityId());
        getDeEntityIdsOfChildren(rootCategorialClass, idSet);
        EntityCache cache = (EntityCache) EntityCache.getInstance();
        Set<EntityInterface> entities = new HashSet<EntityInterface>(idSet.size());
        for (Long id : idSet) {
            entities.add(cache.getEntityById(id));
        }
        return entities;
    }

    /**
     * @param parentCategorialClass
     *            Parent Categorial Class
     * @param idSet
     *            Set of all Source entity Ids present in the children of given
     *            param.
     */
    private void getDeEntityIdsOfChildren(CategorialClass parentCategorialClass, Set<Long> idSet) {

        for (CategorialClass categorialClass : parentCategorialClass.getChildren()) {
            idSet.add(categorialClass.getDeEntityId());
            getDeEntityIdsOfChildren(categorialClass, idSet);
        }

    }

    private void postRetrievalProcess(Category category, EntityCache cache) {
        long deEntityId = category.getDeEntityId();
        category.setCategoryEntity(cache.getEntityById(deEntityId));
        processCategorialClass(category.getRootClass(), (EntityCache) cache);
        for (Category subCategory : category.getSubCategories()) {
            postRetrievalProcess(subCategory, cache);
        }
    }

    private void processCategorialClass(CategorialClass categorialClass, EntityCache cache) {
        Long pathId = categorialClass.getPathFromParentId();
        if (pathId != null && pathId.intValue() != -1) {
            // this is a NON - root class
            IPath path = PathFinder.getInstance().getPathById(pathId);
            categorialClass.setPathFromParent((Path) path);
        }
        long deEntityId = categorialClass.getDeEntityId();
        EntityInterface entity = cache.getEntityById(deEntityId);
        categorialClass.setCategorialClassEntity(entity);
        Category category = categorialClass.getCategory();
        EntityInterface categoryEntity = category.getCategoryEntity();
        for (CategorialAttribute attribute : categorialClass.getCategorialAttributeCollection()) {
            long srcClassAttributeId = attribute.getDeSourceClassAttributeId();
            AttributeInterface attributeOfSrcClass = entity.getAttributeByIdentifier(srcClassAttributeId);
            attribute.setSourceClassAttribute(attributeOfSrcClass);

            long categoryEntityAttributeId = attribute.getDeCategoryAttributeId();
            AttributeInterface attributeOfCategoryEntity = categoryEntity.getAttributeByIdentifier(categoryEntityAttributeId);
            attribute.setCategoryAttribute(attributeOfCategoryEntity);
        }

        for (CategorialClass child : categorialClass.getChildren()) {
            child.setCategory(category);
            processCategorialClass(child, cache);
        }
    }

    /**
     * Returns all the categories which don't have any super category.
     * 
     * @return List of root categories.
     */
    public List<EntityInterface> getAllRootCategories() {
        List<Object> allCategories = null;
        try {
            allCategories = retrieve(Category.class.getName(), null, new String[] { "parentCategory" },
                                     new String[] { "is null" }, new Object[0], null);
        } catch (BizLogicException e) {
            throw new RuntimeException("Error in fetching category", e, ErrorCodeConstants.CATEGORY_RETRIEVE_ERROR);
        }
        List<EntityInterface> categoryEntities = new ArrayList<EntityInterface>(allCategories.size());
        EntityCache cache = (EntityCache) EntityCache.getInstance();
        for (Object object  : allCategories) {
        	Category category =(Category) object;
            Long categoryEntityId = category.getDeEntityId();
            categoryEntities.add(cache.getEntityById(categoryEntityId));
        }
        return categoryEntities;
    }

    /**
     * Returns all the categories available in the system.
     * 
     * @param connection
     *            Connection object reference
     * @return List of all categories.
     */
    public List<Category> getAllCategories() {
        List<Category> allCategories = null;
        try {
            allCategories = retrieve(Category.class.getName());
        } catch (BizLogicException e) {
            throw new RuntimeException("Error in fetching category", e, ErrorCodeConstants.CATEGORY_RETRIEVE_ERROR);
        }
        for (Category category : allCategories) {
            postRetrievalProcess(category, EntityCache.getInstance());
        }
        return allCategories;
    }

    /**
     * @param category
     *            Input Category for which all source attributes are to be
     *            found.
     * @return Set of all source(original) attributes present in given category.
     */
    public Set<AttributeInterface> getAllSourceAttributes(Category category) {
        return getAllChilrenCategorialClasses(category.getRootClass());
    }

    /**
     * Recursive method to traverse the whole tree of CategorialClasses and
     * collect attributes of each categorial class.
     * 
     * @param catClass
     *            Root of the categorial class tree
     * @return Set of all source(original) attributes present in given
     *         CategorialClass tree.
     */
    private Set<AttributeInterface> getAllChilrenCategorialClasses(CategorialClass catClass) {
        EntityCache cache = (EntityCache) EntityCache.getInstance();
        Set<AttributeInterface> attributeSet = new HashSet<AttributeInterface>();
        EntityInterface entity = cache.getEntityById(catClass.getDeEntityId());
        for (CategorialAttribute catAttr : catClass.getCategorialAttributeCollection()) {
            long attrId = catAttr.getDeSourceClassAttributeId();
            attributeSet.add(entity.getAttributeByIdentifier(attrId));
        }

        for (CategorialClass child : catClass.getChildren()) {
            attributeSet.addAll(getAllChilrenCategorialClasses(child));
        }
        return attributeSet;
    }
}