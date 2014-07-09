package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserEIN
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.util.regex.Pattern

/**
 * Created by liangfu on 6/16/14.
 */
@CompileStatic
class UserEINValidatorImpl implements PiiValidator {

    private Pattern allowedPattern

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.EIN.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        UserEIN userEIN = (UserEIN)JsonHelper.jsonNodeToObj(value, UserEIN)

        checkBasicUserEIN(userEIN)
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        UserEIN userEIN = (UserEIN)JsonHelper.jsonNodeToObj(value, UserEIN)
        UserEIN oldUserEIN = (UserEIN)JsonHelper.jsonNodeToObj(oldValue, UserEIN)
        if (userEIN != oldUserEIN) {
            checkBasicUserEIN(userEIN)
        }
        return Promise.pure(null)
    }

    private void checkBasicUserEIN(UserEIN userEIN) {
        if (userEIN.info == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('value.info').exception()
        }

        if (!allowedPattern.matcher(userEIN.info).matches()) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value.info').exception()
        }
    }

    @Required
    void setAllowedPattern(Pattern allowedPattern) {
        this.allowedPattern = allowedPattern
    }
}
