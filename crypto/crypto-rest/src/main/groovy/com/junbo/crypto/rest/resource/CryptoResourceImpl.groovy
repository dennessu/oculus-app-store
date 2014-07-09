/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.rest.resource

import com.junbo.common.id.UserId
import com.junbo.crypto.core.validator.CryptoMessageValidator
import com.junbo.crypto.spec.model.CryptoMessage
import com.junbo.crypto.spec.model.MasterKey
import com.junbo.crypto.spec.resource.CryptoResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.transaction.annotation.Transactional

import java.security.Key

/**
 * Created by liangfu on 5/6/14.
 */
@CompileStatic
@Transactional
class CryptoResourceImpl extends CommonResourceImpl implements CryptoResource {

    private static HashMap<Integer, Key> cachedEncryptMasterKey = new HashMap<>()

    private static HashMap<Integer, Key> cachedDecryptMasterKey = new HashMap<>()

    private CryptoMessageValidator validator

    @Override
    Promise<CryptoMessage> encrypt(UserId userId, CryptoMessage rawMessage) {
        return authorize().then {
            return validator.validateEncrypt(userId, rawMessage).then {
                if (enableUserKeyEncrypt) {
                    return symmetricEncryptUserMessageByUserKey(userId, rawMessage.value).then { String encryptMessage ->
                        CryptoMessage result = new CryptoMessage()
                        result.value = encryptMessage
                        return Promise.pure(result)
                    }
                } else {
                    return symmetricEncryptUserMessageByMasterKey(rawMessage.value).then { String encryptMessage ->
                        CryptoMessage result = new CryptoMessage()
                        result.value = encryptMessage
                        return Promise.pure(result)
                    }
                }
            }
        }
    }

    @Override
    Promise<CryptoMessage> decrypt(UserId userId, CryptoMessage encryptMessage) {
        return authorize().then {
            return validator.validateDecrypt(userId, encryptMessage).then {
                if (enableUserKeyEncrypt) {
                    return symmetricDecryptUserMessageByUserKey(userId, encryptMessage.value).then { String rawMessage ->
                        CryptoMessage result = new CryptoMessage()
                        result.value = rawMessage

                        return Promise.pure(result)
                    }
                } else {
                    return symmetricDecryptUserMessageByMasterKey(encryptMessage.value).then { String rawMessage ->
                        CryptoMessage result = new CryptoMessage()
                        result.value = rawMessage

                        return Promise.pure(result)
                    }
                }
            }
        }
    }

    @Override
    Promise<CryptoMessage> encrypt(CryptoMessage rawMessage) {
        return authorize().then {
            return validator.validateEncrypt(null, rawMessage).then {
                return symmetricEncryptUserMessageByMasterKey(rawMessage.value).then { String encryptMessage ->
                    CryptoMessage result = new CryptoMessage()
                    result.value = encryptMessage
                    return Promise.pure(result)
                }
            }
        }
    }

    @Override
    Promise<CryptoMessage> decrypt(CryptoMessage encryptMessage) {
        return authorize().then {
            return validator.validateDecrypt(null, encryptMessage).then {
                return symmetricDecryptUserMessageByMasterKey(encryptMessage.value).then { String rawMessage ->
                    CryptoMessage result = new CryptoMessage()
                    result.value = rawMessage

                    return Promise.pure(result)
                }
            }
        }
    }

    @Required
    void setValidator(CryptoMessageValidator validator) {
        this.validator = validator
    }

    @Required
    void setEnableEncrypt(Boolean enableEncrypt) {
        this.enableEncrypt = enableEncrypt
    }

    // Used to encrypt and decrypt user message by masterKey
    protected Promise<String> symmetricDecryptUserMessageByMasterKey(String encryptedMessage) {
        String[] messageInfo = (String [])encryptedMessage.split(versionSeparator)
        if (messageInfo == null || messageInfo.length != 2) {
            throw new IllegalArgumentException('message should be separated by ' + versionSeparator)
        }

        Integer masterKeyVersion = Integer.parseInt(messageInfo[0])

        String messageEncryptValue = messageInfo[1]
        return getRawMasterKeyByVersion(masterKeyVersion).then { Key masterKeyLoaded ->
            return Promise.pure(aesCipherService.decrypt(messageEncryptValue, masterKeyLoaded))
        }
    }

    private Promise<Key> getRawMasterKeyByVersion(Integer version) {
        if (cachedDecryptMasterKey.get(version) == null) {
            return getCurrentDecryptedMasterKeyByVersion(version).then { MasterKey masterKey ->
                Key masterKeyLoaded = aesCipherService.stringToKey(masterKey.value)
                cachedDecryptMasterKey.put(masterKey.keyVersion, masterKeyLoaded)
                return Promise.pure(masterKeyLoaded)
            }
        } else {
            return Promise.pure(cachedDecryptMasterKey.get(version))
        }
    }

    protected Promise<String> symmetricEncryptUserMessageByMasterKey(String rawMessage) {
        return getCurrentRawMasterKey().then {
            return Promise.pure(((Integer)(cachedEncryptMasterKey.keySet().asList().get(0))) +
                    versionSeparator + aesCipherService.encrypt(rawMessage, cachedEncryptMasterKey.values().asList().get(0)))
        }
    }

    private Promise<Void> getCurrentRawMasterKey() {
        if (cachedEncryptMasterKey.isEmpty()) {
            return getCurrentDecryptedMasterKey().then { MasterKey masterKey ->
                Key masterKeyLoaded = aesCipherService.stringToKey(masterKey.value)
                cachedEncryptMasterKey.put(masterKey.keyVersion, masterKeyLoaded)
                return Promise.pure(null)
            }
        } else {
            return Promise.pure(null)
        }
    }
}
