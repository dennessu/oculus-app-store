/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.validator.impl
import com.junbo.common.id.UserGroupId
import com.junbo.common.id.UserId
import com.junbo.identity.core.service.validator.UserGroupValidator
import com.junbo.identity.data.repository.GroupRepository
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors

import com.junbo.identity.spec.model.users.User
import com.junbo.identity.spec.model.users.UserGroup
import com.junbo.identity.spec.options.list.UserGroupListOptions
import com.junbo.identity.spec.v1.model.Group
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

    @Override
    Promise<UserGroup> validateForGet(UserId userId, UserGroupId userGroupId) {
        if (userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userGroupId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userGroupId').exception()
        }

        return userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            userGroupRepository.get(userGroupId).then { UserGroup userGroup ->
                if (userGroup == null) {
                    throw AppErrors.INSTANCE.userGroupNotFound(userGroupId).exception()
                }

                if (userId != userGroup.userId) {
                    throw AppErrors.INSTANCE.parameterInvalid('userId and userGroupId doesn\'t match.').exception()
                }

                return Promise.pure(userGroup)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserGroupListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserGroup userGroup) {

        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        checkBasicUserGroupInfo(userGroup)

        if (userGroup.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        if (userGroup.userId != null && userGroup.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userGroup.userId.toString()).exception()
        }

        return userGroupRepository.search(new UserGroupListOptions(
                userId: userId,
                groupId: userGroup.groupId
        )).then { List<UserGroup> existing ->
            if (!CollectionUtils.isEmpty(existing)) {
                throw AppErrors.INSTANCE.fieldDuplicate('groupId').exception()
            }

            return groupRepository.get(userGroup.groupId).then { Group newGroup ->
                if (newGroup == null) {
                    throw AppErrors.INSTANCE.groupNotFound(userGroup.groupId).exception()
                }

                userGroup.setUserId(userId)
                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserGroupId userGroupId,
                                           UserGroup userGroup, UserGroup oldUserGroup) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        return validateForGet(userId, userGroupId).then {
            checkBasicUserGroupInfo(userGroup)

            if (userGroup.id == null) {
                throw new IllegalArgumentException('id is null')
            }

            if (userGroup.id != userGroupId) {
                throw AppErrors.INSTANCE.fieldInvalid('id', userGroupId.value.toString()).exception()
            }

            if (userGroup.id != oldUserGroup.id) {
                throw AppErrors.INSTANCE.fieldInvalid('id', oldUserGroup.id.toString()).exception()
            }

            if (userGroup.groupId != oldUserGroup.groupId) {
                return userGroupRepository.search(new UserGroupListOptions(
                        userId: userId,
                        groupId: userGroup.groupId
                )).then { List<UserGroup> existing ->
                    if (!CollectionUtils.isEmpty(existing)) {
                        throw AppErrors.INSTANCE.fieldDuplicate('groupId').exception()
                    }

                    return groupRepository.get(userGroup.groupId).then { Group existingGroup ->
                        if (existingGroup == null) {
                            throw AppErrors.INSTANCE.groupNotFound(userGroup.groupId).exception()
                        }

                        userGroup.setUserId(userId)
                        return Promise.pure(null)
                    }
                }
            }
            return Promise.pure(null)
        }
    }

    private void checkBasicUserGroupInfo(UserGroup userGroup) {
        if (userGroup == null) {
            throw new IllegalArgumentException('userGroup is null')
        }

        if (userGroup.groupId == null) {
            throw AppErrors.INSTANCE.fieldRequired('groupId').exception()
        }
    }
}
