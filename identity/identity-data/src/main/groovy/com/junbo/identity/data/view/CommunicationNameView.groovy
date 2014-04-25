package com.junbo.identity.data.view

import com.junbo.identity.data.entity.user.CommunicationEntity
import com.junbo.sharding.view.EntityView
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class CommunicationNameView implements EntityView<Long, CommunicationEntity, String> {

    @Override
    String getName() {
        return 'communication_name'
    }

    @Override
    Class<Long> getIdType() {
        return Long
    }

    @Override
    Class<CommunicationEntity> getEntityType() {
        return CommunicationEntity
    }

    @Override
    Class<String> getKeyType() {
        return String
    }

    @Override
    boolean handlesEntity(CommunicationEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return entity.name != null
    }

    @Override
    List<String> mapEntity(CommunicationEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return [entity.name]
    }
}
