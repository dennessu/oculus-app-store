/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.sharding.core.hibernate.SessionFactoryWrapper
import com.junbo.sharding.util.Helper
import com.junbo.sharding.view.ViewQueryFactory
import groovy.transform.CompileStatic
import org.hibernate.Session

/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class ShardedDAOBase {
    protected ViewQueryFactory viewQueryFactory

    private SessionFactoryWrapper sessionFactoryWrapper

    void setSessionFactoryWrapper(SessionFactoryWrapper sessionFactoryWrapper) {
        this.sessionFactoryWrapper = sessionFactoryWrapper
    }

    protected Session currentSession() {
        return sessionFactoryWrapper.resolve(Helper.fetchCurrentThreadLocalShardId()).currentSession
    }

    ViewQueryFactory getViewQueryFactory() {
        return viewQueryFactory
    }

    void setViewQueryFactory(ViewQueryFactory viewQueryFactory) {
        this.viewQueryFactory = viewQueryFactory
    }

    SessionFactoryWrapper getSessionFactoryWrapper() {
        return sessionFactoryWrapper
    }
}
