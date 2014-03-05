package com.goshop.catalog.db.listener

import com.goshop.catalog.db.entity.Entity
import com.mongodb.DBObject
import groovy.transform.CompileStatic
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener

/**
 * Created by kevingu on 11/25/13.
 */
@CompileStatic
class EntityEventListener extends AbstractMongoEventListener<Entity> {

    @Override
    void onBeforeSave(Entity source, DBObject dbo) {
        super.onBeforeSave(source, dbo)

        if (source.entityId == null || source.entityId.isNew()) {
            source.createdBy = "TBD"
            source.createdDate = new Date()
        }

        source.modifiedBy = "TBD"
        source.modifiedDate = new Date()
    }
}
