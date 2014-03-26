/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserFilter
import com.junbo.identity.core.service.validator.UserValidator
import com.junbo.identity.core.service.validator.UsernameValidator
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.User
import com.junbo.identity.spec.options.entity.UserGetOptions
import com.junbo.identity.spec.options.list.UserListOptions
import com.junbo.identity.spec.resource.UserResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

import javax.ws.rs.BeanParam
import javax.ws.rs.ext.Provider

/**
 * Created by kg on 3/17/14.
 */
@Provider
@Component
@Scope('prototype')
@Transactional
@CompileStatic
class UserResourceImpl implements UserResource {

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserRepository userRepository

    @Autowired
    private UserValidator userValidator

    @Autowired
    private UsernameValidator usernameValidator

    @Autowired
    private UserFilter userFilter

    @Override
    Promise<User> create(User user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        user = userFilter.filterForCreate(user)

        userValidator.validateForCreate(user).then {
            userRepository.create(user).then { User newUser ->
                created201Marker.mark((Id) newUser.id)

                newUser = userFilter.filterForGet(newUser, null)
                return Promise.pure(newUser)
            }
        }
    }

    @Override
    Promise<User> put(UserId userId, User user) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        userRepository.get(userId).then { User oldUser ->
            if (oldUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            user = userFilter.filterForPut(user, oldUser)

            userValidator.validateForUpdate(user, oldUser).then {
                userRepository.update(user).then { User newUser ->
                    newUser = userFilter.filterForGet(user, null)
                    return Promise.pure(newUser)
                }
            }
        }
    }

    @Override
    Promise<User> patch(UserId userId, User user) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        userRepository.get(userId).then { User oldUser ->
            if (oldUser == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            user = userFilter.filterForPatch(user, oldUser)

            userValidator.validateForUpdate(user, oldUser).then {
                userRepository.update(user).then { User newUser ->
                    newUser = userFilter.filterForGet(user, null)
                    return Promise.pure(newUser)
                }
            }
        }
    }

    @Override
    Promise<User> get(UserId userId, @BeanParam UserGetOptions getOptions) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userRepository.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            user = userFilter.filterForGet(user, getOptions.properties?.split(',') as List<String>)

            return Promise.pure(user)
        }
    }

    @Override
    Promise<Results<User>> list(@BeanParam UserListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        if (StringUtils.isEmpty(listOptions.username)) {
            throw AppErrors.INSTANCE.fieldRequired('username').exception()
        }

        String canonicalUsername = usernameValidator.normalizeUsername(listOptions.username)
        userRepository.getUserByCanonicalUsername(canonicalUsername).then { User user ->
            def resultList = new Results<User>(items: [])

            if (user != null) {
                user = userFilter.filterForGet(user, listOptions.properties?.split(',') as List<String>)
            }

            if (user != null) {
                resultList.items.add(user)
            }

            return Promise.pure(resultList)
        }
    }
}
