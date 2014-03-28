/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql

import com.junbo.sharding.core.hibernate.SessionFactoryWrapper
import com.junbo.sharding.util.Helper
import groovy.transform.CompileStatic
import org.hibernate.Session

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class ShardedDAOBase {

    private SessionFactoryWrapper sessionFactoryWrapper

    void setSessionFactoryWrapper(SessionFactoryWrapper sessionFactoryWrapper) {
        this.sessionFactoryWrapper = sessionFactoryWrapper
    }

    protected Session currentSession() {
        return sessionFactoryWrapper.resolve(Helper.fetchCurrentThreadLocalShardId()).currentSession
    }
}
