package com.junbo.identity.core.service.validator.impl
import com.junbo.common.id.SecurityQuestionId
import com.junbo.identity.core.service.validator.SecurityQuestionValidator
import com.junbo.identity.data.repository.SecurityQuestionRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.domaindata.SecurityQuestion
import com.junbo.identity.spec.options.list.SecurityQuestionListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
/**
 * Created by liangfu on 3/27/14.
 */
@CompileStatic
class SecurityQuestionValidatorImpl implements SecurityQuestionValidator {

    private SecurityQuestionRepository securityQuestionRepository

    private Integer securityQuestionValueMinLength

    private Integer securityQuestionValueMaxLength

    @Required
    void setSecurityQuestionRepository(SecurityQuestionRepository securityQuestionRepository) {
        this.securityQuestionRepository = securityQuestionRepository
    }

    @Required
    void setSecurityQuestionValueMinLength(Integer securityQuestionValueMinLength) {
        this.securityQuestionValueMinLength = securityQuestionValueMinLength
    }

    @Required
    void setSecurityQuestionValueMaxLength(Integer securityQuestionValueMaxLength) {
        this.securityQuestionValueMaxLength = securityQuestionValueMaxLength
    }

    @Override
    Promise<Void> validateForGet(SecurityQuestionId securityQuestionId) {
        if (securityQuestionId == null) {
            throw new IllegalArgumentException('id is null')
        }
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForSearch(SecurityQuestionListOptions options) {
        if (options.value == null) {
            throw AppErrors.INSTANCE.parameterRequired('value').exception()
        }

        return null
    }

    @Override
    Promise<Void> validateForCreate(SecurityQuestion securityQuestion) {
        basicCheckForSecurityQuestion(securityQuestion)
        if (securityQuestion.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        if (securityQuestion.active != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('active').exception()
        }

        return securityQuestionRepository.search(new SecurityQuestionListOptions(
                value: securityQuestion.value,
                active: true
        )).then { List<SecurityQuestion> existing ->
            existing.each { SecurityQuestion singleObj ->
                if (singleObj != null) {
                    throw AppErrors.INSTANCE.fieldDuplicate('value').exception()
                }
            }
            securityQuestion.setActive(true)
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(SecurityQuestionId id, SecurityQuestion securityQuestion,
                                    SecurityQuestion oldSecurityQuestion) {
        basicCheckForSecurityQuestion(securityQuestion)
        if (id == null) {
            throw new IllegalArgumentException('id is null')
        }
        if (securityQuestion.id != null && securityQuestion.id != id) {
            throw AppErrors.INSTANCE.fieldInvalid('id', id.value.toString()).exception()
        }
        if (securityQuestion.id != null && securityQuestion.id != oldSecurityQuestion.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id', oldSecurityQuestion.id.toString()).exception()
        }
        if (securityQuestion.active == null) {
            throw AppErrors.INSTANCE.fieldRequired('active').exception()
        }
        if (securityQuestion.value != oldSecurityQuestion.value) {
            return securityQuestionRepository.search(new SecurityQuestionListOptions(
                    value: securityQuestion.value,
                    active: true
            )).then { List<SecurityQuestion> existing ->
                existing.each { SecurityQuestion singleObj ->
                    if (singleObj != null) {
                        throw AppErrors.INSTANCE.fieldDuplicate('value').exception()
                    }
                }
                securityQuestion.setActive(true)
                return Promise.pure(null)
            }
        }
        return Promise.pure(null)
    }

    private void basicCheckForSecurityQuestion(SecurityQuestion  securityQuestion) {
        if (securityQuestion == null) {
            throw new IllegalArgumentException()
        }

        if (securityQuestion.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }

        if (securityQuestion.value.length() > securityQuestionValueMaxLength) {
            throw AppErrors.INSTANCE.fieldTooLong('value', securityQuestionValueMaxLength).exception()
        }

        if (securityQuestion.value.length() < securityQuestionValueMinLength) {
            throw AppErrors.INSTANCE.fieldTooShort('value', securityQuestionValueMinLength).exception()
        }
    }
}
