/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserGroupDAO;
import com.junbo.identity.data.entity.user.UserGroupEntity;
import com.junbo.identity.spec.options.list.UserGroupListOptions;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserGroupDAOImpl extends ShardedDAOBase implements UserGroupDAO {
    @Override
    public UserGroupEntity save(UserGroupEntity entity) {
        currentSession().save(entity);

        return get(entity.getId());
    }

    @Override
    public UserGroupEntity update(UserGroupEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public UserGroupEntity get(Long id) {
        return (UserGroupEntity)currentSession().get(UserGroupEntity.class, id);
    }

    @Override
    public List<UserGroupEntity> search(Long userId, UserGroupListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserGroupEntity.class);
        criteria.add(Restrictions.eq("userId", getOption.getUserId().getValue()));
        if (getOption.getGroupId() != null) {
            criteria.add(Restrictions.eq("groupId", getOption.getGroupId().getValue()));
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
        UserGroupEntity entity = (UserGroupEntity)currentSession().get(UserGroupEntity.class, id);
        currentSession().delete(entity);
    }
}
