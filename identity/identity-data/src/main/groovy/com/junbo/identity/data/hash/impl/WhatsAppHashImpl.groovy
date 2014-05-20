package com.junbo.identity.data.hash.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.hash.PiiHash
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserWhatsApp
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 5/16/14.
 */
@CompileStatic
class WhatsAppHashImpl implements PiiHash {
    private String salt

    @Override
    boolean handles(String piiType) {
        if (piiType == UserPersonalInfoType.WHATSAPP.toString()) {
            return true
        }
        return false
    }

    @Override
    String generateHash(JsonNode jsonNode) {
        UserWhatsApp userWhatsApp = (UserWhatsApp)JsonHelper.jsonNodeToObj(jsonNode, UserWhatsApp)
        assert userWhatsApp != null
        return null
    }

    @Override
    String generateHash(String key) {
        return null
    }

    @Required
    void setSalt(String salt) {
        this.salt = salt
    }
}
