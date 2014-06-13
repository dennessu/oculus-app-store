/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.sql
import com.junbo.common.id.OrderItemId
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.OrderItemDao
import com.junbo.order.db.dao.OrderItemRevisionDao
import com.junbo.order.db.entity.OrderItemEntity
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.OrderItemRepository
import com.junbo.order.db.repo.util.Utils
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.OrderItemRevision
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
@Component('sqlOrderItemRepository')
class OrderItemRepositorySqlImpl implements OrderItemRepository {

    @Autowired
    private OrderItemDao orderItemDao

    @Resource(name='orderItemRevisionDao')
    private OrderItemRevisionDao orderItemRevisionDao

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Override
    Promise<OrderItem> get(OrderItemId id) {
        return Promise.pure(modelMapper.toOrderItemModel(orderItemDao.read(id.value), new MappingContext()))
    }

    @Override
    Promise<OrderItem> create(OrderItem orderItem) {
        assert orderItem.orderId != null

        orderItem.id = new OrderItemId(idGenerator.nextId(orderItem.orderId.value))
        def entity = modelMapper.toOrderItemEntity(orderItem, new MappingContext())
        orderItemDao.create(entity)

        Utils.fillDateInfo(orderItem, entity)

        return Promise.pure(orderItem)
    }

    @Override
    Promise<OrderItem> update(OrderItem orderItem) {
        assert orderItem.orderId != null
        def entity = modelMapper.toOrderItemEntity(orderItem, new MappingContext())
        def oldEntity = orderItemDao.read(entity.orderItemId)

        if (oldEntity.latestOrderItemRevisionId != entity.latestOrderItemRevisionId) {
            def latestRevision = orderItem.orderItemRevisions.find() { OrderItemRevision revision ->
                revision.id == entity.latestOrderItemRevisionId
            }
            assert (latestRevision != null)
            orderItemRevisionDao.create(modelMapper.toOrderItemRevisionEntity(latestRevision, new MappingContext()))
            oldEntity.latestOrderItemRevisionId = entity.latestOrderItemRevisionId
            entity = oldEntity
        } else {
            entity.createdTime = oldEntity.createdTime
            entity.createdBy = oldEntity.createdBy
            entity.resourceAge = oldEntity.resourceAge
        }
        orderItemDao.update(entity)
        Utils.fillDateInfo(orderItem, entity)

        return Promise.pure(orderItem)
    }

    @Override
    Promise<Void> delete(OrderItemId id) {
        orderItemDao.markDelete(id.value)
        return Promise.pure(null);
    }

    @Override
    Promise<List<OrderItem>> getByOrderId(Long orderId) {
        List<OrderItem> orderItems = []
        MappingContext context = new MappingContext()
        orderItemDao.readByOrderId(orderId)?.each { OrderItemEntity orderItemEntity ->
            orderItems << modelMapper.toOrderItemModel(orderItemEntity, context)
        }
        return Promise.pure(orderItems)
    }
}
