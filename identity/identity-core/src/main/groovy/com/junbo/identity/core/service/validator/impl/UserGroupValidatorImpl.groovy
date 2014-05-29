/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserGroupId
import com.junbo.identity.core.service.validator.UserGroupValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.repository.GroupRepository
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.data.repository.UserRepository
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
 * Created by liangfu on 3/25/14.
 */
@CompileStatic
class UserGroupValidatorImpl implements UserGroupValidator {

    private UserGroupRepository userGroupRepository

    private UserRepository userRepository

    private GroupRepository groupRepository

    @Override
    Promise<UserGroup> validateForGet(UserGroupId userGroupId) {
        if (userGroupId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userGroupId').exception()
        }

        return userGroupRepository.get(userGroupId).then { UserGroup userGroup ->
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
            throw AppErrors.INSTANCE.parameterRequired('userId or groupId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserGroup userGroup) {

        return checkBasicUserGroupInfo(userGroup).then {
            if (userGroup.id != null) {
                throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
            }

            return userGroupRepository.searchByUserIdAndGroupId(userGroup.userId, userGroup.groupId,
                    Integer.MAX_VALUE, 0).then { List<UserGroup> existing ->
                if (!CollectionUtils.isEmpty(existing)) {
                    throw AppErrors.INSTANCE.fieldDuplicate('groupId').exception()
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
                throw AppErrors.INSTANCE.fieldInvalid('id', userGroupId.value.toString()).exception()
            }

            if (userGroup.id != oldUserGroup.id) {
                throw AppErrors.INSTANCE.fieldInvalid('id', oldUserGroup.id.toString()).exception()
            }

            if (userGroup.groupId != oldUserGroup.groupId || userGroup.userId != oldUserGroup.userId) {
                return userGroupRepository.searchByUserIdAndGroupId(userGroup.userId, userGroup.groupId,
                        Integer.MAX_VALUE, 0).then { List<UserGroup> existing ->
                    if (!CollectionUtils.isEmpty(existing)) {
                        throw AppErrors.INSTANCE.fieldDuplicate('groupId or userId').exception()
                    }

                    return Promise.pure(null)
                }
            }
            return Promise.pure(null)
        }
    }

    private Promise<Void> checkBasicUserGroupInfo(UserGroup userGroup) {
        if (userGroup == null) {
            throw new IllegalArgumentException('userGroup is null')
        }

        if (userGroup.groupId == null) {
            throw AppErrors.INSTANCE.fieldRequired('groupId').exception()
        }

        if (userGroup.userId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userId').exception()
        }

        return userRepository.get(userGroup.userId).then { User existingUser ->
            if (existingUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userGroup.userId).exception()
            }

            if (existingUser.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userGroup.userId).exception()
            }

            if (existingUser.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userGroup.userId).exception()
            }

            return groupRepository.get(userGroup.groupId).then { Group existingGroup ->
                if (existingGroup == null || !existingGroup.active) {
                    throw AppErrors.INSTANCE.groupNotFound(userGroup.groupId).exception()
                }

                return userGroupRepository.searchByUserIdAndGroupId(userGroup.userId, userGroup.groupId,
                        Integer.MAX_VALUE, 0).then { List<UserGroup> existingUserGroupList ->
                    if (CollectionUtils.isEmpty(existingUserGroupList)) {
                        return Promise.pure(null)
                    }

                    existingUserGroupList.removeAll { UserGroup existing ->
                        return existing.id == userGroup.id
                    }

                    if (!CollectionUtils.isEmpty(existingUserGroupList)) {
                        throw AppErrors.INSTANCE.fieldInvalid('groupId').exception()
                    }

                    return Promise.pure(null)
                }
            }
        }
    }


    @Required
    void setUserGroupRepository(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository
    }

}
