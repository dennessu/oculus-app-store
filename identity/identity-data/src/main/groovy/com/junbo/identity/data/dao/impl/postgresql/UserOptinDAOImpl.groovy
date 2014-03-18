/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.identity.data.dao.UserOptinDAO
import com.junbo.identity.data.entity.user.UserOptinEntity
import com.junbo.identity.spec.model.options.UserOptinGetOption
/**
 * Implementation for UserOptinDAO.
 */
class UserOptinDAOImpl extends EntityDAOImpl implements UserOptinDAO {

    @Override
    UserOptinEntity save(UserOptinEntity entity) {
        currentSession().save(entity)

        return get(entity.id)
    }

    @Override
    UserOptinEntity update(UserOptinEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    UserOptinEntity get(Long id) {
        return currentSession().get(UserOptinEntity, id)
    }

    @Override
    List<UserOptinEntity> search(Long userId, UserOptinGetOption getOption) {
        String query = 'select * from user_optin where user_id =  ' + getOption.userId.value +
                (getOption.value == null ? '' : (' and value = ' + getOption.value)) +
                (' order by id limit ' + (getOption.limit == null ? 'ALL' : getOption.limit.toString())) +
                ' offset ' + (getOption.offset == null ? '0' : getOption.offset.toString())

        def entities = currentSession().createSQLQuery(query).addEntity(UserOptinEntity).list()

        return entities
    }

    @Override
    void delete(Long id) {
        UserOptinEntity entity = currentSession().get(UserOptinEntity, id)
        currentSession().delete(entity)
    }
}
