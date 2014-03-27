/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserPasswordDAO;
import com.junbo.identity.data.entity.user.UserPasswordEntity;
import com.junbo.identity.spec.options.list.UserPasswordListOptions;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
@Component
public class UserPasswordDAOImpl extends ShardedDAOBase implements UserPasswordDAO {

    @Override
    public UserPasswordEntity save(UserPasswordEntity entity) {
        currentSession().save(entity);

        return get(entity.getId());
    }

    @Override
    public UserPasswordEntity update(UserPasswordEntity entity) {
        currentSession().merge(entity);
        return get(entity.getId());
    }

    @Override
    public UserPasswordEntity get(Long id) {
        return (UserPasswordEntity)currentSession().get(UserPasswordEntity.class, id);
    }

    @Override
    public List<UserPasswordEntity> search(Long userId, UserPasswordListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserPasswordEntity.class);
        criteria.add(Restrictions.eq("userId", getOption.getUserId().getValue()));
        if (getOption.getActive() != null) {
            criteria.add(Restrictions.eq("active", getOption.getActive()));
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
        UserPasswordEntity entity =
                (UserPasswordEntity)currentSession().get(UserPasswordEntity.class, id);
        currentSession().delete(entity);
    }
}
