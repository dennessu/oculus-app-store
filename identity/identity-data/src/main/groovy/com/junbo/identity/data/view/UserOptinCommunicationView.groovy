package com.junbo.identity.data.view

import com.junbo.identity.data.entity.user.UserCommunicationEntity
import com.junbo.sharding.view.EntityView
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/11/14.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class UserOptinCommunicationView implements EntityView<Long, UserCommunicationEntity, Long> {

    @Override
    String getName() {
        return 'user_communication_ref'
    }

    @Override
    Class<Long> getIdType() {
        return Long
    }

    @Override
    Class<UserCommunicationEntity> getEntityType() {
        return UserCommunicationEntity
    }

    @Override
    Class<Long> getKeyType() {
        return Long
    }

    @Override
    boolean handlesEntity(UserCommunicationEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return entity.communicationId != null
    }

    @Override
    List<Long> mapEntity(UserCommunicationEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return [entity.communicationId]
    }
}
