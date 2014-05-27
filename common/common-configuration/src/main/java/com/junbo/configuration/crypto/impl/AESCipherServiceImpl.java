/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.crypto.impl;

import com.junbo.configuration.crypto.CipherService;
import com.junbo.configuration.crypto.HexHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Created by liangfu on 5/26/14.
 */
public class AESCipherServiceImpl implements CipherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AESCipherServiceImpl.class);
    private static final byte[] IV = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    @Override
    public String encrypt(String message, Key key) {
        if (message == null) {
            LOGGER.error("message to encrypt is null.");
            throw new RuntimeException("message is null");
        }

        if (key == null) {
            LOGGER.error("key is null.");
            throw new RuntimeException("key is null");
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            IvParameterSpec ivspec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
            return HexHelper.byteArrayToHex(cipher.doFinal(message.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException noAlgorithmEx) {
            LOGGER.error("Encrypt: NoSuchAlgorithmException:    " + noAlgorithmEx.getMessage());
            throw new RuntimeException("Encrypt: NoSuchAlgorithmException: " + noAlgorithmEx.getMessage());
        } catch (NoSuchPaddingException noPaddingEx) {
            LOGGER.error("Encrypt: NoSuchPaddingException:  " + noPaddingEx.getMessage());
            throw new RuntimeException("Encrypt: NoSuchPaddingException:   " + noPaddingEx.getMessage());
        } catch (InvalidKeyException invalidKeyEx) {
            LOGGER.error("Encrypt: InvalidKeyException: " + invalidKeyEx.getMessage());
            throw new RuntimeException("Encrypt: InvalidKeyException:  " + invalidKeyEx.getMessage());
        } catch (InvalidAlgorithmParameterException invalidAlgorithmEx) {
            LOGGER.error("Encrypt: InvalidAlgorithmParameterException:  " + invalidAlgorithmEx.getMessage());
            throw new RuntimeException("Encrypt: InvalidAlgorithmParameterException:   " + invalidAlgorithmEx.getMessage());
        } catch (IllegalBlockSizeException illegalBlockSizeEx) {
            LOGGER.error("Encrypt: IllegalBlockSizeException:   " + illegalBlockSizeEx.getMessage());
            throw new RuntimeException("Encrypt: IllegalBlockSizeException:    " + illegalBlockSizeEx.getMessage());
        } catch (BadPaddingException badPaddingEx) {
            LOGGER.error("Encrypt: BadPaddingException: " + badPaddingEx.getMessage());
            throw new RuntimeException("Encrypt: BadPaddingException:  " + badPaddingEx.getMessage());
        } catch (Exception e) {
            LOGGER.error("Encrypt: " + e.getMessage());
            throw new RuntimeException("Encrypt: " + e.getMessage());
        }

    }

    @Override
    public String decrypt(String encryptMessage, Key key) {
        if (encryptMessage == null) {
            LOGGER.error("message to decrypt is null.");
            throw new RuntimeException("message is null");
        }

        if (key == null) {
            LOGGER.error("key is null.");
            throw new RuntimeException("key is null");
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            IvParameterSpec ivspec = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
            return new String(cipher.doFinal(HexHelper.hexStringToByteArray(encryptMessage)), "UTF-8");
        } catch (NoSuchAlgorithmException noAlgorithmEx) {
            LOGGER.error("Encrypt:  NoSuchAlgorithmException: " + noAlgorithmEx.getMessage());
            throw new RuntimeException("Encrypt:  NoSuchAlgorithmException: " + noAlgorithmEx.getMessage());
        } catch (NoSuchPaddingException noPaddingEx) {
            LOGGER.error("Encrypt: NoSuchPaddingException:  " + noPaddingEx.getMessage());
            throw new RuntimeException("Encrypt: NoSuchPaddingException:  " + noPaddingEx.getMessage());
        } catch (InvalidKeyException invalidKeyEx) {
            LOGGER.error("Encrypt: InvalidKeyException: " + invalidKeyEx.getMessage());
            throw new RuntimeException("Encrypt: InvalidKeyException: " + invalidKeyEx.getMessage());
        } catch (InvalidAlgorithmParameterException invalidAlgorithmEx) {
            LOGGER.error("Encrypt: InvalidAlgorithmParameterException:  " + invalidAlgorithmEx.getMessage());
            throw new RuntimeException("Encrypt: InvalidAlgorithmParameterException:   " + invalidAlgorithmEx.getMessage());
        } catch (IllegalBlockSizeException illegalBlockSizeEx) {
            LOGGER.error("Encrypt: IllegalBlockSizeException:   " + illegalBlockSizeEx.getMessage());
            throw new RuntimeException("Encrypt: IllegalBlockSizeException:    " + illegalBlockSizeEx.getMessage());
        } catch (BadPaddingException badPaddingEx) {
            LOGGER.error("Encrypt: BadPaddingException: " + badPaddingEx.getMessage());
            throw new RuntimeException("Encrypt: BadPaddingException:  " + badPaddingEx.getMessage());
        } catch (Exception e) {
            LOGGER.error("Encrypt: " + e.getMessage());
            throw new RuntimeException("Encrypt: " + e.getMessage());
        }

    }

    @Override
    public String getKeyAlgorithm() {
        return "AES";
    }
}
