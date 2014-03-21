/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao

import com.junbo.oauth.db.entity.RefreshTokenEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
interface RefreshTokenDAO extends BaseDAO<RefreshTokenEntity, String> {
    List<RefreshTokenEntity> findByUserIdClientId(Long userId, String clientId)
}
