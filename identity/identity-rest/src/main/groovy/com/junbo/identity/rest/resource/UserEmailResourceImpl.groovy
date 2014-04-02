/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource
import com.junbo.common.id.Id
import com.junbo.common.id.UserEmailId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserEmailFilter
import com.junbo.identity.core.service.validator.UserEmailValidator
import com.junbo.identity.data.repository.UserEmailRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserEmail
import com.junbo.identity.spec.options.entity.UserEmailGetOptions
import com.junbo.identity.spec.options.list.UserEmailListOptions
import com.junbo.identity.spec.resource.UserEmailResource
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
class UserEmailResourceImpl implements UserEmailResource {

    @Autowired
    private UserEmailRepository userEmailRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserEmailFilter userEmailFilter

    @Autowired
    private UserEmailValidator userEmailValidator

    @Override
    Promise<UserEmail> create(UserId userId, UserEmail userEmail) {
        if (userEmail == null) {
            throw new IllegalArgumentException('userEmail is null')
        }

        userEmail = userEmailFilter.filterForCreate(userEmail)

        userEmailValidator.validateForCreate(userId, userEmail).then {
            userEmailRepository.create(userEmail).then { UserEmail newUserEmail ->
                created201Marker.mark((Id)newUserEmail.id)

                newUserEmail = userEmailFilter.filterForGet(newUserEmail, null)
                return Promise.pure(newUserEmail)
            }
        }
    }

    @Override
    Promise<UserEmail> put(UserId userId, UserEmailId userEmailId, UserEmail userEmail) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userEmailId == null) {
            throw new IllegalArgumentException('userEmailId is null')
        }

        if (userEmail == null) {
            throw new IllegalArgumentException('userEmail is null')
        }

        return userEmailRepository.get(userEmailId).then { UserEmail oldUserEmail ->
            if (oldUserEmail == null) {
                throw AppErrors.INSTANCE.userEmailNotFound(userEmailId).exception()
            }

            userEmail = userEmailFilter.filterForPut(userEmail, oldUserEmail)

            userEmailValidator.validateForUpdate(userId,
                    userEmailId, userEmail, oldUserEmail).then {
                userEmailRepository.update(userEmail).then { UserEmail newUserEmail ->
                    newUserEmail = userEmailFilter.filterForGet(newUserEmail, null)
                    return Promise.pure(newUserEmail)
                }
            }
        }
    }

    @Override
    Promise<UserEmail> patch(UserId userId, UserEmailId userEmailId, UserEmail userEmail) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userEmailId == null) {
            throw new IllegalArgumentException('userEmailId is null')
        }

        if (userEmail == null) {
            throw new IllegalArgumentException('userEmail is null')
        }

        return userEmailRepository.get(userEmailId).then { UserEmail oldUserEmail ->
            if (oldUserEmail == null) {
                throw AppErrors.INSTANCE.userEmailNotFound(userEmailId).exception()
            }

            userEmail = userEmailFilter.filterForPatch(userEmail, oldUserEmail)

            userEmailValidator.validateForUpdate(userId,
                    userEmailId, userEmail, oldUserEmail).then {
                userEmailRepository.update(userEmail).then { UserEmail newUserEmail ->
                    newUserEmail = userEmailFilter.filterForGet(newUserEmail, null)
                    return Promise.pure(newUserEmail)
                }
            }
        }
    }

    @Override
    Promise<UserEmail> delete(UserId userId, UserEmailId userEmailId) {
        return userEmailValidator.validateForGet(userId, userEmailId).then {
            userEmailRepository.delete(userEmailId)

            return Promise.pure(null)
        }
    }

    @Override
    Promise<UserEmail> get(UserId userId, UserEmailId userEmailId, @BeanParam UserEmailGetOptions getOptions) {

        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userEmailValidator.validateForGet(userId, userEmailId).then { UserEmail userEmail ->
            userEmail = userEmailFilter.filterForGet(userEmail,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(userEmail)
        }
    }

    @Override
    Promise<Results<UserEmail>> list(UserId userId, @BeanParam UserEmailListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('option is null')
        }
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        listOptions.setUserId(userId)
        return list(listOptions)
    }

    @Override
    Promise<Results<UserEmail>> list(@BeanParam UserEmailListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return userEmailValidator.validateForSearch(listOptions).then {
            userEmailRepository.search(listOptions).then { List<UserEmail> userEmailList ->
                def result = new Results<UserEmail>(items: [])

                userEmailList.each { UserEmail newUserEmail ->
                    if (newUserEmail != null) {
                        newUserEmail = userEmailFilter.filterForGet(newUserEmail,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserEmail != null) {
                        result.items.add(newUserEmail)
                    }
                }

                return Promise.pure(result)
            }
        }
    }
}
