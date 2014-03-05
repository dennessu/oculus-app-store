package com.goshop.catalog.db.listener

import com.goshop.catalog.db.entity.Entity
import com.goshop.catalog.db.entity.EntityHist
import com.mongodb.DBObject
import groovy.transform.CompileStatic
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener

/**
 * Created by kevingu on 11/25/13.
 */
@CompileStatic
class EntityHistEventListener extends AbstractMongoEventListener<EntityHist> {

    @Override
    void onBeforeSave(EntityHist source, DBObject dbo) {
        super.onBeforeSave(source, dbo)

        def entity = (Entity) (source["item"])

        if (entity != null && entity.version == 0) {
            if (entity.entityId == null || entity.entityId.isNew()) {
                entity.createdBy = "TBD"
                entity.createdDate = new Date()
            }

            entity.modifiedBy = "TBD"
            entity.modifiedDate = new Date()
        }
    }
}
