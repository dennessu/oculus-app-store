/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.repo

import com.junbo.oauth.spec.model.RefreshToken
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
interface RefreshTokenRepository {
    RefreshToken save(RefreshToken accessToken)

    RefreshToken get(String tokenValue)

    List<RefreshToken> findByUserIdClientId(Long userId, String clientId)

    RefreshToken getAndRemove(String tokenValue)

    boolean isValidRefreshToken(String tokenValue)
}
