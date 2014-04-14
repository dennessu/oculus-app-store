/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.db.dao.ApiDefinitionDAO
import com.junbo.oauth.db.entity.ApiDefinitionEntity
import groovy.transform.CompileStatic

/**
 * CouchApiDefinitionDAOImpl.
 */
@CompileStatic
class CouchApiDefinitionDAOImpl extends CouchBaseDAO<ApiDefinitionEntity> implements ApiDefinitionDAO {
    @Override
    List<ApiDefinitionEntity> getAll() {
        return super.all
    }

    @Override
    protected CouchViews getCouchViews() {
        return null
    }
}
