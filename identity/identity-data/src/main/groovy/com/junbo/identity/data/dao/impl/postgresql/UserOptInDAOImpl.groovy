/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.common.id.UserOptinId
import com.junbo.identity.data.dao.UserOptinDAO
import com.junbo.identity.data.entity.user.UserOptinEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.spec.model.options.UserOptinGetOption
import com.junbo.identity.spec.model.users.UserOptin
import com.junbo.oom.core.MappingContext
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
/**
 * Implementation for UserOptinDAO.
 */
class UserOptinDAOImpl implements UserOptinDAO {
    @Autowired
    @Qualifier('sessionFactory')
    private SessionFactory sessionFactory

    @Autowired
    private ModelMapper modelMapper

    private Session currentSession() {
        sessionFactory.currentSession
    }

    @Override
    UserOptin save(UserOptin entity) {
        UserOptinEntity userOptInEntity = modelMapper.toUserOptin(entity, new MappingContext())
        currentSession().save(userOptInEntity)

        return get(userOptInEntity.id)
    }

    @Override
    UserOptin update(UserOptin entity) {
        UserOptinEntity userOptInEntity = modelMapper.toUserOptin(entity, new MappingContext())

        currentSession().merge(userOptInEntity)
        currentSession().flush()

        return get(userOptInEntity.id)
    }

    @Override
    UserOptin get(UserOptinId id) {
        return modelMapper.toUserOptin(currentSession().get(UserOptinEntity, id), new MappingContext())
    }

    @Override
    List<UserOptin> search(UserOptinGetOption getOption) {
        def result = []
        String query = 'select * from user_optin where user_id =  ' + getOption.userId.value +
                (getOption.value == null ? '' : (' and value = ' + getOption.value)) +
                (' order by id limit ' + (getOption.limit == null ? 'ALL' : getOption.limit.toString())) +
                ' offset ' + (getOption.offset == null ? '0' : getOption.offset.toString())

        def entities = sessionFactory.currentSession.createSQLQuery(query).addEntity(UserOptinEntity).list()

        entities.flatten { i ->
            result.add(modelMapper.toUserOptin(i, new MappingContext()))
        }
        return result
    }

    @Override
    void delete(UserOptinId id) {
        UserOptinEntity entity = currentSession().get(UserOptinEntity, id.value)
        currentSession().delete(entity)
    }
}
