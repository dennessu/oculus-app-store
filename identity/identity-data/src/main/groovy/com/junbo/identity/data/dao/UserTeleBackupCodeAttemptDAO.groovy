package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserTeleBackupCodeAttemptEntity
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeAttemptListOptions
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTeleBackupCodeAttemptDAO {
    UserTeleBackupCodeAttemptEntity create(UserTeleBackupCodeAttemptEntity entity)
    UserTeleBackupCodeAttemptEntity update(UserTeleBackupCodeAttemptEntity entity)
    UserTeleBackupCodeAttemptEntity get(Long id)
    void delete(Long id)

    List<UserTeleBackupCodeAttemptEntity> search(UserTeleBackupCodeAttemptListOptions listOptions)
}
