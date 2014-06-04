package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.UserTeleBackupCodeEntity
import com.junbo.identity.spec.v1.option.list.UserTFABackupCodeListOptions
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
interface UserTeleBackupCodeDAO {
    UserTeleBackupCodeEntity create(UserTeleBackupCodeEntity entity)
    UserTeleBackupCodeEntity update(UserTeleBackupCodeEntity entity)
    UserTeleBackupCodeEntity get(Long id)
    void delete(Long id)

    List<UserTeleBackupCodeEntity> search(UserTFABackupCodeListOptions listOptions)
}
