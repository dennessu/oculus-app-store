/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.configuration.crypto.CipherService
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.EmailVerifyCodeRepository
import com.junbo.oauth.spec.model.EmailVerifyCode
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * CloudantEmailVerifyCodeRepositoryImpl.
 */
@CompileStatic
class CloudantEmailVerifyCodeRepositoryImpl extends CloudantClient<EmailVerifyCode>
        implements EmailVerifyCodeRepository {
    private TokenGenerator tokenGenerator

    private CipherService cipherService

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Required
    void setCipherService(CipherService cipherService) {
        this.cipherService = cipherService
    }

    @Override
    EmailVerifyCode getAndRemove(String code) {
        if (StringUtils.isEmpty(code)) {
            return null
        }

        String hashed = tokenGenerator.hashKey(code)
        EmailVerifyCode entity = cloudantGetSync(hashed)
        cloudantDeleteSync(hashed)
        if (entity != null) {
            entity.code = code
        }
        return entity
    }

    @Override
    void save(EmailVerifyCode emailVerifyCode) {
        if (emailVerifyCode.code == null) {
            emailVerifyCode.code = tokenGenerator.generateEmailVerifyCode()
            emailVerifyCode.encryptedCode = cipherService.encrypt(emailVerifyCode.code)
            emailVerifyCode.hashedCode = tokenGenerator.hashKey(emailVerifyCode.code)
        }
        cloudantPostSync(emailVerifyCode)
    }

    @Override
    void removeByUserIdEmail(Long userId, String email) {
        List<EmailVerifyCode> entities = queryViewSync('by_user_id_email', "$userId:$email")
        for (EmailVerifyCode code : entities) {
            cloudantDeleteSync(code)
        }
    }

    @Override
    List<EmailVerifyCode> getByUserIdEmail(Long userId, String email) {
        return queryViewSync('by_user_id_email', "$userId:$email").collect { EmailVerifyCode code ->
            code.code = cipherService.decrypt(code.encryptedCode)
            return code
        }
    }
}
