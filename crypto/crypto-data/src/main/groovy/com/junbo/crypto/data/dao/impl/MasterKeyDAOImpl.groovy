package com.junbo.crypto.data.dao.impl

import com.junbo.configuration.topo.DataCenters
import com.junbo.crypto.data.dao.MasterKeyDAO
import com.junbo.crypto.data.entity.MasterKeyEntity
import com.junbo.sharding.hibernate.ShardScope
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.criterion.Order

/**
 * Created by liangfu on 6/11/14.
 */
@CompileStatic
class MasterKeyDAOImpl extends BaseDAOImpl implements MasterKeyDAO {

    private static Integer DEFAULT_SHARD_ID = 0

    @Override
    MasterKeyEntity get(Long id) {
        return (MasterKeyEntity) getCurrentDCSession().get(MasterKeyEntity, id)
    }

    @Override
    MasterKeyEntity create(MasterKeyEntity masterKeyEntity) {
        if (masterKeyEntity == null) {
            throw new IllegalArgumentException('masterKeyEntity is null')
        }

        // todo:    Here we need to get the createdBy and createdTime from context
        masterKeyEntity.createdBy = 123L
        masterKeyEntity.createdTime = new Date()
        Session session = getCurrentDCSession()
        session.save(masterKeyEntity)
        session.flush()

        return get(masterKeyEntity.keyVersion)
    }

    @Override
    List<MasterKeyEntity> getAll() {
        Criteria criteria = getCurrentDCSession().createCriteria(MasterKeyEntity)
        criteria.addOrder(Order.asc('id'))
        return criteria.list()
    }

    private Session getCurrentDCSession() {
        return ShardScope.with(DataCenters.instance().currentDataCenterId(),
                shardAlgorithm.shardId(DEFAULT_SHARD_ID)) { sessionFactory.currentSession }
    }
}
