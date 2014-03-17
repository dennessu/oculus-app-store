/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo

import com.junbo.oauth.spec.model.AccessToken
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
interface AccessTokenRepository {
    AccessToken save(AccessToken accessToken)

    AccessToken get(String tokenValue)

    List<AccessToken> findByRefreshToken(String refreshTokenValue)

    AccessToken update(AccessToken accessToken)

    void remove(String tokenValue)

    boolean isValidAccessToken(String tokenValue)
}
