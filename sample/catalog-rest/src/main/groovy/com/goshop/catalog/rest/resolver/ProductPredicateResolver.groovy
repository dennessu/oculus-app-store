package com.goshop.catalog.rest.resolver

import com.goshop.catalog.db.entity.category.CategoryEntityId
import com.goshop.catalog.db.entity.product.ProductEntity
import com.goshop.catalog.db.entity.product.ProductEntityId
import com.goshop.catalog.db.entity.product.QProductEntity
import com.mysema.query.BooleanBuilder
import com.mysema.query.types.Predicate
import groovy.transform.CompileStatic
/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class ProductPredicateResolver implements PredicateResolver {
    private static final String PRODUCT_ID = "productId";

    private static final String MODIFIED_SINCE = "modifiedSince"

    private static final String MODIFIED_UNTIL = "modifiedUntil"

    private static final String NAME = "name"

    private static final String STATUS = "status"

    private static final String CATEGORY_ID = "categoryId";

    private static final String STORE_ID = "storeId";

    @Override
    boolean canResolve(Class<?> entityClass) {
        return entityClass == ProductEntity
    }

    @Override
    Predicate resolve(Class<?> entityClass, Map<String, Object> options) {
        def builder = new BooleanBuilder()

        if (options[PRODUCT_ID] != null) {
            def productId = (String) options[PRODUCT_ID]
            builder.and(QProductEntity.productEntity.entityId.eq(new ProductEntityId(productId)))
        }

        if (options[MODIFIED_SINCE] != null) {
            def date = (Date) options[MODIFIED_SINCE]
            builder.and(QProductEntity.productEntity.modifiedDate.after(date))
        }

        if (options[MODIFIED_UNTIL] != null) {
            def date = (Date) options[MODIFIED_UNTIL]
            builder.and(QProductEntity.productEntity.modifiedDate.before(date))
        }

        if (options[NAME] != null) {
            def name = (String) options[NAME]
            builder.and(QProductEntity.productEntity.name.eq(name))
        }

        if (options[STATUS] != null) {
            def status = (String) options[STATUS]
            builder.and(QProductEntity.productEntity.status.eq(status))
        }

        if (options[CATEGORY_ID] != null) {
            def categoryId = (String) options[CATEGORY_ID]
            builder.and(QProductEntity.productEntity.categoryId.eq(new CategoryEntityId(categoryId)))
        }

        return builder
    }
}
