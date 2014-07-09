package com.junbo.crypto.rest.resource
import com.junbo.authorization.AuthorizeContext
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.crypto.core.service.CipherService
import com.junbo.crypto.core.service.KeyStoreService
import com.junbo.crypto.data.repo.MasterKeyRepo
import com.junbo.crypto.data.repo.UserCryptoKeyRepo
import com.junbo.crypto.spec.model.MasterKey
import com.junbo.crypto.spec.model.UserCryptoKey
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

import java.security.Key
import java.security.PublicKey
/**
 * Created by liangfu on 5/13/14.
 */
@CompileStatic
@SuppressWarnings('UnnecessaryGetter')
abstract class CommonResourceImpl {
    protected static final String CRYPTO_SERVICE_SCOPE = 'crypto.service'

    protected String versionSeparator

    protected MasterKeyRepo masterKeyRepo

    protected UserCryptoKeyRepo userCryptoKeyRepo

    protected CipherService aesCipherService

    protected CipherService rsaCipherService

    protected KeyStoreService keyStoreService

    protected Boolean enableUserKeyEncrypt

    @Required
    void setVersionSeparator(String versionSeparator) {
        this.versionSeparator = versionSeparator
    }

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

    @Required
    void setEnableUserKeyEncrypt(Boolean enableUserKeyEncrypt) {
        this.enableUserKeyEncrypt = enableUserKeyEncrypt
    }

// Used to encrypt and decrypt userKey
    protected Promise<String> symmetricDecryptUserKey(String encryptedUserKey) {
        String[] userKeyInfo = (String [])encryptedUserKey.split(versionSeparator)
        if (userKeyInfo == null || userKeyInfo.length != 2) {
            throw new IllegalArgumentException('userKey should be separated by ' + versionSeparator)
        }

        Integer masterKeyVersion = Integer.parseInt(userKeyInfo[0])
        String userEncryptValue = userKeyInfo[1]
        return masterKeyRepo.getMasterKeyByVersion(masterKeyVersion).then { MasterKey masterKey ->
            if (masterKey == null) {
                throw new IllegalArgumentException('master key with version: ' + masterKeyVersion + ' not found.')
            }

            String decryptedMasterKey = asymmetricDecryptMasterKey(masterKey.encryptValue)

            Key masterKeyLoaded = aesCipherService.stringToKey(decryptedMasterKey)

            return Promise.pure(aesCipherService.decrypt(userEncryptValue, masterKeyLoaded))
        }
    }

    protected Promise<String> symmetricEncryptUserKey(String rawUserKey) {
        return getCurrentMasterKey().then { MasterKey current ->

            String decryptMasterKey = asymmetricDecryptMasterKey(current.encryptValue)

            String encryptUserKey = aesCipherService.encrypt(rawUserKey, aesCipherService.stringToKey(decryptMasterKey))

            return Promise.pure(current.keyVersion.toString() + versionSeparator + encryptUserKey)
        }
    }

    // Used to encrypt and decrypt master key
    protected String asymmetricDecryptMasterKey(String encryptMasterKey) {
        String[] messageInfo = (String [])encryptMasterKey.split(versionSeparator)

        if (messageInfo == null || messageInfo.length != 2) {
            throw new IllegalArgumentException('encryptMessage should be separated by ' + versionSeparator)
        }

        Integer masterKeyVersion = Integer.parseInt(messageInfo[0])
        String encryptMessage = messageInfo[1]

        Key privateKey = keyStoreService.getPrivateKeyByVersion(masterKeyVersion)

        if (privateKey == null) {
            throw new IllegalStateException('Can\'t find private master key of version ' + masterKeyVersion)
        }

        String decryptedValue = rsaCipherService.decrypt(encryptMessage, privateKey)

        if (decryptedValue == null) {
            throw new RuntimeException('Decrypt master key failed.')
        }

        return decryptedValue
    }

    protected String asymmetricEncryptMasterKey(String rawData) {
        Map<Integer, PublicKey> publicKeyMap = keyStoreService.getPublicKeys()
        Integer maxVersion = publicKeyMap.keySet().max()

        PublicKey publicKey = publicKeyMap[maxVersion]

        return maxVersion.toString() + versionSeparator + rsaCipherService.encrypt(rawData, publicKey)
    }

    // Used to encrypt and decrypt user message by userKey
    protected Promise<String> symmetricDecryptUserMessageByUserKey(UserId userId, String message) {
        String[] messageInfo = (String [])message.split(versionSeparator)

        if (messageInfo == null || messageInfo.length != 2) {
            throw new IllegalArgumentException('encryptMessage should be separated by ' + versionSeparator)
        }

        Integer userKeyVersion = Integer.parseInt(messageInfo[0])
        String encryptMessage = messageInfo[1]

        return userCryptoKeyRepo.getUserCryptoKeyByVersion(userId, userKeyVersion).then { UserCryptoKey key ->
            if (key == null) {
                throw new IllegalArgumentException('user key with version: ' + userKeyVersion + ' not found.')
            }

            return symmetricDecryptUserKey(key.encryptValue).then { String userKey ->
                Key userKeyLoaded = aesCipherService.stringToKey(userKey)
                String value = aesCipherService.decrypt(encryptMessage, userKeyLoaded)

                return Promise.pure(value)
            }
        }
    }

    protected Promise<String> symmetricEncryptUserMessageByUserKey(UserId userId, String message) {
        return getCurrentUserCryptoKey(userId).then { Integer userKeyVersion ->
            return userCryptoKeyRepo.getUserCryptoKeyByVersion(userId, userKeyVersion).then { UserCryptoKey key ->
                if (key == null) {
                    throw new IllegalArgumentException('user key with version: ' + userKeyVersion + ' not found.')
                }

                return symmetricDecryptUserKey(key.encryptValue).then { String userKey ->
                    Key userKeyLoaded = aesCipherService.stringToKey(userKey)
                    String value = aesCipherService.encrypt(message, userKeyLoaded)

                    return Promise.pure(userKeyVersion.toString() + versionSeparator + value)
                }
            }
        }
    }

    // Used to get raw masterKey
    protected Promise<MasterKey> getCurrentDecryptedMasterKey() {
        return getCurrentMasterKey().then { MasterKey masterKey ->
            if (masterKey == null) {
                throw new IllegalArgumentException('master key doesn\'t exist in current system')
            }

            String decryptedMasterKey = asymmetricDecryptMasterKey(masterKey.encryptValue)
            masterKey.value = decryptedMasterKey
            return Promise.pure(masterKey)
        }
    }

    // Used to get raw masterKey by version
    protected Promise<MasterKey> getCurrentDecryptedMasterKeyByVersion(Integer keyVersion) {
        return masterKeyRepo.getMasterKeyByVersion(keyVersion).then { MasterKey masterKey ->
            if (masterKey == null) {
                throw new IllegalArgumentException('master key with version: ' + keyVersion + ' not found.')
            }

            String decryptedMasterKey = asymmetricDecryptMasterKey(masterKey.encryptValue)
            masterKey.value = decryptedMasterKey
            return Promise.pure(masterKey)
        }
    }

    protected Promise<Integer> getCurrentUserCryptoKey(UserId userId) {
        return userCryptoKeyRepo.getAllUserCryptoKeys(userId).then { List<UserCryptoKey> userCryptoKeyList ->
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

    protected Promise<MasterKey> getCurrentMasterKey() {
        return masterKeyRepo.getAllMaterKeys().then { List<MasterKey> masterKeyList ->

            if (CollectionUtils.isEmpty(masterKeyList)) {
                return Promise.pure(null)
            }

            MasterKey key = masterKeyList.max(new Comparator<MasterKey>() {
                @Override
                int compare(MasterKey o1, MasterKey o2) {
                    return o1.keyVersion <=> o2.keyVersion
                }
            })

            return Promise.pure(key)
        }
    }

    protected static Promise<Void> authorize() {
        if (!AuthorizeContext.hasScopes(CRYPTO_SERVICE_SCOPE)) {
            throw AppCommonErrors.INSTANCE.forbidden().exception()
        }

        return Promise.pure(null)
    }
}
