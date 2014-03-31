/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserSecurityQuestionAttemptDAO;
import com.junbo.identity.data.entity.user.UserSecurityQuestionAttemptEntity;
import com.junbo.identity.spec.options.list.UserSecurityQuestionAttemptListOptions;
import com.junbo.sharding.annotations.SeedParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by liangfu on 3/25/14.
 */
public class UserSecurityQuestionAttemptDAOImpl extends ShardedDAOBase implements UserSecurityQuestionAttemptDAO {

    @Override
    public UserSecurityQuestionAttemptEntity save(UserSecurityQuestionAttemptEntity entity) {
        currentSession().save(entity);

        return get(entity.getId());
    }

    @Override
    public UserSecurityQuestionAttemptEntity update(UserSecurityQuestionAttemptEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public UserSecurityQuestionAttemptEntity get(@SeedParam Long id) {
        return (UserSecurityQuestionAttemptEntity)currentSession().get(UserSecurityQuestionAttemptEntity.class, id);
    }

    @Override
    public List<UserSecurityQuestionAttemptEntity> search(@SeedParam Long userId,
                                                          UserSecurityQuestionAttemptListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserSecurityQuestionAttemptEntity.class);
        criteria.add(Restrictions.eq("userId", getOption.getUserId().getValue()));
        if (getOption.getSecurityQuestionId() != null) {
           criteria.add(Restrictions.eq("securityQuestionId", getOption.getSecurityQuestionId().getValue()));
        }
        criteria.addOrder(Order.asc("id"));
        if (getOption.getLimit() != null) {
            criteria.setMaxResults(getOption.getLimit());
        }
        if (getOption.getOffset() != null) {
            criteria.setFirstResult(getOption.getOffset());
        }
        return criteria.list();
    }
}
