package com.junbo.identity.data.hash.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.identity.common.util.HashHelper
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.data.hash.PiiHash
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.Email
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 5/16/14.
 */
@CompileStatic
class EmailHashImpl implements PiiHash {

    private String algorithm

    private String salt

    @Override
    boolean handles(String piiType) {
        if (piiType == UserPersonalInfoType.EMAIL.toString()) {
            return true
        }
        return false
    }

    @Override
    String generateHash(JsonNode jsonNode) {
        Email email = (Email)JsonHelper.jsonNodeToObj(jsonNode, Email)
        assert email != null

        return generateHash(email.info.toLowerCase(Locale.ENGLISH))
    }

    @Override
    String generateHash(String mail) {
        return HashHelper.shaHash(mail.toLowerCase(Locale.ENGLISH), salt, algorithm)
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
