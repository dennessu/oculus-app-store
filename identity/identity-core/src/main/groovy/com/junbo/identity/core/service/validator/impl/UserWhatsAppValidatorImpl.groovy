package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserWhatsApp
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class UserWhatsAppValidatorImpl implements PiiValidator {

    private Integer minWhatsAppLength
    private Integer maxWhatsAppLength

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.WHATSAPP.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        UserWhatsApp userWhatsApp = (UserWhatsApp)JsonHelper.jsonNodeToObj(value, UserWhatsApp)
        checkUserWhatsApp(userWhatsApp)
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        UserWhatsApp userWhatsApp = (UserWhatsApp)JsonHelper.jsonNodeToObj(value, UserWhatsApp)
        UserWhatsApp oldUserWhatsApp = (UserWhatsApp)JsonHelper.jsonNodeToObj(oldValue, UserWhatsApp)

        if (userWhatsApp != oldUserWhatsApp) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'value can\'t be updated.').exception()
        }
        return Promise.pure(null)
    }

    private void checkUserWhatsApp(UserWhatsApp userWhatsApp) {
        if (userWhatsApp.info != null) {
            if (userWhatsApp.info.length() > maxWhatsAppLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('value.info', maxWhatsAppLength).exception()
            }
            if (userWhatsApp.info.length() < minWhatsAppLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('value.info', maxWhatsAppLength).exception()
            }
        }
    }

    @Required
    void setMinWhatsAppLength(Integer minWhatsAppLength) {
        this.minWhatsAppLength = minWhatsAppLength
    }

    @Required
    void setMaxWhatsAppLength(Integer maxWhatsAppLength) {
        this.maxWhatsAppLength = maxWhatsAppLength
    }
}
