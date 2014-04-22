/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserCredentialVerifyAttemptDAO
import com.junbo.identity.data.entity.user.UserCredentialVerifyAttemptEntity
import com.junbo.identity.spec.v1.option.list.UserCredentialAttemptListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.springframework.util.StringUtils
/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class UserCredentialVerifyAttemptDAOImpl extends BaseDAO implements UserCredentialVerifyAttemptDAO {
    @Override
    UserCredentialVerifyAttemptEntity save(UserCredentialVerifyAttemptEntity entity) {
        if (entity.id == null) {
            entity.id = idGenerator.nextId(entity.userId)
        }
        Session session = currentSession(entity.id)
        session.save(entity)
        session.flush()

        return get(entity.id)
    }

    @Override
    UserCredentialVerifyAttemptEntity update(UserCredentialVerifyAttemptEntity entity) {
        Session session = currentSession(entity.id)
        session.merge(entity)
        session.flush()

        return get(entity.id)
    }

    @Override
    UserCredentialVerifyAttemptEntity get(Long id) {
        return (UserCredentialVerifyAttemptEntity)currentSession(id).get(UserCredentialVerifyAttemptEntity, id)
    }

    @Override
    List<UserCredentialVerifyAttemptEntity> search(Long userId, UserCredentialAttemptListOptions getOption) {
        Criteria criteria = currentSession(userId).createCriteria(UserCredentialVerifyAttemptEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (!StringUtils.isEmpty(getOption.type)) {
            criteria.add(Restrictions.eq('type', getOption.type))
        }
        criteria.addOrder(Order.asc('id'))
        if (getOption.limit != null) {
            criteria.setMaxResults(getOption.limit)
        }
        if (getOption.offset != null) {
            criteria.setFirstResult(getOption.offset)
        }
        return criteria.list()
    }

    @Override
    void delete(Long id) {
        Session session = currentSession(id)
        UserCredentialVerifyAttemptEntity entity =
                (UserCredentialVerifyAttemptEntity)session.get(UserCredentialVerifyAttemptEntity, id)
        session.delete(entity)
        session.flush()
    }
}
