package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required

import java.util.regex.Pattern

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserEmailValidatorImpl implements PiiValidator {

    private List<Pattern> allowedEmailPatterns
    private Integer minEmailLength
    private Integer maxEmailLength

    private UserPersonalInfoRepository userPersonalInfoRepository

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.EMAIL.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId) {
        Email email = (Email)JsonHelper.jsonNodeToObj(value, Email)
        checkUserEmail(email)

        return checkAdvanceUserEmail(email)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue, UserId userId) {
        Email email = (Email)JsonHelper.jsonNodeToObj(value, Email)
        Email oldEmail = (Email)JsonHelper.jsonNodeToObj(oldValue, Email)

        if (email != oldEmail) {
            checkUserEmail(email)

            if (email.value != oldEmail.value) {
                return checkAdvanceUserEmail(email)
            }

            return Promise.pure(null)
        }

        return Promise.pure(null)
    }

    private void checkUserEmail(Email email) {
        if (email.value == null) {
            throw AppErrors.INSTANCE.fieldInvalid('value').exception()
        }

        if (email.value.length() < minEmailLength) {
            throw AppErrors.INSTANCE.fieldTooShort('value', minEmailLength).exception()
        }
        if (email.value.length() > maxEmailLength) {
            throw AppErrors.INSTANCE.fieldTooLong('value', maxEmailLength).exception()
        }

        if (!allowedEmailPatterns.any {
            Pattern pattern -> pattern.matcher(email.value).matches()
        }) {
            throw AppErrors.INSTANCE.fieldInvalid('value').exception()
        }
    }

    private Promise<Void> checkAdvanceUserEmail(Email email) {
        return userPersonalInfoRepository.searchByEmail(email.value).then { List<UserPersonalInfo> existing ->
            if (!CollectionUtils.isEmpty(existing)) {
                throw AppErrors.INSTANCE.fieldDuplicate('value').exception()
            }

            return Promise.pure(null)
        }
    }

    @Required
    void setAllowedEmailPatterns(List<String> allowedEmailPatterns) {
        this.allowedEmailPatterns = allowedEmailPatterns.collect { String allowedEmailPattern ->
            Pattern.compile(allowedEmailPattern)
        }
    }

    @Required
    void setMinEmailLength(Integer minEmailLength) {
        this.minEmailLength = minEmailLength
    }

    @Required
    void setMaxEmailLength(Integer maxEmailLength) {
        this.maxEmailLength = maxEmailLength
    }

    @Required
    void setUserPersonalInfoRepository(UserPersonalInfoRepository userPersonalInfoRepository) {
        this.userPersonalInfoRepository = userPersonalInfoRepository
    }
}
