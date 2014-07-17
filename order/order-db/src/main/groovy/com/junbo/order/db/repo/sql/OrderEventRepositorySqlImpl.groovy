/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.sql

import com.junbo.common.id.OrderEventId
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.OrderEventDao
import com.junbo.order.db.entity.OrderEventEntity
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.OrderEventRepository
import com.junbo.order.db.repo.util.Utils
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.PageParam
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
@Component('sqlOrderEventRepository')
class OrderEventRepositorySqlImpl implements OrderEventRepository {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderEventRepositorySqlImpl)

    @Autowired
    private OrderEventDao orderEventDao

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    private static final int orderEventsNumThreshHold = 200

    @Override
    Promise<OrderEvent> get(OrderEventId id) {
        return Promise.pure(modelMapper.toOrderEventModel(orderEventDao.read(id.value), new MappingContext()))
    }

    @Override
    Promise<OrderEvent> create(OrderEvent event) {
        assert (event != null && event.order != null)
        LOGGER.info('name=Create_Order_Event, event: {},{},{},{},{}',
                event.flowName, event.order.value, event.action, event.status, event.trackingUuid)
        def entity = modelMapper.toOrderEventEntity(event, new MappingContext())
        entity.eventId = idGenerator.nextId(entity.orderId)
        orderEventDao.create(entity)
        event.setId(new OrderEventId(entity.eventId))
        Utils.fillDateInfo(event, entity)

        return Promise.pure(event)
    }

    @Override
    Promise<OrderEvent> update(OrderEvent event, OrderEvent oldEvent) {
        assert event != null && event.order != null

        def entity = modelMapper.toOrderEventEntity(event, new MappingContext())
        orderEventDao.update(entity)
        Utils.fillDateInfo(event, entity)

        return Promise.pure(event)
    }

    @Override
    Promise<Void> delete(OrderEventId id) {
        orderEventDao.markDelete(id.value)
        return Promise.pure(null)
    }

    @Override
    Promise<List<OrderEvent>> getByOrderId(Long orderId, PageParam pageParam) {
        List<OrderEvent> events = []
        orderEventDao.readByOrderId(orderId, pageParam?.start, pageParam?.count).each { OrderEventEntity entity ->
            OrderEvent event = modelMapper.toOrderEventModel(entity, new MappingContext())
            events << event
        }
        if (events.size() > orderEventsNumThreshHold) {
            LOGGER.warn('name=Too_Many_Order_Events, orderId={}, threshHold={}, current={}', orderId,
                    orderEventsNumThreshHold, events.size())
        }
        return Promise.pure(events)
    }
}
