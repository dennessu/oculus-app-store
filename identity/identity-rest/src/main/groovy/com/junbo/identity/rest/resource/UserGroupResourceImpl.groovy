/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource
import com.junbo.common.id.Id
import com.junbo.common.id.UserGroupId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserGroupFilter
import com.junbo.identity.core.service.validator.UserGroupValidator
import com.junbo.identity.data.repository.UserGroupRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserGroup
import com.junbo.identity.spec.options.entity.UserGroupGetOptions
import com.junbo.identity.spec.options.list.UserGroupListOptions
import com.junbo.identity.spec.resource.UserGroupResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.ws.rs.BeanParam
import javax.ws.rs.ext.Provider
/**
 * Created by liangfu on 3/14/14.
 */
@Provider
@Component
@Scope('prototype')
@Transactional
@CompileStatic
class UserGroupResourceImpl implements UserGroupResource {

    @Autowired
    private UserGroupRepository userGroupRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserGroupFilter userGroupFilter

    @Autowired
    private UserGroupValidator userGroupValidator

    @Override
    Promise<UserGroup> create(UserId userId, UserGroup userGroup) {

        if (userGroup == null) {
            throw new IllegalArgumentException('userGroup is null')
        }

        userGroup = userGroupFilter.filterForCreate(userGroup)

        return userGroupValidator.validateForCreate(userId, userGroup).then {
            userGroupRepository.create(userGroup).then { UserGroup newUserGroup ->
                created201Marker.mark((Id)newUserGroup.id)

                newUserGroup = userGroupFilter.filterForGet(newUserGroup, null)
                return Promise.pure(newUserGroup)
            }
        }
    }

    @Override
    Promise<UserGroup> put(UserId userId, UserGroupId userGroupId, UserGroup userGroup) {

        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userGroupId == null) {
            throw new IllegalArgumentException('userGroupId is null')
        }

        if (userGroup == null) {
            throw new IllegalArgumentException('userGroup is null')
        }

        return userGroupRepository.get(userGroupId).then { UserGroup oldUserGroup ->
            if (oldUserGroup == null) {
                throw AppErrors.INSTANCE.userGroupNotFound(userGroupId).exception()
            }

            userGroup = userGroupFilter.filterForPut(userGroup, oldUserGroup)

            userGroupValidator.validateForUpdate(userId, userGroupId, userGroup, oldUserGroup).then {
                userGroupRepository.update(userGroup).then { UserGroup newUserGroup ->
                    newUserGroup = userGroupFilter.filterForGet(newUserGroup, null)
                    return Promise.pure(newUserGroup)
                }
            }
        }
    }

    @Override
    Promise<UserGroup> patch(UserId userId, UserGroupId userGroupId, UserGroup userGroup) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userGroupId == null) {
            throw new IllegalArgumentException('userGroupId is null')
        }

        if (userGroup == null) {
            throw new IllegalArgumentException('userGroup is null')
        }

        return userGroupRepository.get(userGroupId).then { UserGroup oldUserGroup ->
            if (oldUserGroup == null) {
                throw AppErrors.INSTANCE.userGroupNotFound(userGroupId).exception()
            }

            userGroup = userGroupFilter.filterForPatch(userGroup, oldUserGroup)

            userGroupValidator.validateForUpdate(userId, userGroupId, userGroup, oldUserGroup).then {

                userGroupRepository.update(userGroup).then { UserGroup newUserGroup ->
                    newUserGroup = userGroupFilter.filterForGet(newUserGroup, null)
                    return Promise.pure(newUserGroup)
                }
            }
        }
    }

    @Override
    Promise<UserGroup> delete(UserId userId, UserGroupId userGroupId) {

        return userGroupValidator.validateForGet(userId, userGroupId).then {
            userGroupRepository.delete(userGroupId)

            return Promise.pure(null)
        }
    }

    @Override
    Promise<UserGroup> get(UserId userId, UserGroupId userGroupId, @BeanParam UserGroupGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userGroupValidator.validateForGet(userId, userGroupId).then { UserGroup newUserGroup ->
            newUserGroup = userGroupFilter.filterForGet(newUserGroup, getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserGroup)
        }
    }

    @Override
    Promise<Results<UserGroup>> list(UserId userId, @BeanParam UserGroupListOptions listOptions) {

        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }
        listOptions.setUserId(userId)

        return userGroupValidator.validateForSearch(listOptions).then {
            userGroupRepository.search(listOptions).then { List<UserGroup> userGroupList ->
                def result = new Results<UserGroup>(items: [])

                userGroupList.each { UserGroup newUserGroup ->
                    if (newUserGroup != null) {
                        newUserGroup = userGroupFilter.filterForGet(newUserGroup,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserGroup != null) {
                        result.items.add(newUserGroup)
                    }
                }
                result.setItems(userGroupList)

                return Promise.pure(result)
            }
        }
    }
}
