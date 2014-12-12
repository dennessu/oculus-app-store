package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.UserCredentialValidator
import com.junbo.identity.core.service.validator.UserPasswordValidator
import com.junbo.identity.core.service.validator.UserPinValidator
import com.junbo.identity.data.identifiable.CredentialType
import com.junbo.identity.data.identifiable.UserStatus
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.service.UserPersonalInfoService
import com.junbo.identity.service.UserService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserCredential
import com.junbo.identity.spec.v1.model.UserLoginName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.list.UserCredentialListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Check allowed types only password and pin
 * Call password and pin validator
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
class UserCredentialValidatorImpl implements UserCredentialValidator {

    private UserPasswordValidator userPasswordValidator

    private UserPinValidator userPinValidator

    private UserService userService

    private UserPersonalInfoService userPersonalInfoService

    private ModelMapper modelMapper

    private List<String> allowedTypes

    @Override
    Promise<Void> validateForSearch(UserId userId, UserCredentialListOptions options) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId != null && options.userId != userId) {
            throw AppCommonErrors.INSTANCE.parameterInvalid('userId').exception()
        }

        if (options.type == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('credentialType').exception()
        }
        options.setUserId(userId)

        return Promise.pure(null)
    }

    @Override
    Promise<Object> validateForCreate(UserId userId, UserCredential userCredential) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
        }
        if (userCredential == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        userCredential.userId = userId

        if (userCredential.type == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('type').exception()
        }
        if (!(userCredential.type in allowedTypes)) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('type', allowedTypes.join(',')).exception()
        }

        return userService.getNonDeletedUser(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            if (user.isAnonymous || user.status != UserStatus.ACTIVE.toString()) {
                throw AppErrors.INSTANCE.userInInvalidStatus(userId).exception()
            }

            return Promise.pure(user)
        }.then { User user ->
            if (userCredential.type == CredentialType.PASSWORD.toString()) {
                return userPasswordValidator.validateForOldPassword(userId, userCredential.currentPassword).then {
                    UserPassword userPassword = modelMapper.credentialToPassword(userCredential, new MappingContext())
                    if (userPassword == null) {
                        throw new IllegalArgumentException('mapping to password exception')
                    }

                    return userPersonalInfoService.get(user.username).then { UserPersonalInfo userPersonalInfo ->
                        if (userPersonalInfo == null) {
                            throw AppCommonErrors.INSTANCE.fieldInvalid('username').exception()
                        }

                        UserLoginName loginName = (UserLoginName)JsonHelper.jsonNodeToObj(userPersonalInfo.value, UserLoginName)
                        if (userCredential.value.toLowerCase().contains(loginName.userName.toLowerCase())) {
                            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'password can\'t contain username').exception()
                        }
                        return userPasswordValidator.validateForCreate(userId, userPassword).then {
                            return Promise.pure(userPassword)
                        }
                    }
                }
            } else if (userCredential.type == CredentialType.PIN.toString()) {
                return userPasswordValidator.validateForOldPassword(userId, userCredential.currentPassword).then {
                    UserPin userPin = modelMapper.credentialToPin(userCredential, new MappingContext())
                    if (userPin == null) {
                        throw new IllegalArgumentException('mapping to pin exception')
                    }
                    return userPinValidator.validateForCreate(userId, userPin).then {
                        return Promise.pure(userPin)
                    }
                }
            }
            throw new IllegalArgumentException('not defined mapping')
        }
    }

    @Required
    void setUserPasswordValidator(UserPasswordValidator userPasswordValidator) {
        this.userPasswordValidator = userPasswordValidator
    }

    @Required
    void setUserPinValidator(UserPinValidator userPinValidator) {
        this.userPinValidator = userPinValidator
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Required
    void setUserPersonalInfoService(UserPersonalInfoService userPersonalInfoService) {
        this.userPersonalInfoService = userPersonalInfoService
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }

    @Required
    void setAllowedTypes(List<String> allowedTypes) {
        this.allowedTypes = allowedTypes
    }
}
