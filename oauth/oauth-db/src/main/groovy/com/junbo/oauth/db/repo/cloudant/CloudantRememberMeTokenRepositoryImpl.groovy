/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.oauth.db.generator.TokenGenerator
import com.junbo.oauth.db.repo.RememberMeTokenRepository
import com.junbo.oauth.spec.model.RememberMeToken
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * CloudantRememberMeTokenRepositoryImpl.
 */
@CompileStatic
class CloudantRememberMeTokenRepositoryImpl extends CloudantClient<RememberMeToken>
        implements RememberMeTokenRepository {
    private TokenGenerator tokenGenerator

    @Required
    void setTokenGenerator(TokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator
    }

    @Override
    RememberMeToken save(RememberMeToken rememberMeToken) {
        if (rememberMeToken.tokenValue == null) {
            rememberMeToken.tokenValue = tokenGenerator.generateRememberMeToken() +
                    '.' + tokenGenerator.generateRememberMeTokenSeries()
            rememberMeToken.hashedTokenValue = tokenGenerator.hashKey(rememberMeToken.tokenValue)
        } else {
            String[] tokens = rememberMeToken.tokenValue.split('\\.')
            Assert.isTrue(tokens.length == 2)

            rememberMeToken.tokenValue = tokens[0] + '.' + tokenGenerator.generateRememberMeTokenSeries()
            rememberMeToken.hashedTokenValue = tokenGenerator.hashKey(rememberMeToken.hashedTokenValue)
        }

        return cloudantPostSync(rememberMeToken)
    }

    @Override
    RememberMeToken get(String tokenValue) {
        if (StringUtils.isEmpty(tokenValue)) {
            return null
        }

        RememberMeToken token = cloudantGetSync(tokenGenerator.hashKey(tokenValue))
        if (token != null) {
            token.tokenValue = tokenValue
        }

        return token
    }

    @Override
    RememberMeToken getAndRemove(String tokenValue) {
        if (StringUtils.isEmpty(tokenValue)) {
            return null
        }

        String hashed = tokenGenerator.hashKey(tokenValue)
        RememberMeToken entity = cloudantGetSync(hashed)
        if (entity != null) {
            entity.tokenValue = tokenValue
        }

        cloudantDeleteSync(hashed)
        return entity
    }
}
