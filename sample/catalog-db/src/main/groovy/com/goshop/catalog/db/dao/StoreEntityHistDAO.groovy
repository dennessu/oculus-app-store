package com.goshop.catalog.db.dao

import com.goshop.catalog.db.entity.store.StoreEntityHist
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
/**
 * Created by kevingu on 11/21/13.
 */
interface StoreEntityHistDAO extends MongoRepository<StoreEntityHist, ObjectId> {

}
