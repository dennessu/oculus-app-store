/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.common.id.UserGroupId;
import com.junbo.identity.data.dao.UserGroupDAO;
import com.junbo.identity.data.entity.user.UserGroupEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.spec.model.options.UserGroupGetOption;
import com.junbo.identity.spec.model.users.UserGroup;
import com.junbo.oom.core.MappingContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 3/17/14.
 */
@Component
public class UserGroupDAOImpl implements UserGroupDAO {
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private ModelMapper modelMapper;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public UserGroup save(UserGroup entity) {
        UserGroupEntity userGroupEntity = modelMapper.toUserGroup(entity, new MappingContext());
        currentSession().save(userGroupEntity);

        return get(entity.getId());
    }

    @Override
    public UserGroup update(UserGroup entity) {
        UserGroupEntity userGroupEntity = modelMapper.toUserGroup(entity, new MappingContext());
        currentSession().merge(userGroupEntity);

        return get(entity.getId());
    }

    @Override
    public UserGroup get(UserGroupId id) {
        return modelMapper.toUserGroup((UserGroupEntity)currentSession().get(UserGroupEntity.class, id.getValue()),
                new MappingContext());
    }

    @Override
    public List<UserGroup> search(UserGroupGetOption getOption) {
        String query = "select * from user_group where user_id = " + (getOption.getUserId().getValue()) +
                (getOption.getGroupId() == null ? "" : (" and group_id = " + getOption.getGroupId().getValue())) +
                (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
                " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());

        List entities = sessionFactory.getCurrentSession().createSQLQuery(query)
                .addEntity(UserGroupEntity.class).list();

        List<UserGroup> results = new ArrayList<UserGroup>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserGroup((UserGroupEntity)entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserGroupId id) {
        UserGroupEntity entity = (UserGroupEntity)currentSession().get(UserGroupEntity.class, id.getValue());
        currentSession().delete(entity);
    }
}
