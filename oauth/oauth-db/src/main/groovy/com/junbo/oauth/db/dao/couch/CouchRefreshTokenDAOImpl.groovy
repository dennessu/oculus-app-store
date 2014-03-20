/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.db.dao.RefreshTokenDAO
import com.junbo.oauth.db.entity.RefreshTokenEntity
import groovy.transform.CompileStatic

/**
 * CouchRefreshTokenDAOImpl.
 */
@CompileStatic
class CouchRefreshTokenDAOImpl extends CouchBaseDAO<RefreshTokenEntity> implements RefreshTokenDAO {
    protected static final CouchViews VIEWS = new CouchViews(
            views: ['by_user_id_client_id': new CouchViews.CouchView(
                    map: 'function(doc) {' +
                            '  emit(doc.userId + \':\' + doc.clientId, doc._id)' +
                            '}',
                    resultClass: String)]
    )

    @Override
    protected CouchViews getCouchViews() {
        return VIEWS
    }

    @Override
    List<RefreshTokenEntity> findByUserIdClientId(Long userId, String clientId) {
        return queryView('by_user_id_client_id', userId.toString() + ':' + clientId)
    }
}
