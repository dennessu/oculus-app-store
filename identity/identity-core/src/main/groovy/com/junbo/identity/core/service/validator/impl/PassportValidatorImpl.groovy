package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserPassport
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class PassportValidatorImpl implements PiiValidator {

    private Integer minPassportLength
    private Integer maxPassportLength

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.PASSPORT.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId) {
        UserPassport userPassport = (UserPassport)JsonHelper.jsonNodeToObj(value, UserPassport)
        checkUserPassport(userPassport)
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue, UserId userId) {
        UserPassport userPassport = (UserPassport)JsonHelper.jsonNodeToObj(value, UserPassport)
        UserPassport oldUserPassport = (UserPassport)JsonHelper.jsonNodeToObj(oldValue, UserPassport)

        if (userPassport != oldUserPassport) {
            checkUserPassport(userPassport)
        }
        return Promise.pure(null)
    }

    private void checkUserPassport(UserPassport userPassport) {
        if (userPassport.info != null) {
            if (userPassport.info.length() > maxPassportLength) {
                throw AppErrors.INSTANCE.fieldTooLong('value.info', maxPassportLength).exception()
            }
            if (userPassport.info.length() < minPassportLength) {
                throw AppErrors.INSTANCE.fieldTooShort('value.info', minPassportLength).exception()
            }
        }
    }

    @Required
    void setMinPassportLength(Integer minPassportLength) {
        this.minPassportLength = minPassportLength
    }

    @Required
    void setMaxPassportLength(Integer maxPassportLength) {
        this.maxPassportLength = maxPassportLength
    }
}
