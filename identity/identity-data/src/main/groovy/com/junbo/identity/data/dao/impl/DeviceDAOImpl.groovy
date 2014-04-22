package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.DeviceDAO
import com.junbo.identity.data.entity.device.DeviceEntity
import groovy.transform.CompileStatic
import org.hibernate.Session
import org.springframework.util.CollectionUtils

/**
 * Created by xiali_000 on 4/8/2014.
 */
@CompileStatic
class DeviceDAOImpl extends BaseDAO implements DeviceDAO {

    @Override
    DeviceEntity get(Long deviceId) {
        return (DeviceEntity)currentSession(deviceId).get(DeviceEntity, deviceId)
    }

    @Override
    DeviceEntity create(DeviceEntity device) {
        if (device.id == null) {
            device.id = idGenerator.nextIdByShardId(shardAlgorithm.shardId())
        }
        Session session = currentSession(device.id)
        session.save(device)
        session.flush()

        return get((Long)device.id)
    }

    @Override
    DeviceEntity update(DeviceEntity device) {
        Session session = currentSession(device.id)
        session.merge(device)
        session.flush()

        return get((Long)device.id)
    }

    @Override
    DeviceEntity findIdByExternalRef(String externalRef) {
        DeviceEntity example = new DeviceEntity()
        example.setExternalRef(externalRef)

        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def ids = viewQuery.list()

            Long id = CollectionUtils.isEmpty(ids) ? null : (Long)(ids.get(0))
            if (id != null) {
                return get(id)
            }
        }

        return null
    }
}
