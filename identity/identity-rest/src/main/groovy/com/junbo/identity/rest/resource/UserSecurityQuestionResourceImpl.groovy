/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource
import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.UserSecurityQuestionFilter
import com.junbo.identity.core.service.validator.UserSecurityQuestionValidator
import com.junbo.identity.data.repository.UserSecurityQuestionRepository
import com.junbo.identity.spec.model.users.UserSecurityQuestion
import com.junbo.identity.spec.options.entity.UserSecurityQuestionGetOptions
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOptions
import com.junbo.identity.spec.resource.UserSecurityQuestionResource
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
class UserSecurityQuestionResourceImpl implements UserSecurityQuestionResource {
    @Autowired
    private UserSecurityQuestionRepository userSecurityQuestionRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private UserSecurityQuestionFilter userSecurityQuestionFilter

    @Autowired
    private UserSecurityQuestionValidator userSecurityQuestionValidator

    @Override
    Promise<UserSecurityQuestion> create(UserId userId, UserSecurityQuestion userSecurityQuestion) {
        if (userSecurityQuestion == null) {
            throw new IllegalArgumentException('userSecurityQuestion is null')
        }

        userSecurityQuestion = userSecurityQuestionFilter.filterForCreate(userSecurityQuestion)

        userSecurityQuestionValidator.validateForCreate(userId, userSecurityQuestion).then {

            userSecurityQuestionRepository.search(new UserSecurityQuestionListOptions(
                    userId: userId,
                    securityQuestionId: userSecurityQuestion.securityQuestionId,
                    active: true
            )).then { List<UserSecurityQuestion> existing ->
                if (existing != null && existing.size() > 2) {
                    throw new IllegalArgumentException('userSecurityQuestion have multiple active status.')
                }

                if (existing != null && existing.size() == 1) {
                    UserSecurityQuestion existingUserSecurityQuestion = existing.get(0)
                    existingUserSecurityQuestion.setActive(false)
                    userSecurityQuestionRepository.update(existingUserSecurityQuestion)
                }

                return userSecurityQuestionRepository.create(userSecurityQuestion).
                        then { UserSecurityQuestion newUserSecurityQuestion ->
                    created201Marker.mark((Id)newUserSecurityQuestion.id)

                    newUserSecurityQuestion = userSecurityQuestionFilter.filterForGet(newUserSecurityQuestion, null)
                    return Promise.pure(newUserSecurityQuestion)
                }
            }
        }
    }

    @Override
    Promise<UserSecurityQuestion> get(UserId userId, UserSecurityQuestionId userSecurityQuestionId,
                                             @BeanParam UserSecurityQuestionGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        userSecurityQuestionValidator.validateForGet(userId, userSecurityQuestionId).
                then { UserSecurityQuestion newUserSecurityQuestion ->
                    newUserSecurityQuestion = userSecurityQuestionFilter.filterForGet(newUserSecurityQuestion,
                    getOptions.properties?.split(',') as List<String>)

            return Promise.pure(newUserSecurityQuestion)
        }
    }

    @Override
    Promise<Results<UserSecurityQuestion>> list(UserId userId,
                                                          @BeanParam UserSecurityQuestionListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }
        listOptions.setUserId(userId)

        return userSecurityQuestionValidator.validateForSearch(listOptions).then {
            userSecurityQuestionRepository.search(listOptions)
                    .then { List<UserSecurityQuestion> userSecurityQuestionList ->
                def result = new Results<UserSecurityQuestion>(items: [])

                userSecurityQuestionList.each { UserSecurityQuestion newUserSecurityQuestion ->
                    if (newUserSecurityQuestion != null) {
                        newUserSecurityQuestion = userSecurityQuestionFilter.filterForGet(newUserSecurityQuestion,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newUserSecurityQuestion != null) {
                        result.items.add(newUserSecurityQuestion)
                    }
                }

                return Promise.pure(result)
            }
        }
    }
}
