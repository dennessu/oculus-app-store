package com.goshop.catalog.db.entity.product;

import com.goshop.catalog.db.entity.EntityId;
import org.bson.types.ObjectId;

public class SkuEntityId extends EntityId {

    public SkuEntityId() {
        super();
    }

    public SkuEntityId(ObjectId objectId) {
        super(objectId);
    }

    public SkuEntityId(String objectId) {
        super(objectId);
    }

    public SkuEntityId(String objectId, boolean throwsAppError) {
        super(objectId, throwsAppError);
    }
}
