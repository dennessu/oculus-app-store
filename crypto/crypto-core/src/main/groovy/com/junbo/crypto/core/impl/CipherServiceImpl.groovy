package com.junbo.crypto.core.impl

import com.junbo.crypto.core.CipherService
import groovy.transform.CompileStatic
import org.glassfish.jersey.internal.util.Base64
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter
import java.security.Key

/**
 * Created by liangfu on 5/7/14.
 */
@CompileStatic
class CipherServiceImpl implements CipherService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CipherServiceImpl)

    private String algorithm

    private String keyAlgorithm

    @Override
    String encrypt(String message, String keyHexString) {
        if (message == null) {
            LOGGER.error('message to encrypt is null.')
            throw new IllegalArgumentException('message is null')
        }
        byte[] keyValue = DatatypeConverter.parseHexBinary(keyHexString)
        Key key = new SecretKeySpec(keyValue, keyAlgorithm)
        Cipher cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key);
        String encryptedString = Base64.encodeAsString(cipher.doFinal(message.bytes));
        return encryptedString;
    }

    @Override
    String decrypt(String encryptMessage, String keyHexString) {
        if (encryptMessage == null) {
            LOGGER.error('message to decrypt is null.')
            throw new IllegalArgumentException('message is null')
        }
        byte[] encryptedBytes = DatatypeConverter.parseHexBinary(keyHexString)
        Cipher cipher = Cipher.getInstance(algorithm);
        SecretKeySpec secretKey = new SecretKeySpec(encryptedBytes, keyAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        String decryptedString = new String(cipher.doFinal(Base64.decode(encryptMessage.bytes)));
        return decryptedString;
    }

    @Required
    void setAlgorithm(String algorithm) {
        this.algorithm = algorithm
    }

    @Required
    void setKeyAlgorithm(String keyAlgorithm) {
        this.keyAlgorithm = keyAlgorithm
    }
}
