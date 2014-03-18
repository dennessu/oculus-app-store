/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.identity.data.dao.UserAuthenticatorDAO
import com.junbo.identity.data.entity.user.UserAuthenticatorEntity
import com.junbo.identity.spec.model.options.UserAuthenticatorGetOption
/**
 * Implementation for UserAuthenticatorDAO.
 */
class UserAuthenticatorDAOImpl extends EntityDAOImpl implements UserAuthenticatorDAO {
    @Override
    UserAuthenticatorEntity save(UserAuthenticatorEntity entity) {
        currentSession().save(entity)

        return get(entity.id)
    }

    @Override
    UserAuthenticatorEntity update(UserAuthenticatorEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    UserAuthenticatorEntity get(Long id) {
        return (UserAuthenticatorEntity)currentSession().get(UserAuthenticatorEntity, id)
    }

    @Override
    List<UserAuthenticatorEntity> search(Long userId, UserAuthenticatorGetOption getOption) {
        String query = 'select * from user_authenticator where user_id =  ' + getOption.userId.value +
                (getOption.type == null ? '' : ' and type = ' + getOption.type) +
                (getOption.value == null ? '' : ' and value like \'%' + getOption.value + '%\'') +
                (' order by id limit ' + (getOption.limit == null ? 'ALL' : getOption.limit.toString())) +
                ' offset ' + (getOption.offset == null ? '0' : getOption.offset.toString())

        return currentSession().createSQLQuery(query).addEntity(UserAuthenticatorEntity).list()
    }

    @Override
    void delete(Long id) {
        UserAuthenticatorEntity entity = currentSession().get(UserAuthenticatorEntity, id)
        currentSession().delete(entity)
    }
}
