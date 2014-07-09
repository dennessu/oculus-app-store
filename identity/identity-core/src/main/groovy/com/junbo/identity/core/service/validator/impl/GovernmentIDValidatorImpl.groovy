package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserGovernmentID
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class GovernmentIDValidatorImpl implements PiiValidator {

    private Integer minGovernmentIDLength
    private Integer maxGovernmentIDLength

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.GOVERNMENT_ID.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        UserGovernmentID userGovernmentID = (UserGovernmentID)JsonHelper.jsonNodeToObj(value, UserGovernmentID)
        checkUserGovernmentId(userGovernmentID)

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        UserGovernmentID userGovernmentID = (UserGovernmentID)JsonHelper.jsonNodeToObj(value, UserGovernmentID)
        UserGovernmentID oldUserGovernmentId = (UserGovernmentID)JsonHelper.jsonNodeToObj(oldValue, UserGovernmentID)

        if (userGovernmentID != oldUserGovernmentId) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'value can\'t be updated.').exception()
        }

        return Promise.pure(null)
    }

    private void checkUserGovernmentId(UserGovernmentID userGovernmentID) {
        if (userGovernmentID.info != null) {
            if (userGovernmentID.info.length() > maxGovernmentIDLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('value.info', maxGovernmentIDLength).exception()
            }
            if (userGovernmentID.info.length() < minGovernmentIDLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('value.info', minGovernmentIDLength).exception()
            }
        }
    }

    @Required
    void setMinGovernmentIDLength(Integer minGovernmentIDLength) {
        this.minGovernmentIDLength = minGovernmentIDLength
    }

    @Required
    void setMaxGovernmentIDLength(Integer maxGovernmentIDLength) {
        this.maxGovernmentIDLength = maxGovernmentIDLength
    }
}
