/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource

import com.junbo.common.id.Id
import com.junbo.common.id.SecurityQuestionId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.SecurityQuestionFilter
import com.junbo.identity.core.service.validator.SecurityQuestionValidator
import com.junbo.identity.data.repository.SecurityQuestionRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.domaindata.SecurityQuestion
import com.junbo.identity.spec.options.entity.SecurityQuestionGetOptions
import com.junbo.identity.spec.options.list.SecurityQuestionListOptions
import com.junbo.identity.spec.resource.SecurityQuestionResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 3/14/14.
 */
@Transactional
@CompileStatic
class SecurityQuestionResourceImpl implements SecurityQuestionResource {

    @Autowired
    private SecurityQuestionRepository securityQuestionRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private SecurityQuestionFilter securityQuestionFilter

    @Autowired
    private SecurityQuestionValidator securityQuestionValidator

    @Override
    Promise<SecurityQuestion> create(SecurityQuestion securityQuestion) {
        securityQuestion = securityQuestionFilter.filterForCreate(securityQuestion)

        return securityQuestionValidator.validateForCreate(securityQuestion).then {
            return securityQuestionRepository.create(securityQuestion).then { SecurityQuestion newSecurityQuestion ->
                created201Marker.mark((Id)newSecurityQuestion.id)

                newSecurityQuestion = securityQuestionFilter.filterForGet(newSecurityQuestion, null)
                return Promise.pure(newSecurityQuestion)
            }
        }
    }

    @Override
    Promise<SecurityQuestion> put(SecurityQuestionId securityQuestionId, SecurityQuestion securityQuestion) {
        if (securityQuestionId == null) {
            throw new IllegalArgumentException('securityQuestionId is null')
        }

        return securityQuestionRepository.get(securityQuestionId).then { SecurityQuestion oldSecurityQuestion ->
            if (oldSecurityQuestion == null) {
                throw AppErrors.INSTANCE.securityQuestionNotFound(securityQuestionId).exception()
            }

            securityQuestion = securityQuestionFilter.filterForPut(securityQuestion, oldSecurityQuestion)

            return securityQuestionValidator.validateForUpdate(securityQuestionId, securityQuestion, oldSecurityQuestion).
                    then {
                return securityQuestionRepository.update(securityQuestion).then { SecurityQuestion newSecurityQuestion ->
                    newSecurityQuestion = securityQuestionFilter.filterForGet(newSecurityQuestion, null)
                    return Promise.pure(newSecurityQuestion)
                }
            }
        }
    }

    @Override
    Promise<SecurityQuestion> patch(SecurityQuestionId securityQuestionId, SecurityQuestion securityQuestion) {
        if (securityQuestionId == null) {
            throw new IllegalArgumentException('securityQuestionId is null')
        }

        return securityQuestionRepository.get(securityQuestionId).then { SecurityQuestion oldSecurityQuestion ->
            if (oldSecurityQuestion == null) {
                throw AppErrors.INSTANCE.securityQuestionNotFound(securityQuestionId).exception()
            }

            securityQuestion = securityQuestionFilter.filterForPatch(securityQuestion, oldSecurityQuestion)

            return securityQuestionValidator.validateForUpdate(securityQuestionId,
                    securityQuestion, oldSecurityQuestion).then {
                return securityQuestionRepository.update(securityQuestion).then {
                    SecurityQuestion newSecurityQuestion ->
                    newSecurityQuestion = securityQuestionFilter.filterForGet(newSecurityQuestion, null)
                    return Promise.pure(newSecurityQuestion)
                }
            }
        }
    }

    @Override
    Promise<SecurityQuestion> get(SecurityQuestionId securityQuestionId, SecurityQuestionGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException()
        }
        return securityQuestionValidator.validateForGet(securityQuestionId).then {
            return securityQuestionRepository.get(securityQuestionId).then { SecurityQuestion newSecurityQuestion ->
                if (newSecurityQuestion == null) {
                    throw AppErrors.INSTANCE.securityQuestionNotFound(securityQuestionId).exception()
                }

                newSecurityQuestion = securityQuestionFilter.filterForGet(newSecurityQuestion,
                        getOptions.properties?.split(',') as List<String>)
                return Promise.pure(newSecurityQuestion)
            }
        }
    }

    @Override
    Promise<Results<SecurityQuestion>> list(SecurityQuestionListOptions listOptions) {

        return securityQuestionValidator.validateForSearch(listOptions).then {
            return securityQuestionRepository.search(listOptions).then { List<SecurityQuestion> securityQuestionList ->
                def result = new Results<SecurityQuestion>(items: [])

                securityQuestionList.each { SecurityQuestion newSecurityQuestion ->
                    if (newSecurityQuestion != null) {
                        newSecurityQuestion = securityQuestionFilter.filterForGet(newSecurityQuestion,
                                listOptions.properties?.split(',') as List<String>)
                    }

                    if (newSecurityQuestion != null) {
                        result.items.add(newSecurityQuestion)
                    }
                }

                return Promise.pure(result)
            }
        }
    }
}
