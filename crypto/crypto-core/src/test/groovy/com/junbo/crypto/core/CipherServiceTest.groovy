/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.core

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.testng.annotations.Test

import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import java.security.SecureRandom
import java.security.spec.KeySpec

/**
 * Created by liangfu on 5/7/14.
 */
@ContextConfiguration(locations = ['classpath:test/spring/core-test.xml'])
@TransactionConfiguration(defaultRollback = false)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
public class CipherServiceTest extends AbstractTestNGSpringContextTests {

    char[] DEFAULT_CODEC = "1234567890ABCDEF".toCharArray();
    Random random = new SecureRandom();

    @Autowired
    private CipherService cipherService

    @Test(enabled = false)
    public void testEncrypt() {
        String message = 'Hello World!'
        // Get the KeyGenerator
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(256); // 192 and 256 bits may not be available

        // Generate the secret key specs.
        SecretKey skey = kgen.generateKey();
        String key = skey.getEncoded().toString();

        String encryptedValue = cipherService.encrypt(message, key)

        String decryptedValue = cipherService.decrypt(encryptedValue, key)

        assert message == decryptedValue
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
