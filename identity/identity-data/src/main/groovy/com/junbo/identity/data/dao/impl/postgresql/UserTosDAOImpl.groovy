/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.common.id.UserTosId
import com.junbo.identity.data.dao.UserTosDAO
import com.junbo.identity.data.entity.user.UserTosEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.spec.model.options.UserTosGetOption
import com.junbo.identity.spec.model.users.UserTos
import com.junbo.oom.core.MappingContext
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
/**
 * Implementation for User Tos Acceptance DAO interface.
 */
class UserTosDAOImpl implements UserTosDAO {
    @Autowired
    @Qualifier('sessionFactory')
    private SessionFactory sessionFactory
    @Autowired
    private ModelMapper modelMapper

    private Session currentSession() {
        sessionFactory.currentSession
    }

    @Override
    UserTos save(UserTos entity) {
        UserTosEntity userTosAcceptanceEntity = modelMapper.toUserTos(entity, new MappingContext())
        currentSession().save(userTosAcceptanceEntity)
        return get(entity.id)
    }

    @Override
    UserTos update(UserTos entity) {
        UserTosEntity userTosAcceptanceEntity = modelMapper.toUserTos(entity, new MappingContext())

        currentSession().merge(userTosAcceptanceEntity)
        currentSession().flush()

        return get(entity.id)
    }

    @Override
    UserTos get(UserTosId id) {
        return modelMapper.toUserTos(currentSession().get(UserTosEntity, id.value), new MappingContext())
    }

    @Override
    List<UserTos> search(UserTosGetOption getOption) {
        def result = []
        String query = 'select * from user_tos where user_id =  ' + getOption.userId.value +
                (getOption.tosUri == null ? '' : ' and tos_uri = ' + getOption.tosUri) +
                (' order by id limit ' + (getOption.limit == null ? 'ALL' : getOption.limit.toString())) +
                ' offset ' + (getOption.offset == null ? '0' : getOption.offset.toString())

        def entities = currentSession().createSQLQuery(query).addEntity(UserTosEntity).list()

        entities.flatten { i ->
            result.add(modelMapper.toUserTos(i, new MappingContext()))
        }
        return result
    }

    @Override
    void delete(UserTosId id) {
        UserTosEntity entity = currentSession().get(UserTosEntity, id.value)
        currentSession().delete(entity)
    }
}
