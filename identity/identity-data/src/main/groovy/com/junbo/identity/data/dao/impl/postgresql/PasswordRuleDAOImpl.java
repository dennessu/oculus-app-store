/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql;

import com.junbo.identity.data.dao.PasswordRuleDAO;
import com.junbo.identity.data.entity.password.PasswordRuleEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

/**
 * Created by liangfu on 2/24/14.
 */
@Component
public class PasswordRuleDAOImpl implements PasswordRuleDAO {
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
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
