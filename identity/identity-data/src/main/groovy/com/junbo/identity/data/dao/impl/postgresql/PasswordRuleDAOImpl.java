/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.PasswordRuleDAO;
import com.junbo.identity.data.entity.password.PasswordRuleEntity;
import com.junbo.sharding.core.hibernate.SessionFactoryWrapper;
import com.junbo.sharding.util.Helper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 2/24/14.
 */
@Component
public class PasswordRuleDAOImpl implements PasswordRuleDAO {
    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    private SessionFactoryWrapper sessionFactoryWrapper;

    public void setSessionFactoryWrapper(SessionFactoryWrapper sessionFactoryWrapper) {
        this.sessionFactoryWrapper = sessionFactoryWrapper;
    }

    private Session currentSession() {
        return sessionFactoryWrapper.resolve(Helper.getCurrentThreadLocalShardId()).getCurrentSession();
    }

    @Override
    public PasswordRuleEntity get(Long id) {
        return (PasswordRuleEntity)currentSession().get(PasswordRuleEntity.class, id);
    }

    @Override
    public PasswordRuleEntity save(PasswordRuleEntity passwordRule) {
        currentSession().persist(passwordRule);

        return get(passwordRule.getId());
    }

    @Override
    public void delete(Long id) {
        PasswordRuleEntity entity = (PasswordRuleEntity)currentSession().get(PasswordRuleEntity.class, id);

        currentSession().delete(entity);
    }

    @Override
    public PasswordRuleEntity update(PasswordRuleEntity passwordRule) {
        currentSession().merge(passwordRule);
        currentSession().flush();

        return get(passwordRule.getId());
    }
}
