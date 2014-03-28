/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource

import com.junbo.common.id.Id
import com.junbo.common.id.UserLoginAttemptId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserLoginAttemptFilter
import com.junbo.identity.core.service.validator.UserLoginAttemptValidator
import com.junbo.identity.data.repository.UserLoginAttemptRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserLoginAttempt
import com.junbo.identity.spec.options.entity.UserLoginAttemptGetOptions
import com.junbo.identity.spec.options.list.UserLoginAttemptListOptions
import com.junbo.identity.spec.resource.UserLoginAttemptResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
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
class UserLoginAttemptResourceImpl implements UserLoginAttemptResource {

    @Autowired
    private UserLoginAttemptRepository userLoginAttemptRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserLoginAttemptFilter userLoginAttemptFilter

    @Autowired
    private UserLoginAttemptValidator userLoginAttemptValidator

    @Override
    Promise<UserLoginAttempt> create(UserLoginAttempt userLoginAttempt) {
        if (userLoginAttempt == null) {
            throw new IllegalArgumentException('userLoginAttempt is null')
        }

        userLoginAttempt = userLoginAttemptFilter.filterForCreate(userLoginAttempt)

        userLoginAttemptValidator.validateForCreate(userLoginAttempt).then {

            createInNewTran(userLoginAttempt).then { UserLoginAttempt newUserLoginAttempt ->

                if (newUserLoginAttempt.succeeded == true) {
                    created201Marker.mark((Id)newUserLoginAttempt.id)

                    newUserLoginAttempt = userLoginAttemptFilter.filterForGet(newUserLoginAttempt, null)
                    return Promise.pure(newUserLoginAttempt)
                }
                if (userLoginAttempt.type == 'password') {
                    throw AppErrors.INSTANCE.userPasswordIncorrect().exception()
                }
                else {
                    throw AppErrors.INSTANCE.userPinIncorrect().exception()
                }
            }
        }
    }

    @Override
    Promise<UserLoginAttempt> get(UserLoginAttemptId userLoginAttemptId,
                                         @BeanParam UserLoginAttemptGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userLoginAttemptValidator.validateForGet(userLoginAttemptId).then { UserLoginAttempt newUserLoginAttempt ->
            newUserLoginAttempt = userLoginAttemptFilter.filterForGet(newUserLoginAttempt,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserLoginAttempt)
        }
    }

    @Override
    Promise<Results<UserLoginAttempt>> list(@BeanParam UserLoginAttemptListOptions listOptions) {

        userLoginAttemptValidator.validateForSearch(listOptions).then {
            userLoginAttemptRepository.search(listOptions).then { List<UserLoginAttempt> userLoginAttemptList ->
                def result = new Results<UserLoginAttempt>()

                userLoginAttemptList.each { UserLoginAttempt userLoginAttempt ->
                    userLoginAttempt = userLoginAttemptFilter.filterForGet(userLoginAttempt,
                                                listOptions.properties?.split(',') as List<String>)
                    result.items.add(userLoginAttempt)
                }

                return Promise.pure(result)
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Promise<UserLoginAttempt> createInNewTran(UserLoginAttempt userLoginAttempt) {
        return userLoginAttemptRepository.create(userLoginAttempt)
    }
}
