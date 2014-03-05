package com.goshop.catalog.db.dao

import com.goshop.catalog.db.entity.store.StoreEntity
import com.goshop.catalog.db.entity.store.StoreEntityId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.querydsl.QueryDslPredicateExecutor

/**
 * Created by kevingu on 11/21/13.
 */
interface StoreEntityDAO extends MongoRepository<StoreEntity, StoreEntityId>, QueryDslPredicateExecutor<StoreEntity> {

}
