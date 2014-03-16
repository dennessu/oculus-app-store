/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.dao.impl.postgresql
import com.junbo.common.id.UserAuthenticatorId
import com.junbo.identity.data.dao.UserAuthenticatorDAO
import com.junbo.identity.data.entity.user.UserAuthenticatorEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.spec.model.options.UserAuthenticatorGetOption
import com.junbo.identity.spec.model.users.UserAuthenticator
import com.junbo.oom.core.MappingContext
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

    @Autowired
    private ModelMapper modelMapper

    @Override
    UserAuthenticator save(UserAuthenticator entity) {
        UserAuthenticatorEntity userAuthenticatorEntity = modelMapper.toUserAuthenticator(entity, new MappingContext())
        sessionFactory.currentSession.save(userAuthenticatorEntity)

        return get(userAuthenticatorEntity.id)
    }

    @Override
    UserAuthenticator update(UserAuthenticator entity) {
        UserAuthenticatorEntity userFederationEntity = modelMapper.toUserAuthenticator(entity, new MappingContext())
        sessionFactory.currentSession.evict(
                sessionFactory.currentSession.get(UserAuthenticatorEntity, entity.id.value)
        )
        sessionFactory.currentSession.save(userFederationEntity)
        sessionFactory.currentSession.flush()

        return get(userFederationEntity.id)
    }

    @Override
    UserAuthenticator get(UserAuthenticatorId id) {
        return modelMapper.toUserAuthenticator(sessionFactory.currentSession.
                get(UserAuthenticatorEntity, id.value), new MappingContext())
    }

    @Override
    List<UserAuthenticator> search(UserAuthenticatorGetOption getOption) {
        def result = []
        String query = 'select * from user_authenticator where user_id =  ' + getOption.userId.value +
                (getOption.type == null ? '' : ' and type = ' + getOption.type) +
                (getOption.value == null ? '' : ' and value like %' + getOption.value + '%') +
                (' order by id limit ' + (getOption.limit == null ? 'ALL' : getOption.limit.toString())) +
                ' offset ' + (getOption.offset == null ? '0' : getOption.offset.toString())

        def entities = sessionFactory.currentSession.createSQLQuery(query).addEntity(UserAuthenticatorEntity).list()

        entities.flatten { i ->
            result.add(modelMapper.toUserAuthenticator(i, new MappingContext()))
        }
        return result
    }

    @Override
    void delete(UserAuthenticatorId id) {
        UserAuthenticatorEntity entity = sessionFactory.currentSession.get(UserAuthenticatorEntity, id.value)
        sessionFactory.currentSession.delete(entity)
    }
}
