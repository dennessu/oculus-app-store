/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.ResetPasswordCodeRepository
import com.junbo.oauth.spec.model.ResetPasswordCode
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * CloudantResetPasswordCodeRepositoryImpl.
 */
@CompileStatic
class CloudantResetPasswordCodeRepositoryImpl extends CloudantClient<ResetPasswordCode>
        implements ResetPasswordCodeRepository{
    private TokenGenerator tokenGenerator

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    ResetPasswordCode getAndRemove(String code) {
        ResetPasswordCode entity = cloudantGetSync(code)
        cloudantDeleteSync(code)
        return entity
    }

    @Override
    void save(ResetPasswordCode resetPasswordCode) {
        if (resetPasswordCode.code == null) {
            resetPasswordCode.code = tokenGenerator.generateResetPasswordCode()
        }

        cloudantPostSync(resetPasswordCode)
    }

    @Override
    void removeByUserIdEmail(Long userId, String email) {
        List<ResetPasswordCode> entities = queryViewSync('by_user_id_email', "$userId:$email")
        for (ResetPasswordCode code : entities) {
            cloudantDeleteSync(code)
        }
    }
}
