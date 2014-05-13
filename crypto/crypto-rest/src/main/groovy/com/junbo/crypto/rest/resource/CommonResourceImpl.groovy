package com.junbo.crypto.rest.resource

import com.junbo.common.id.UserId
import com.junbo.crypto.core.service.CipherService
import com.junbo.crypto.core.service.KeyStoreService
import com.junbo.crypto.data.repo.MasterKeyRepo
import com.junbo.crypto.data.repo.UserCryptoKeyRepo
import com.junbo.crypto.spec.error.AppErrors
import com.junbo.crypto.spec.model.MasterKey
import com.junbo.crypto.spec.model.UserCryptoKey
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

import javax.crypto.spec.SecretKeySpec
import java.security.Key
import java.security.PublicKey

/**
 * Created by liangfu on 5/13/14.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
abstract class CommonResourceImpl {

    public static final String VERSION_SEPARATOR = ':'

    protected MasterKeyRepo masterKeyRepo

    protected UserCryptoKeyRepo userCryptoKeyRepo

    protected CipherService aesCipherService

    protected CipherService rsaCipherService

    protected KeyStoreService keyStoreService

    @Required
    void setMasterKeyRepo(MasterKeyRepo masterKeyRepo) {
        this.masterKeyRepo = masterKeyRepo
    }

    @Required
    void setUserCryptoKeyRepo(UserCryptoKeyRepo userCryptoKeyRepo) {
        this.userCryptoKeyRepo = userCryptoKeyRepo
    }

    @Required
    void setAesCipherService(CipherService aesCipherService) {
        this.aesCipherService = aesCipherService
    }

    @Required
    void setRsaCipherService(CipherService rsaCipherService) {
        this.rsaCipherService = rsaCipherService
    }

    @Required
    void setKeyStoreService(KeyStoreService keyStoreService) {
        this.keyStoreService = keyStoreService
    }

    // Used to encrypt and decrypt userKey
    protected Promise<String> symmetricDecryptUserKey(String encryptedUserKey) {
        String[] userKeyInfo = (String [])encryptedUserKey.split(VERSION_SEPARATOR)
        if (userKeyInfo == null || userKeyInfo.length != 2) {
            throw new IllegalArgumentException('userKey should be separated by ' + VERSION_SEPARATOR)
        }

        Integer masterKeyVersion = Integer.parseInt(userKeyInfo[0])
        String userEncryptValue = userKeyInfo[1]
        return masterKeyRepo.getAllMaterKeys().then { List<MasterKey> masterKeys ->
            MasterKey masterKey = masterKeys.find { MasterKey temp ->
                return temp.keyVersion == masterKeyVersion
            }

            if (masterKey == null) {
                throw new IllegalArgumentException('master key with version: ' + masterKeyVersion + ' not found.')
            }

            String decryptedMasterKey = asymmetricDecryptMasterKey(masterKey.encryptValue)

            Key masterKeyLoaded = stringToKey(decryptedMasterKey)

            return Promise.pure(aesCipherService.decrypt(userEncryptValue, masterKeyLoaded))
        }
    }

    protected Promise<String> symmetricEncryptUserKey(String rawUserKey) {
        return masterKeyRepo.getAllMaterKeys().then { List<MasterKey> masterKeyList ->
            if (masterKeyList == null) {
                throw new IllegalArgumentException('No master key found.')
            }
            masterKeyList.sort(new Comparator<MasterKey>() {
                @Override
                int compare(MasterKey o1, MasterKey o2) {
                    return o2.keyVersion <=> o1.keyVersion
                }
            })

            MasterKey current = masterKeyList.get(0)

            String decryptMasterKey = asymmetricDecryptMasterKey(current.encryptValue)

            String encryptUserKey = aesCipherService.encrypt(rawUserKey, stringToKey(decryptMasterKey))

            return Promise.pure(current.keyVersion.toString() + VERSION_SEPARATOR + encryptUserKey)
        }
    }

    protected Key stringToKey(String keyStr) {
        byte [] bytes = keyStr.getBytes()
        return new SecretKeySpec(bytes, 0, bytes.length, aesCipherService.getKeyAlgorithm());
    }

    // Used to encrypt and decrypt master key
    protected String asymmetricDecryptMasterKey(String encryptMasterKey) {
        String[] messageInfo = (String [])encryptMasterKey.split(VERSION_SEPARATOR)

        if (messageInfo == null || messageInfo.length != 2) {
            throw new IllegalArgumentException('encryptMessage should be separated by ' + VERSION_SEPARATOR)
        }

        Integer masterKeyVersion = Integer.parseInt(messageInfo[0])
        String encryptMessage = messageInfo[1]

        Key privateKey = keyStoreService.getPrivateKeyByVersion(masterKeyVersion)

        if (privateKey == null) {
            throw new IllegalStateException('Can\'t find private master key of version ' + masterKeyVersion)
        }

        String decryptedValue = rsaCipherService.decrypt(encryptMessage, privateKey)

        if (decryptedValue == null) {
            throw AppErrors.INSTANCE.internalError('Decrypt master key error.').exception()
        }

        return decryptedValue
    }

    protected String asymmetricEncryptMasterKey(String rawMaterKey) {
        Map<Integer, PublicKey> publicKeyMap = keyStoreService.getPublicKeys()
        Integer maxVersion = publicKeyMap.keySet().max()

        PublicKey publicKey = publicKeyMap[maxVersion]

        return maxVersion.toString() + VERSION_SEPARATOR + rsaCipherService.encrypt(rawMaterKey, publicKey)
    }

    // Used to encrypt and decrypt user message
    protected Promise<String> symmetricDecryptUserMessage(UserId userId, String message) {
        String[] messageInfo = (String [])message.split(VERSION_SEPARATOR)

        if (messageInfo == null || messageInfo.length != 2) {
            throw new IllegalArgumentException('encryptMessage should be separated by ' + VERSION_SEPARATOR)
        }

        Integer userKeyVersion = Integer.parseInt(messageInfo[0])
        String encryptMessage = messageInfo[1]

        return userCryptoKeyRepo.searchAllUserCryptoKeys(userId).then { List<UserCryptoKey> userCryptoKeyList ->
            UserCryptoKey key = userCryptoKeyList.find { UserCryptoKey userCryptoKey ->
                return userCryptoKey.keyVersion == userKeyVersion
            }

            if (key == null) {
                throw new IllegalArgumentException('user key with version: ' + userKeyVersion + ' not found.')
            }

            return symmetricDecryptUserKey(key.encryptValue).then { String userKey ->
                Key userKeyLoaded = stringToKey(userKey)
                String value = aesCipherService.decrypt(encryptMessage, userKeyLoaded)

                return Promise.pure(value)
            }
        }
    }

    protected Promise<String> symmetricEncryptUserMessage(UserId userId, String message) {
        return getCurrentUserCryptoKey(userId).then { Integer userKeyVersion ->
            return userCryptoKeyRepo.searchAllUserCryptoKeys(userId).then { List<UserCryptoKey> userCryptoKeyList ->
                UserCryptoKey key = userCryptoKeyList.find { UserCryptoKey userCryptoKey ->
                    return userCryptoKey.keyVersion == userKeyVersion
                }

                if (key == null) {
                    throw new IllegalArgumentException('user key with version: ' + userKeyVersion + ' not found.')
                }

                return symmetricDecryptUserKey(key.encryptValue).then { String userKey ->
                    Key userKeyLoaded = stringToKey(userKey)
                    String value = aesCipherService.encrypt(message, userKeyLoaded)

                    return Promise.pure(userKeyVersion.toString() + VERSION_SEPARATOR + value)
                }
            }
        }
    }

    protected Promise<Integer> getCurrentUserCryptoKey(UserId userId) {
        return userCryptoKeyRepo.searchAllUserCryptoKeys(userId).then { List<UserCryptoKey> userCryptoKeyList ->
            if (CollectionUtils.isEmpty(userCryptoKeyList)) {
                return Promise.pure(0)
            }
            UserCryptoKey key = userCryptoKeyList.max(new Comparator<UserCryptoKey>() {
                @Override
                int compare(UserCryptoKey o1, UserCryptoKey o2) {
                    return o1.keyVersion <=> o2.keyVersion
                }
            })

            return Promise.pure(key.keyVersion)
        }
    }

    protected Promise<Integer> getCurrentMasterKey() {
        return masterKeyRepo.getAllMaterKeys().then { List<MasterKey> masterKeyList ->

            if (CollectionUtils.isEmpty(masterKeyList)) {
                return Promise.pure(0)
            }

            MasterKey key = masterKeyList.max(new Comparator<MasterKey>() {
                @Override
                int compare(MasterKey o1, MasterKey o2) {
                    return o1.keyVersion <=> o2.keyVersion
                }
            })

            return Promise.pure(key.keyVersion)
        }
    }
}
