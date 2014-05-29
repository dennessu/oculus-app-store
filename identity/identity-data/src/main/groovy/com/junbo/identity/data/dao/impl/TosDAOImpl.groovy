/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.TosDAO
import com.junbo.identity.data.entity.user.TosEntity
import com.junbo.identity.spec.v1.option.list.TosListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions

/**
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
class TosDAOImpl extends BaseDAO implements TosDAO {

    @Override
    TosEntity get(Long tosId) {
        return (TosEntity)currentSession(tosId).get(TosEntity, tosId)
    }

    @Override
    TosEntity create(TosEntity tos) {
        if (tos.id == null) {
            tos.id = idGenerator.nextId()
        }
        def session = currentSession(tos.id)
        session.save(tos)
        session.flush()

        return get((Long)tos.id)
    }

    @Override
    void delete(Long tosId) {
        def session = currentSession(tosId)
        def entity = session.get(TosEntity, tosId)

        session.delete(entity)
        session.flush()
    }

    @Override
    List<TosEntity> search(TosListOptions options) {
        Criteria criteria = currentSession(0).createCriteria(TosEntity)
        criteria.addOrder(Order.asc('id'))
        if (options.limit != null) {
            criteria.setMaxResults(options.limit)
        }
        if (options.offset != null) {
            criteria.setFirstResult(options.offset)
        }
        return criteria.list()
    }
}
