/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource

import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionAttemptId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserSecurityQuestionAttemptFilter
import com.junbo.identity.core.service.validator.UserSecurityQuestionAttemptValidator
import com.junbo.identity.data.repository.UserSecurityQuestionAttemptRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserSecurityQuestionAttempt
import com.junbo.identity.spec.options.entity.UserSecurityQuestionAttemptGetOption
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions
import com.junbo.identity.spec.resource.UserSecurityQuestionAttemptResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionCallback

import javax.ws.rs.BeanParam
import javax.ws.rs.ext.Provider

/**
 * Created by liangfu on 3/25/14.
 */
@Provider
@Component
@Scope('prototype')
@CompileStatic
class UserSecurityQuestionAttemptResourceImpl implements UserSecurityQuestionAttemptResource {

    @Autowired
    private UserSecurityQuestionAttemptRepository userSecurityQuestionAttemptRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserSecurityQuestionAttemptFilter userSecurityQuestionAttemptFilter

    @Autowired
    private UserSecurityQuestionAttemptValidator userSecurityQuestionAttemptValidator

    @Autowired
    private PlatformTransactionManager transactionManager

    @Override
    @Transactional
    Promise<UserSecurityQuestionAttempt> create(UserId userId,
                                                       UserSecurityQuestionAttempt userSecurityQuestionAttempt) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }

        if (userSecurityQuestionAttempt == null) {
            throw new IllegalArgumentException('userSecurityQuestionAttempt')
        }

        userSecurityQuestionAttempt = userSecurityQuestionAttemptFilter.filterForCreate(userSecurityQuestionAttempt)

        userSecurityQuestionAttemptValidator.validateForCreate(userId, userSecurityQuestionAttempt).then {

            createInNewTran(userSecurityQuestionAttempt).then { UserSecurityQuestionAttempt attempt ->

                if (attempt.succeeded == true) {
                    created201Marker.mark((Id)attempt.id)

                    attempt = userSecurityQuestionAttemptFilter.filterForGet(attempt, null)
                    return Promise.pure(attempt)
                }

                throw AppErrors.INSTANCE.userSecurityQuestionIncorrect().exception()
            }
        }
    }

    @Override
    Promise<UserSecurityQuestionAttempt> get(UserId userId, UserSecurityQuestionAttemptId id,
                                                    @BeanParam UserSecurityQuestionAttemptGetOption getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return userSecurityQuestionAttemptValidator.validateForGet(userId, id).
                then { UserSecurityQuestionAttempt newUserSecurityQuestionAttempt ->
            newUserSecurityQuestionAttempt = userSecurityQuestionAttemptFilter.
                    filterForGet(newUserSecurityQuestionAttempt, getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserSecurityQuestionAttempt)
        }
    }

    @Override
    Promise<Results<UserSecurityQuestionAttempt>> list(UserId userId,
                                                  @BeanParam UserSecurityQuestionAttemptListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('userId is null')
        }
        listOptions.setUserId(userId)
        userSecurityQuestionAttemptValidator.validateForSearch(listOptions).then {
            userSecurityQuestionAttemptRepository.search(listOptions).
                    then { List<UserSecurityQuestionAttempt> userSecurityQuestionAttemptList ->
                def result = new Results<UserSecurityQuestionAttempt>(items: [])

                userSecurityQuestionAttemptList.each { UserSecurityQuestionAttempt attempt ->
                    attempt = userSecurityQuestionAttemptFilter.filterForGet(attempt,
                            listOptions.properties?.split(',') as List<String>)
                    result.items.add(attempt)
                }

                return Promise.pure(result)
            }
        }
    }

    Promise<UserSecurityQuestionAttempt> createInNewTran(UserSecurityQuestionAttempt userLoginAttempt) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return template.execute(new TransactionCallback<Promise<UserSecurityQuestionAttempt>>() {
            Promise<UserSecurityQuestionAttempt> doInTransaction(TransactionStatus txnStatus) {
                return userSecurityQuestionAttemptRepository.create(userLoginAttempt)
            }
        }
        )
    }
}
