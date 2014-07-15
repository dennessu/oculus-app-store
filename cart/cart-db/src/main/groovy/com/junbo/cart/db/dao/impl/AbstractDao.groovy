package com.junbo.cart.db.dao.impl

import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import com.junbo.sharding.hibernate.ShardScope
import groovy.transform.CompileStatic
import org.hibernate.Session
import org.hibernate.SessionFactory

/**
 * Created by fzhang on 4/14/2014.
 */
@CompileStatic
class AbstractDao {

    protected SessionFactory sessionFactory

    protected ShardAlgorithm shardAlgorithm

    protected IdGenerator idGenerator

    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    Session getSession(Object key) {
        return ShardScope.with(shardAlgorithm.dataCenterId(key), shardAlgorithm.shardId(key)) { sessionFactory.currentSession }
    }

    void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory
    }
}
