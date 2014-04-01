/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.UserPhoneNumberDAO
import com.junbo.identity.data.entity.user.UserPhoneNumberEntity
import com.junbo.identity.spec.options.list.UserPhoneNumberListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.springframework.util.StringUtils
/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class UserPhoneNumberDAOImpl extends ShardedDAOBase implements UserPhoneNumberDAO {
    @Override
    UserPhoneNumberEntity save(UserPhoneNumberEntity entity) {
        currentSession().save(entity)

        return get(entity.id)
    }

    @Override
    UserPhoneNumberEntity update(UserPhoneNumberEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    UserPhoneNumberEntity get(Long id) {
        return (UserPhoneNumberEntity)currentSession().get(UserPhoneNumberEntity, id)
    }

    @Override
    List<UserPhoneNumberEntity> search(Long userId, UserPhoneNumberListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserPhoneNumberEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (!StringUtils.isEmpty(getOption.type)) {
            criteria.add(Restrictions.eq('type', getOption.type))
        }
        if (!StringUtils.isEmpty(getOption.value)) {
            criteria.add(Restrictions.eq('value', getOption.value))
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
        UserPhoneNumberEntity entity =
                (UserPhoneNumberEntity)currentSession().get(UserPhoneNumberEntity, id)
        currentSession().delete(entity)
    }
}
