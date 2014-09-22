/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.oauth.db.repo.AuthorizationCodeRepository
import com.junbo.oauth.spec.model.AuthorizationCode
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils
/**
 * CloudantAuthorizationCodeRepositoryImpl.
 */
@CompileStatic
class CloudantAuthorizationCodeRepositoryImpl
        extends CloudantTokenRepositoryBase<AuthorizationCode> implements AuthorizationCodeRepository {

    @Override
    void save(AuthorizationCode code) {
        if (code.code == null) {
            code.code = tokenGenerator.generateAuthorizationCode(code.userId)
            code.hashedCode = tokenGenerator.hashKey(code.code)
        }

        cloudantPostSync(code)
    }

    @Override
    AuthorizationCode getAndRemove(String code) {
        if (StringUtils.isEmpty(code)) {
            return null
        }

        String tokenHash = tokenGenerator.hashKey(code)
        AuthorizationCode entity = cloudantGetSyncWithFallback(code, tokenHash)
        if (entity != null) {
            entity.code = code
        }

        cloudantDeleteSync(tokenHash)
        return entity
    }
}
