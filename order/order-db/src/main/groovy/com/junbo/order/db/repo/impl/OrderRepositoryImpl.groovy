/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.impl

import com.junbo.common.id.OrderItemId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.oom.core.MappingContext
import com.junbo.common.id.BalanceId
import com.junbo.common.id.OrderId
import com.junbo.order.db.dao.*
import com.junbo.order.db.entity.OrderBillingEventEntity
import com.junbo.order.db.entity.OrderDiscountInfoEntity
import com.junbo.order.db.entity.OrderEntity
import com.junbo.order.db.entity.OrderItemEntity
import com.junbo.order.db.entity.OrderPaymentInfoEntity
import com.junbo.order.db.entity.enums.BillingAction
import com.junbo.order.db.entity.enums.EventStatus
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.*
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
@Component('orderRepository')
class OrderRepositoryImpl implements OrderRepository {

    @Autowired
    OrderDao orderDao
    @Autowired
    OrderItemDao orderItemDao
    @Autowired
    OrderDiscountInfoDao discountDao
    @Autowired
    OrderEventDao orderEventDao
    @Autowired
    OrderItemFulfillmentEventDao orderItemFulfillmentEventDao
    @Autowired
    OrderPaymentInfoDao orderPaymentInfoDao
    @Autowired
    OrderBillingEventDao orderBillingEventDao
    @Autowired
    OrderItemPreorderEventDao orderItemPreorderEventDao
    @Autowired
    OrderItemPreorderInfoDao orderItemPreorderInfoDao
    @Autowired
    OrderItemPreorderUpdateHistoryDao orderItemPreorderUpdateHistoryDao

    @Autowired
    ModelMapper modelMapper
    @Autowired
    IdGenerator idGenerator

    @Override
    Order createOrder(Order order, OrderEvent orderEvent) {

        MappingContext context = new MappingContext()
        def orderEntity = modelMapper.toOrderEntity(order, context)

        // Save Order
        orderEntity.setOrderId(idGenerator.nextId(order.user.value))
        def id = orderDao.create(orderEntity)
        def orderId = new OrderId()
        orderId.setValue(id)

        order.setId(orderId)

        // Save OrderItem
        order.orderItems?.each { OrderItem item ->
            item.id = new OrderItemId(idGenerator.nextId(orderEntity.orderId))
            item.orderId = orderId
            def itemEntity = modelMapper.toOrderItemEntity(item, context)
            orderItemDao.create(itemEntity)
        }

        // Save Discount
        order.discounts?.each { Discount discount ->
            def discountEntity = modelMapper.toDiscountEntity(discount, context)
            discountEntity.discountInfoId = idGenerator.nextId(orderEntity.orderId)
            discountDao.create(discountEntity)
        }

        // Save order event
        orderEvent.order = order.id
        def orderEventEntity = modelMapper.toOrderEventEntity(orderEvent, context)
        orderEventEntity.eventId = idGenerator.nextId(orderEntity.orderId)
        // orderEventDao.create(orderEventEntity)

        // Save Balance Event

        // Save Order Item Fulfillment Event

        // Save Payment Info

        // Save Order Item Tax Info

        // Save Order Item Preorder Info

        return order
    }

    @Override
    Order getOrder(Long orderId) {

        OrderEntity orderEntity = orderDao.read(orderId)
        MappingContext context = new MappingContext()
        return modelMapper.toOrderModel(orderEntity, context)
    }

    @Override
    OrderEvent createOrderEvent(OrderEvent event) {
        def entity = modelMapper.toOrderEventEntity(event, new MappingContext())
        entity.eventId = idGenerator.nextId(entity.orderId)
        // todo set the flowName & tracking guuid
        entity.flowName = UUID.randomUUID()
        entity.trackingUuid = UUID.randomUUID()
        orderEventDao.create(entity)
        return modelMapper.toOrderEventModel(entity, new MappingContext())
    }

    @Override
    FulfillmentEvent createFulfillmentEvent(Long orderId, FulfillmentEvent event) {
        def entity = modelMapper.toOrderItemFulfillmentEventEntity(event, new MappingContext())
        entity.eventId = idGenerator.nextId(orderId)
        entity.orderId = orderId
        orderItemFulfillmentEventDao.create(entity)
        return modelMapper.toFulfillmentEventModel(entity, new MappingContext())
    }

    @Override
    void saveBillingEvent(OrderId orderId, BalanceId balanceId, BillingAction action, EventStatus status) {
        def entity = new OrderBillingEventEntity()
        entity.eventId = idGenerator.nextId(orderId.value)
        entity.orderId = orderId.value
        entity.balanceId = balanceId.value
        entity.action = action
        entity.status = status
        orderBillingEventDao.create(entity)
    }

    @Override
    List<OrderItem> getOrderItems(Long orderId) {
        List<OrderItem> orderItems = []
        MappingContext context = new MappingContext()
        orderItemDao.readByOrderId(orderId)?.each { OrderItemEntity orderItemEntity ->
            orderItems << modelMapper.toOrderItemModel(orderItemEntity, context)
        }
        return orderItems
    }

    @Override
    List<Discount> getDiscounts(Long orderId) {
        List<Discount> discounts = []
        MappingContext context = new MappingContext()
        discountDao.readByOrderId(orderId)?.each { OrderDiscountInfoEntity orderDiscountInfoEntity ->
            discounts << modelMapper.toDiscountModel(orderDiscountInfoEntity, context)
        }
        return discounts
    }

    @Override
    List<PaymentInstrumentId> getPaymentInstrumentIds(Long orderId) {
        List<PaymentInstrumentId> paymentInstrumentIds = []
        orderPaymentInfoDao.readByOrderId(orderId)?.each { OrderPaymentInfoEntity paymentInfo ->
            paymentInstrumentIds << new PaymentInstrumentId(paymentInfo.orderPaymentId)
        }
        return paymentInstrumentIds
    }

    @Override
    Order updateOrder(Order order, OrderEvent orderEvent) {
        // Validations
        // TODO Log error and throw exception
        if (order == null) { return null }
        if (order.id == null) { return null }

        // Update Order
        MappingContext context = new MappingContext()
        def orderEntity = modelMapper.toOrderEntity(order, context)
        orderDao.update(orderEntity)

        // Update OrderItem
        order.orderItems?.each { OrderItem item ->
            def itemEntity = modelMapper.toOrderItemEntity(item, context)
            itemEntity.orderItemId = idGenerator.nextId(orderEntity.orderId)
            orderItemDao.update(itemEntity)
        }

        // Update Discount
        order.discounts?.each { Discount discount ->
            def discountEntity = modelMapper.toDiscountEntity(discount, context)
            discountEntity.discountInfoId = idGenerator.nextId(orderEntity.orderId)
            discountDao.update(discountEntity)
        }

        // Save order event
        orderEvent.order = order.id
        def orderEventEntity = modelMapper.toOrderEventEntity(orderEvent, context)
        orderEventEntity.eventId = idGenerator.nextId(orderEntity.orderId)
        orderEventDao.create(orderEventEntity)

        // Save Balance Event

        // Save Order Item Fulfillment Event

        // Save Payment Info

        // Save Order Item Tax Info

        // Save Order Item Preorder Info

        return order
    }
}
