package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserSMS
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class SMSValidatorImpl implements PiiValidator {

    private Integer minTextMessageLength
    private Integer maxTextMessageLength

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.SMS.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        UserSMS userSMS = (UserSMS)JsonHelper.jsonNodeToObj(value, UserSMS)
        checkUserSMS(userSMS)

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        UserSMS userSMS = (UserSMS)JsonHelper.jsonNodeToObj(value, UserSMS)
        UserSMS oldUserSMS = (UserSMS)JsonHelper.jsonNodeToObj(oldValue, UserSMS)
        if (userSMS != oldUserSMS) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'value can\'t be updated.').exception()
        }
        return Promise.pure(null)
    }

    private void checkUserSMS(UserSMS userSMS) {
        if (userSMS.info != null) {
            if (userSMS.info.length() > maxTextMessageLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('value.info', maxTextMessageLength).exception()
            }
            if (userSMS.info.length() < minTextMessageLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('value.info', minTextMessageLength).exception()
            }
        }
    }

    @Required
    void setMinTextMessageLength(Integer minTextMessageLength) {
        this.minTextMessageLength = minTextMessageLength
    }

    @Required
    void setMaxTextMessageLength(Integer maxTextMessageLength) {
        this.maxTextMessageLength = maxTextMessageLength
    }
}
