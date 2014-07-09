package com.junbo.crypto.data.dao.impl

import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import com.junbo.sharding.hibernate.ShardScope
import com.junbo.sharding.view.ViewQueryFactory
import groovy.transform.CompileStatic
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/4/14.
 */
@CompileStatic
abstract class BaseDAOImpl {

    protected SessionFactory sessionFactory
    protected ShardAlgorithm shardAlgorithm
    protected IdGenerator idGenerator

    @Required
    void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    protected Session currentSession(Object key) {
        return ShardScope.with(shardAlgorithm.dataCenterId(key), shardAlgorithm.shardId(key)) { sessionFactory.currentSession }
    }
}
