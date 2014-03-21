/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.db.dao.ScopeDAO
import com.junbo.oauth.db.entity.ScopeEntity
import groovy.transform.CompileStatic

/**
 * CouchScopeDAOImpl.
 */
@CompileStatic
class CouchScopeDAOImpl extends CouchBaseDAO<ScopeEntity> implements ScopeDAO {
    @Override
    protected CouchViews getCouchViews() {
        return null
    }
}
