/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserEmailDAO;
import com.junbo.identity.data.entity.user.UserEmailEntity;
import com.junbo.identity.spec.options.list.UserEmailListOptions;
import com.junbo.sharding.annotations.SeedParam;
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
public class UserEmailDAOImpl extends ShardedDAOBase implements UserEmailDAO {

    @Override
    public void delete(Long id) {
        UserEmailEntity entity = (UserEmailEntity)currentSession().get(UserEmailEntity.class, id);
        currentSession().delete(entity);
    }

    @Override
    public List<UserEmailEntity> search(@SeedParam Long userId, UserEmailListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserEmailEntity.class);
        criteria.add(Restrictions.eq("userId", getOption.getUserId().getValue()));
        if(!StringUtils.isEmpty(getOption.getType())) {
            criteria.add(Restrictions.eq("type", getOption.getType()));
        }
        if(!StringUtils.isEmpty(getOption.getValue())) {
            criteria.add(Restrictions.eq("value", getOption.getValue()));
        }
        criteria.addOrder(Order.asc("id"));
        if(getOption.getLimit() != null) {
            criteria.setMaxResults(getOption.getLimit());
        }
        if(getOption.getOffset() != null) {
            criteria.setFirstResult(getOption.getOffset());
        }
        return criteria.list();
    }

    @Override
    public UserEmailEntity get(Long id) {
        return (UserEmailEntity)currentSession().get(UserEmailEntity.class, id);
    }

    @Override
    public UserEmailEntity update(UserEmailEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public UserEmailEntity save(UserEmailEntity entity) {
        currentSession().save(entity);

        return get(entity.getId());
    }
}
