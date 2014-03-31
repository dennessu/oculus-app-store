/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql

import com.junbo.identity.data.dao.UserLoginAttemptDAO
import com.junbo.identity.data.entity.user.UserLoginAttemptEntity
import com.junbo.identity.spec.options.list.UserLoginAttemptListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.springframework.util.StringUtils
/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class UserLoginAttemptDAOImpl extends ShardedDAOBase implements UserLoginAttemptDAO {
    @Override
    UserLoginAttemptEntity save(UserLoginAttemptEntity entity) {
        currentSession().save(entity)

        return get(entity.id)
    }

    @Override
    UserLoginAttemptEntity update(UserLoginAttemptEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    UserLoginAttemptEntity get(Long id) {
        return (UserLoginAttemptEntity)currentSession().get(UserLoginAttemptEntity, id)
    }

    @Override
    List<UserLoginAttemptEntity> search(Long userId, UserLoginAttemptListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserLoginAttemptEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (!StringUtils.isEmpty(getOption.type)) {
            criteria.add(Restrictions.eq('type', getOption.type))
        }
        if (!StringUtils.isEmpty(getOption.ipAddress)) {
            criteria.add(Restrictions.eq('ipAddress', getOption.ipAddress))
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
        UserLoginAttemptEntity entity =
                (UserLoginAttemptEntity)currentSession().get(UserLoginAttemptEntity, id)
        currentSession().delete(entity)
    }
}
