package com.goshop.catalog.db.entity;

import com.goshop.catalog.common.error.AppErrors;
import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * Created by kevingu on 11/21/13.
 */
public class EntityId implements Serializable {
    private final ObjectId objectId;

    public EntityId() {
        this(new ObjectId());
    }

    public EntityId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public EntityId(String objectId, boolean throwsAppError) {
        if (throwsAppError) {
            try {
                this.objectId = new ObjectId(objectId);
            } catch (IllegalArgumentException ex) {
                throw AppErrors.INSTANCE.objectIdInvalid(objectId, "objectId").exception();
            }
        } else {
            this.objectId = new ObjectId(objectId);
        }
    }

    public EntityId(String objectId) {
        this(objectId, false);
    }

    public ObjectId toObjectId() {
        return objectId;
    }

    @Override
    public String toString() {
        return objectId.toString();
    }

    public boolean isNew() {
        return objectId.isNew();
    }

}
