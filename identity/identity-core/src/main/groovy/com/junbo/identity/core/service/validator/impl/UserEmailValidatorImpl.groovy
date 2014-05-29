package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

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
    private UserRepository userRepository

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

        email.info = StringUtils.isEmpty(email.info) ? email.info : email.info.toLowerCase()
        oldEmail.info = StringUtils.isEmpty(oldEmail.info) ? oldEmail.info : oldEmail.info.toLowerCase()

        if (email != oldEmail) {
            throw AppErrors.INSTANCE.fieldInvalidException('value', 'value can\'t be updated').exception()
        }

        return Promise.pure(null)
    }

    private void checkUserEmail(Email email) {
        if (email.info == null) {
            throw AppErrors.INSTANCE.fieldInvalid('value.info').exception()
        }

        if (email.info.length() < minEmailLength) {
            throw AppErrors.INSTANCE.fieldTooShort('value.info', minEmailLength).exception()
        }
        if (email.info.length() > maxEmailLength) {
            throw AppErrors.INSTANCE.fieldTooLong('value.info', maxEmailLength).exception()
        }

        if (!allowedEmailPatterns.any {
            Pattern pattern -> pattern.matcher(email.info.toLowerCase()).matches()
        }) {
            throw AppErrors.INSTANCE.fieldInvalid('value.info').exception()
        }
    }

    private Promise<Void> checkAdvanceUserEmail(Email email) {
        // 2.	User’s default email is required to be globally unique - no two users can use the same email as their default email.
        //      The first user set this email to default will get this email.
        return userPersonalInfoRepository.searchByEmail(email.info.toLowerCase(), Integer.MAX_VALUE, 0).then {
            List<UserPersonalInfo> existing ->
            if (CollectionUtils.isEmpty(existing)) {
                return Promise.pure(null)
            }

            return Promise.each(existing) { UserPersonalInfo info ->
                return userRepository.get(info.userId).then { User user ->
                    if (user == null || CollectionUtils.isEmpty(user.emails)) {
                        return Promise.pure(null)
                    }

                    UserPersonalInfoLink link = user.emails.find { UserPersonalInfoLink userPersonalInfoLink ->
                        return userPersonalInfoLink.isDefault == true
                    }

                    if (link != null) {
                        return userPersonalInfoRepository.get(link.value).then { UserPersonalInfo userPersonalInfo ->
                            Email existingEmail = (Email)JsonHelper.jsonNodeToObj(userPersonalInfo.value, Email)

                            if (existingEmail.info.toLowerCase() == email.info.toLowerCase()) {
                                throw AppErrors.INSTANCE.fieldInvalid('value.info', 'Mail is already used.').exception()
                            }

                            return Promise.pure(null)
                        }
                    }

                    return Promise.pure(null)
                }
            }.then {
                // 1:   All emails are treated case-insensitive - no matter what case is provided by the API-client,
                //      the value in the resource is always forced to lower-case.
                email.info = email.info.toLowerCase()
                return Promise.pure(null)
            }
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

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }
}
