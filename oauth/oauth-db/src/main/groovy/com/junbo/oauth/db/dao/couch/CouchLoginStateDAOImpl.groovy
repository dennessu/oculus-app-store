/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.db.dao.LoginStateDAO
import com.junbo.oauth.db.entity.LoginStateEntity
import groovy.transform.CompileStatic

/**
 * CouchLoginStateDAOImpl.
 */
@CompileStatic
class CouchLoginStateDAOImpl extends CouchBaseDAO<LoginStateEntity> implements LoginStateDAO {
    @Override
    protected CouchViews getCouchViews() {
        return null
    }
}
