package com.junbo.crypto.core.generator.impl

import com.junbo.crypto.core.generator.SecurityKeyGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.security.SecureRandom

/**
 * Created by liangfu on 5/14/14.
 */
@CompileStatic
class SecurityKeyGeneratorImpl implements SecurityKeyGenerator {

    private static final char[] DEFAULT_CODEC =
            "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private final Random random = new SecureRandom();

    private Integer userKeyLength

    @Override
    String generateUserKey() {
        return generate(userKeyLength)
    }

    @Required
    void setUserKeyLength(Integer userKeyLength) {
        this.userKeyLength = userKeyLength
    }

    private String generate(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);

        return getString(bytes);
    }

    private String getString(byte[] bytes) {
        char[] chars = new char[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char)DEFAULT_CODEC[((bytes[i] & 0xFF) % DEFAULT_CODEC.length)];
        }

        return new String(chars);
    }
}
