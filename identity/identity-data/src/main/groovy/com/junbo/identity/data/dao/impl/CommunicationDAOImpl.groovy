package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.CommunicationDAO
import com.junbo.identity.data.entity.user.CommunicationEntity
import groovy.transform.CompileStatic
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
class CommunicationDAOImpl extends BaseDAO implements CommunicationDAO {

    @Override
    CommunicationEntity get(Long communicationId) {
        def currentSession = currentSession(communicationId)
        return (CommunicationEntity)currentSession.get(CommunicationEntity, communicationId)
    }

    @Override
    CommunicationEntity save(CommunicationEntity entity) {
        if (entity.id == null) {
            entity.id = idGenerator.nextId()
        }
        def currentSession = currentSession(entity.id)
        currentSession.save(entity)
        currentSession.flush()

        return get((Long)entity.id)
    }

    @Override
    CommunicationEntity update(CommunicationEntity entity) {
        def currentSession = currentSession(entity.id)
        currentSession.merge(entity)
        currentSession.flush()

        return get((Long)entity.id)
    }

    @Override
    CommunicationEntity findIdByName(String name) {
        CommunicationEntity example = new CommunicationEntity()
        example.setName(name)

        def viewQuery = viewQueryFactory.from(example)
        if (viewQuery != null) {
            def ids = viewQuery.list()

            Long id = CollectionUtils.isEmpty(ids) ? null : (Long) (ids.get(0))

            if (id != null) {
                return get(id)
            }
        }

        return null
    }
}
