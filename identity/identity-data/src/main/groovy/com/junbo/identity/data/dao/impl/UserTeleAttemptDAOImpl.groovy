package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserTeleAttemptDAO
import com.junbo.identity.data.entity.user.UserTeleAttemptEntity
import com.junbo.identity.spec.v1.option.list.UserTeleAttemptListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleAttemptDAOImpl extends BaseDAO implements UserTeleAttemptDAO {

    @Override
    UserTeleAttemptEntity create(UserTeleAttemptEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)

        def currentSession = currentSession(entity.id)
        currentSession.save(entity)
        currentSession.flush()

        return get(entity.id)
    }

    @Override
    UserTeleAttemptEntity update(UserTeleAttemptEntity entity) {
        def currentSession = currentSession(entity.id)
        currentSession.merge(entity)
        currentSession.flush()

        return get(entity.id)
    }

    @Override
    UserTeleAttemptEntity get(Long id) {
        return (UserTeleAttemptEntity)currentSession(id).get(UserTeleAttemptEntity, id)
    }

    @Override
    List<UserTeleAttemptEntity> searchByUserId(Long userId, UserTeleAttemptListOptions listOptions) {
        Criteria criteria = currentSession(userId).createCriteria(UserTeleAttemptEntity)
        criteria.add(Restrictions.eq('userId', userId))

        criteria.addOrder(Order.asc('id'))
        if (listOptions.limit != null) {
            criteria.setMaxResults(listOptions.limit)
        }
        if (listOptions.offset != null) {
            criteria.setFirstResult(listOptions.offset)
        }

        return criteria.list()
    }

    @Override
    void delete(Long id) {
        def currentSession = currentSession(id)
        def entity = currentSession.get(UserTeleAttemptEntity, id)
        currentSession.delete(entity)
        currentSession.flush()
    }
}
