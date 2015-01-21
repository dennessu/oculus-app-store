/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.repo.sql

import com.junbo.common.id.SubledgerEventId
import com.junbo.common.id.SubledgerId
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.SubledgerEventDao
import com.junbo.order.db.entity.SubledgerEventEntity
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.SubledgerEventRepository
import com.junbo.order.db.repo.util.Utils
import com.junbo.order.spec.model.SubledgerEvent
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
/**
 * Created by fzhang on 2015/1/18.
 */
@CompileStatic
@TypeChecked
@Component('sqlSubledgerEventRepository')
class SubledgerEventRepositorySqlImpl implements SubledgerEventRepository {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubledgerEventRepositorySqlImpl)

    @Autowired
    private SubledgerEventDao subledgerEventDao

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Override
    Promise<SubledgerEvent> get(SubledgerEventId id) {
        return Promise.pure(modelMapper.toSubledgerEventModel(subledgerEventDao.read(id.value), new MappingContext()))
    }

    @Override
    Promise<SubledgerEvent> create(SubledgerEvent event) {
        assert (event != null && event.subledger != null)
        LOGGER.info('name=Create_Subledger_Event, event: {},{},{}', event.subledger.value, event.action, event.status)
        def entity = modelMapper.toSubledgerEventEntity(event, new MappingContext())
        entity.eventId = idGenerator.nextId(entity.subledgerId)
        subledgerEventDao.create(entity)
        event.setId(new SubledgerEventId(entity.eventId))
        Utils.fillDateInfo(event, entity)

        return Promise.pure(event)
    }

    @Override
    Promise<SubledgerEvent> update(SubledgerEvent event, SubledgerEvent oldEvent) {
        throw new UnsupportedOperationException('Update not allowed for SubledgerEvent.');
    }

    @Override
    Promise<Void> delete(SubledgerEventId id) {
        throw new UnsupportedOperationException('Delete not allowed for SubledgerEvent.');
    }

    @Override
    Promise<List<SubledgerEvent>> getBySubledgerId(SubledgerId subledgerId) {
        List<SubledgerEvent> events = []
        subledgerEventDao.getSubledgerEventsBySubledgerId(subledgerId.value).each { SubledgerEventEntity entity ->
            SubledgerEvent event = modelMapper.toSubledgerEventModel(entity, new MappingContext())
            events << event
        }
        return Promise.pure(events)
    }
}
