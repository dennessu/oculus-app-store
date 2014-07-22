package com.junbo.identity.data.hash.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.identity.common.util.HashHelper
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.hash.PiiHash
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserName
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 5/16/14.
 */
@CompileStatic
class NameHashImpl implements PiiHash {

    private String salt

    private String algorithm

    @Override
    boolean handles(String piiType) {
        if (piiType == UserPersonalInfoType.NAME.toString()) {
            return true
        }
        return false
    }

    @Override
    String generateHash(JsonNode jsonNode) {
        UserName name = (UserName)JsonHelper.jsonNodeToObj(jsonNode, UserName)
        assert name != null
        // name hash info based on "FirstName LastName"
        return generateHash("$name.givenName $name.familyName")
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
