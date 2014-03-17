/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.common.id.UserLoginAttemptId;
import com.junbo.identity.data.dao.UserLoginAttemptDAO;
import com.junbo.identity.data.entity.user.UserLoginAttemptEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.spec.model.options.UserLoginAttemptGetOption;
import com.junbo.identity.spec.model.users.LoginAttempt;
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
public class UserLoginAttemptDAOImpl implements UserLoginAttemptDAO {
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Autowired
    private ModelMapper modelMapper;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public LoginAttempt save(LoginAttempt entity) {
        UserLoginAttemptEntity userLoginAttemptEntity = modelMapper.toUserLoginAttempt(entity, new MappingContext());
        currentSession().save(userLoginAttemptEntity);

        return get(entity.getId());
    }

    @Override
    public LoginAttempt update(LoginAttempt entity) {
        UserLoginAttemptEntity userLoginAttemptEntity = modelMapper.toUserLoginAttempt(entity, new MappingContext());
        currentSession().merge(userLoginAttemptEntity);

        return get(entity.getId());
    }

    @Override
    public LoginAttempt get(UserLoginAttemptId id) {
        return modelMapper.toUserLoginAttempt((UserLoginAttemptEntity)currentSession().
                get(UserLoginAttemptEntity.class, id.getValue()), new MappingContext());
    }

    @Override
    public List<LoginAttempt> search(UserLoginAttemptGetOption getOption) {
        String query = "select * from user_login_attempt where user_id = " + (getOption.getUserId().getValue()) +
            (StringUtils.isEmpty(getOption.getType()) ? "" : (" and type = " + getOption.getType())) +
            (StringUtils.isEmpty(getOption.getIpAddress()) ? "" : (" and ip_address = " + getOption.getIpAddress())) +
            (" order by id limit " + (getOption.getLimit() == null ? "ALL" : getOption.getLimit().toString())) +
            " offset " + (getOption.getOffset() == null ? "0" : getOption.getOffset().toString());

        List entities = sessionFactory.getCurrentSession().createSQLQuery(query)
                .addEntity(UserLoginAttemptEntity.class).list();

        List<LoginAttempt> results = new ArrayList<LoginAttempt>();
        for(int i =0 ; i< entities.size(); i++) {
            results.add(modelMapper.toUserLoginAttempt((UserLoginAttemptEntity)entities.get(i), new MappingContext()));
        }
        return results;
    }

    @Override
    public void delete(UserLoginAttemptId id) {
        UserLoginAttemptEntity entity =
                (UserLoginAttemptEntity)currentSession().get(UserLoginAttemptEntity.class, id.getValue());
        currentSession().delete(entity);
    }
}
