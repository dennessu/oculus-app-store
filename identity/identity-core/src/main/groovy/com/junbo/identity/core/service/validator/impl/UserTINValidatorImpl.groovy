package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserTIN
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.util.regex.Pattern

/**
 * Created by liangfu on 6/16/14.
 */
@CompileStatic
class UserTINValidatorImpl implements PiiValidator {

    private Pattern allowedPatten

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.TIN.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        UserTIN userTIN = (UserTIN)JsonHelper.jsonNodeToObj(value, UserTIN)

        checkBasicUserTIN(userTIN)
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        UserTIN userTIN = (UserTIN)JsonHelper.jsonNodeToObj(value, UserTIN)
        UserTIN oldUserTIN = (UserTIN)JsonHelper.jsonNodeToObj(oldValue, UserTIN)

        if (userTIN != oldUserTIN) {
            checkBasicUserTIN(userTIN)
        }
        return Promise.pure(null)
    }

    @Override
    JsonNode updateJsonNode(JsonNode value) {
        return value
    }

    private void checkBasicUserTIN(UserTIN userTIN) {
        if (userTIN.info == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('value.info').exception()
        }

        if (!allowedPatten.matcher(userTIN.info).matches()) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value.info').exception()
        }
    }

    @Required
    void setAllowedPatten(String pattern) {
        this.allowedPatten = Pattern.compile(pattern)
    }
}
