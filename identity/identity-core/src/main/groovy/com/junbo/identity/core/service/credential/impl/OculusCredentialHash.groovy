package com.junbo.identity.core.service.credential.impl

import com.junbo.identity.common.util.Constants
import com.junbo.identity.common.util.HashHelper
import com.junbo.identity.core.service.credential.CredentialHash
import com.junbo.identity.core.service.util.SaltGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 5/15/14.
 */
@CompileStatic
class OculusCredentialHash implements CredentialHash {

    private static final Integer SUPPORT_VERSION = 1

    private static final String MD5_ALGORITHM = 'MD5'
    private static final String SHA_1_ALGORITHM = 'SHA-1'
    private static final String SHA_256_ALGORITHM = 'SHA-256'

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
        String pepper = generator.generateSalt()
        return generateCipherHash(key, salt, pepper)
    }

    @Override
    boolean matches(String valueToValidate, String hash) {
        String[] hashInfo = hash.split(Constants.COLON)
        if (hashInfo == null || !strToInteger(hashInfo[0])) {
            throw new IllegalStateException('hash must be in the format as version:salt:pepper:hash')
        }

        Integer version = Integer.parseInt(hashInfo[0])
        if (version != SUPPORT_VERSION) {
            return false
        }

        if (hashInfo.length != 4) {
            throw new IllegalStateException('hash must be in the format as version:salt:pepper:hash')
        }

        String salt = hashInfo[1]
        String pepper = hashInfo[2]

        String valueToValidateHash = generateCipherHash(valueToValidate, salt, pepper)
        if (valueToValidateHash == hash) {
            return true
        }

        return false
    }
/**
     * Generates shaHash string from plain-text password, with either the existing salt and pepper for verification
     * or creates randomly generated salt and pepper, if omitted.
     *
     * @param password
     * @param passwordSalt
     * @param passwordPepper
     * @return string - [version]:[salt]:[pepper]:[password shaHash]
     */
    public String generateCipherHash(String password, String passwordSalt, String passwordPepper) {
        if (passwordSalt == null || passwordPepper == null) {
            throw new RuntimeException()
        }
        passwordSalt = passwordSalt.replace(':', 'f')
        passwordPepper = passwordPepper.replace(':', 'f')

        String temp = HashHelper.shaHash(password, null, MD5_ALGORITHM)
        temp = shuffleString(temp)
        temp = temp + passwordSalt
        temp = HashHelper.shaHash(temp, null, SHA_256_ALGORITHM)
        temp = passwordPepper + temp
        temp = shuffleString(temp)
        temp = HashHelper.shaHash(temp, null, SHA_1_ALGORITHM)
        temp = shuffleString(temp)
        temp = HashHelper.shaHash(temp, null, SHA_256_ALGORITHM)
        temp = shuffleString(temp)

        return SUPPORT_VERSION + Constants.COLON + passwordSalt + Constants.COLON +
                passwordPepper + Constants.COLON + temp
    }

    @Required
    void setGenerator(SaltGenerator generator) {
        this.generator = generator
    }

    private String shuffleString(String s) {
        StringBuilder sb1 = new StringBuilder()
        StringBuilder sb2 = new StringBuilder()

        for (int i=0; i < s.length(); i++) {
            if (i % 2 == 0) {
                sb1.append(s.charAt(i))
            } else {
                sb2.append(s.charAt(i))
            }
        }
        return sb1.toString() + sb2.toString()
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
