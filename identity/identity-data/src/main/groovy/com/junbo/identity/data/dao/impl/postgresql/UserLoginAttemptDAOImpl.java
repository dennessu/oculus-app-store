/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserLoginAttemptDAO;
import com.junbo.identity.data.entity.user.UserLoginAttemptEntity;
import com.junbo.identity.spec.model.options.UserLoginAttemptGetOption;
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
    public List<UserLoginAttemptEntity> search(Long userId, UserLoginAttemptGetOption getOption) {
        String query = "select * from user_login_attempt where user_id = " + (getOption.getUserId().getValue()) +
    (StringUtils.isEmpty(getOption.getType()) ? "" : (" and type = " + getOption.getType())) +
    (StringUtils.isEmpty(getOption.getIpAddress()) ? "" : (" and ip_address = \'" + getOption.getIpAddress())) + "\'" +
    (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
    " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());

        List entities = currentSession().createSQLQuery(query).addEntity(UserLoginAttemptEntity.class).list();

        return entities;
    }

    @Override
    public void delete(Long id) {
        UserLoginAttemptEntity entity =
                (UserLoginAttemptEntity)currentSession().get(UserLoginAttemptEntity.class, id);
        currentSession().delete(entity);
    }
}
