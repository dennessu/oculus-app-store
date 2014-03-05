package com.goshop.catalog.db.entity.category;

import com.goshop.catalog.db.entity.EntityId;
import org.bson.types.ObjectId;

/**
 * Created by kevingu on 11/21/13.
 */
public class CategoryEntityId extends EntityId {
    public CategoryEntityId() {
        super();
    }

    public CategoryEntityId(ObjectId objectId) {
        super(objectId);
    }

    public CategoryEntityId(String objectId) {
        super(objectId);
    }

    public CategoryEntityId(String objectId, boolean throwsAppError) {
        super(objectId, throwsAppError);
    }
}
