/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.AuthorizationCodeRepository
import com.junbo.oauth.spec.model.AuthorizationCode
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
/**
 * CloudantAuthorizationCodeRepositoryImpl.
 */
@CompileStatic
class CloudantAuthorizationCodeRepositoryImpl extends CloudantClient<AuthorizationCode>
        implements AuthorizationCodeRepository {

    private TokenGenerator tokenGenerator

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    void save(AuthorizationCode code) {
        if (code.code == null) {
            code.code = tokenGenerator.generateAuthorizationCode()
        }

        cloudantPostSync(code)
    }

    @Override
    AuthorizationCode getAndRemove(String code) {
        AuthorizationCode entity = cloudantGetSync(code)
        cloudantDeleteSync(code)
        return entity
    }
}
