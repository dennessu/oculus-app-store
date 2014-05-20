package com.junbo.identity.data.hash.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.identity.common.util.HashHelper
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.hash.PiiHash
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.PhoneNumber
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 5/16/14.
 */
@CompileStatic
class PhoneHashImpl implements PiiHash {

    private String salt

    private String algorithm

    @Override
    boolean handles(String piiType) {
        if (piiType == UserPersonalInfoType.PHONE.toString()) {
            return true
        }
        return false
    }

    @Override
    String generateHash(JsonNode jsonNode) {
        PhoneNumber phoneNumber = (PhoneNumber)JsonHelper.jsonNodeToObj(jsonNode, PhoneNumber)
        assert phoneNumber != null

        return generateHash(phoneNumber.info)
    }

    @Override
    String generateHash(String key) {
        return HashHelper.shaHash(key, salt, algorithm)
    }

    @Required
    void setSalt(String salt) {
        this.salt = salt
    }

    @Required
    void setAlgorithm(String algorithm) {
        this.algorithm = algorithm
    }
}
