package com.goshop.catalog.db.dao

import com.goshop.catalog.db.entity.category.CategoryEntity
import com.goshop.catalog.db.entity.category.CategoryEntityId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor
/**
 * Created by kevingu on 11/21/13.
 */
interface CategoryEntityDAO extends MongoRepository<CategoryEntity, CategoryEntityId>, QueryDslPredicateExecutor<CategoryEntity> {
}
