/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserAddressDAO
import com.junbo.identity.data.entity.user.UserAddressEntity
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions

/**
 * Created by liangfu on 4/16/14.
 */
@CompileStatic
class UserAddressDAOImpl extends BaseDAO implements UserAddressDAO  {

    @Override
    UserAddressEntity save(UserAddressEntity entity) {
        if (entity.id == null) {
            entity.id = idGenerator.nextId(entity.userPiiId)
        }
        def currentSession = currentSession(entity.id)
        currentSession.save(entity)
        currentSession.flush()

        get(entity.id)
    }

    @Override
    UserAddressEntity get(Long id) {
        return (UserAddressEntity)currentSession(id).get(UserAddressEntity, id)
    }

    @Override
    void delete(Long id) {
        def currentSession = currentSession(id)
        def entity = currentSession.get(UserAddressEntity, id)
        currentSession.delete(entity)
        currentSession.flush()
    }

    @Override
    List<UserAddressEntity> search(Long userPiiId) {
        Criteria criteria = currentSession(userPiiId).createCriteria(UserAddressEntity)
        criteria.add(Restrictions.eq('userPiiId', userPiiId))
        criteria.addOrder(Order.asc('id'))
        return criteria.list()
    }
}
