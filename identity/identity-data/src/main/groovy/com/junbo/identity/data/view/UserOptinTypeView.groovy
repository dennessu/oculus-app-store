package com.junbo.identity.data.view

import com.junbo.identity.data.entity.user.UserOptinEntity
import com.junbo.sharding.view.EntityView
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/11/14.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class UserOptinTypeView implements EntityView<Long, UserOptinEntity, String> {

    @Override
    String getName() {
        return 'user_optin_type_ref'
    }

    @Override
    Class<Long> getIdType() {
        return Long
    }

    @Override
    Class<UserOptinEntity> getEntityType() {
        return UserOptinEntity
    }

    @Override
    Class<String> getKeyType() {
        return String
    }

    @Override
    boolean handlesEntity(UserOptinEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return entity.type != null
    }

    @Override
    List<String> mapEntity(UserOptinEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return [entity.type]
    }
}
