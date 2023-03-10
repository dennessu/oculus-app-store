/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment.apihelper.clientencryption;

/**
 * Created by weiyu_000 on 7/30/14.
 */

import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by weiyu_000 on 7/30/14.
 */

public class Encrypter {

    private static final String PREFIX = "adyenan";
    private static final String VERSION = "0_1_1";
    private static final String SEPARATOR = "$";

    private PublicKey pubKey;
    private Cipher aesCipher;
    private Cipher rsaCipher;
    private SecureRandom srandom;

    public Encrypter(String publicKeyString) throws EncrypterException {

        srandom = new SecureRandom();
        String[] keyComponents = publicKeyString.split("\\|");

        // The bytes can be converted back to a public key object
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return;
        }

        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
                new BigInteger(keyComponents[1].toLowerCase(), 16),
                new BigInteger(keyComponents[0].toLowerCase(), 16));

        try {
            pubKey = keyFactory.generatePublic(pubKeySpec);
        } catch (InvalidKeySpecException e) {
            throw new EncrypterException("Problem reading public key: " + publicKeyString, e);
        }

        try {
            aesCipher = Cipher.getInstance("AES/CCM/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            throw new EncrypterException("Problem instantiation AES Cipher Algorithm", e);
        } catch (NoSuchPaddingException e) {
            throw new EncrypterException("Problem instantiation AES Cipher Padding", e);
        }

        try {
            rsaCipher = Cipher.getInstance("RSA/None/PKCS1Padding");
            rsaCipher.init(Cipher.ENCRYPT_MODE, pubKey);

        } catch (NoSuchAlgorithmException e) {
            throw new EncrypterException("Problem instantiation RSA Cipher Algorithm", e);
        } catch (NoSuchPaddingException e) {
            throw new EncrypterException("Problem instantiation RSA Cipher Padding", e);
        } catch (InvalidKeyException e) {
            throw new EncrypterException("Invalid public key: " + publicKeyString, e);
        }

    }


    public String encrypt(String plainText) throws EncrypterException {
        SecretKey aesKey = generateAESKey(256);

        byte[] iv = generateIV(12);

        byte[] encrypted;
        try {
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));
            // getBytes is UTF-8 on Android by default
            encrypted = aesCipher.doFinal(plainText.getBytes());
        } catch (IllegalBlockSizeException e) {
            throw new EncrypterException("Incorrect AES Block Size", e);
        } catch (BadPaddingException e) {
            throw new EncrypterException("Incorrect AES Padding", e);
        } catch (InvalidKeyException e) {
            throw new EncrypterException("Invalid AES Key", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new EncrypterException("Invalid AES Parameters", e);
        }

        byte[] result = new byte[iv.length + encrypted.length];
        // copy IV to result
        System.arraycopy(iv, 0, result, 0, iv.length);
        // copy encrypted to result
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);

        byte[] encryptedAESKey;
        try {
            encryptedAESKey = rsaCipher.doFinal(aesKey.getEncoded());
            return PREFIX + VERSION + SEPARATOR + Base64.encodeBase64String(encryptedAESKey) +
                    SEPARATOR + Base64.encodeBase64String(result);

        } catch (IllegalBlockSizeException e) {
            throw new EncrypterException("Incorrect RSA Block Size", e);
        } catch (BadPaddingException e) {
            throw new EncrypterException("Incorrect RSA Padding", e);
        }
    }


    private SecretKey generateAESKey(int keySize) throws EncrypterException {
        KeyGenerator kgen;
        try {
            kgen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            throw new EncrypterException("Unable to get AES algorithm", e);
        }
        kgen.init(keySize);
        return kgen.generateKey();
    }

    private synchronized byte[] generateIV(int ivSize) {

        byte[] iv = new byte[ivSize];//generate random IV AES is always 16bytes, but in CCM mode this represents the NONCE
        srandom.nextBytes(iv);
        return iv;
    }
}
