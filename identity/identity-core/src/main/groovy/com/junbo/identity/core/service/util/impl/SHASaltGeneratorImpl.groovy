package com.junbo.identity.core.service.util.impl

import com.junbo.identity.core.service.util.SaltGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

import java.security.SecureRandom

/**
 * Created by liangfu on 5/15/14.
 */
@CompileStatic
class SHASaltGeneratorImpl implements SaltGenerator {

    private static final char[] ALLOWED_CHARS =
            '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'.toCharArray()

    private Integer saltLength

    private final SecureRandom random = new SecureRandom()

    @Override
    String generateSalt() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < saltLength; i++) {
            char c = ALLOWED_CHARS[random.nextInt(ALLOWED_CHARS.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    @Required
    void setSaltLength(Integer saltLength) {
        this.saltLength = saltLength
    }
}
