/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserPhoneNumberDAO;
import com.junbo.identity.data.entity.user.UserPhoneNumberEntity;
import com.junbo.identity.spec.options.list.UserPhoneNumberListOption;
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
    public List<UserPhoneNumberEntity> search(Long userId, UserPhoneNumberListOption getOption) {
        String query = "select * from user_phone_number where user_id = " + (getOption.getUserId().getValue()) +
                (StringUtils.isEmpty(getOption.getType()) ? "" : (" and type = \'" + getOption.getType()) + "\'") +
                (StringUtils.isEmpty(getOption.getValue()) ? "" : (" and value = \'" + getOption.getValue()) + "\'") +
                (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
                " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());

        List entities = currentSession().createSQLQuery(query).addEntity(UserPhoneNumberEntity.class).list();

        return entities;
    }

    @Override
    public void delete(Long id) {
        UserPhoneNumberEntity entity =
                (UserPhoneNumberEntity)currentSession().get(UserPhoneNumberEntity.class, id);
        currentSession().delete(entity);
    }
}
