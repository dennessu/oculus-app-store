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
    void save(RefreshToken accessToken)

    RefreshToken getAndRemove(String tokenValue)
}
