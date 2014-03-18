/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.redis

import com.junbo.oauth.db.dao.ClientDAO
import com.junbo.oauth.db.entity.ClientEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class RedisClientDAOImpl extends RedisBaseDAO<ClientEntity> implements ClientDAO {
}
