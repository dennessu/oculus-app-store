/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.EmailVerifyCodeRepository
import com.junbo.oauth.spec.model.EmailVerifyCode
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * CloudantEmailVerifyCodeRepositoryImpl.
 */
@CompileStatic
class CloudantEmailVerifyCodeRepositoryImpl extends CloudantClient<EmailVerifyCode>
        implements EmailVerifyCodeRepository {
    private TokenGenerator tokenGenerator

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    EmailVerifyCode getAndRemove(String code) {
        EmailVerifyCode entity = cloudantGet(code).get()
        cloudantDelete(code).get()
        return entity
    }

    @Override
    void save(EmailVerifyCode emailVerifyCode) {
        if (emailVerifyCode.code == null) {
            emailVerifyCode.code = tokenGenerator.generateEmailVerifyCode()
        }
        cloudantPost(emailVerifyCode).get()
    }

    @Override
    void removeByUserIdEmail(Long userId, String email) {
        List<EmailVerifyCode> entities = queryView('by_user_id_email', "$userId:$email").get()
        for (EmailVerifyCode code : entities) {
            cloudantDelete(code).get()
        }
    }
}
