/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.identity.data.dao.UserSecurityQuestionDAO
import com.junbo.identity.data.entity.user.UserSecurityQuestionEntity
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOptions
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
/**
 * Created by liangfu on 3/17/14.
 */
@CompileStatic
class UserSecurityQuestionDAOImpl extends ShardedDAOBase implements UserSecurityQuestionDAO {
    @Override
    UserSecurityQuestionEntity save(UserSecurityQuestionEntity entity) {
        currentSession().save(entity)

        return get(entity.id)
    }

    @Override
    UserSecurityQuestionEntity update(UserSecurityQuestionEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    UserSecurityQuestionEntity get(Long id) {
        return (UserSecurityQuestionEntity)currentSession().get(UserSecurityQuestionEntity, id)
    }

    @Override
    List<UserSecurityQuestionEntity> search(Long userId, UserSecurityQuestionListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserSecurityQuestionEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (getOption.securityQuestionId) {
            criteria.add(Restrictions.eq('securityQuestionId', getOption.securityQuestionId.value))
        }
        if (getOption.active != null) {
            criteria.add(Restrictions.eq('active', getOption.active))
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
        UserSecurityQuestionEntity entity = (UserSecurityQuestionEntity)currentSession().
                get(UserSecurityQuestionEntity, id)
        currentSession().delete(entity)
    }
}
