package com.junbo.identity.data.dao.impl

import com.junbo.common.id.UserId
import com.junbo.identity.data.dao.UserPiiDAO
import com.junbo.identity.data.entity.user.UserPiiEntity
import com.junbo.identity.spec.v1.model.UserPii
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.hibernate.Criteria
import org.hibernate.criterion.Restrictions

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
class UserPiiDAOImpl extends BaseDAO implements UserPiiDAO {

    @Override
    UserPiiEntity save(UserPiiEntity entity) {
        if (entity.id == null) {
            entity.id = idGenerator.nextId(entity.userId)
        }

        def currentSession = currentSession(entity.id)
        currentSession.save(entity)
        currentSession.flush()

        return get((Long)entity.id)
    }

    @Override
    UserPiiEntity update(UserPiiEntity entity) {
        def currentSession = currentSession(entity.id)

        currentSession.merge(entity)
        currentSession.flush()
        return get((Long)entity.id)
    }

    @Override
    UserPiiEntity get(Long id) {
        return (UserPiiEntity)currentSession(id).get(UserPiiEntity, id)
    }

    @Override
    void delete(Long id) {
        def currentSession = currentSession(id)

        def entity = currentSession.get(UserPiiEntity, id)
        currentSession.delete(entity)
        currentSession.flush()
    }

    @Override
    UserPiiEntity findByUserId(Long userId) {
        Criteria criteria = currentSession(userId).createCriteria(UserPiiEntity)
        criteria.add(Restrictions.eq('userId', userId))
        def list =  criteria.list()

        if (CollectionUtils.isEmpty(list)) {
            return null
        }
        return (UserPiiEntity)list.get(0)
    }
}
