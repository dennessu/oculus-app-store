package com.junbo.crypto.core.service.impl
import com.junbo.crypto.core.service.CipherService
import groovy.transform.CompileStatic
import org.apache.commons.codec.binary.Base64
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.Key
/**
 * This is RSA asymmetric cipher.
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class RSACipherServiceImpl implements CipherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSACipherServiceImpl)

    private static final String ALGORITHM = 'RSA'

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
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return new String(Base64.encodeBase64(cipher.doFinal(message.getBytes("UTF-8"))));
    }

    @Override
    String decrypt(String encryptMessage, Key key) {
        if (encryptMessage == null) {
            LOGGER.error('message to decrypt is null.')
            throw new IllegalArgumentException('message is null')
        }
        if (key == null) {
            LOGGER.error('key is null.')
            throw new IllegalArgumentException('key is null')
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decodeBase64(encryptMessage)), "UTF-8");
    }

    @Override
    Key stringToKey(String keyStr) {
        byte [] bytes = keyStr.getBytes()
        return new SecretKeySpec(bytes, 0, bytes.length, ALGORITHM)
    }
}
