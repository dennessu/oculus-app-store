package com.goshop.catalog.db.dao

import com.goshop.catalog.db.entity.product.ProductEntityHist
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by kevingu on 11/21/13.
 */
interface ProductEntityHistDAO extends MongoRepository<ProductEntityHist, ObjectId> {

}
