/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.common.id.UserPhoneNumberId;
import com.junbo.identity.data.dao.UserPhoneNumberDAO;
import com.junbo.identity.data.entity.user.UserPhoneNumberEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.spec.model.options.UserPhoneNumberGetOption;
import com.junbo.identity.spec.model.users.UserPhoneNumber;
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
public class UserPhoneNumberDAOImpl implements UserPhoneNumberDAO {
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private ModelMapper modelMapper;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public UserPhoneNumber save(UserPhoneNumber entity) {
        UserPhoneNumberEntity userPhoneNumberEntity = modelMapper.toUserPhoneNumber(entity, new MappingContext());
        currentSession().save(userPhoneNumberEntity);

        return get(entity.getId());
    }

    @Override
    public UserPhoneNumber update(UserPhoneNumber entity) {
        UserPhoneNumberEntity userPhoneNumberEntity = modelMapper.toUserPhoneNumber(entity, new MappingContext());
        currentSession().merge(userPhoneNumberEntity);

        return get(entity.getId());
    }

    @Override
    public UserPhoneNumber get(UserPhoneNumberId id) {
        return modelMapper.toUserPhoneNumber((UserPhoneNumberEntity)currentSession().
                get(UserPhoneNumberEntity.class, id.getValue()), new MappingContext());
    }

    @Override
    public List<UserPhoneNumber> search(UserPhoneNumberGetOption getOption) {
        String query = "select * from user_phone_number where user_id = " + (getOption.getUserId().getValue()) +
                (StringUtils.isEmpty(getOption.getType()) ? "" : (" and type = " + getOption.getType())) +
                (StringUtils.isEmpty(getOption.getValue()) ? "" : (" and value = " + getOption.getValue())) +
                (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
                " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());

        List entities = currentSession().createSQLQuery(query).addEntity(UserPhoneNumberEntity.class).list();

        List<UserPhoneNumber> results = new ArrayList<UserPhoneNumber>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserPhoneNumber((UserPhoneNumberEntity)entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserPhoneNumberId id) {
        UserPhoneNumberEntity entity =
                (UserPhoneNumberEntity)currentSession().get(UserPhoneNumberEntity.class, id.getValue());
        currentSession().delete(entity);
    }
}
