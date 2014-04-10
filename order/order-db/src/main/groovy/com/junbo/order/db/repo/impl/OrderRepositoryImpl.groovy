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
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.*
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.IdGeneratorFacade
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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
        fillDateInfo(order, orderEntity)

        saveOrderItems(order.id, order.orderItems)
        saveDiscounts(order.id, order.discounts)
        savePaymentInstruments(order.id, order.paymentInstruments)

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
        List<Order> orders = []
        List<OrderEntity> orderEntities = orderDao.readByUserId(userId,
                orderQueryParam.tentative, pageParam?.start, pageParam?.count)
        MappingContext context = new MappingContext()
        orderEntities.each { OrderEntity orderEntity ->
            orders.add(modelMapper.toOrderModel(orderEntity, context))
        }
        return orders
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
    List<PaymentInstrumentId> getPaymentInstrumentIds(Long orderId) {
        List<PaymentInstrumentId> paymentInstrumentIds = []
        orderPaymentInfoDao.readByOrderId(orderId)?.each { OrderPaymentInfoEntity paymentInfo ->
            paymentInstrumentIds << new PaymentInstrumentId(Long.parseLong(paymentInfo.paymentInstrumentId))
        }
        return paymentInstrumentIds
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
        fillDateInfo(order, orderEntity)

        if (!updateOnlyOrder) {
            saveOrderItems(order.id, order.orderItems)
            saveDiscounts(order.id, order.discounts)
            savePaymentInstruments(order.id, order.paymentInstruments)
        }
        return order
    }

    @Override
    Order getOrderByTrackingUuid(UUID trackingUuid) {
        def orders = orderDao.readByTrackingUuid(trackingUuid)
        if (CollectionUtils.isEmpty(orders)) {
            return null
        }

        // assert only one order is returned.
        checkOrdersByTrackingUuid(orders)
        return modelMapper.toOrderModel(orders[0], new MappingContext())
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

    void savePaymentInstruments(OrderId orderId, List<PaymentInstrumentId> paymentInstruments) {
        def repositoryFuncSet = new RepositoryFuncSet()
        def paymentInfoEntityList = orderPaymentInfoDao.readByOrderId(orderId.value)
        def oldPaymentInstruments = new ArrayList<PaymentInstrumentId>()
        paymentInfoEntityList.each { OrderPaymentInfoEntity paymentInfo ->
            oldPaymentInstruments << new PaymentInstrumentId(Long.parseLong(paymentInfo.paymentInstrumentId))
        }
        repositoryFuncSet.create = { PaymentInstrumentId pi ->
            savePaymentInstrument(orderId, pi)
        }
        repositoryFuncSet.update = { PaymentInstrumentId newPi, PaymentInstrumentId oldPi ->
            assert newPi == oldPi
            return false
        }
        repositoryFuncSet.delete = { PaymentInstrumentId pi ->
            def entity = paymentInfoEntityList.find { OrderPaymentInfoEntity entity ->
                return Long.parseLong(entity.paymentInstrumentId) == pi.value
            }
            if (entity != null) {
                orderPaymentInfoDao.markDelete(entity.orderPaymentId)
                return true
            }
            return false
        }
        def keyFunc = { PaymentInstrumentId piId ->
            return piId
        }
        updateListTypeField(paymentInstruments, oldPaymentInstruments, repositoryFuncSet, keyFunc,
                'paymentInstruments')
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
        fillDateInfo(orderItem, entity)
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
        fillDateInfo(discount, entity)
    }

    void savePaymentInstrument(OrderId orderId, PaymentInstrumentId paymentInstrumentId) {
        def orderPaymentInfoEntity = new OrderPaymentInfoEntity()
        orderPaymentInfoEntity.paymentInstrumentId =
                (paymentInstrumentId == null || paymentInstrumentId.value == null) ? null :
                        paymentInstrumentId.value.toString()
        orderPaymentInfoEntity.orderId = orderId == null ? null : orderId.value
        orderPaymentInfoEntity.paymentInstrumentType = 'CREDIT_CAR' // todo may not need to save this field in db
        orderPaymentInfoEntity.orderPaymentId = idGenerator.nextId(orderId.value)
        orderPaymentInfoDao.create(orderPaymentInfoEntity)
    }

    void fillDateInfo(BaseModelWithDate baseModelWithDate, CommonDbEntityWithDate commonDbEntityWithDate) {
        baseModelWithDate.createdBy = commonDbEntityWithDate.createdBy
        baseModelWithDate.createdTime = commonDbEntityWithDate.createdTime
        baseModelWithDate.updatedBy = commonDbEntityWithDate.updatedBy
        baseModelWithDate.updatedTime = commonDbEntityWithDate.updatedTime
    }

    static void checkOrdersByTrackingUuid(List<OrderEntity> orders) {
        if (orders.size() > 1) {
            LOGGER.error('name=Multiple_Orders_With_Same_TrackingUuid, ' +
                    'trackingUuid={}',
                    orders[0].trackingUuid)
            throw AppErrors.INSTANCE.orderDuplicateTrackingGuid(0L, orders[0].trackingUuid).exception()
        }
    }
}
