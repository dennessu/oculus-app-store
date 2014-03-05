package com.goshop.catalog.rest.validation;

import com.goshop.catalog.common.error.AppError;
import com.goshop.catalog.common.error.AppErrors;
import com.goshop.catalog.db.dao.CategoryEntityDAO;
import com.goshop.catalog.db.entity.Entity;
import com.goshop.catalog.db.entity.EntityStatus;
import com.goshop.catalog.db.entity.category.CategoryEntity;
import com.goshop.catalog.db.entity.category.CategoryEntityId;
import com.goshop.catalog.rest.resolver.CategoryPredicateResolver;
import com.goshop.catalog.rest.resource.CommonResource;
import com.mysema.query.types.Predicate;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryValidation extends CommonResource implements Validation {

    private CategoryEntityDAO categoryEntityDAO;

    @Required
    public void setCategoryEntityDAO(CategoryEntityDAO categoryEntityDAO) {
        this.categoryEntityDAO = categoryEntityDAO;
    }

    @Override
    public AppError validateCreation(Entity entity) {
        if(entity.getEntityId() != null && !StringUtils.isEmpty(entity.getEntityId().toString())) {
            return AppErrors.INSTANCE.objectIdInvalid(entity.getEntityId().toString(), "EntityId should be empty during creation.");
        }

        return validate(entity);
    }

    @Override
    public AppError validateUpdate(Entity entityToUpdate, Entity entityUpdate) {
        if (!entityToUpdate.getEntityId().toString().equals(entityUpdate.getEntityId().toString())) {
            return AppErrors.INSTANCE.objectIdMissMatch(entityToUpdate.getEntityId().toString(), entityToUpdate.getEntityId().toString());
        }

        return validate(entityUpdate);
    }

    @Override
    public AppError validateRelease(Entity entity) {
        if(!entity.getStatus().equalsIgnoreCase(EntityStatus.DRAFT)) {
            return AppErrors.INSTANCE.draftNotFound(entity.getEntityId().toString(), "Status : " + EntityStatus.DRAFT);
        }

        return validate(entity);
    }

    @Override
    public AppError validateDelete(Entity entity) {
        if(!entity.getStatus().equalsIgnoreCase(EntityStatus.RELEASED)) {
            return AppErrors.INSTANCE.objectIdNotFound(entity.getEntityId().toString(), "Status : " + EntityStatus.RELEASED);
        }

        // check category valid
        CategoryEntity categoryEntity = (CategoryEntity)entity;
        if(categoryEntity.getParentCategoryId() != null) {
            Map<String, Object> options = new HashMap<String, Object>();
            options.put("parentCategoryId", categoryEntity.getEntityId().toString());
            options.put("status", EntityStatus.RELEASED);
            Predicate predicate = predicateResolver.resolve(CategoryEntity.class, options);
            // check whether it has released children
            Iterable<CategoryEntity> iterable = categoryEntityDAO.findAll(predicate);
            if(iterable != null && ((ArrayList)iterable).size() != 0) {
                List<String> objectIds = new ArrayList<String>();
                for(int size = 0; size < ((ArrayList)iterable).size(); size++) {
                    CategoryEntity entityIterator = (CategoryEntity)(((ArrayList) iterable).get(size));
                    objectIds.add(entityIterator.getEntityId().toString());
                }

                return AppErrors.INSTANCE.objectIdInUsing(((CategoryEntity) entity).getEntityId().toString(), objectIds.toArray(new String[0]));
            }
        }

        // check category has no valid product


        return null;
    }

    @Override
    public AppError validate(Entity entity) {
        if(entity == null) {
            return AppErrors.INSTANCE.invalidNullInputParam();
        }

        CategoryEntity categoryEntity = (CategoryEntity)entity;
        // 1):  Need to check category Type matches one of the attribute definition.

        // 2):  Need to check category Attributes according to attribute definition.

        // 3):  check cycle
        List<String> existingCategoryIds = new ArrayList<String>();
        CategoryEntity existingCategoryEntity = categoryEntity;
        while( existingCategoryEntity.getParentCategoryId() != null
            && !org.springframework.util.StringUtils.isEmpty(categoryEntity.getParentCategoryId().toString())) {
            CategoryEntityId id = existingCategoryEntity.getParentCategoryId();
            if(existingCategoryIds.contains(id.toString())) {
                return AppErrors.INSTANCE.cycleDetectError("Category.validate", categoryEntity.getParentCategoryId().toString());
            }
            existingCategoryEntity = getCategoryByStatus(existingCategoryEntity.getParentCategoryId().toString(), EntityStatus.RELEASED);
            if(existingCategoryEntity == null) {
                return AppErrors.INSTANCE.objectIdNotFound(id.toString(), "status : " + EntityStatus.RELEASED);
            }
        }

        return null;
    }
}