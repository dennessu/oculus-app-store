/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql

import com.junbo.identity.data.dao.UserAuthenticatorDAO
import com.junbo.identity.data.entity.user.UserAuthenticatorEntity
import com.junbo.identity.spec.model.options.UserAuthenticatorGetOption
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
/**
 * Implementation for UserAuthenticatorDAO.
 */
class UserAuthenticatorDAOImpl implements UserAuthenticatorDAO {
    @Autowired
    @Qualifier('sessionFactory')
    private SessionFactory sessionFactory

    @Override
    UserAuthenticatorEntity save(UserAuthenticatorEntity entity) {
        sessionFactory.currentSession.save(entity)

        return get(entity.id)
    }

    @Override
    UserAuthenticatorEntity update(UserAuthenticatorEntity entity) {
        sessionFactory.currentSession.merge(entity)
        sessionFactory.currentSession.flush()

        return get(entity.id)
    }

    @Override
    UserAuthenticatorEntity get(Long id) {
        return (UserAuthenticatorEntity)sessionFactory.currentSession.get(UserAuthenticatorEntity, id)
    }

    @Override
    List<UserAuthenticatorEntity> search(UserAuthenticatorGetOption getOption) {
        String query = 'select * from user_authenticator where user_id =  ' + getOption.userId.value +
                (getOption.type == null ? '' : ' and type = ' + getOption.type) +
                (getOption.value == null ? '' : ' and value like \'%' + getOption.value + '%\'') +
                (' order by id limit ' + (getOption.limit == null ? 'ALL' : getOption.limit.toString())) +
                ' offset ' + (getOption.offset == null ? '0' : getOption.offset.toString())

        return sessionFactory.currentSession.createSQLQuery(query).addEntity(UserAuthenticatorEntity).list()
    }

    @Override
    void delete(Long id) {
        UserAuthenticatorEntity entity = sessionFactory.currentSession.get(UserAuthenticatorEntity, id)
        sessionFactory.currentSession.delete(entity)
    }
}
