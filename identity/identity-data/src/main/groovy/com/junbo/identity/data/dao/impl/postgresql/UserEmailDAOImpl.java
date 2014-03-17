/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.UserEmailDAO;
import com.junbo.identity.data.entity.user.UserEmailEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.spec.model.options.UserEmailGetOption;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserEmailDAOImpl implements UserEmailDAO {
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private ModelMapper modelMapper;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void delete(Long id) {
        UserEmailEntity entity = (UserEmailEntity)currentSession().get(UserEmailEntity.class, id);
        currentSession().delete(entity);
    }

    @Override
    public List<UserEmailEntity> search(UserEmailGetOption getOption) {
        String query = "select * from user_email where user_id = " + (getOption.getUserId().getValue()) +
            (StringUtils.isEmpty(getOption.getType()) ? "" : (" and type = " + getOption.getType())) +
            (StringUtils.isEmpty(getOption.getValue()) ? "" : (" and value like \'%") + getOption.getValue() + "%\'") +
            (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
            " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());

        List entities = sessionFactory.getCurrentSession().createSQLQuery(query)
                .addEntity(UserEmailEntity.class).list();

        return entities;
    }

    @Override
    public UserEmailEntity get(Long id) {
        return (UserEmailEntity)currentSession().get(UserEmailEntity.class, id);
    }

    @Override
    public UserEmailEntity update(UserEmailEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();

        return get(entity.getId());
    }

    @Override
    public UserEmailEntity save(UserEmailEntity entity) {
        currentSession().save(entity);

        return get(entity.getId());
    }
}
