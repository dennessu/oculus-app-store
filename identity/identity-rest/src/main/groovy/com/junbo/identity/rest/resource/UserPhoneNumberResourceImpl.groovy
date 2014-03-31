/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource
import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPhoneNumberId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserPhoneNumberFilter
import com.junbo.identity.core.service.validator.UserPhoneNumberValidator
import com.junbo.identity.data.repository.UserPhoneNumberRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPhoneNumber
import com.junbo.identity.spec.options.entity.UserPhoneNumberGetOptions
import com.junbo.identity.spec.options.list.UserPhoneNumberListOptions
import com.junbo.identity.spec.resource.UserPhoneNumberResource
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
class UserPhoneNumberResourceImpl implements UserPhoneNumberResource {

    @Autowired
    private UserPhoneNumberRepository userPhoneNumberRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserPhoneNumberFilter userPhoneNumberFilter

    @Autowired
    private UserPhoneNumberValidator userPhoneNumberValidator

    @Override
    Promise<UserPhoneNumber> create(UserId userId, UserPhoneNumber userPhoneNumber) {
        if (userPhoneNumber == null) {
            throw new IllegalArgumentException('userPhoneNumber is null')
        }

        userPhoneNumber = userPhoneNumberFilter.filterForCreate(userPhoneNumber)

        userPhoneNumberValidator.validateForCreate(userId, userPhoneNumber).then {
            userPhoneNumberRepository.create(userPhoneNumber).then { UserPhoneNumber newUserPhoneNumber ->
                created201Marker.mark((Id)newUserPhoneNumber.id)

                newUserPhoneNumber = userPhoneNumberFilter.filterForGet(newUserPhoneNumber, null)
                return Promise.pure(newUserPhoneNumber)
            }
        }
    }

    @Override
    Promise<UserPhoneNumber> put(UserId userId, UserPhoneNumberId userPhoneNumberId,
                                        UserPhoneNumber userPhoneNumber) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userPhoneNumberId == null) {
            throw new IllegalArgumentException('userPhoneNumberId is null')
        }

        if (userPhoneNumber == null) {
            throw new IllegalArgumentException('userPhoneNumber is null')
        }

        return userPhoneNumberRepository.get(userPhoneNumberId).then { UserPhoneNumber oldUserPhoneNumber ->
            if (oldUserPhoneNumber == null) {
                throw AppErrors.INSTANCE.userPhoneNumberNotFound(userPhoneNumberId).exception()
            }

            userPhoneNumber = userPhoneNumberFilter.filterForPut(userPhoneNumber, oldUserPhoneNumber)

            return userPhoneNumberValidator.validateForUpdate(userId, userPhoneNumberId,
                    userPhoneNumber, oldUserPhoneNumber).then {
                userPhoneNumberRepository.update(userPhoneNumber).then { UserPhoneNumber newUserPhoneNumber ->
                    newUserPhoneNumber = userPhoneNumberFilter.filterForGet(newUserPhoneNumber, null)
                    return Promise.pure(newUserPhoneNumber)
                }
            }
        }
    }

    @Override
    Promise<UserPhoneNumber> patch(UserId userId, UserPhoneNumberId userPhoneNumberId,
                                          UserPhoneNumber userPhoneNumber) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userPhoneNumberId == null) {
            throw new IllegalArgumentException('userPhoneNumberId is null')
        }

        if (userPhoneNumber == null) {
            throw new IllegalArgumentException('userPhoneNumber is null')
        }

        return userPhoneNumberRepository.get(userPhoneNumberId).then { UserPhoneNumber oldUserPhoneNumber ->
            if (oldUserPhoneNumber == null) {
                throw AppErrors.INSTANCE.userPhoneNumberNotFound(userPhoneNumberId).exception()
            }

            userPhoneNumber = userPhoneNumberFilter.filterForPatch(userPhoneNumber, oldUserPhoneNumber)

            userPhoneNumberValidator.validateForUpdate(userId, userPhoneNumberId,
                    userPhoneNumber, oldUserPhoneNumber).then {

                userPhoneNumberRepository.update(userPhoneNumber).then { UserPhoneNumber newUserPhoneNumber ->
                    newUserPhoneNumber = userPhoneNumberFilter.filterForGet(newUserPhoneNumber, null)
                    return Promise.pure(newUserPhoneNumber)
                }
            }
        }
    }

    @Override
    Promise<UserPhoneNumber> delete(UserId userId, UserPhoneNumberId userPhoneNumberId) {
        return userPhoneNumberValidator.validateForGet(userId, userPhoneNumberId).then {
            userPhoneNumberRepository.delete(userPhoneNumberId)

            return Promise.pure(null)
        }
    }

    @Override
    Promise<UserPhoneNumber> get(UserId userId, UserPhoneNumberId userPhoneNumberId,
                                        @BeanParam UserPhoneNumberGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userPhoneNumberValidator.validateForGet(userId, userPhoneNumberId).then { UserPhoneNumber newUserPhoneNumber ->
            newUserPhoneNumber = userPhoneNumberFilter.filterForGet(newUserPhoneNumber,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserPhoneNumber)
        }
    }

    @Override
    Promise<Results<UserPhoneNumber>> list(UserId userId, @BeanParam UserPhoneNumberListOptions listOptions) {

        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }
        listOptions.setUserId(userId)

        return userPhoneNumberValidator.validateForSearch(listOptions).then {
            userPhoneNumberRepository.search(listOptions).then { List<UserPhoneNumber> userPhoneNumberList ->
                def result = new Results<UserPhoneNumber>(items: [])

                userPhoneNumberList.each { UserPhoneNumber newUserPhoneNumber ->
                    if (newUserPhoneNumber != null) {
                        newUserPhoneNumber = userPhoneNumberFilter.filterForGet(newUserPhoneNumber,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserPhoneNumber != null) {
                        result.items.add(newUserPhoneNumber)
                    }
                }

                return Promise.pure(result)
            }
        }
    }
}
