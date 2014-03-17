/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserDAO;
import com.junbo.identity.data.entity.user.UserEntity;
import com.junbo.identity.spec.model.options.UserGetOption;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Implementation for User DAO..
 */
@Component
public class UserDAOImpl implements UserDAO {

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public UserEntity save(UserEntity user) {
        currentSession().save(user);

        return get(user.getId());
    }

    @Override
    public UserEntity update(UserEntity user) {
        currentSession().merge(user);
        currentSession().flush();

        return get(user.getId());
    }

    @Override
    public UserEntity get(Long userId) {
        return (UserEntity)currentSession().get(UserEntity.class, userId);
    }

    @Override
    public List<UserEntity> search(UserGetOption getOption) {
        String query = "select * from user_login_attempt where 1 = 1" + " " +
        (StringUtils.isEmpty(getOption.getUserName()) ? "" : ("user_name like \'%" + getOption.getUserName() + "%\'")) +
        (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
        " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());

        List entities = currentSession().createSQLQuery(query).addEntity(UserEntity.class).list();
        return entities;
    }

    @Override
    public void delete(Long userId) {
        UserEntity entity = (UserEntity)currentSession().get(UserEntity.class, userId);
        currentSession().delete(entity);
    }
}
