/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl
import com.junbo.common.id.UserAuthenticatorId
import com.junbo.identity.data.dao.UserAuthenticatorDAO
import com.junbo.identity.data.dao.index.UserAuthenticatorReverseIndexDAO
import com.junbo.identity.data.entity.reverselookup.UserAuthenticatorReverseIndexEntity
import com.junbo.identity.data.entity.user.UserAuthenticatorEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserAuthenticatorRepository
import com.junbo.identity.spec.model.users.UserAuthenticator
import com.junbo.identity.spec.options.list.UserAuthenticatorListOptions
import com.junbo.oom.core.MappingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
/**
 * Implementation for UserAuthenticatorDAO.
 */
class UserAuthenticatorRepositoryImpl implements UserAuthenticatorRepository {
    @Autowired
    @Qualifier('userAuthenticatorDAO')
    private UserAuthenticatorDAO authenticatorDAO

    @Autowired
    @Qualifier('identityModelMapperImpl')
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('userAuthenticatorReverseIndexDAO')
    private UserAuthenticatorReverseIndexDAO authenticatorReverseIndexDAO

    @Override
    UserAuthenticator save(UserAuthenticator entity) {
        UserAuthenticatorEntity userAuthenticatorEntity = modelMapper.toUserAuthenticator(entity, new MappingContext())
        authenticatorDAO.save(userAuthenticatorEntity)

        UserAuthenticatorReverseIndexEntity reverseIndexEntity = new UserAuthenticatorReverseIndexEntity()
        reverseIndexEntity.setUserAuthenticatorId(userAuthenticatorEntity.id)
        reverseIndexEntity.setValue(entity.value)
        authenticatorReverseIndexDAO.save(reverseIndexEntity)

        return get(new UserAuthenticatorId(userAuthenticatorEntity.id))
    }

    @Override
    UserAuthenticator update(UserAuthenticator entity) {
        UserAuthenticatorEntity userFederationEntity = modelMapper.toUserAuthenticator(entity, new MappingContext())
        UserAuthenticatorEntity existing = authenticatorDAO.get(entity.id.value)
        if (existing.value != entity.value) {
            authenticatorReverseIndexDAO.delete(existing.value)
            UserAuthenticatorReverseIndexEntity reverseIndexEntity = new UserAuthenticatorReverseIndexEntity()
            reverseIndexEntity.setValue(entity.value)
            reverseIndexEntity.setUserAuthenticatorId(entity.id.value)
            authenticatorReverseIndexDAO.save(reverseIndexEntity)
        }
        authenticatorDAO.update(userFederationEntity)

        return get(entity.id)
    }

    @Override
    UserAuthenticator get(UserAuthenticatorId id) {
        return modelMapper.toUserAuthenticator(authenticatorDAO.get(id.value), new MappingContext())
    }

    @Override
    List<UserAuthenticator> search(UserAuthenticatorListOptions getOption) {
        def result = []
        if (getOption != null && getOption.userId != null) {
            def entities = authenticatorDAO.search(getOption.userId.value, getOption)

            entities.flatten { i ->
                result.add(modelMapper.toUserAuthenticator(i, new MappingContext()))
            }
            return result
        }
        else if (getOption != null && getOption.value != null) {
            result.add(searchByAuthenticatorValue(getOption.value))
            return result
        }
    }

    private UserAuthenticator searchByAuthenticatorValue(String value) {
        def authenticatorReverseEntity = authenticatorReverseIndexDAO.get(value)
        def userAuthenticator = authenticatorDAO.get(authenticatorReverseEntity.userAuthenticatorId)

        return modelMapper.toUserAuthenticator(userAuthenticator, new MappingContext())
    }

    @Override
    void delete(UserAuthenticatorId id) {
        authenticatorDAO.delete(id.value)
        authenticatorReverseIndexDAO.delete(id.value)
    }
}
