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
    AccessTokenEntity get(String id) {
        return internalGet(id, AccessTokenEntity)
    }
}
