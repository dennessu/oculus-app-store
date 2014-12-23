/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.spec.model.TokenType
import groovy.transform.CompileStatic
import org.apache.commons.codec.binary.Base64
import org.springframework.util.Assert

/**
 * AuthorizationHeaderUtil.
 */
@CompileStatic
class AuthorizationHeaderUtil {
    private static final int TOKENS_LENGTH = 2

    static ClientCredential extractClientCredential(String authorization) {
        Assert.notNull(authorization, 'authorization is null')

        String[] tokens = authorization.split(' ')
        if (tokens.length != TOKENS_LENGTH || !tokens[0].equalsIgnoreCase('Basic')) {
            throw AppErrors.INSTANCE.invalidAuthorization().exception()
        }

        String decoded = new String(Base64.decodeBase64(tokens[1]))

        tokens = decoded.split(':')

        if (tokens.length != TOKENS_LENGTH) {
            throw AppErrors.INSTANCE.invalidAuthorization().exception()
        }

        return new ClientCredential(
                clientId: tokens[0],
                clientSecret: tokens[1]
        )
    }

    static String extractAccessToken(String authorization) {
        Assert.notNull(authorization, 'authorization is null')

        String[] tokens = authorization.split(' ')
        if (tokens.length != TOKENS_LENGTH || !tokens[0].equalsIgnoreCase(TokenType.BEARER.name())) {
            throw AppErrors.INSTANCE.invalidAuthorization().exception()
        }

        return tokens[1]
    }

    static class ClientCredential {
        String clientId
        String clientSecret
    }
}
