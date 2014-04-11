package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserId
import com.junbo.identity.core.service.validator.UserCredentialValidator
import com.junbo.identity.core.service.validator.UserPasswordValidator
import com.junbo.identity.core.service.validator.UserPinValidator
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.UserPassword
import com.junbo.identity.spec.model.users.UserPin
import com.junbo.identity.spec.v1.model.UserCredential
import com.junbo.identity.spec.v1.option.list.UserCredentialListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.glassfish.jersey.internal.util.Base64
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
class UserCredentialValidatorImpl implements UserCredentialValidator {

    private UserPasswordValidator userPasswordValidator

    private UserPinValidator userPinValidator

    private ModelMapper modelMapper

    private List<String> allowedTypes

    @Override
    Promise<Void> validateForSearch(UserId userId, UserCredentialListOptions options) {
        if (userId == null) {
            throw new IllegalArgumentException('userId is null')
        }
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.userId != null && options.userId != userId) {
            throw AppErrors.INSTANCE.parameterInvalid('userId').exception()
        }

        if (options.type == null) {
            throw AppErrors.INSTANCE.parameterRequired('type').exception()
        }
        options.setUserId(userId)

        return Promise.pure(null)
    }

    @Override
    Promise<Object> validateForCreate(UserId userId, UserCredential userCredential) {
        if (userCredential == null) {
            throw new IllegalArgumentException('userCredential is null')
        }

        if (userCredential.userId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userId').exception()
        }

        if (userCredential.userId != userId) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', userId.toString()).exception()
        }

        if (userCredential.type == null) {
            throw AppErrors.INSTANCE.fieldRequired('type').exception()
        }
        if (!(userCredential.type in allowedTypes)) {
            throw AppErrors.INSTANCE.fieldInvalid('type', allowedTypes.join(',')).exception()
        }

        if (userCredential.type == 'password') {
            return userPasswordValidator.validateForOldPassword(userId, userCredential.oldValue).then {
                String decoded = Base64.decodeAsString(userCredential.oldValue)
                String[] split = decoded.split(':')
                UserPassword userPassword = modelMapper.credentialToPassword(userCredential, new MappingContext())
                userPassword.value = split[1]
                if (userPassword == null) {
                    throw new IllegalArgumentException('mapping to password exception')
                }
                return userPasswordValidator.validateForCreate(userId, userPassword).then {
                    return Promise.pure(userPassword)
                }
            }
        } else if (userCredential.type == 'pin') {
            return userPinValidator.validateForOldPassword(userId, userCredential.oldValue).then {
                String decoded = Base64.decodeAsString(userCredential.oldValue)
                String[] split = decoded.split(':')
                UserPin userPin = modelMapper.credentialToPin(userCredential, new MappingContext())
                userPin.value = split[1]
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

    @Required
    void setUserPasswordValidator(UserPasswordValidator userPasswordValidator) {
        this.userPasswordValidator = userPasswordValidator
    }

    @Required
    void setUserPinValidator(UserPinValidator userPinValidator) {
        this.userPinValidator = userPinValidator
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
