package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserGroupId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.model.users.UserGroup
import com.junbo.identity.spec.options.list.UserGroupListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
interface UserGroupValidator {
    Promise<UserGroup> validateForGet(UserId userId, UserGroupId userGroupId)
    Promise<Void> validateForSearch(UserGroupListOptions options)
    Promise<Void> validateForCreate(UserId userId, UserGroup userGroup)
    Promise<Void> validateForUpdate(UserId userId, UserGroupId userGroupId, UserGroup userGroup, UserGroup oldUserGroup)
}
