/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.db.dao.AuthorizationCodeDAO
import com.junbo.oauth.db.entity.AuthorizationCodeEntity
import groovy.transform.CompileStatic

/**
 * CouchAuthorizationCodeDAOImpl.
 */
@CompileStatic
class CouchAuthorizationCodeDAOImpl extends CouchBaseDAO<AuthorizationCodeEntity> implements AuthorizationCodeDAO {
    @Override
    AuthorizationCodeEntity get(String id) {
        return internalGet(id, AuthorizationCodeEntity)
    }
}
