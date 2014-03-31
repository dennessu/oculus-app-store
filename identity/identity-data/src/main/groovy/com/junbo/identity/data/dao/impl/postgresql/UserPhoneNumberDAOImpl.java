/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserPhoneNumberDAO;
import com.junbo.identity.data.entity.user.UserPhoneNumberEntity;
import com.junbo.identity.spec.options.list.UserPhoneNumberListOptions;
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
public class UserPhoneNumberDAOImpl extends ShardedDAOBase implements UserPhoneNumberDAO {
    @Override
    public UserPhoneNumberEntity save(UserPhoneNumberEntity entity) {
        currentSession().save(entity);

        return get(entity.getId());
    }

    @Override
    public UserPhoneNumberEntity update(UserPhoneNumberEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public UserPhoneNumberEntity get(Long id) {
        return (UserPhoneNumberEntity)currentSession().get(UserPhoneNumberEntity.class, id);
    }

    @Override
    public List<UserPhoneNumberEntity> search(Long userId, UserPhoneNumberListOptions getOption) {
        Criteria criteria = currentSession().createCriteria(UserPhoneNumberEntity.class);
        criteria.add(Restrictions.eq("userId", getOption.getUserId().getValue()));
        if (!StringUtils.isEmpty(getOption.getType())) {
            criteria.add(Restrictions.eq("type", getOption.getType()));
        }
        if (!StringUtils.isEmpty(getOption.getValue())) {
            criteria.add(Restrictions.eq("value", getOption.getValue()));
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
        UserPhoneNumberEntity entity =
                (UserPhoneNumberEntity)currentSession().get(UserPhoneNumberEntity.class, id);
        currentSession().delete(entity);
    }
}
