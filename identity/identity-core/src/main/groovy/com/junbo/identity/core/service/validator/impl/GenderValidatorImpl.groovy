package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserGender
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class GenderValidatorImpl implements PiiValidator {

    private List<String> allowedValues;

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.GENDER.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        UserGender userGender = (UserGender)JsonHelper.jsonNodeToObj(value, UserGender)
        checkUserGender(userGender)
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        UserGender userGender = (UserGender)JsonHelper.jsonNodeToObj(value, UserGender)
        UserGender oldUserGender = (UserGender)JsonHelper.jsonNodeToObj(oldValue, UserGender)

        if (userGender != oldUserGender) {
            throw AppErrors.INSTANCE.fieldInvalidException('value', 'value can\'t be updated.').exception()
        }
        return null
    }

    private void checkUserGender(UserGender userGender) {
        if (userGender.info != null) {
            if (!(userGender.info in allowedValues)) {
                throw AppErrors.INSTANCE.fieldInvalid('value.info', allowedValues.join(',')).exception()
            }
        }
    }

    @Required
    void setAllowedValues(List<String> allowedValues) {
        this.allowedValues = allowedValues
    }
}
