/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.TosId
import com.junbo.identity.data.dao.TosDAO
import com.junbo.identity.data.entity.user.TosEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.TosRepository
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
class TosRepositorySqlImpl implements TosRepository {
    // todo:    Liangfu:    In fact here we don't want any db, we just want to use cdn to store tos
    // So we just implement the simplest case here
    private TosDAO tosDAO
    private ModelMapper modelMapper

    @Required
    void setTosDAO(TosDAO tosDAO) {
        this.tosDAO = tosDAO
    }

    @Required
    void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper
    }

    @Override
    Promise<Tos> get(TosId tosId) {
        def entity = tosDAO.get(tosId.value)
        if (entity == null) {
            return Promise.pure(null)
        }
        return Promise.pure(modelMapper.toTos(entity, new MappingContext()))
    }

    @Override
    Promise<Tos> create(Tos tos) {
        def entity = modelMapper.toTos(tos, new MappingContext())

        entity = tosDAO.create(entity)
        return get(new TosId((Long)entity.id))
    }

    @Override
    Promise<Void> delete(TosId tosId) {
        tosDAO.delete(tosId.value)

        return Promise.pure(null)
    }

    @Override
    Promise<Tos> update(Tos model) {
        throw new IllegalStateException('update tos not support')
    }

    @Override
    Promise<List<Tos>> search(TosListOptions options) {
        List<TosEntity> entities = tosDAO.search(options)

        def result = []
        entities.each { TosEntity tosEntity ->
            result.add(modelMapper.toTos(tosEntity, new MappingContext()))
        }

        return Promise.pure(result)
    }
}