package com.goshop.catalog.rest.util
import com.goshop.catalog.db.entity.Entity
import com.goshop.catalog.db.entity.EntityId
import com.goshop.catalog.db.entity.EntityStatus
import groovy.transform.CompileStatic
/**
 * Created by kevingu on 11/23/13.
 */
@CompileStatic
class EntityUtil {

    static <E extends Entity, K extends EntityId> E initializeForCreate(E entity, Class<K> entityIdClass) {

        entity.entityId = entityIdClass.newInstance()
        entity.version = 0
        entity.status = EntityStatus.DRAFT

        return entity
    }

    static <E extends Entity, K extends EntityId> E initializeForUpdate(E entity) {

        entity.version = 0
        entity.status = EntityStatus.DRAFT

        return entity
    }
}
