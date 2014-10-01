/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.sharding.id.oculus

import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import com.junbo.sharding.ShardAlgorithm
import com.junbo.sharding.hibernate.ShardScope
import groovy.transform.CompileStatic
import org.hibernate.SessionFactory
import org.hibernate.exception.ConstraintViolationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate

/**
 * Created by liangfu on 3/5/14.
 */
@Component
@CompileStatic
@SuppressWarnings('UnnecessaryGString')
class OculusGlobalCounterInDBImpl implements OculusGlobalCounter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OculusGlobalCounterInDBImpl)

    private TransactionTemplate transactionTemplate

    private SessionFactory sessionFactory

    private ShardAlgorithm shardAlgorithm

    @Required
    void setTransactionManager(PlatformTransactionManager transactionManager) {
        transactionTemplate = new AsyncTransactionTemplate(transactionManager)
        transactionTemplate.propagationBehavior = TransactionDefinition.PROPAGATION_NOT_SUPPORTED
    }

    @Required
    void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Override
    int getAndIncrease(int dataCenterId, int shardId, int idType) {
        transactionTemplate.execute {
            while (true) {
                Integer value = getValue(dataCenterId, idType, shardId)

                if (value == null) {
                    if (saveValue(dataCenterId, idType, shardId)) {
                        return 0
                    }
                } else {
                    if (updateValue(dataCenterId, idType, shardId, value)) {
                        return value + 1
                    }
                }
            }
        }
    }

    private Integer getValue(int dataCenterId, int idType, int shardId) {
        def currentSession = ShardScope.with(dataCenterId, shardId) { sessionFactory.openSession() }

        def sqlQuery = currentSession.createSQLQuery("""
SELECT global_count FROM id_global_counter
WHERE id_type = ? AND shard_id = ?""")

        sqlQuery.setParameter(0, idType)
        sqlQuery.setParameter(1, shardId)

        sqlQuery.addScalar('global_count', sessionFactory.typeHelper.basic(Integer))

        def list = sqlQuery.list()

        return list.size() == 1 ? (Integer) list.get(0) : (Integer) null
    }

    private boolean saveValue(int dataCenterId, int idType, int shardId) {
        def currentSession = ShardScope.with(dataCenterId, shardId) { sessionFactory.openSession() }

        def sqlQuery = currentSession.createSQLQuery("""
INSERT INTO id_global_counter (id_type, shard_id, global_count)
VALUES (?, ?, 0)""")
        sqlQuery.setParameter(0, idType)
        sqlQuery.setParameter(1, shardId)

        try {
            return sqlQuery.executeUpdate() == 1
        } catch (ConstraintViolationException ex) {
            LOGGER.warn("Unable to insert a counter due to race condition", ex)
            return false
        }
    }

    private boolean updateValue(int dataCenterId, int idType, int shardId, int value) {
        def currentSession = ShardScope.with(dataCenterId, shardId) { sessionFactory.openSession() }

        def sqlQuery = currentSession.createSQLQuery("""
UPDATE id_global_counter
SET global_count = global_count + 1
WHERE id_type = ? AND shard_id = ? AND global_count = ?""")

        sqlQuery.setParameter(0, idType)
        sqlQuery.setParameter(1, shardId)
        sqlQuery.setParameter(2, value)

        return sqlQuery.executeUpdate() == 1
    }
}
