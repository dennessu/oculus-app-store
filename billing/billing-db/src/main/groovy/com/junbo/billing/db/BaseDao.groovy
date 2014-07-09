package com.junbo.billing.db

import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import com.junbo.sharding.hibernate.ShardScope
import com.junbo.sharding.view.ViewQueryFactory
import groovy.transform.CompileStatic
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Required

/**
 * Created by xmchen on 14-4-14.
 */
@CompileStatic
abstract class BaseDao {

    protected SessionFactory sessionFactory
    protected ShardAlgorithm shardAlgorithm
    protected ViewQueryFactory viewQueryFactory
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
    void setViewQueryFactory(ViewQueryFactory viewQueryFactory) {
        this.viewQueryFactory = viewQueryFactory
    }

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    protected Session currentSession(Object key) {
        return ShardScope.with(shardAlgorithm.dataCenterId(key), shardAlgorithm.shardId(key)) { sessionFactory.currentSession }
    }
}
