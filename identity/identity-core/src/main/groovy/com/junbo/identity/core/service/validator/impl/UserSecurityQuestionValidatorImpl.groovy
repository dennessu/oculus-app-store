package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.id.UserSecurityQuestionId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.credential.CredentialHash
import com.junbo.identity.core.service.credential.CredentialHashFactory
import com.junbo.identity.core.service.validator.UserSecurityQuestionValidator
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.service.UserSecurityQuestionService
import com.junbo.identity.service.UserService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserSecurityQuestion
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
class UserSecurityQuestionValidatorImpl implements UserSecurityQuestionValidator {
    private UserService userService
    private UserSecurityQuestionService userSecurityQuestionService

    private Integer minSecurityQuestionLength
    private Integer maxSecurityQuestionLength

    private Integer currentCredentialVersion
    private CredentialHashFactory credentialHashFactory

    private Integer minAnswerLength
    private Integer maxAnswerLength
    // Any data that will use this data should be data issue, we may need to fix this.
    private Integer maximumFetchSize

    @Override
    Promise<UserSecurityQuestion> validateForGet(UserId userId, UserSecurityQuestionId userSecurityQuestionId) {

        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        if (userSecurityQuestionId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userSecurityQuestionId').exception()
        }

        return userService.getNonDeletedUser(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            if (user.isAnonymous) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            if (user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            return userSecurityQuestionService.get(userSecurityQuestionId).then { UserSecurityQuestion userSecurityQuestion ->
                if (userSecurityQuestion == null) {
                    throw AppErrors.INSTANCE.userSecurityQuestionNotFound(userSecurityQuestionId).exception()
                }

                if (userId != userSecurityQuestion.userId) {
                    throw AppCommonErrors.INSTANCE.
                            parameterInvalid('userId', 'userId and userSecurityQuestion.userId doesn\'t match.').exception()
                }

                return Promise.pure(userSecurityQuestion)
            }
        }
    }

    @Override
    Promise<Void> validateForSearch(UserSecurityQuestionListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserId userId, UserSecurityQuestion userSecurityQuestion) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        checkBasicUserSecurityQuestionInfo(userSecurityQuestion)
        if (userSecurityQuestion.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }
        if (userSecurityQuestion.userId != null && userSecurityQuestion.userId != userId) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('userId', userSecurityQuestion.userId, userId).exception()
        }

        // Check whether this security question is used before
        return userService.getNonDeletedUser(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            if (user.isAnonymous || user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }
            return Promise.pure(null)
        }.then {
            return userSecurityQuestionService.searchByUserId(userId, maximumFetchSize, 0).then { Results<UserSecurityQuestion> userSecurityQuestionList ->
                if (userSecurityQuestionList != null && !CollectionUtils.isEmpty(userSecurityQuestionList.items)) {
                    boolean exists = userSecurityQuestionList.items.any { UserSecurityQuestion existing ->
                        return (existing.securityQuestion == userSecurityQuestion.securityQuestion)
                    }
                    if (exists) {
                        throw AppCommonErrors.INSTANCE.fieldInvalid('securityQuestion').exception()
                    }
                }
                userSecurityQuestion.setUserId(userId)

                List<CredentialHash> credentialHashList = credentialHashFactory.getAllCredentialHash()
                CredentialHash matched = credentialHashList.find { CredentialHash hash ->
                    return hash.handles(currentCredentialVersion)
                }

                if (matched == null) {
                    throw new IllegalStateException('No matched version: ' + currentCredentialVersion
                            + ' for CredentialHash')
                }

                userSecurityQuestion.setAnswerHash(matched.hash(userSecurityQuestion.answer))
                return Promise.pure(null)
            }
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserId userId, UserSecurityQuestionId userSecurityQuestionId,
                        UserSecurityQuestion userSecurityQuestion, UserSecurityQuestion oldUserSecurityQuestion) {
        if (userSecurityQuestion == null) {
            throw new IllegalArgumentException('userSecurityQuestion is null')
        }
        if (oldUserSecurityQuestion == null) {
            throw new IllegalArgumentException('oldUserSecurityQuestion is null')
        }

        if (userSecurityQuestion.id != userSecurityQuestionId) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('userSecurityQuestionId').exception()
        }

        if (userSecurityQuestion.id != oldUserSecurityQuestion.id) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('userSecurityQuestionId').exception()
        }

        if (userSecurityQuestion.userId != userId) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('userId').exception()
        }

        if (oldUserSecurityQuestion.userId != userId) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('userId').exception()
        }

        checkBasicUserSecurityQuestionInfo(userSecurityQuestion)

        return validateForGet(userId, userSecurityQuestionId).then { UserSecurityQuestion existingSecurityQuestion ->
            if (existingSecurityQuestion == null) {
                throw AppErrors.INSTANCE.userSecurityQuestionNotFound(userSecurityQuestionId).exception()
            }
            if (userSecurityQuestion.userId != existingSecurityQuestion.userId) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('userId').exception()
            }

            if (userSecurityQuestion.securityQuestion != oldUserSecurityQuestion.securityQuestion) {
                return userSecurityQuestionService.searchByUserId(userId, maximumFetchSize, 0).then { Results<UserSecurityQuestion> userSecurityQuestionList ->
                    if (userSecurityQuestionList == null || CollectionUtils.isEmpty(userSecurityQuestionList.items)) {
                        return Promise.pure(null)
                    }
                    boolean securityQuestionExists = userSecurityQuestionList.items.any { UserSecurityQuestion existing ->
                        return (existing.securityQuestion == userSecurityQuestion.securityQuestion)
                    }
                    if (securityQuestionExists) {
                        throw AppCommonErrors.INSTANCE.fieldInvalid('securityQuestion').exception()
                    }
                    return Promise.pure(null)
                }
            }

            return Promise.pure(null)
        }
    }

    private void checkBasicUserSecurityQuestionInfo(UserSecurityQuestion userSecurityQuestion) {
        if (userSecurityQuestion == null) {
            throw new IllegalArgumentException('userSecurityQuestion is null')
        }

        if (userSecurityQuestion.answer == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('answer').exception()
        }
        if (userSecurityQuestion.answer.length() > maxAnswerLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('answer', maxAnswerLength).exception()
        }
        if (userSecurityQuestion.answer.length() < minAnswerLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('answer', minAnswerLength).exception()
        }

        if (userSecurityQuestion.securityQuestion == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('securityQuestion').exception()
        }
        if (userSecurityQuestion.securityQuestion.length() > maxSecurityQuestionLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('securityQuestion', maxSecurityQuestionLength).exception()
        }
        if (userSecurityQuestion.securityQuestion.length() < minSecurityQuestionLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('securityQuestion', minSecurityQuestionLength).exception()
        }
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Required
    void setUserSecurityQuestionService(UserSecurityQuestionService userSecurityQuestionService) {
        this.userSecurityQuestionService = userSecurityQuestionService
    }

    @Required
    void setMinSecurityQuestionLength(Integer minSecurityQuestionLength) {
        this.minSecurityQuestionLength = minSecurityQuestionLength
    }

    @Required
    void setMaxSecurityQuestionLength(Integer maxSecurityQuestionLength) {
        this.maxSecurityQuestionLength = maxSecurityQuestionLength
    }

    @Required
    void setCurrentCredentialVersion(Integer currentCredentialVersion) {
        this.currentCredentialVersion = currentCredentialVersion
    }

    @Required
    void setCredentialHashFactory(CredentialHashFactory credentialHashFactory) {
        this.credentialHashFactory = credentialHashFactory
    }

    @Required
    void setMinAnswerLength(Integer minAnswerLength) {
        this.minAnswerLength = minAnswerLength
    }

    @Required
    void setMaxAnswerLength(Integer maxAnswerLength) {
        this.maxAnswerLength = maxAnswerLength
    }

    @Required
    void setMaximumFetchSize(Integer maximumFetchSize) {
        this.maximumFetchSize = maximumFetchSize
    }
}
