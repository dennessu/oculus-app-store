package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserSSN
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.util.regex.Pattern

/**
 * Created by liangfu on 6/16/14.
 */
@CompileStatic
class UserSSNValidatorImpl implements PiiValidator {
    private Pattern allowedSSNPattern

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.SSN.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId) {
        UserSSN userSSN = (UserSSN)JsonHelper.jsonNodeToObj(value, UserSSN)

        checkBasicUserSSN(userSSN)

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue, UserId userId) {
        UserSSN userSSN = (UserSSN)JsonHelper.jsonNodeToObj(value, UserSSN)
        UserSSN oldUserSSN = (UserSSN)JsonHelper.jsonNodeToObj(oldValue, UserSSN)

        if (userSSN != oldUserSSN) {
            checkBasicUserSSN(userSSN)
        }
        return Promise.pure(null)
    }

    private void checkBasicUserSSN(UserSSN userSSN) {
        if (userSSN.info == null) {
            throw AppErrors.INSTANCE.fieldRequired('value.info').exception()
        }

        if (!this.allowedSSNPattern.matcher(userSSN.info).matches()) {
            throw AppErrors.INSTANCE.fieldInvalid('value.info').exception()
        }
    }

    @Required
    void setAllowedSSNPattern(String allowedSSNPattern) {
        this.allowedSSNPattern = Pattern.compile(allowedSSNPattern)
    }
}