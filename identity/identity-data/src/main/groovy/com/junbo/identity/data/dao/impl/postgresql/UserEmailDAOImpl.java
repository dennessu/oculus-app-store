/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.common.id.UserEmailId;
import com.junbo.identity.data.dao.UserEmailDAO;
import com.junbo.identity.data.entity.user.UserEmailEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.spec.model.options.UserEmailGetOption;
import com.junbo.identity.spec.model.users.UserEmail;
import com.junbo.oom.core.MappingContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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
    public void delete(UserEmailId id) {
        UserEmailEntity entity = (UserEmailEntity)currentSession().get(UserEmailEntity.class, id.getValue());
        currentSession().delete(entity);
    }

    @Override
    public List<UserEmail> search(UserEmailGetOption getOption) {
        String query = "select * from user_email where user_id = " + (getOption.getUserId().getValue()) +
            (StringUtils.isEmpty(getOption.getType()) ? "" : (" and type = " + getOption.getType())) +
            (StringUtils.isEmpty(getOption.getValue()) ? "" : (" and value like \'%") + getOption.getValue() + "%\'") +
            (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
            " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());

        List entities = sessionFactory.getCurrentSession().createSQLQuery(query)
                .addEntity(UserEmailEntity.class).list();

        List<UserEmail> results = new ArrayList<UserEmail>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserEmail((UserEmailEntity)entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public UserEmail get(UserEmailId id) {
        return modelMapper.toUserEmail((UserEmailEntity)currentSession().get(UserEmailEntity.class, id.getValue()),
                new MappingContext());
    }

    @Override
    public UserEmail update(UserEmail entity) {
        UserEmailEntity userEmailEntity = modelMapper.toUserEmail(entity, new MappingContext());
        currentSession().merge(userEmailEntity);

        return get(entity.getId());
    }

    @Override
    public UserEmail save(UserEmail entity) {
        UserEmailEntity userEmailEntity = modelMapper.toUserEmail(entity, new MappingContext());
        currentSession().save(userEmailEntity);

        return get(entity.getId());
    }
}
