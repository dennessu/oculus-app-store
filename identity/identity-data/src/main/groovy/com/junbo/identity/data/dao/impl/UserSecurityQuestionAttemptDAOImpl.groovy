/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserSecurityQuestionAttemptDAO
import com.junbo.identity.data.entity.user.UserSecurityQuestionAttemptEntity
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionAttemptListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions

/**
 * Created by liangfu on 3/25/14.
 */
@CompileStatic
class UserSecurityQuestionAttemptDAOImpl extends BaseDAO implements UserSecurityQuestionAttemptDAO {

    @Override
    UserSecurityQuestionAttemptEntity save(UserSecurityQuestionAttemptEntity entity) {
        if (entity.id == null) {
            entity.id = idGenerator.nextId(entity.userId)
        }
        Session session = currentSession(entity.id)
        session.save(entity)
        session.flush()

        return get(entity.id)
    }

    @Override
    UserSecurityQuestionAttemptEntity update(UserSecurityQuestionAttemptEntity entity) {
        Session session = currentSession(entity.id)
        session.merge(entity)
        session.flush()

        return get(entity.id)
    }

    @Override
    UserSecurityQuestionAttemptEntity get(Long id) {
        return (UserSecurityQuestionAttemptEntity)currentSession(id).get(UserSecurityQuestionAttemptEntity, id)
    }

    @Override
    List<UserSecurityQuestionAttemptEntity> search(Long userId, UserSecurityQuestionAttemptListOptions getOption) {
        Criteria criteria = currentSession(userId).createCriteria(UserSecurityQuestionAttemptEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (getOption.userSecurityQuestionId != null) {
           criteria.add(Restrictions.eq('userSecurityQuestionId', getOption.userSecurityQuestionId.value))
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
}
