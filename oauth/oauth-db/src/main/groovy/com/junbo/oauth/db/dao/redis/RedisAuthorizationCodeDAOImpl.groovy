/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.db.dao.redis

import com.junbo.oauth.db.dao.AuthorizationCodeDAO
import com.junbo.oauth.db.entity.AuthorizationCodeEntity
import groovy.transform.CompileStatic

/**
 * Javadoc.
 */
@CompileStatic
class RedisAuthorizationCodeDAOImpl extends RedisBaseDAO<AuthorizationCodeEntity> implements AuthorizationCodeDAO {
}
