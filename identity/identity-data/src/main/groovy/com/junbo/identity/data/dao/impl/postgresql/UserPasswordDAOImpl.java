/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserPasswordDAO;
import com.junbo.identity.data.entity.user.UserPasswordEntity;
import com.junbo.identity.spec.model.options.UserPasswordGetOption;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
@Component
public class UserPasswordDAOImpl extends EntityDAOImpl implements UserPasswordDAO {

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
    public List<UserPasswordEntity> search(UserPasswordGetOption getOption) {

        String query = "select * from user_password where user_id = " + (getOption.getUserId().getValue()) +
                (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
                " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());
        List entities = currentSession().createSQLQuery(query)
                .addEntity(UserPasswordEntity.class).list();

        return entities;
    }

    @Override
    public void delete(Long id) {
        UserPasswordEntity entity =
                (UserPasswordEntity)currentSession().get(UserPasswordEntity.class, id);
        currentSession().delete(entity);
    }
}
