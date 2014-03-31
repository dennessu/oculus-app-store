/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.identity.data.dao.UserEmailDAO
import com.junbo.identity.data.entity.user.UserEmailEntity
import com.junbo.identity.spec.options.list.UserEmailListOptions
import com.junbo.sharding.annotations.SeedParam
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
import org.springframework.util.StringUtils
/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class UserEmailDAOImpl extends ShardedDAOBase implements UserEmailDAO {

    @Override
    void delete(Long id) {
        UserEmailEntity entity = (UserEmailEntity)currentSession().get(UserEmailEntity, id)
        currentSession().delete(entity)
    }

    @Override
    List<UserEmailEntity> search(@SeedParam Long userId, UserEmailListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserEmailEntity)
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
    UserEmailEntity get(Long id) {
        return (UserEmailEntity)currentSession().get(UserEmailEntity, id)
    }

    @Override
    UserEmailEntity update(UserEmailEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    UserEmailEntity save(UserEmailEntity entity) {
        currentSession().save(entity)

        return get(entity.id)
    }
}
