/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserTosDAO
import com.junbo.identity.data.entity.user.UserTosAgreementEntity
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.Session
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.springframework.util.StringUtils

/**
 * Implementation for User Tos Acceptance DAO interface.
 */
@CompileStatic
class UserTosDAOImpl extends BaseDAO implements UserTosDAO {
    @Override
    UserTosAgreementEntity save(UserTosAgreementEntity entity) {
        entity.id = idGenerator.nextId(entity.userId)
        Session session = currentSession(entity.id)
        session.save(entity)
        session.flush()
        return get(entity.id)
    }

    @Override
    UserTosAgreementEntity update(UserTosAgreementEntity entity) {
        Session session = currentSession(entity.id)
        session.merge(entity)
        session.flush()

        return get(entity.id)
    }

    @Override
    UserTosAgreementEntity get(Long id) {
        return (UserTosAgreementEntity)currentSession(id).get(UserTosAgreementEntity, id)
    }

    @Override
    List<UserTosAgreementEntity> search(Long userId, UserTosAgreementListOptions getOption) {
        Criteria criteria = currentSession(userId).createCriteria(UserTosAgreementEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (!StringUtils.isEmpty(getOption.tosId)) {
            criteria.add(Restrictions.eq('tosId', getOption.tosId.value))
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
        UserTosAgreementEntity entity = (UserTosAgreementEntity)session.get(UserTosAgreementEntity, id)
        session.delete(entity)
        session.flush()
    }
}
