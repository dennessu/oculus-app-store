/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.db.generator.impl;

import com.junbo.csr.db.generator.TokenGenerator;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Javadoc.
 */
public class SecureRandomTokenGenerator implements TokenGenerator {

    private static final Pattern DEFAULT_CODEC_PATTERN = Pattern.compile("[0-9A-Za-z=\\-~]+");

    private final Random random = new SecureRandom();

    private int csrInvitationCode;

    @Required
    public void setCsrInvitationCode(int csrInvitationCode) {
        this.csrInvitationCode = csrInvitationCode;
    }

    private String generate(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);

        return Base64.encodeBase64URLSafeString(bytes).replace('_', '~');
    }

    @Override
    public String generateCsrInvitationCode() {
        return generate(csrInvitationCode);
    }

    @Override
    public boolean isCsrInvitationCode(String codeValue) {
        Assert.notNull(codeValue);
        return DEFAULT_CODEC_PATTERN.matcher(codeValue).matches();
    }
}
