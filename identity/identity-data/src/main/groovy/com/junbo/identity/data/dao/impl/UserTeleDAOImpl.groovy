package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserTeleDAO
import com.junbo.identity.data.entity.user.UserTeleCodeEntity
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleDAOImpl extends BaseDAO implements UserTeleDAO {
    @Override
    UserTeleCodeEntity create(UserTeleCodeEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)

        def currentSession = currentSession(entity.id)
        currentSession.save(entity)
        currentSession.flush()

        return get(entity.id)
    }

    @Override
    UserTeleCodeEntity update(UserTeleCodeEntity entity) {
        def currentSession = currentSession(entity.id)

        currentSession.merge(entity)
        currentSession.flush()

        return get(entity.id)
    }

    @Override
    UserTeleCodeEntity get(Long id) {
        return (UserTeleCodeEntity)currentSession(id).get(UserTeleCodeEntity, id)
    }

    @Override
    void delete(Long id) {
        def currentSession = currentSession(id)

        def entity = currentSession.get(UserTeleCodeEntity, id)
        currentSession.delete(entity)
        currentSession.flush()
    }

    @Override
    UserTeleCodeEntity getActiveUserTeleCode(Long userId, String phoneNumber) {
        Criteria criteria = currentSession(userId).createCriteria(UserTeleCodeEntity)
        criteria.add(Restrictions.eq('userId', userId))
        criteria.add(Restrictions.eq('personalInfo', phoneNumber))
        criteria.add(Restrictions.le('expiresBy', new Date()))
        criteria.addOrder(Order.desc('expiresBy'))
        criteria.setMaxResults(1)

        List list = criteria.list()
        if (list == null) {
            return null
        }
        return (UserTeleCodeEntity)criteria.list().get(0)
    }
}
