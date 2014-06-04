package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserTeleAttemptEntity
import com.junbo.identity.spec.v1.option.list.UserTFAAttemptListOptions
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTeleAttemptDAO {
    UserTeleAttemptEntity create(UserTeleAttemptEntity entity)

    UserTeleAttemptEntity update(UserTeleAttemptEntity entity)

    UserTeleAttemptEntity get(Long id)

    List<UserTeleAttemptEntity> searchByUserId(Long userId, UserTFAAttemptListOptions listOptions)

    void delete(Long id)
}
