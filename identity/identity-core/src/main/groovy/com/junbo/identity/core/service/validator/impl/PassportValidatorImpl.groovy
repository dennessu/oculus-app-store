package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserPassport
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Check minimum and maximum passport length
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
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        UserPassport userPassport = (UserPassport)JsonHelper.jsonNodeToObj(value, UserPassport)
        checkUserPassport(userPassport)
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        UserPassport userPassport = (UserPassport)JsonHelper.jsonNodeToObj(value, UserPassport)
        UserPassport oldUserPassport = (UserPassport)JsonHelper.jsonNodeToObj(oldValue, UserPassport)

        if (userPassport != oldUserPassport) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'value can\'t be updated.').exception()
        }
        return Promise.pure(null)
    }

    @Override
    JsonNode updateJsonNode(JsonNode value) {
        return value
    }

    private void checkUserPassport(UserPassport userPassport) {
        if (userPassport.info != null) {
            if (userPassport.info.length() > maxPassportLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('value.info', maxPassportLength).exception()
            }
            if (userPassport.info.length() < minPassportLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('value.info', minPassportLength).exception()
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
