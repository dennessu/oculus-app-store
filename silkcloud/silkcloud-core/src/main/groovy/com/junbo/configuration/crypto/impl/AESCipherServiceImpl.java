/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.crypto.impl;

import com.junbo.configuration.crypto.CipherService;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;

/**
 * Created by liangfu on 5/26/14.
 */
public class AESCipherServiceImpl implements CipherService {
    private SecureRandom rand = new SecureRandom();
    private static final Integer IV_LENGTH = 16;
    private static final String IV_SEPARATOR = "#";
    private Key key;

    public AESCipherServiceImpl(String keyStr) {
        this.key = stringToKey(keyStr);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AESCipherServiceImpl.class);
    private static final byte[] DEFAULT_IV = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    @Override
    public String encrypt(String message) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }

        if (key == null) {
            throw new IllegalStateException("key is null");
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = generateIV(IV_LENGTH);
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
            return new String(Hex.encodeHex(iv)) + IV_SEPARATOR + new String(Hex.encodeHex(cipher.doFinal(message.getBytes("UTF-8"))));
        } catch (Exception e) {
            throw new RuntimeException("Encrypt exception: ", e);
        }
    }

    @Override
    public String decrypt(String encryptedMessage) {
        if (encryptedMessage == null) {
            throw new IllegalArgumentException("encryptedMessage is null");
        }

        if (key == null) {
            throw new IllegalStateException("key is null");
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            String [] info = encryptedMessage.split(IV_SEPARATOR);
            if (info.length != 2) {
                info = new String[2];
                info[0] = new String(Hex.encodeHex(DEFAULT_IV));
                info[1] = encryptedMessage;
            }
            IvParameterSpec ivspec = new IvParameterSpec(Hex.decodeHex(info[0].toCharArray()));
            cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
            return new String(cipher.doFinal(Hex.decodeHex(info[1].toCharArray())), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Decrypt exception : ", e);
        }
    }

    public Key stringToKey(String keyStr) {
        if (StringUtils.isEmpty(keyStr)) {
            LOGGER.warn("keyStr is empty.");
            return null;
        }
        try {
            byte [] bytes = Hex.decodeHex(keyStr.toCharArray());
            return new SecretKeySpec(bytes, 0, bytes.length, "AES");
        } catch (DecoderException decodeException) {
            throw new RuntimeException("Key String to AES key: ", decodeException);
        }
    }

    private byte[] generateIV(int length) {
        byte[] bytes = new byte[length];
        rand.nextBytes(bytes);

        return bytes;
    }
}
