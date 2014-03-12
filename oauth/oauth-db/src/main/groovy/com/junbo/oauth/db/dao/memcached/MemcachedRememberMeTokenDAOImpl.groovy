/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.memcached

import com.junbo.oauth.db.dao.RememberMeTokenDAO
import com.junbo.oauth.db.entity.RememberMeTokenEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class MemcachedRememberMeTokenDAOImpl extends MemcachedBaseDAO<RememberMeTokenEntity> implements RememberMeTokenDAO {
    @Override
    RememberMeTokenEntity get(String id) {
        return internalGet(id, RememberMeTokenEntity)
    }
}
