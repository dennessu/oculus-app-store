/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserNameDAO;
import com.junbo.identity.data.entity.user.UserNameEntity;
import com.junbo.sharding.annotations.SeedParam;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * Created by liangfu on 3/18/14.
 */
public class UserNameDAOImpl extends ShardedDAOBase implements UserNameDAO {
    @Override
    public UserNameEntity get(@SeedParam Long id) {
        return (UserNameEntity)currentSession().get(UserNameEntity.class, id);
    }

    @Override
    public UserNameEntity save(UserNameEntity entity) {
        currentSession().save(entity);
        return get(entity.getId());
    }

    @Override
    public UserNameEntity update(UserNameEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public void delete(@SeedParam Long id) {
        UserNameEntity entity = (UserNameEntity)currentSession().get(UserNameEntity.class, id);
        currentSession().delete(entity);
    }

    @Override
    public UserNameEntity findByUserId(@SeedParam Long userId) {
        Criteria criteria = currentSession().createCriteria(UserNameEntity.class);
        criteria.add(Restrictions.eq("userId", userId));

        return criteria.list() == null ? null : (UserNameEntity)criteria.list().get(0);
    }
}
