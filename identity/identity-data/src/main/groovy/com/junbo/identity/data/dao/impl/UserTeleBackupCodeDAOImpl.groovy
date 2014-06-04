package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserTeleBackupCodeDAO
import com.junbo.identity.data.entity.user.UserTeleBackupCodeEntity
import com.junbo.identity.spec.v1.option.list.UserTFABackupCodeListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions

/**
 * Created by liangfu on 4/23/14.
 */
@CompileStatic
class UserTeleBackupCodeDAOImpl extends BaseDAO implements UserTeleBackupCodeDAO {

    @Override
    UserTeleBackupCodeEntity create(UserTeleBackupCodeEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)

        def currentSession = currentSession(entity.id)
        currentSession.save(entity)
        currentSession.flush()

        return get(entity.id)
    }

    @Override
    UserTeleBackupCodeEntity update(UserTeleBackupCodeEntity entity) {
        def currentSession = currentSession(entity.id)
        currentSession.merge(entity)
        currentSession.flush()

        return get(entity.id)
    }

    @Override
    UserTeleBackupCodeEntity get(Long id) {
        return (UserTeleBackupCodeEntity)currentSession(id).get(UserTeleBackupCodeEntity, id)
    }

    @Override
    void delete(Long id) {
        def currentSession = currentSession(id)
        def entity = currentSession.get(UserTeleBackupCodeEntity, id)
        currentSession.delete(entity)
        currentSession.flush()
    }

    @Override
    List<UserTeleBackupCodeEntity> search(UserTFABackupCodeListOptions listOptions) {
        Criteria criteria = currentSession(listOptions.userId.value).createCriteria(UserTeleBackupCodeEntity)
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
