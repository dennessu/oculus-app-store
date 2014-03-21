/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.db.dao.AccessTokenDAO
import com.junbo.oauth.db.dao.couch.CouchViews.CouchView
import com.junbo.oauth.db.entity.AccessTokenEntity
import groovy.transform.CompileStatic

/**
 * CouchAccessTokenDAOImpl.
 */
@CompileStatic
class CouchAccessTokenDAOImpl extends CouchBaseDAO<AccessTokenEntity> implements AccessTokenDAO {
    protected CouchViews views = new CouchViews(
            views: ['by_refresh_token': new CouchView(
                    map: 'function(doc) {' +
                            '  if (doc.refreshToken != null) {' +
                            '    emit(doc.refreshToken, doc._id);' +
                            '  }' +
                            '}',
                    resultClass: String),
                    'by_user_id_client_id': new CouchView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId + \':\' + doc.clientId, doc._id)' +
                                    '}',
                            resultClass: String)]
    )

    @Override
    CouchViews getCouchViews() {
        return views
    }

    @Override
    List<AccessTokenEntity> findByRefreshToken(String refreshTokenValue) {
        return queryView('by_refresh_token', refreshTokenValue)
    }

    @Override
    List<AccessTokenEntity> findByUserIdClientId(Long userId, String clientId) {
        return queryView('by_user_id_client_id', userId.toString() + ':' + clientId)
    }
}
