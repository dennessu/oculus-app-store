/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.PasswordDAO;
import com.junbo.identity.data.entity.password.PasswordRuleEntity;
import com.junbo.identity.data.mapper.ModelMapper;
import com.junbo.identity.data.util.Constants;
import com.junbo.identity.spec.model.password.PasswordRule;
import com.junbo.oom.core.MappingContext;
import com.junbo.sharding.core.hibernate.SessionFactoryWrapper;
import com.junbo.sharding.util.Helper;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by liangfu on 2/24/14.
 */
@Component
public class PasswordDAOImpl implements PasswordDAO {

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
    public PasswordRule get(Long id) {
        PasswordRuleEntity entity = (PasswordRuleEntity)currentSession().get(PasswordRuleEntity.class, id);

        return modelMapper.toPasswordRule(entity, new MappingContext());
    }

    @Override
    public PasswordRule save(PasswordRule passwordRule) {
        PasswordRuleEntity entity = modelMapper.toPasswordRule(passwordRule, new MappingContext());
        entity.setCreatedBy(Constants.DEFAULT_CLIENT_ID);
        entity.setCreatedTime(new Date());
        currentSession().persist(entity);

        return get(entity.getId());
    }

    @Override
    public void delete(Long id) {
        PasswordRuleEntity entity = (PasswordRuleEntity)currentSession().get(PasswordRuleEntity.class, id);

        currentSession().delete(entity);
    }

    @Override
    public PasswordRule update(PasswordRule passwordRule) {
        PasswordRuleEntity entity = modelMapper.toPasswordRule(passwordRule, new MappingContext());
        PasswordRuleEntity entityInDB = (PasswordRuleEntity)currentSession().get(PasswordRuleEntity.class,
                passwordRule.getId().getValue());
        currentSession().evict(entityInDB);
        entity.setCreatedBy(entityInDB.getCreatedBy());
        entity.setCreatedTime(entityInDB.getCreatedTime());
        entity.setUpdatedBy(Constants.DEFAULT_CLIENT_ID);
        entity.setUpdatedTime(new Date());
        currentSession().update(entity);
        currentSession().flush();

        return get(entity.getId());
    }
}
