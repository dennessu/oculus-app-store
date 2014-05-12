/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.core

import com.junbo.crypto.common.HexHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.testng.annotations.Test

import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import java.security.SecureRandom

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
    private AESCipherService cipherService

    @Test
    public void testEncrypt() {
        // Get the KeyGenerator
        KeyGenerator kgen = KeyGenerator.getInstance("AES");

        // Generate the secret key specs.
        SecretKey skey = kgen.generateKey();
        String message = 'Hello World!'
        // Get the KeyGenerator

        String encryptedValue = cipherService.encrypt(message, skey)

        String decryptedValue = cipherService.decrypt(encryptedValue, skey)

        assert message == decryptedValue
    }

    @Test
    public void testHexDecodeAndEncode() {
        Integer size = Math.abs(random.nextInt() % 50)
        String value = generate(size * 2)

        byte[] encodeValue = HexHelper.hexStringToByteArray(value);
        String decodedValue = HexHelper.byteArrayToHex(encodeValue);

        assert  decodedValue == value
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
