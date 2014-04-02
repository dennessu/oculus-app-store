package com.junbo.identity.data.view

import com.junbo.identity.data.entity.user.UserAuthenticatorEntity
import com.junbo.sharding.view.EntityView
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/2/14.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class UserAuthenticatorValueView implements EntityView<Long, UserAuthenticatorEntity, String>  {

    @Override
    String getName() {
        return null
    }

    @Override
    Class<Long> getIdType() {
        return null
    }

    @Override
    Class<UserAuthenticatorEntity> getEntityType() {
        return null
    }

    @Override
    Class<String> getKeyType() {
        return null
    }

    @Override
    boolean handlesEntity(UserAuthenticatorEntity entity) {
        return false
    }

    @Override
    List<String> mapEntity(UserAuthenticatorEntity entity) {
        return null
    }
}
