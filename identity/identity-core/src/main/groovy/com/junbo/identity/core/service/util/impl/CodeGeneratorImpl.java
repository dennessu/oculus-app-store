/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service.util.impl;

import com.junbo.identity.core.service.util.CodeGenerator;
import groovy.transform.CompileStatic;
import org.springframework.beans.factory.annotation.Required;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by liangfu on 5/6/14.
 */
@CompileStatic
public class CodeGeneratorImpl implements CodeGenerator {

    private final Random random = new SecureRandom();

    private Integer codeLength;
    private String codec;

    private String generate(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);

        return getString(bytes);
    }

    private String getString(byte[] bytes) {
        char[] chars = new char[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            chars[i] = codec.charAt(((bytes[i] & 0xFF) % codec.length()));
        }

        return new String(chars);
    }

    @Override
    public String generateCode() {
        return generate(codeLength);
    }

    @Required
    public void setCodeLength(Integer codeLength) {
        this.codeLength = codeLength;
    }

    @Required
    public void setCodec(String codec) {
        this.codec = codec;
    }
}
