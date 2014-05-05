package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.error.AppErrors
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
    Promise<Void> validate(JsonNode value, UserId userId) {
        UserWhatsApp userWhatsApp = (UserWhatsApp)JsonHelper.jsonNodeToObj(value, UserWhatsApp)
        if (userWhatsApp.value != null) {
            if (userWhatsApp.value.length() > maxWhatsAppLength) {
                throw AppErrors.INSTANCE.fieldTooLong('whatsApp', maxWhatsAppLength).exception()
            }
            if (userWhatsApp.value.length() < minWhatsAppLength) {
                throw AppErrors.INSTANCE.fieldTooShort('whatsApp', maxWhatsAppLength).exception()
            }
        }
        return Promise.pure(null)
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
