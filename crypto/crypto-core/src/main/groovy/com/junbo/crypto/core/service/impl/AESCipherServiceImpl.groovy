package com.junbo.crypto.core.service.impl

import com.junbo.crypto.core.service.CipherService
import groovy.transform.CompileStatic
import org.apache.commons.codec.binary.Hex
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.Key
import java.security.SecureRandom

/**
 * This is AES asymmetric cipher.
 * Created by liangfu on 5/7/14.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class AESCipherServiceImpl implements CipherService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AESCipherServiceImpl)
    private static final String ALGORITHM = 'AES/CBC/PKCS5Padding'
    private static final byte [] DEFAULT_IV = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    private SecureRandom rand = new SecureRandom()
    private static final Integer IV_LENGTH = 16
    private static final String IV_SEPARATOR = '#'

    @Override
    String encrypt(String message, Key key) {
        if (message == null) {
            LOGGER.error('message to encrypt is null.')
            throw new IllegalArgumentException('message is null')
        }
        if (key == null) {
            LOGGER.error('key is null.')
            throw new IllegalArgumentException('key is null')
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM)
        byte[] iv = generateIV(IV_LENGTH)
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
        return new String(Hex.encodeHex(iv)) + IV_SEPARATOR + new String(Hex.encodeHex(cipher.doFinal(message.getBytes("UTF-8"))));
    }

    @Override
    String decrypt(String encryptMessage, Key key) {
        if (encryptMessage == null) {
            LOGGER.error('message to decrypt is null.')
            throw new IllegalArgumentException('message is null')
        }
        String [] info = encryptMessage.split(IV_SEPARATOR)
        if (info.size() != 2) {
            info = new String[2]
            info[0] = Hex.encodeHex(DEFAULT_IV)
            info[1] = encryptMessage
        }
        if (key == null) {
            LOGGER.error('key is null.')
            throw new IllegalArgumentException('key is null')
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec ivspec = new IvParameterSpec(Hex.decodeHex(info[0].toCharArray()));
        cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
        return new String(cipher.doFinal(Hex.decodeHex(info[1].toCharArray())), "UTF-8");
    }

    @Override
    Key stringToKey(String keyStr) {
        byte [] bytes = keyStr.getBytes()
        return new SecretKeySpec(bytes, 0, bytes.length, 'AES')
    }

    private byte[] generateIV(int length) {
        byte[] bytes = new byte[length]
        rand.nextBytes(bytes)

        return bytes
    }
}