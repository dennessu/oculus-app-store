/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.db.dao.ConsentDAO
import com.junbo.oauth.db.entity.ConsentEntity
import groovy.transform.CompileStatic

/**
 * CouchConsentDAOImpl.
 */
@CompileStatic
class CouchConsentDAOImpl extends CouchBaseDAO<ConsentEntity> implements ConsentDAO {
    @Override
    protected CouchViews getCouchViews() {
        return null
    }
}
