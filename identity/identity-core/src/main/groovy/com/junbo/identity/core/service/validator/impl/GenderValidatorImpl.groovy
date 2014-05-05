package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.util.JsonHelper
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
    Promise<Void> validate(JsonNode value, UserId userId) {
        UserGender userGender = (UserGender)JsonHelper.jsonNodeToObj(value, UserGender)
        if (userGender.value != null) {
            if (!(userGender.value in allowedValues)) {
                throw AppErrors.INSTANCE.fieldInvalid('value', allowedValues.join(',')).exception()
            }
        }

        return Promise.pure(null)
    }

    @Required
    void setAllowedValues(List<String> allowedValues) {
        this.allowedValues = allowedValues
    }
}
