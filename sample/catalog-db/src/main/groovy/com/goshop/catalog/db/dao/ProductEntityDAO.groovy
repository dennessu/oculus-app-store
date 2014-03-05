package com.goshop.catalog.db.dao

import com.goshop.catalog.db.entity.product.ProductEntity
import com.goshop.catalog.db.entity.product.ProductEntityId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

/**
 * Created by kevingu on 11/21/13.
 */
interface ProductEntityDAO extends MongoRepository<ProductEntity, ProductEntityId>, QueryDslPredicateExecutor<ProductEntity> {

}
