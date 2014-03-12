/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.couch

import com.junbo.oauth.db.dao.ClientDAO
import com.junbo.oauth.db.entity.ClientEntity
import groovy.transform.CompileStatic

/**
 * CouchClientDAOImpl.
 */
@CompileStatic
class CouchClientDAOImpl extends CouchBaseDAO<ClientEntity> implements ClientDAO {
    @Override
    ClientEntity get(String id) {
        return internalGet(id, ClientEntity)
    }
}
