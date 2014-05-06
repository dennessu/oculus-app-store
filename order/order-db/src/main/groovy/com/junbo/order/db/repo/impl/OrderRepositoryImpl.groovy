/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.impl
import com.google.common.collect.HashMultimap
import com.junbo.common.id.OrderId
import com.junbo.common.id.OrderItemId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.*
import com.junbo.order.db.entity.*
import com.junbo.order.db.entity.enums.OrderStatus
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.*
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.IdGeneratorFacade
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
@Component('orderRepository')
class OrderRepositoryImpl implements OrderRepository {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderRepositoryImpl)

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
    IdGeneratorFacade idGeneratorFacade
    @Autowired
    @Qualifier('oculus48IdGenerator')
    IdGenerator idGenerator

    int orderEventsNumThreshHold = 200

    private class RepositoryFuncSet {
        Closure create
        Closure update
        Closure delete
    }

    @Override
    Order createOrder(Order order) {

        MappingContext context = new MappingContext()
        def orderEntity = modelMapper.toOrderEntity(order, context)

        // Save Order
        orderEntity.setOrderId(idGeneratorFacade.nextId(OrderId, order.user.value))
        def id = orderDao.create(orderEntity)
        def orderId = new OrderId(id)
        order.setId(orderId)
        Utils.fillDateInfo(order, orderEntity)

        saveOrderItems(order.id, order.orderItems)
        saveDiscounts(order.id, order.discounts)
        savePaymentInstruments(order.id, order.payments)

        // Save Order Item Tax Info
        return order
    }

    @Override
    Order getOrder(Long orderId) {
        OrderEntity orderEntity = orderDao.read(orderId)
        MappingContext context = new MappingContext()
        return modelMapper.toOrderModel(orderEntity, context)
    }

    @Override
    List<Order> getOrdersByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam) {
        return orderDao.readByUserId(userId,
                orderQueryParam.tentative, pageParam?.start, pageParam?.count).collect { OrderEntity entity ->
            modelMapper.toOrderModel(entity, new MappingContext())
        }
    }

    @Override
    List<Order> getOrdersByStatus(Object shardKey, List<String> statusList,
                                  boolean updatedByAscending, PageParam pageParam) {
        return orderDao.readByStatus((Integer) shardKey,
                statusList.collect { String status -> OrderStatus.valueOf(status) },
                updatedByAscending, pageParam?.start, pageParam?.count).collect { OrderEntity entity ->
            modelMapper.toOrderModel(entity, new MappingContext())
        }
    }

    @Override
    OrderEvent createOrderEvent(OrderEvent event) {
        assert (event != null && event.order != null)
        LOGGER.info('name=Create_Order_Event, event: {},{},{},{},{}',
                event.flowName, event.order.value, event.action, event.status, event.trackingUuid)
        def entity = modelMapper.toOrderEventEntity(event, new MappingContext())
        entity.eventId = idGenerator.nextId(entity.orderId)
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
    BillingEvent createBillingEvent(Long orderId, BillingEvent event) {
        def entity = modelMapper.toOrderBillingEventEntity(event, new MappingContext())
        entity.eventId = idGenerator.nextId(orderId)
        entity.orderId = orderId
        orderBillingEventDao.create(entity)
        return modelMapper.toOrderBillingEventModel(entity, new MappingContext())
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
    OrderItem getOrderItem(Long orderItemId) {
        return modelMapper.toOrderItemModel(orderItemDao.read(orderItemId), new MappingContext())
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
    List<PaymentInfo> getPayments(Long orderId) {
        List<PaymentInfo> paymentInfos = []
        orderPaymentInfoDao.readByOrderId(orderId)?.each { OrderPaymentInfoEntity paymentInfoEntity ->
            paymentInfos << modelMapper.toPaymentInfo(paymentInfoEntity, new MappingContext())
        }
        return paymentInfos
    }

    @Override
    Order updateOrder(Order order, boolean updateOnlyOrder) {
        // Validations
        // TODO Log error and throw exception
        if (order == null) {
            return null
        }
        if (order.id == null) {
            return null
        }
        def oldEntity = orderDao.read(order.id.value)
        if (oldEntity == null) {
            throw new IllegalArgumentException('name=Order_Not_Found')
        }

        // Update Order
        def orderEntity = modelMapper.toOrderEntity(order, new MappingContext())
        orderEntity.createdTime = oldEntity.createdTime
        orderEntity.createdBy = oldEntity.createdBy
        orderDao.update(orderEntity)
        Utils.fillDateInfo(order, orderEntity)

        if (!updateOnlyOrder) {
            saveOrderItems(order.id, order.orderItems)
            saveDiscounts(order.id, order.discounts)
            savePaymentInstruments(order.id, order.payments)
        }
        return order
    }

    @Override
    List<OrderEvent> getOrderEvents(Long orderId, PageParam pageParam) {
        List<OrderEvent> events = []
        orderEventDao.readByOrderId(orderId, pageParam?.start, pageParam?.count).each { OrderEventEntity entity ->
            OrderEvent event = modelMapper.toOrderEventModel(entity, new MappingContext())
            events << event
        }
        if (events.size() > orderEventsNumThreshHold) {
            LOGGER.warn('name=Too_Many_Order_Events, orderId={}, threshHold={}, current={}', orderId,
                    orderEventsNumThreshHold, events.size())
        }
        return events
    }

    @Override
    List<PreorderInfo> getPreorderInfo(Long orderItemId) {
        List<PreorderInfo> preorderInfoList = []
        MappingContext context = new MappingContext()
        orderItemPreorderInfoDao.readByOrderItemId(orderItemId).each { OrderItemPreorderInfoEntity entity ->
            preorderInfoList << modelMapper.toPreOrderInfoModel(entity, context)
        }
        return preorderInfoList
    }

    void saveOrderItems(OrderId orderId, List<OrderItem> orderItems) {
        def repositoryFuncSet = new RepositoryFuncSet()
        orderItems.each { OrderItem item ->
            item.orderId = orderId
        }
        repositoryFuncSet.create = { OrderItem item ->
            saveOrderItem(item, true)
        }
        repositoryFuncSet.update = { OrderItem newItem, OrderItem oldItem ->
            newItem.orderItemId = oldItem.orderItemId
            newItem.createdBy = oldItem.createdBy
            newItem.createdTime = oldItem.createdTime
            saveOrderItem(newItem, false)
            return true
        }
        repositoryFuncSet.delete = { OrderItem item ->
            orderItemDao.markDelete(item.orderItemId.value)
            return true
        }
        def keyFunc = { OrderItem item ->
            return item.offer
        }
        updateListTypeField(orderItems, getOrderItems(orderId.value), repositoryFuncSet, keyFunc, 'orderItems')
    }

    void saveDiscounts(OrderId orderId, List<Discount> discounts) {
        def repositoryFuncSet = new RepositoryFuncSet()
        discounts.each { Discount discount ->
            discount.orderId = orderId
            if (discount.ownerOrderItem != null) {
                discount.orderItemId = discount.ownerOrderItem.orderItemId
            }
        }
        repositoryFuncSet.create = { Discount discount ->
            saveDiscount(discount, true)
        }
        repositoryFuncSet.update = { Discount newDiscount, Discount oldDiscount ->
            newDiscount.discountInfoId = oldDiscount.discountInfoId
            saveDiscount(newDiscount, false)
            return true
        }
        repositoryFuncSet.delete = { Discount discount ->
            discountDao.markDelete(discount.discountInfoId)
            return true
        }
        def keyFunc = { Discount discount ->
            return [discount.orderId, discount.orderItemId]
        }
        updateListTypeField(discounts, getDiscounts(orderId.value), repositoryFuncSet, keyFunc, 'discounts')
    }

    void savePaymentInstruments(OrderId orderId, List<PaymentInfo> paymentInfos) {
        def repositoryFuncSet = new RepositoryFuncSet()

        def oldPaymentInfoEntityList = orderPaymentInfoDao.readByOrderId(orderId.value)
        def oldPaymentInfos = [] as ArrayList<PaymentInfo>
        def oldPaymentInfoEntityMap = [:] as HashMap<PaymentInstrumentId, OrderPaymentInfoEntity>

        oldPaymentInfoEntityList.each { OrderPaymentInfoEntity paymentInfo ->
            oldPaymentInfos << modelMapper.toPaymentInfo(paymentInfo, new MappingContext())
            oldPaymentInfoEntityMap[new PaymentInstrumentId(Long.parseLong(paymentInfo.paymentInstrumentId))] =
                    paymentInfo
        }

        repositoryFuncSet.create = { PaymentInfo pi ->
            savePaymentInstrument(orderId, pi, null)
        }

        repositoryFuncSet.update = { PaymentInfo newPi, PaymentInfo oldPi ->
            assert newPi.paymentInstrument == oldPi.paymentInstrument
            savePaymentInstrument(orderId, newPi, oldPaymentInfoEntityMap[oldPi.paymentInstrument].orderPaymentId)
            return true
        }

        repositoryFuncSet.delete = { PaymentInfo pi ->
            def entity = oldPaymentInfoEntityMap[pi.paymentInstrument]
            if (entity != null) {
                orderPaymentInfoDao.markDelete(entity.orderPaymentId)
                return true
            }
            return false
        }
        def keyFunc = { PaymentInfo pi ->
            return pi.paymentInstrument
        }

        updateListTypeField(paymentInfos, oldPaymentInfos, repositoryFuncSet, keyFunc,
                'payments')
    }

    void updateListTypeField(List newList, List oldList, RepositoryFuncSet repositoryFuncSet, Closure keyFunc,
                             String field) {
        def oldMap = HashMultimap.create()
        oldList.each {
            oldMap.put(keyFunc.call(it), it)
        }
        def numCreated = 0, numUpdated = 0, numDeleted = 0

        def lookupAndRemove = {
            def values = oldMap.get(keyFunc.call(it))
            if (values.empty) {
                return null
            }
            def iterator = values.iterator()
            def found = iterator.next()
            iterator.remove()
            return found
        }

        newList.each { newItem ->
            def oldItem = lookupAndRemove(newItem)
            if (oldItem == null) { // create
                repositoryFuncSet.create.call(newItem)
                numCreated++
            } else { // update
                if (repositoryFuncSet.update.call(newItem, oldItem)) {
                    numUpdated++
                }
            }
        }

        oldMap.values().each { // delete
            if (repositoryFuncSet.delete.call(it)) {
                numDeleted++
            }
        }

        LOGGER.debug('name=Save_List_Fields, fieldName={}, numCreated={}, numUpdated={}, numDeleted={}',
                field, numCreated, numUpdated, numDeleted)
    }

    void saveOrderItem(OrderItem orderItem, boolean isCreate) {
        def entity
        assert orderItem.orderId != null
        if (isCreate) {
            orderItem.orderItemId = new OrderItemId(idGeneratorFacade.nextId(OrderItemId, orderItem.orderId.value))
            entity = modelMapper.toOrderItemEntity(orderItem, new MappingContext())
            orderItemDao.create(entity)
        } else {
            entity = modelMapper.toOrderItemEntity(orderItem, new MappingContext())
            def oldEntity = orderItemDao.read(entity.orderItemId)
            entity.createdTime = oldEntity.createdTime
            entity.createdBy = oldEntity.createdBy
            orderItemDao.update(entity)
        }
        Utils.fillDateInfo(orderItem, entity)
    }

    void saveDiscount(Discount discount, boolean isCreate) {
        def entity
        assert discount.orderId != null
        if (isCreate) {
            discount.discountInfoId = idGeneratorFacade.nextId(OrderItemId, discount.orderId.value)
            entity = modelMapper.toDiscountEntity(discount, new MappingContext())
            discountDao.create(entity)
        } else {
            entity = modelMapper.toDiscountEntity(discount, new MappingContext())
            def oldEntity = discountDao.read(entity.discountInfoId)
            entity.createdTime = oldEntity.createdTime
            entity.createdBy = oldEntity.createdBy
            discountDao.update(entity)
        }
        Utils.fillDateInfo(discount, entity)
    }

    void savePaymentInstrument(OrderId orderId, PaymentInfo paymentInfo, Long oldEntityId) {
        def entity = modelMapper.toOrderPaymentInfoEntity(paymentInfo, new MappingContext())
        entity.orderId = orderId == null ? null : orderId.value
        entity.paymentInstrumentType = 'CREDIT_CAR' // todo may not need to save this field in db

        if (oldEntityId == null) { // create
            entity.orderPaymentId = idGenerator.nextId(orderId.value)
            orderPaymentInfoDao.create(entity)
        } else { // update
            def oldEntity = orderPaymentInfoDao.read(oldEntityId)
            entity.createdTime = oldEntity.createdTime
            entity.createdBy = oldEntity.createdBy
            entity.orderPaymentId = oldEntityId
            orderPaymentInfoDao.update(entity)
        }
    }
}
