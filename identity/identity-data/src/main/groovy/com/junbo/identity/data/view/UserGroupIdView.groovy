package com.junbo.identity.data.view

import com.junbo.identity.data.entity.user.UserGroupEntity
import com.junbo.sharding.view.EntityView
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class UserGroupIdView implements EntityView<Long, UserGroupEntity, Long>  {

    @Override
    String getName() {
        return 'user_group_group_id_ref'
    }

    @Override
    Class<Long> getIdType() {
        return Long
    }

    @Override
    Class<UserGroupEntity> getEntityType() {
        return UserGroupEntity
    }

    @Override
    Class<Long> getKeyType() {
        return Long
    }

    @Override
    boolean handlesEntity(UserGroupEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return entity.groupId != null
    }

    @Override
    List<Long> mapEntity(UserGroupEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException('entity is null')
        }

        return [entity.groupId]
    }
}
