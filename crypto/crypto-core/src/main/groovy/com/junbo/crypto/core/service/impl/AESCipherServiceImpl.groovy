package com.junbo.crypto.core.service.impl

import com.junbo.crypto.core.service.CipherService
import com.junbo.crypto.spec.error.AppErrors
import groovy.transform.CompileStatic
import org.apache.commons.codec.binary.Hex
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.Key
import java.security.NoSuchAlgorithmException

/**
 * This is AES asymmetric cipher.
 * Created by liangfu on 5/7/14.
 */
@CompileStatic
@SuppressWarnings('GetterMethodCouldBeProperty')
class AESCipherServiceImpl implements CipherService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AESCipherServiceImpl)

    private static final byte [] IV = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

    private static final String ALGORITHM = 'AES/CBC/PKCS5Padding'

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

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM)
            IvParameterSpec ivspec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
            return new String(Hex.encodeHex(cipher.doFinal(message.getBytes("UTF-8"))));
        } catch (NoSuchAlgorithmException noAlgorithmEx) {
            throw AppErrors.INSTANCE.noSuchAlgorithmException("Encrypt: " + noAlgorithmEx.message).exception()
        } catch (NoSuchPaddingException noPaddingEx) {
            throw AppErrors.INSTANCE.noSuchPaddingException("Encrypt: " + noPaddingEx.message).exception()
        } catch (InvalidKeyException invalidKeyEx) {
            throw AppErrors.INSTANCE.invalidKeyException("Encrypt: " + invalidKeyEx.message).exception()
        } catch (InvalidAlgorithmParameterException invalidAlgorithmEx) {
            throw AppErrors.INSTANCE.invalidAlgorithmParameterException(
                    "Encrypt: " + invalidAlgorithmEx.message).exception()
        } catch (IllegalBlockSizeException illegalBlockSizeEx) {
            throw AppErrors.INSTANCE.illegalBlockSizeException("Encrypt: " + illegalBlockSizeEx.message).exception()
        } catch (BadPaddingException badPaddingEx) {
            throw AppErrors.INSTANCE.badPaddingException("Encrypt: " + badPaddingEx.message).exception()
        } catch (Exception e) {
            throw AppErrors.INSTANCE.internalError("Encrypt: "  + e.message).exception()
        }
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
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            IvParameterSpec ivspec = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, key, ivspec);
            return new String(cipher.doFinal(Hex.decodeHex(encryptMessage.toCharArray())), "UTF-8");
        } catch (NoSuchAlgorithmException noAlgorithmEx) {
            throw AppErrors.INSTANCE.noSuchAlgorithmException("Encrypt: " + noAlgorithmEx.message).exception()
        } catch (NoSuchPaddingException noPaddingEx) {
            throw AppErrors.INSTANCE.noSuchPaddingException("Encrypt: " + noPaddingEx.message).exception()
        } catch (InvalidKeyException invalidKeyEx) {
            throw AppErrors.INSTANCE.invalidKeyException("Encrypt: " + invalidKeyEx.message).exception()
        } catch (InvalidAlgorithmParameterException invalidAlgorithmEx) {
            throw AppErrors.INSTANCE.invalidAlgorithmParameterException(
                    "Encrypt: " + invalidAlgorithmEx.message).exception()
        } catch (IllegalBlockSizeException illegalBlockSizeEx) {
            throw AppErrors.INSTANCE.illegalBlockSizeException("Encrypt: " + illegalBlockSizeEx.message).exception()
        } catch (BadPaddingException badPaddingEx) {
            throw AppErrors.INSTANCE.badPaddingException("Encrypt: " + badPaddingEx.message).exception()
        } catch (Exception e) {
            throw AppErrors.INSTANCE.internalError("Encrypt: "  + e.message).exception()
        }
    }

    @Override
    Key stringToKey(String keyStr) {
        byte [] bytes = keyStr.getBytes()
        return new SecretKeySpec(bytes, 0, bytes.length, 'AES')
    }
}