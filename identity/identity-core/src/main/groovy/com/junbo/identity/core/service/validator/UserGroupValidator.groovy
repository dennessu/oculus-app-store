package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserGroupId
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
interface UserGroupValidator {
    Promise<UserGroup> validateForGet(UserGroupId userGroupId)
    Promise<Void> validateForSearch(UserGroupListOptions options)
    Promise<Void> validateForCreate(UserGroup userGroup)
    Promise<Void> validateForUpdate(UserGroupId userGroupId, UserGroup userGroup, UserGroup oldUserGroup)
}
