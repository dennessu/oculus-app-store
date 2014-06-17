/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.db.dao.AccessTokenDAO
import com.junbo.oauth.db.entity.AccessTokenEntity
import groovy.transform.CompileStatic

/**
 * CouchAccessTokenDAOImpl.
 */
@CompileStatic
class CouchAccessTokenDAOImpl extends CouchBaseDAO<AccessTokenEntity> implements AccessTokenDAO {

    @Override
    List<AccessTokenEntity> findByRefreshToken(String refreshTokenValue) {
        return queryView('by_refresh_token', refreshTokenValue)
    }

    @Override
    List<AccessTokenEntity> findByUserIdClientId(Long userId, String clientId) {
        return queryView('by_user_id_client_id', userId.toString() + ':' + clientId)
    }
}
