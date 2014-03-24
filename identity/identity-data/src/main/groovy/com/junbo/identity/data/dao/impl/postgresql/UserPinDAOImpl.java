/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserPinDAO;
import com.junbo.identity.data.entity.user.UserPinEntity;
import com.junbo.identity.spec.options.list.UserPinListOption;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
@Component
public class UserPinDAOImpl extends ShardedDAOBase implements UserPinDAO {
    @Override
    public UserPinEntity save(UserPinEntity entity) {
        currentSession().save(entity);
        return get(entity.getId());
    }

    @Override
    public UserPinEntity update(UserPinEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public UserPinEntity get(Long id) {
        return (UserPinEntity)currentSession().get(UserPinEntity.class, id);
    }

    @Override
    public List<UserPinEntity> search(Long userId, UserPinListOption getOption) {
        String query = "select * from user_pin where user_id = " + (getOption.getUserId().getValue()) +
                (getOption.getActive() == null ? "" : " and active = " + getOption.getActive()) +
                (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
                " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());
        List entities = currentSession().createSQLQuery(query)
                .addEntity(UserPinEntity.class).list();

        return entities;
    }

    @Override
    public void delete(Long id) {
        UserPinEntity entity = (UserPinEntity)currentSession().get(UserPinEntity.class, id);
        currentSession().delete(entity);
    }
}
