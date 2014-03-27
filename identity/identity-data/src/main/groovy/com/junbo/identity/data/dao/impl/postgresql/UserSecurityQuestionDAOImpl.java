/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserSecurityQuestionDAO;
import com.junbo.identity.data.entity.user.UserSecurityQuestionEntity;
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOptions;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserSecurityQuestionDAOImpl extends ShardedDAOBase implements UserSecurityQuestionDAO {
    @Override
    public UserSecurityQuestionEntity save(UserSecurityQuestionEntity entity) {
        currentSession().save(entity);

        return get(entity.getId());
    }

    @Override
    public UserSecurityQuestionEntity update(UserSecurityQuestionEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public UserSecurityQuestionEntity get(Long id) {
        return (UserSecurityQuestionEntity)currentSession().get(UserSecurityQuestionEntity.class, id);
    }

    @Override
    public List<UserSecurityQuestionEntity> search(Long userId, UserSecurityQuestionListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserSecurityQuestionEntity.class);
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

    @Override
    public void delete(Long id) {
        UserSecurityQuestionEntity entity = (UserSecurityQuestionEntity)currentSession().
                get(UserSecurityQuestionEntity.class, id);
        currentSession().delete(entity);
    }
}
