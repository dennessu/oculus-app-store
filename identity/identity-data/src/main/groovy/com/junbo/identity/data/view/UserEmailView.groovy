package com.junbo.identity.data.view

import com.junbo.identity.data.entity.user.UserEmailEntity
import com.junbo.sharding.view.EntityView
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/3/14.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class UserEmailView implements EntityView<Long, UserEmailEntity, String> {

    @Override
    String getName() {
        return 'user_useremail'
    }

    @Override
    Class<Long> getIdType() {
        return Long
    }

    @Override
    Class<UserEmailEntity> getEntityType() {
        return UserEmailEntity
    }

    @Override
    Class<String> getKeyType() {
        return String
    }

    @Override
    boolean handlesEntity(UserEmailEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return entity.value != null
    }

    @Override
    List<String> mapEntity(UserEmailEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return [entity.value]
    }
}

