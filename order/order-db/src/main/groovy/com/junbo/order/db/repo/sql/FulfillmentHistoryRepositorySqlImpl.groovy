/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.sql
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.OrderItemFulfillmentHistoryDao
import com.junbo.order.db.entity.OrderItemFulfillmentHistoryEntity
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.FulfillmentHistoryRepository
import com.junbo.order.spec.model.FulfillmentHistory
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
@Component('sqlFulfillmentHistoryRepository')
class FulfillmentHistoryRepositorySqlImpl implements FulfillmentHistoryRepository {

    @Autowired
    private OrderItemFulfillmentHistoryDao orderItemFulfillmentHistoryDao

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Override
    Promise<FulfillmentHistory> get(Long id) {
        return Promise.pure(modelMapper.toFulfillmentHistoryModel(orderItemFulfillmentHistoryDao.read(id), new MappingContext()))
    }

    @Override
    Promise<FulfillmentHistory> create(FulfillmentHistory history) {
        def entity = modelMapper.toOrderItemFulfillmentHistoryEntity(history, new MappingContext())
        entity.historyId = idGenerator.nextId(history.orderItemId)
        orderItemFulfillmentHistoryDao.create(entity)
        return Promise.pure(modelMapper.toFulfillmentHistoryModel(entity, new MappingContext()))
    }

    @Override
    Promise<FulfillmentHistory> update(FulfillmentHistory history, FulfillmentHistory oldHistory) {
        def entity = modelMapper.toOrderItemFulfillmentHistoryEntity(history, new MappingContext())
        orderItemFulfillmentHistoryDao.update(entity)
        return Promise.pure(modelMapper.toFulfillmentHistoryModel(entity, new MappingContext()))
    }

    @Override
    Promise<Void> delete(Long id) {
        orderItemFulfillmentHistoryDao.markDelete(id)
        return Promise.pure(null)
    }

    @Override
    Promise<List<FulfillmentHistory>> getByOrderItemId(Long orderItemId) {
        List<FulfillmentHistory> fulfillmentHistories = []
        MappingContext context = new MappingContext()
        orderItemFulfillmentHistoryDao.readByOrderItemId(orderItemId)?.each {
            OrderItemFulfillmentHistoryEntity history ->
                fulfillmentHistories << modelMapper.toFulfillmentHistoryModel(history, context)
        }
        return Promise.pure(fulfillmentHistories)
    }
}
