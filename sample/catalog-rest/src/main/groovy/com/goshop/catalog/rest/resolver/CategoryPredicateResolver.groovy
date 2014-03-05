package com.goshop.catalog.rest.resolver

import com.goshop.catalog.db.entity.EntityStatus
import com.goshop.catalog.db.entity.category.CategoryEntity
import com.goshop.catalog.db.entity.category.CategoryEntityId
import com.goshop.catalog.db.entity.category.QCategoryEntity
import com.mysema.query.BooleanBuilder
import com.mysema.query.types.Predicate
import groovy.transform.CompileStatic
/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class CategoryPredicateResolver implements PredicateResolver {
    private static final String CATEGORY_ID = "categoryId";

    private static final String MODIFIED_SINCE = "modifiedSince"

    private static final String MODIFIED_UNTIL = "modifiedUntil"

    private static final String NAME = "name"

    private static final String TYPE = "type"

    private static final String PARENT_CATEGORY_ID = "parentCategoryId"

    private static final String STATUS = "status"

    @Override
    boolean canResolve(Class<?> entityClass) {
        return entityClass == CategoryEntity
    }

    @Override
    Predicate resolve(Class<?> entityClass, Map<String, Object> options) {
        def builder = new BooleanBuilder()

        if (options[CATEGORY_ID] !=  null) {
            def categoryId = (String) options[CATEGORY_ID];
            builder.and(QCategoryEntity.categoryEntity.entityId.eq(new CategoryEntityId(categoryId)))
        }

        if (options[MODIFIED_SINCE] != null) {
            def date = (Date) options[MODIFIED_SINCE]
            builder.and(QCategoryEntity.categoryEntity.modifiedDate.after(date))
        }

        if (options[MODIFIED_UNTIL] != null) {
            def date = (Date) options[MODIFIED_UNTIL]
            builder.and(QCategoryEntity.categoryEntity.modifiedDate.before(date))
        }

        if (options[NAME] != null) {
            def name = (String) options[NAME]
            builder.and(QCategoryEntity.categoryEntity.name.eq(name))
        }

        if (options[TYPE] != null) {
            def type = (String) options[TYPE]
            builder.and(QCategoryEntity.categoryEntity.@type.eq(type))
        }

        if (options[PARENT_CATEGORY_ID] != null) {
            def parentCategoryId = (String) options[PARENT_CATEGORY_ID]
            builder.and(QCategoryEntity.categoryEntity.parentCategoryId.eq(new CategoryEntityId(parentCategoryId)))
        }

        if (options[STATUS] != null) {
            def status = (String) options[STATUS];
            builder.and(QCategoryEntity.categoryEntity.status.eq(status));
        } else {
            builder.and(QCategoryEntity.categoryEntity.status.eq(EntityStatus.RELEASED));
        }
        return builder
    }
}
