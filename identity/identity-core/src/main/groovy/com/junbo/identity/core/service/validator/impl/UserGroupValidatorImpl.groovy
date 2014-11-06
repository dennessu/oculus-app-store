/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserGroupId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.validator.UserGroupValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.service.GroupService
import com.junbo.identity.service.UserGroupService
import com.junbo.identity.service.UserService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Group
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.identity.spec.v1.option.list.UserGroupListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

/**
 * Check user valid
 * Check group valid
 * Created by liangfu on 3/25/14.
 */
@CompileStatic
class UserGroupValidatorImpl implements UserGroupValidator {

    private UserGroupService userGroupService

    private UserService userService

    private GroupService groupService
    // Any data that will use this data should be data issue, we may need to fix this.
    private Integer maximumFetchSize

    @Override
    Promise<UserGroup> validateForGet(UserGroupId userGroupId) {
        if (userGroupId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userGroupId').exception()
        }

        return userGroupService.get(userGroupId).then { UserGroup userGroup ->
            if (userGroup == null) {
                throw AppErrors.INSTANCE.userGroupNotFound(userGroupId).exception()
            }

            return Promise.pure(userGroup)
        }
    }

    @Override
    Promise<Void> validateForSearch(UserGroupListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null && options.groupId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId or groupId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserGroup userGroup) {

        return checkBasicUserGroupInfo(userGroup).then {
            if (userGroup.id != null) {
                throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
            }

            return userGroupService.searchByUserIdAndGroupId(userGroup.userId, userGroup.groupId, 1, 0).then { Results<UserGroup> existing ->
                if (existing != null && !CollectionUtils.isEmpty(existing.items)) {
                    throw AppCommonErrors.INSTANCE.fieldDuplicate('groupId').exception()
                }

                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserGroupId userGroupId, UserGroup userGroup, UserGroup oldUserGroup) {

        return validateForGet(userGroupId).then {
            return checkBasicUserGroupInfo(userGroup)
        }.then {
            if (userGroup.id == null) {
                throw new IllegalArgumentException('id is null')
            }

            if (userGroup.id != userGroupId) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('id', userGroup.id, userGroupId).exception()
            }

            if (userGroup.id != oldUserGroup.id) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('id', userGroup.id, oldUserGroup.id).exception()
            }

            if (userGroup.groupId != oldUserGroup.groupId) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('groupId', userGroup.groupId, oldUserGroup.groupId).exception()
            }

            if (userGroup.userId != oldUserGroup.userId) {
                throw AppCommonErrors.INSTANCE.fieldNotWritable('userId', userGroup.userId, oldUserGroup.userId).exception()
            }

            return Promise.pure(null)
        }
    }

    private Promise<Void> checkBasicUserGroupInfo(UserGroup userGroup) {
        if (userGroup == null) {
            throw new IllegalArgumentException('userGroup is null')
        }

        if (userGroup.groupId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('groupId').exception()
        }

        if (userGroup.userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }

        return userService.getNonDeletedUser(userGroup.userId).then { User existingUser ->
            if (existingUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userGroup.userId).exception()
            }

            if (existingUser.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userGroup.userId).exception()
            }

            if (existingUser.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userGroup.userId).exception()
            }

            return groupService.getActiveGroup(userGroup.groupId).then { Group existingGroup ->
                if (existingGroup == null) {
                    throw AppErrors.INSTANCE.groupNotFound(userGroup.groupId).exception()
                }

                return userGroupService.searchByUserIdAndGroupId(userGroup.userId, userGroup.groupId,
                        maximumFetchSize, 0).then { Results<UserGroup> existingUserGroupList ->
                    if (existingUserGroupList == null || CollectionUtils.isEmpty(existingUserGroupList.items)) {
                        return Promise.pure(null)
                    }

                    existingUserGroupList.items.removeAll { UserGroup existing ->
                        return existing.id == userGroup.id
                    }

                    if (!CollectionUtils.isEmpty(existingUserGroupList.items)) {
                        throw AppCommonErrors.INSTANCE.fieldInvalid('groupId').exception()
                    }

                    return Promise.pure(null)
                }
            }
        }
    }

    @Required
    void setUserGroupService(UserGroupService userGroupService) {
        this.userGroupService = userGroupService
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Required
    void setGroupService(GroupService groupService) {
        this.groupService = groupService
    }

    @Required
    void setMaximumFetchSize(Integer maximumFetchSize) {
        this.maximumFetchSize = maximumFetchSize
    }
}
