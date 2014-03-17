/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.common.id.UserPINId;
import com.junbo.identity.data.dao.UserPINDAO;
import com.junbo.identity.data.entity.user.UserPINEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.spec.model.options.UserPinGetOption;
import com.junbo.identity.spec.model.users.UserPIN;
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
public class UserPINDAOImpl implements UserPINDAO {
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserPIN save(UserPIN entity) {
        UserPINEntity userPINEntity = modelMapper.toUserPIN(entity, new MappingContext());
        sessionFactory.getCurrentSession().save(userPINEntity);
        return get(entity.getId());
    }

    @Override
    public UserPIN update(UserPIN entity) {
        UserPINEntity userPINEntity = modelMapper.toUserPIN(entity, new MappingContext());
        sessionFactory.getCurrentSession().merge(userPINEntity);

        return get(entity.getId());
    }

    @Override
    public UserPIN get(UserPINId id) {
        return modelMapper.toUserPIN((UserPINEntity) sessionFactory.getCurrentSession()
                .get(UserPINEntity.class, id.getValue()), new MappingContext());
    }

    @Override
    public List<UserPIN> search(UserPinGetOption getOption) {
        String query = "select * from user_pin where user_id = " + (getOption.getUserId().getValue()) +
                (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
                " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());
        List entities = sessionFactory.getCurrentSession().createSQLQuery(query)
                .addEntity(UserPINEntity.class).list();

        List<UserPIN> results = new ArrayList<UserPIN>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserPIN((UserPINEntity) entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserPINId id) {
        UserPINEntity entity =
                (UserPINEntity)sessionFactory.getCurrentSession().get(UserPINEntity.class, id.getValue());
        sessionFactory.getCurrentSession().delete(entity);
    }
}
