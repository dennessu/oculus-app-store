package com.goshop.catalog.db.entity.product;

import com.goshop.catalog.db.entity.EntityId;
import org.bson.types.ObjectId;

/**
 * Created by kevingu on 11/21/13.
 */
public class ProductEntityId extends EntityId {

    public ProductEntityId() {
        super();
    }

    public ProductEntityId(ObjectId objectId) {
        super(objectId);
    }

    public ProductEntityId(String objectId) {
        super(objectId);
    }

    public ProductEntityId(String objectId, boolean throwsAppError) {
        super(objectId, throwsAppError);
    }
}
