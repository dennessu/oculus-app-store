package com.junbo.identity.data.hash.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.identity.common.util.HashHelper
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.hash.PiiHash
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserLoginName
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 8/6/14.
 */
@CompileStatic
class UserNameImpl implements PiiHash {

    private String algorithm

    private String salt

    @Override
    boolean handles(String piiType) {
        if (piiType == UserPersonalInfoType.USERNAME.toString()) {
            return true
        }
        return false
    }

    @Override
    String generateHash(JsonNode jsonNode) {
        UserLoginName loginName = (UserLoginName)JsonHelper.jsonNodeToObj(jsonNode, UserLoginName)
        assert loginName != null

        return generateHash(loginName.canonicalUsername.toLowerCase(Locale.ENGLISH))
    }

    @Override
    String generateHash(String canonicalUsername) {
        return HashHelper.shaHash(canonicalUsername.toLowerCase(Locale.ENGLISH), salt, algorithm)
    }

    @Required
    void setAlgorithm(String algorithm) {
        this.algorithm = algorithm
    }

    @Required
    void setSalt(String salt) {
        this.salt = salt
    }
}
