package com.junbo.crypto.core.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.crypto.core.validator.MasterKeyValidator
import com.junbo.crypto.spec.model.MasterKey
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Required

import java.security.SecureRandom

/**
 * Created by liangfu on 5/13/14.
 */
@CompileStatic
class MasterKeyValidatorImpl implements MasterKeyValidator {
    private static final char[] ALLOWED_CHARS =
            '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'.toCharArray()

    private Integer masterKeyBitLength

    private SecureRandom rand = new SecureRandom()

    @Override
    Promise<Void> validateMasterKeyCreate(MasterKey key) {
        if (key == null) {
            throw new IllegalArgumentException('key is null')
        }

        key.value = generateMasterKey((Integer)(masterKeyBitLength/8))

        if (key.value == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('value').exception()
        }
        if (key.encryptValue != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('encryptValue').exception()
        }

        return Promise.pure(null)
    }

    @Required
    void setMasterKeyBitLength(Integer masterKeyBitLength) {
        this.masterKeyBitLength = masterKeyBitLength
    }

    private String generateMasterKey(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = ALLOWED_CHARS[rand.nextInt(ALLOWED_CHARS.length)];
            sb.append(c);
        }
        return sb.toString();
    }
}
