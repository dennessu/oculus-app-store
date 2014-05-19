package com.junbo.identity.data.hash.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.identity.data.hash.PiiHash
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 5/16/14.
 */
@CompileStatic
class WipedHashImpl implements PiiHash {
    private String salt

    @Override
    boolean handles(String piiType) {
        if (piiType == UserPersonalInfoType.WIPED.toString()) {
            return true
        }
        return false
    }

    @Override
    String generateHash(JsonNode jsonNode) {
        // todo:    Later add hash here
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
