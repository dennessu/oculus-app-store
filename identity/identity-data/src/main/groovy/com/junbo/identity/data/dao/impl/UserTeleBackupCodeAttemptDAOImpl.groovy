package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserTeleBackupCodeAttemptDAO
import com.junbo.identity.data.entity.user.UserTeleBackupCodeAttemptEntity
import com.junbo.identity.spec.v1.option.list.UserTeleBackupCodeAttemptListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleBackupCodeAttemptDAOImpl extends BaseDAO implements UserTeleBackupCodeAttemptDAO {

    @Override
    UserTeleBackupCodeAttemptEntity create(UserTeleBackupCodeAttemptEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)

        def currentSession = currentSession(entity.id)
        currentSession.save(entity)
        currentSession.flush()

        return get(entity.id)
    }

    @Override
    UserTeleBackupCodeAttemptEntity update(UserTeleBackupCodeAttemptEntity entity) {
        def currentSession = currentSession(entity.id)
        currentSession.merge(entity)
        currentSession.flush()

        return get(entity.id)
    }

    @Override
    UserTeleBackupCodeAttemptEntity get(Long id) {
        return (UserTeleBackupCodeAttemptEntity)currentSession(id).get(UserTeleBackupCodeAttemptEntity, id)
    }

    @Override
    void delete(Long id) {
        def currentSession = currentSession(id)
        def entity = currentSession.get(UserTeleBackupCodeAttemptEntity, id)
        currentSession.delete(entity)
        currentSession.flush()
    }

    @Override
    List<UserTeleBackupCodeAttemptEntity> search(UserTeleBackupCodeAttemptListOptions listOptions) {
        Criteria criteria = currentSession(listOptions.userId.value).createCriteria(UserTeleBackupCodeAttemptEntity)
        criteria.add(Restrictions.eq('userId', listOptions.userId.value))
        criteria.addOrder(Order.asc('id'))
        if (listOptions.limit != null) {
            criteria.setMaxResults(listOptions.limit)
        }
        if (listOptions.offset != null) {
            criteria.setFirstResult(listOptions.offset)
        }

        return criteria.list()
    }
}
