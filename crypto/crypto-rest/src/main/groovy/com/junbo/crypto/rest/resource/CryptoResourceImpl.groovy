/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.rest.resource

import com.junbo.common.id.UserId
import com.junbo.crypto.core.validator.CryptoMessageValidator
import com.junbo.crypto.spec.model.CryptoMessage
import com.junbo.crypto.spec.resource.CryptoResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 5/6/14.
 */
@CompileStatic
@Transactional
class CryptoResourceImpl extends CommonResourceImpl implements CryptoResource {

    private CryptoMessageValidator validator

    private Boolean enableEncrypt

    @Override
    Promise<CryptoMessage> encrypt(UserId userId, CryptoMessage rawMessage) {
        if (!enableEncrypt) {
            return Promise.pure(rawMessage)
        }
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

    @Override
    Promise<CryptoMessage> decrypt(UserId userId, CryptoMessage encryptMessage) {
        if (!enableEncrypt) {
            return Promise.pure(encryptMessage)
        }
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

    @Required
    void setValidator(CryptoMessageValidator validator) {
        this.validator = validator
    }

    @Required
    void setEnableEncrypt(Boolean enableEncrypt) {
        this.enableEncrypt = enableEncrypt
    }
}
