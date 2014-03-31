/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource

import com.junbo.common.id.Id
import com.junbo.common.id.UserAuthenticatorId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserAuthenticatorFilter

import com.junbo.identity.core.service.validator.UserAuthenticatorValidator
import com.junbo.identity.data.repository.UserAuthenticatorRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserAuthenticator
import com.junbo.identity.spec.options.entity.UserAuthenticatorGetOptions
import com.junbo.identity.spec.options.list.UserAuthenticatorListOptions
import com.junbo.identity.spec.resource.UserAuthenticatorResource
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
class UserAuthenticatorResourceImpl implements UserAuthenticatorResource {
    @Autowired
    private UserAuthenticatorRepository userAuthenticatorRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserAuthenticatorFilter userAuthenticatorFilter

    @Autowired
    private UserAuthenticatorValidator userAuthenticatorValidator

    @Override
    Promise<UserAuthenticator> create(UserId userId, UserAuthenticator userAuthenticator) {
        if (userAuthenticator == null) {
            throw new IllegalArgumentException('userAuthenticator is null')
        }

        userAuthenticator = userAuthenticatorFilter.filterForCreate(userAuthenticator)

        userAuthenticatorValidator.validateForCreate(userId, userAuthenticator).then {
            userAuthenticatorRepository.create(userAuthenticator).then { UserAuthenticator newUserAuthenticator ->
                created201Marker.mark((Id)newUserAuthenticator.id)

                newUserAuthenticator = userAuthenticatorFilter.filterForGet(newUserAuthenticator, null)
                return Promise.pure(newUserAuthenticator)
            }
        }
    }

    @Override
    Promise<UserAuthenticator> put(UserId userId, UserAuthenticatorId userAuthenticatorId,
                                          UserAuthenticator userAuthenticator) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userAuthenticatorId == null) {
            throw new IllegalArgumentException('userAuthenticatorId is null')
        }

        if (userAuthenticator == null) {
            throw new IllegalArgumentException('userAuthenticator is null')
        }

        return userAuthenticatorRepository.get(userAuthenticatorId).then { UserAuthenticator oldUserAuthenticator ->
            if (oldUserAuthenticator == null) {
                throw AppErrors.INSTANCE.userAuthenticatorNotFound(userAuthenticatorId).exception()
            }

            userAuthenticator = userAuthenticatorFilter.filterForPut(userAuthenticator, oldUserAuthenticator)

            userAuthenticatorValidator.validateForUpdate(userId,
                    userAuthenticatorId, userAuthenticator, oldUserAuthenticator).then {
                userAuthenticatorRepository.update(userAuthenticator).then { UserAuthenticator newUserAuthenticator ->
                    newUserAuthenticator = userAuthenticatorFilter.filterForGet(newUserAuthenticator, null)
                    return Promise.pure(newUserAuthenticator)
                }
            }
        }
    }

    @Override
    Promise<UserAuthenticator> patch(UserId userId, UserAuthenticatorId userAuthenticatorId,
                                            UserAuthenticator userAuthenticator) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userAuthenticatorId == null) {
            throw new IllegalArgumentException('userAuthenticatorId is null')
        }

        if (userAuthenticator == null) {
            throw new IllegalArgumentException('userAuthenticator is null')
        }

        return userAuthenticatorRepository.get(userAuthenticatorId).then { UserAuthenticator oldUserAuthenticator ->
            if (oldUserAuthenticator == null) {
                throw AppErrors.INSTANCE.userAuthenticatorNotFound(userAuthenticatorId).exception()
            }

            userAuthenticator = userAuthenticatorFilter.filterForPatch(userAuthenticator, oldUserAuthenticator)

            userAuthenticatorValidator.validateForUpdate(userId,
                    userAuthenticatorId, userAuthenticator, oldUserAuthenticator).then {
                userAuthenticatorRepository.update(userAuthenticator).then { UserAuthenticator newUserAuthenticator ->
                    newUserAuthenticator = userAuthenticatorFilter.filterForGet(newUserAuthenticator, null)
                    return Promise.pure(newUserAuthenticator)
                }
            }
        }
    }

    @Override
    Promise<Void> delete(UserId userId, UserAuthenticatorId userAuthenticatorId) {
        return userAuthenticatorValidator.validateForGet(userId, userAuthenticatorId).then {
            userAuthenticatorRepository.delete(userAuthenticatorId)

            return Promise.pure(null)
        }
    }

    @Override
    Promise<UserAuthenticator> get(UserId userId, UserAuthenticatorId userAuthenticatorId,
                                          @BeanParam UserAuthenticatorGetOptions getOptions) {

        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userAuthenticatorValidator.validateForGet(userId, userAuthenticatorId).then { UserAuthenticator authenticator ->
            authenticator = userAuthenticatorFilter.filterForGet(authenticator,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(authenticator)
        }
    }

    @Override
    Promise<Results<UserAuthenticator>> list(UserId userId,
                                                    @BeanParam UserAuthenticatorListOptions listOptions) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        listOptions.setUserId(userId)
        return list(listOptions)
    }

    @Override
    Promise<Results<UserAuthenticator>> list(@BeanParam UserAuthenticatorListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return userAuthenticatorValidator.validateForSearch(listOptions).then {
            userAuthenticatorRepository.search(listOptions).then { List<UserAuthenticator> authenticatorList ->
                def result = new Results<UserAuthenticator>(items: [])

                authenticatorList.each { UserAuthenticator newUserAuthenticator ->
                    if (newUserAuthenticator != null) {
                        newUserAuthenticator = userAuthenticatorFilter.filterForGet(newUserAuthenticator,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserAuthenticator != null) {
                        result.items.add(newUserAuthenticator)
                    }
                }
                result.setItems(authenticatorList)

                return Promise.pure(result)
            }
        }
    }
}
