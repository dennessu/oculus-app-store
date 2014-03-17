/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.identity.data.dao.UserTosDAO
import com.junbo.identity.data.entity.user.UserTosEntity
import com.junbo.identity.spec.model.options.UserTosGetOption
/**
 * Implementation for User Tos Acceptance DAO interface.
 */
class UserTosDAOImpl extends EntityDAOImpl implements UserTosDAO {
    @Override
    UserTosEntity save(UserTosEntity entity) {
        currentSession().save(entity)
        return get(entity.id)
    }

    @Override
    UserTosEntity update(UserTosEntity entity) {
        currentSession().merge(entity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    UserTosEntity get(Long id) {
        return currentSession().get(UserTosEntity, id)
    }

    @Override
    List<UserTosEntity> search(UserTosGetOption getOption) {
        String query = 'select * from user_tos where user_id =  ' + getOption.userId.value +
                (getOption.tosUri == null ? '' : ' and tos_uri = ' + getOption.tosUri) +
                (' order by id limit ' + (getOption.limit == null ? 'ALL' : getOption.limit.toString())) +
                ' offset ' + (getOption.offset == null ? '0' : getOption.offset.toString())

        return currentSession().createSQLQuery(query).addEntity(UserTosEntity).list()
    }

    @Override
    void delete(Long id) {
        UserTosEntity entity = currentSession().get(UserTosEntity, id)
        currentSession().delete(entity)
    }
}
