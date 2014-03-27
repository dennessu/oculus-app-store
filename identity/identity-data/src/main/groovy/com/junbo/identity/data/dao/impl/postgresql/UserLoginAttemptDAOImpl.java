/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserLoginAttemptDAO;
import com.junbo.identity.data.entity.user.UserLoginAttemptEntity;
import com.junbo.identity.spec.options.list.LoginAttemptListOptions;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserLoginAttemptDAOImpl extends ShardedDAOBase implements UserLoginAttemptDAO {
    @Override
    public UserLoginAttemptEntity save(UserLoginAttemptEntity entity) {
        currentSession().save(entity);

        return get(entity.getId());
    }

    @Override
    public UserLoginAttemptEntity update(UserLoginAttemptEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public UserLoginAttemptEntity get(Long id) {
        return (UserLoginAttemptEntity)currentSession().get(UserLoginAttemptEntity.class, id);
    }

    @Override
    public List<UserLoginAttemptEntity> search(Long userId, LoginAttemptListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserLoginAttemptEntity.class);
        criteria.add(Restrictions.eq("userId", getOption.getUserId().getValue()));
        if (!StringUtils.isEmpty(getOption.getType())) {
            criteria.add(Restrictions.eq("type", getOption.getType()));
        }
        if (!StringUtils.isEmpty(getOption.getIpAddress())) {
            criteria.add(Restrictions.eq("ipAddress", getOption.getIpAddress()));
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

    @Override
    public void delete(Long id) {
        UserLoginAttemptEntity entity =
                (UserLoginAttemptEntity)currentSession().get(UserLoginAttemptEntity.class, id);
        currentSession().delete(entity);
    }
}
