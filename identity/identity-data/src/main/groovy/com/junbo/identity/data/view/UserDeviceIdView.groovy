package com.junbo.identity.data.view

import com.junbo.identity.data.entity.user.UserDeviceEntity
import com.junbo.sharding.view.EntityView
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class UserDeviceIdView implements EntityView<Long, UserDeviceEntity, Long>  {

    @Override
    String getName() {
        return 'user_device_device_id_ref'
    }

    @Override
    Class<Long> getIdType() {
        return Long
    }

    @Override
    Class<UserDeviceEntity> getEntityType() {
        return UserDeviceEntity
    }

    @Override
    Class<Long> getKeyType() {
        return Long
    }

    @Override
    boolean handlesEntity(UserDeviceEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return entity.deviceId != null
    }

    @Override
    List<Long> mapEntity(UserDeviceEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return [entity.deviceId]
    }
}
