package com.junbo.identity.data.hash.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.hash.PiiHash
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserGender
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 5/16/14.
 */
@CompileStatic
class GenderHashImpl implements PiiHash {

    private String salt

    @Override
    boolean handles(String piiType) {
        if (piiType == UserPersonalInfoType.GENDER.toString()) {
            return true
        }
        return false
    }

    @Override
    String generateHash(JsonNode jsonNode) {
        UserGender userGender = (UserGender)JsonHelper.jsonNodeToObj(jsonNode, UserGender)
        assert userGender != null

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
