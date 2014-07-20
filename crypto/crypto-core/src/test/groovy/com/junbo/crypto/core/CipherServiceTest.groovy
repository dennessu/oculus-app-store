/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.core

import com.junbo.crypto.core.service.CipherService
import com.junbo.crypto.core.service.KeyStoreService
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.testng.annotations.Test

import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
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
    @Qualifier('aesCipherService')
    private CipherService aesCipherService

    @Autowired
    @Qualifier('rsaCipherService')
    private CipherService rsaCipherService

    @Autowired
    private KeyStoreService keyStoreService

    @Test(enabled = false)
    public void testAESEncryptAndDecrypt() {
        // Get the KeyGenerator
        KeyGenerator kgen = KeyGenerator.getInstance("AES");

        // Generate the secret key specs.
        SecretKey skey = kgen.generateKey();

        String message = 'Hello World!'
        // Get the KeyGenerator

        String encryptedValue = aesCipherService.encrypt(message, skey)

        String decryptedValue = aesCipherService.decrypt(encryptedValue, skey)

        assert message == decryptedValue
    }

    @Test(enabled = false)
    public void testRSAEncryptAndDecrypt() {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(512); // 512 is the keysize.
        KeyPair kp = kpg.generateKeyPair();
        PublicKey pubk = kp.getPublic();
        PrivateKey prvk = kp.getPrivate();

        String message = 'Hello World!'

        String encryptValue = rsaCipherService.encrypt(message, pubk)

        String decryptValue = rsaCipherService.decrypt(encryptValue, prvk)

        assert message == decryptValue
    }

    @Test(enabled = false)
    public void testHexDecodeAndEncode() {
        Integer size = Math.abs(random.nextInt() % 50)
        String value = generate(size * 2)

        byte[] encodeValue = Base64.decodeBase64(value)
        String decodedValue = new String(Base64.encodeBase64(encodeValue))

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
