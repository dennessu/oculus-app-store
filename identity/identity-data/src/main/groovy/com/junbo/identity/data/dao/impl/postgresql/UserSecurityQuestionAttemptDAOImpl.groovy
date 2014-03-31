/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.identity.data.dao.UserSecurityQuestionAttemptDAO
import com.junbo.identity.data.entity.user.UserSecurityQuestionAttemptEntity
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions
import com.junbo.sharding.annotations.SeedParam
import groovy.transform.CompileStatic
import org.hibernate.Criteria
import org.hibernate.criterion.Order
import org.hibernate.criterion.Restrictions
/**
 * Created by liangfu on 3/25/14.
 */
@CompileStatic
class UserSecurityQuestionAttemptDAOImpl extends ShardedDAOBase implements UserSecurityQuestionAttemptDAO {

    @Override
    UserSecurityQuestionAttemptEntity save(UserSecurityQuestionAttemptEntity entity) {
        currentSession().save(entity)

        return get(entity.id)
    }

    @Override
    UserSecurityQuestionAttemptEntity update(UserSecurityQuestionAttemptEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    UserSecurityQuestionAttemptEntity get(@SeedParam Long id) {
        return (UserSecurityQuestionAttemptEntity)currentSession().get(UserSecurityQuestionAttemptEntity, id)
    }

    @Override
    List<UserSecurityQuestionAttemptEntity> search(@SeedParam Long userId,
                                                          UserSecurityQuestionAttemptListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserSecurityQuestionAttemptEntity)
        criteria.add(Restrictions.eq('userId', getOption.userId.value))
        if (getOption.securityQuestionId != null) {
           criteria.add(Restrictions.eq('securityQuestionId', getOption.securityQuestionId.value))
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
