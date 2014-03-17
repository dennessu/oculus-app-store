/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserPinDAO;
import com.junbo.identity.data.entity.user.UserPINEntity;
import com.junbo.identity.spec.model.options.UserPinGetOption;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
@Component
public class UserPinDAOImpl extends EntityDAOImpl implements UserPinDAO {
    @Override
    public UserPINEntity save(UserPINEntity entity) {
        currentSession().save(entity);
        return get(entity.getId());
    }

    @Override
    public UserPINEntity update(UserPINEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public UserPINEntity get(Long id) {
        return (UserPINEntity)currentSession().get(UserPINEntity.class, id);
    }

    @Override
    public List<UserPINEntity> search(UserPinGetOption getOption) {
        String query = "select * from user_pin where user_id = " + (getOption.getUserId().getValue()) +
                (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
                " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());
        List entities = currentSession().createSQLQuery(query)
                .addEntity(UserPINEntity.class).list();

        return entities;
    }

    @Override
    public void delete(Long id) {
        UserPINEntity entity = (UserPINEntity)currentSession().get(UserPINEntity.class, id);
        currentSession().delete(entity);
    }
}
