/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.common.id.UserId;
import com.junbo.identity.data.dao.UserDAO;
import com.junbo.identity.data.entity.user.UserEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.spec.model.options.UserGetOption;
import com.junbo.identity.spec.model.users.User;
import com.junbo.oom.core.MappingContext;
import com.junbo.sharding.core.hibernate.SessionFactoryWrapper;
import com.junbo.sharding.util.Helper;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation for User DAO..
 */
@Component
public class UserDAOImpl implements UserDAO {
    @Autowired
    private ModelMapper modelMapper;

    private SessionFactoryWrapper sessionFactoryWrapper;

    public void setSessionFactoryWrapper(SessionFactoryWrapper sessionFactoryWrapper) {
        this.sessionFactoryWrapper = sessionFactoryWrapper;
    }

    private Session currentSession() {
        return sessionFactoryWrapper.resolve(Helper.getCurrentThreadLocalShardId()).getCurrentSession();
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext());
        currentSession().save(userEntity);

        return get(user.getId());
    }

    @Override
    public User update(User user) {
        UserEntity userEntity = modelMapper.toUser(user, new MappingContext());
        currentSession().merge(userEntity);

        return get(user.getId());
    }

    @Override
    public User get(UserId userId) {
        return modelMapper.toUser((UserEntity)currentSession().get(User.class, userId.getValue()),
                new MappingContext());
    }

    @Override
    public List<User> search(UserGetOption getOption) {
        String query = "select * from user_login_attempt where 1 = 1" + " " +
        (StringUtils.isEmpty(getOption.getUserName()) ? "" : ("user_name like \'%" + getOption.getUserName() + "%\'")) +
        (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
        " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());

        List entities = currentSession().createSQLQuery(query).addEntity(UserEntity.class).list();

        List<User> results = new ArrayList<User>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUser((UserEntity)entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserId userId) {
        UserEntity entity = (UserEntity)currentSession().get(UserEntity.class, userId.getValue());
        currentSession().delete(entity);
    }
}
