/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao

import com.junbo.oauth.db.entity.AccessTokenEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
interface AccessTokenDAO extends BaseDAO<AccessTokenEntity, String> {
    List<AccessTokenEntity> findByRefreshToken(String refreshTokenValue)

    List<AccessTokenEntity> findByUserIdClientId(Long userId, String clientId)
}
