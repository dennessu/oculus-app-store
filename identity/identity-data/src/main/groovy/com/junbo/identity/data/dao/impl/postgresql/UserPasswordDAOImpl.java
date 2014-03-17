/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.common.id.UserPasswordId;
import com.junbo.identity.data.dao.UserPasswordDAO;
import com.junbo.identity.data.entity.user.UserPasswordEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.spec.model.options.UserPasswordGetOption;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.oom.core.MappingContext;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 3/16/14.
 */
@Component
public class UserPasswordDAOImpl implements UserPasswordDAO {
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserPassword save(UserPassword entity) {
        UserPasswordEntity userPasswordEntity = modelMapper.toUserPassword(entity, new MappingContext());
        sessionFactory.getCurrentSession().save(userPasswordEntity);

        return get(entity.getId());
    }

    @Override
    public UserPassword update(UserPassword entity) {
        UserPasswordEntity userPasswordEntity = modelMapper.toUserPassword(entity, new MappingContext());
        sessionFactory.getCurrentSession().merge(userPasswordEntity);
        return get(entity.getId());
    }

    @Override
    public UserPassword get(UserPasswordId id) {
        return modelMapper.toUserPassword((UserPasswordEntity)sessionFactory.getCurrentSession()
                .get(UserPasswordEntity.class, id.getValue()),
                new MappingContext());
    }

    @Override
    public List<UserPassword> search(UserPasswordGetOption getOption) {

        String query = "select * from user_password where user_id = " + (getOption.getUserId().getValue()) +
                (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
                " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());
        List entities = sessionFactory.getCurrentSession().createSQLQuery(query)
                .addEntity(UserPasswordEntity.class).list();

        List<UserPassword> results = new ArrayList<UserPassword>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserPassword((UserPasswordEntity)entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserPasswordId id) {
        UserPasswordEntity entity =
                (UserPasswordEntity)sessionFactory.getCurrentSession().get(UserPasswordEntity.class, id.getValue());
        sessionFactory.getCurrentSession().delete(entity);
    }
}
