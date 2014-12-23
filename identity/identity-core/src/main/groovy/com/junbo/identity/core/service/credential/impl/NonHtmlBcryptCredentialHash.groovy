package com.junbo.identity.core.service.credential.impl

import com.junbo.identity.common.util.Constants
import com.junbo.identity.common.util.HashHelper
import com.junbo.identity.core.service.credential.CredentialHash
import com.junbo.identity.core.service.util.SaltGenerator
import org.springframework.beans.factory.annotation.Required
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.util.StringUtils

/**
 * Created by xiali_000 on 2014/12/18.
 */
class NonHtmlBcryptCredentialHash implements CredentialHash {
    private static final Integer SUPPORT_VERSION = 3

    private SaltGenerator generator

    @Override
    boolean handles(Integer version) {
        if (version == SUPPORT_VERSION) {
            return true
        }
        return false
    }

    @Override
    String hash(String key) {
        String salt = generator.generateSalt()
        return SUPPORT_VERSION + Constants.COLON + HashHelper.bcryptHash(key, salt)
    }

    @Override
    boolean matches(String valueToValidate, String hash) {
        String[] hashInfo = hash.split(Constants.COLON)
        if (hashInfo == null || !strToInteger(hashInfo[0])) {
            throw new IllegalStateException('hash must be in the format as version:hash')
        }

        Integer version = Integer.parseInt(hashInfo[0])
        if (version != SUPPORT_VERSION) {
            return false
        }

        if (hashInfo.length != 2) {
            throw new IllegalStateException('hash must be in the format as version:hash')
        }
        String keyHash = hashInfo[1]

        if (BCrypt.checkpw(valueToValidate, keyHash)) {
            return true
        }
        return false
    }

    @Required
    void setGenerator(SaltGenerator generator) {
        this.generator = generator
    }

    private boolean strToInteger(String str) {
        if (StringUtils.isEmpty(str)) {
            return false
        }
        try {
            Integer.parseInt(str)
        } catch (Exception e) {
            return false
        }
        return true
    }
}
