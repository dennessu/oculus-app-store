package com.junbo.identity.data.hash.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.identity.data.hash.PiiHash
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 6/16/14.
 */
@CompileStatic
class UserTINImpl implements PiiHash {
    @Override
    boolean handles(String piiType) {
        if (piiType == UserPersonalInfoType.TIN.toString()) {
            return true
        }
        return false
    }

    @Override
    String generateHash(JsonNode jsonNode) {
        return null
    }

    @Override
    String generateHash(String key) {
        return null
    }
}
