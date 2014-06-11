package com.junbo.crypto.data.dao.impl

import com.junbo.crypto.data.dao.MasterKeyDAO
import com.junbo.crypto.data.entity.MasterKeyEntity
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 6/11/14.
 */
@CompileStatic
class MasterKeyDAOImpl extends BaseDAOImpl implements MasterKeyDAO {

    public static final Long INIT_SHARD_ID = 0

    @Override
    MasterKeyEntity get(Long id) {
        return (MasterKeyEntity) currentSession(id).get(MasterKeyEntity, id)
    }

    @Override
    MasterKeyEntity create(MasterKeyEntity masterKeyEntity) {
        if (masterKeyEntity == null) {
            throw new IllegalArgumentException('masterKeyEntity is null')
        }
        if (masterKeyEntity.id == null) {
            masterKeyEntity.id = idGenerator.nextId(INIT_SHARD_ID)
        }

        // todo:    Here we need to get the createdBy and createdTime from context
        masterKeyEntity.createdBy = 123L
        masterKeyEntity.createdTime = new Date()
        Session session = currentSession(masterKeyEntity.id)
        session.save(masterKeyEntity)
        session.flush()

        return get((Long) masterKeyEntity.id)
    }

    @Override
    List<MasterKeyEntity> getAll() {
        Criteria criteria = currentSession(INIT_SHARD_ID).createCriteria(MasterKeyEntity)
        criteria.addOrder(Order.asc('id'))
        return criteria.list()
    }

    @Override
    MasterKeyEntity getByKeyVersion(Integer keyVersion) {
        Criteria criteria = currentSession(INIT_SHARD_ID).createCriteria(MasterKeyEntity)
        criteria.add(Restrictions.eq('keyVersion', keyVersion))
        List list = criteria.list()
        return CollectionUtils.isEmpty(list) ? null : (MasterKeyEntity)list.get(0)
    }
}
