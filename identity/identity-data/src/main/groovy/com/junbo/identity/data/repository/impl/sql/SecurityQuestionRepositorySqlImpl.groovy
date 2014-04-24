/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.SecurityQuestionId
import com.junbo.identity.data.dao.SecurityQuestionDAO
import com.junbo.identity.data.entity.domaindata.SecurityQuestionEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.SecurityQuestionRepository
import com.junbo.identity.spec.model.domaindata.SecurityQuestion
import com.junbo.identity.spec.options.list.SecurityQuestionListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 3/16/14.
 */
@CompileStatic
class SecurityQuestionRepositorySqlImpl implements SecurityQuestionRepository {
    private SecurityQuestionDAO securityQuestionDAO
    private ModelMapper modelMapper

    @Required
    void setSecurityQuestionDAO(SecurityQuestionDAO securityQuestionDAO) {
        this.securityQuestionDAO = securityQuestionDAO
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }

    @Override
    Promise<SecurityQuestion> create(SecurityQuestion entity) {
        SecurityQuestionEntity securityQuestionEntity = modelMapper.toSecurityQuestion(entity, new MappingContext())
        securityQuestionDAO.save(securityQuestionEntity)

        return get(new SecurityQuestionId(securityQuestionEntity.id))
    }

    @Override
    Promise<SecurityQuestion> update(SecurityQuestion entity) {
        SecurityQuestionEntity securityQuestionEntity = modelMapper.toSecurityQuestion(entity, new MappingContext())
        securityQuestionDAO.update(securityQuestionEntity)

        return get((SecurityQuestionId)entity.id)
    }

    @Override
    Promise<SecurityQuestion> get(SecurityQuestionId id) {
        return Promise.pure(
                modelMapper.toSecurityQuestion(securityQuestionDAO.get(id.value), new MappingContext()))
    }

    @Override
    Promise<List<SecurityQuestion>> search(SecurityQuestionListOptions listOption) {
        List entities = securityQuestionDAO.search(listOption)

        List<SecurityQuestion> results = new ArrayList<SecurityQuestion>()
        entities.each { SecurityQuestionEntity entity ->
            results.add(modelMapper.toSecurityQuestion(entity, new MappingContext()))
        }
        return Promise.pure(results)
    }

    @Override
    Promise<Void> delete(SecurityQuestionId id) {
        securityQuestionDAO.delete(id.value)

        return Promise.pure(null)
    }
}