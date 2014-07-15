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
        } else {
            String[] tokens = rememberMeToken.tokenValue.split('\\.')
            Assert.isTrue(tokens.length == 2)

            rememberMeToken.tokenValue = tokens[0] + '.' + tokenGenerator.generateRememberMeTokenSeries()
        }

        return cloudantPostSync(rememberMeToken)
    }

    @Override
    RememberMeToken get(String tokenValue) {
        return cloudantGetSync(tokenValue)
    }

    @Override
    RememberMeToken getAndRemove(String tokenValue) {
        RememberMeToken entity = cloudantGetSync(tokenValue)
        cloudantDeleteSync(tokenValue)
        return entity
    }
}
