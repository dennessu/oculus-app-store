package com.goshop.catalog.db.entity.store;

import com.goshop.catalog.db.entity.EntityId;
import org.bson.types.ObjectId;

/**
 * Created by kevingu on 11/21/13.
 */
public class StoreEntityId extends EntityId {
    public StoreEntityId() {
        super();
    }

    public StoreEntityId(ObjectId objectId) {
        super(objectId);
    }

    public StoreEntityId(String objectId) {
        super(objectId);
    }

    public StoreEntityId(String objectId, boolean throwsAppError) {
        super(objectId, throwsAppError);
    }
}
