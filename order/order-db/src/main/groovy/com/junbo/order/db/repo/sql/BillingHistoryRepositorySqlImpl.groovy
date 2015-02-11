/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.sql
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.OrderBillingHistoryDao
import com.junbo.order.db.entity.OrderBillingHistoryEntity
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.BillingHistoryRepository
import com.junbo.order.spec.model.BillingHistory
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
@Component('sqlBillingHistoryRepository')
class BillingHistoryRepositorySqlImpl implements BillingHistoryRepository {

    @Autowired
    private OrderBillingHistoryDao orderBillingHistoryDao

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Override
    Promise<BillingHistory> get(Long id) {
        return Promise.pure(modelMapper.toOrderBillingHistoryModel(orderBillingHistoryDao.read(id), new MappingContext()))
    }

    @Override
    Promise<BillingHistory> create(BillingHistory history) {
        def entity = modelMapper.toOrderBillingHistoryEntity(history, new MappingContext())
        entity.historyId = idGenerator.nextId(history.orderId)
        orderBillingHistoryDao.create(entity)
        return Promise.pure(modelMapper.toOrderBillingHistoryModel(entity, new MappingContext()))
    }

    @Override
    Promise<BillingHistory> update(BillingHistory history, BillingHistory oldHistory) {
        history.resourceAge = oldHistory.resourceAge
        history.createdTime = oldHistory.createdTime
        history.createdBy = oldHistory.createdBy
        def entity = modelMapper.toOrderBillingHistoryEntity(history, new MappingContext())
        orderBillingHistoryDao.update(entity)
        return Promise.pure(modelMapper.toOrderBillingHistoryModel(entity, new MappingContext()))
    }

    @Override
    Promise<Void> delete(Long id) {
        orderBillingHistoryDao.markDelete(id)
        return Promise.pure(null)
    }

    @Override
    Promise<List<BillingHistory>> getByOrderId(Long orderId) {
        List<BillingHistory> billingHistories = []
        MappingContext context = new MappingContext()
        orderBillingHistoryDao.readByOrderId(orderId)?.each { OrderBillingHistoryEntity history ->
            billingHistories << modelMapper.toOrderBillingHistoryModel(history, context)
        }
        return Promise.pure(billingHistories)
    }
}
