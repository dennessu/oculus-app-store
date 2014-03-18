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
                    resultClass: String)]
    )

    @Override
    CouchViews getCouchViews() {
        return views
    }

    @Override
    List<AccessTokenEntity> findByRefreshToken(String refreshTokenValue) {
        CouchSearchResult<String> searchResult = (CouchSearchResult<String>) queryView('by_refresh_token',
                refreshTokenValue)
        if (searchResult.rows != null) {
            return searchResult.rows.collect { CouchSearchResult.ResultObject result ->
                return get(result.id)
            }
        }

        return []
    }
}
