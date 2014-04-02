package com.junbo.sharding.view
import com.junbo.sharding.ShardAlgorithm
import com.junbo.sharding.core.hibernate.SessionFactoryWrapper
import com.junbo.sharding.hibernate.ShardScope
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
/**
 * Created by kg on 3/30/2014.
 */
@CompileStatic
class DefaultMultimapDAO implements MultimapDAO {

    private SessionFactoryWrapper sessionFactoryWrapper

    private ShardAlgorithm shardAlgorithm

    @Required
    void setSessionFactoryWrapper(SessionFactoryWrapper sessionFactoryWrapper) {
        this.sessionFactoryWrapper = sessionFactoryWrapper
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Override
    void put(EntityView view, Object key, Object value) {
        if (view == null) {
            throw new IllegalArgumentException('view is null')
        }

        if (key == null) {
            throw new IllegalArgumentException('key is null')
        }

        if (value == null) {
            throw new IllegalArgumentException('value is null')
        }

        ShardScope.with(shardAlgorithm.shardId(key)) {
            def sqlQuery = sessionFactoryWrapper.resolve(shardAlgorithm.shardId(key)).currentSession.createSQLQuery("""
INSERT INTO ${view.name}
(key, value, created_by, created_time, modified_by, modified_time, deleted)
VALUES (?, ?, ?, ?, ?, ?, FALSE)""")

            sqlQuery.setParameter(0, key)
            sqlQuery.setParameter(1, value)
            sqlQuery.setParameter(2, 'todo')
            sqlQuery.setParameter(3, new Date())
            sqlQuery.setParameter(4, 'todo')
            sqlQuery.setParameter(5, new Date())

            sqlQuery.executeUpdate()
        }
    }

    @Override
    void remove(EntityView view, Object key, Object value) {
        if (view == null) {
            throw new IllegalArgumentException('view is null')
        }

        if (key == null) {
            throw new IllegalArgumentException('key is null')
        }

        if (value == null) {
            throw new IllegalArgumentException('value is null')
        }

        ShardScope.with(shardAlgorithm.shardId(key)) {
            def sqlQuery = sessionFactoryWrapper.resolve(shardAlgorithm.shardId(key)).currentSession
                    .createSQLQuery("""UPDATE ${view.name}
SET deleted = TRUE, modified_by = ?, modified_time = ?
WHERE key = ? AND value = ?""")


            sqlQuery.setParameter(0, 'todo')
            sqlQuery.setParameter(1, new Date())
            sqlQuery.setParameter(2, key)
            sqlQuery.setParameter(3, value)

            sqlQuery.executeUpdate()
        }
    }

    @Override
    List get(EntityView view, Object key) {
        if (view == null) {
            throw new IllegalArgumentException('view is null')
        }

        if (key == null) {
            throw new IllegalArgumentException('key is null')
        }

        ShardScope.with(shardAlgorithm.shardId(key)) {
            def sqlQuery = sessionFactoryWrapper.resolve(shardAlgorithm.shardId(key)).currentSession
                    .createSQLQuery("""SELECT value
FROM ${view.name}
WHERE key = ? AND deleted = FALSE""")

            sqlQuery.setParameter(0, key)

            return sqlQuery.list()
        }
    }
}
