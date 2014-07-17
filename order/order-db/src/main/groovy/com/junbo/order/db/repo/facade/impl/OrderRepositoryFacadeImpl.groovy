/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.repo.facade.impl

import com.junbo.common.id.OrderId
import com.junbo.common.id.OrderItemId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.db.repo.*
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.db.repo.util.RepositoryFuncSet
import com.junbo.order.db.repo.util.Utils
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.*
import com.junbo.order.spec.model.enums.OrderItemRevisionType
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.hibernate.StaleObjectStateException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Created by chriszhu on 2/18/14.
 */
@CompileStatic
@TypeChecked
@Component('orderRepositoryFacade')
class OrderRepositoryFacadeImpl implements OrderRepositoryFacade {

    @Autowired
    @Qualifier('orderRepository')
    private OrderRepository orderRepository;

    @Autowired
    @Qualifier('orderItemRepository')
    private OrderItemRepository orderItemRepository;

    @Autowired
    @Qualifier('orderEventRepository')
    private OrderEventRepository orderEventRepository;

    @Autowired
    @Qualifier('discountRepository')
    private DiscountRepository discountRepository;

    @Autowired
    @Qualifier('billingHistoryRepository')
    private BillingHistoryRepository billingHistoryRepository;

    @Autowired
    @Qualifier('fulfillmentHistoryRepository')
    private FulfillmentHistoryRepository fulfillmentHistoryRepository;

    @Autowired
    @Qualifier('preorderInfoRepository')
    private PreorderInfoRepository preorderInfoRepository;

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Override
    Order createOrder(Order order) {
        return ((Promise<Order>) orderRepository.create(order).then { Order savedOrder ->
            return saveOrderItems(savedOrder.getId(), order.orderItems, false, null).then {
                return saveDiscounts(savedOrder.getId(), order.discounts);
            };
        }.then {
            return Promise.pure(order);
        }).get();
    }

    @Override
    Order updateOrder(Order order, Boolean updateOnlyOrder,
                      Boolean saveRevision, OrderItemRevisionType revisionType) {
        try {
            def existingOrder = orderRepository.get(order.getId()).get()
            if (existingOrder == null) {
                throw AppErrors.INSTANCE.orderNotFound().exception()
            }

            if (!existingOrder.tentative && saveRevision) {
                def orderRevision = toOrderRevision(order)
                existingOrder.latestOrderRevisionId = orderRevision.getId()
                existingOrder.orderRevisions << orderRevision
                existingOrder.status = order.status
            } else {
                existingOrder = order
            }

            return ((Promise<Order>) orderRepository.update(existingOrder).then { Order savedOrder ->
                if (!updateOnlyOrder) {
                    // update non-tentative order items to item revision
                    return saveOrderItems(savedOrder.getId(), order.orderItems,
                            saveRevision, revisionType).then {
                        return saveDiscounts(savedOrder.getId(), order.discounts)
                    }.then {
                        return Promise.pure(order)
                    }

                } else {
                    return Promise.pure(order)
                }
            }).get()
        } catch (StaleObjectStateException ex) {
            throw AppErrors.INSTANCE.orderConcurrentUpdate().exception()
        }
    }

    @Override
    Order getOrder(Long orderId) {
        def order = orderRepository.get(new OrderId(orderId)).get()
        if (order == null) {
            return (Order) null
        }
        // update order with order revision for non-tentative order
        if (!order.tentative && !CollectionUtils.isEmpty(order.orderRevisions)) {
            def latestRevision = order.orderRevisions.find() { OrderRevision revision ->
                revision.id == order.latestOrderRevisionId
            }
            assert (latestRevision != null)
            fillOrderWithRevision(order, latestRevision)
        }
        return order
    }

    @Override
    List<Order> getOrdersByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam) {
        List<Order> orders = orderRepository.getByUserId(userId, orderQueryParam, pageParam).get()
        if (orders == null) {
            return []
        }
        orders.each { Order order ->
            if (!order.tentative && !CollectionUtils.isEmpty(order.orderRevisions)) {
                def latestRevision = order.orderRevisions.find() { OrderRevision revision ->
                    revision.id == order.latestOrderRevisionId
                }
                assert (latestRevision != null)
                fillOrderWithRevision(order, latestRevision)
            }
        }
    }

    @Override
    List<Order> getOrdersByStatus(Integer dataCenterId, Object shardKey, List<String> statusList,
                                  boolean updatedByAscending, PageParam pageParam) {
        return orderRepository.getByStatus(dataCenterId, shardKey, statusList, updatedByAscending, pageParam).get();
    }

    @Override
    OrderEvent createOrderEvent(OrderEvent event) {
        return orderEventRepository.create(event).get();
    }

    @Override
    FulfillmentHistory createFulfillmentHistory(FulfillmentHistory event) {
        return fulfillmentHistoryRepository.create(event).get();
    }

    @Override
    BillingHistory createBillingHistory(Long orderId, BillingHistory history) {
        history.setOrderId(orderId);
        return billingHistoryRepository.create(history).get();
    }

    @Override
    List<OrderItem> getOrderItems(Long orderId) {
        def orderItems = orderItemRepository.getByOrderId(orderId).get()
        orderItems?.collect() { OrderItem item ->
            if (!CollectionUtils.isEmpty(item.orderItemRevisions)) {
                def latestItemRevision = item.orderItemRevisions.find() { OrderItemRevision itemRevision ->
                    itemRevision.id == item.latestOrderItemRevisionId
                }
                assert (latestItemRevision != null)
                fillOrderItemWithRevision(item, latestItemRevision)
            }
        }
        return orderItems
    }

    @Override
    OrderItem getOrderItem(Long orderItemId) {
        def item = orderItemRepository.get(new OrderItemId(orderItemId)).get()
        if (!CollectionUtils.isEmpty(item.orderItemRevisions)) {
            def latestItemRevision = item.orderItemRevisions.find() { OrderItemRevision itemRevision ->
                itemRevision.id == item.latestOrderItemRevisionId
            }
            assert (latestItemRevision != null)
            fillOrderItemWithRevision(item, latestItemRevision)
        }
        return item
    }

    @Override
    List<Discount> getDiscounts(Long orderId) {
        return discountRepository.getByOrderId(orderId).get();
    }

    @Override
    List<OrderEvent> getOrderEvents(Long orderId, PageParam pageParam) {
        return orderEventRepository.getByOrderId(orderId, pageParam).get();
    }

    @Override
    List<PreorderInfo> getPreorderInfo(Long orderItemId) {
        return preorderInfoRepository.getByOrderItemId(orderItemId).get();
    }

    @Override
    List<BillingHistory> getBillingHistories(Long orderId) {
        return billingHistoryRepository.getByOrderId(orderId).get();
    }

    @Override
    List<FulfillmentHistory> getFulfillmentHistories(Long orderItemId) {
        return fulfillmentHistoryRepository.getByOrderItemId(orderItemId).get();
    }

    private Promise<Void> saveOrderItems(OrderId orderId, List<OrderItem> orderItems,
                                         Boolean nonTentative, OrderItemRevisionType revisionType) {
        def repositoryFuncSet = new RepositoryFuncSet()
        orderItems.each { OrderItem item ->
            item.orderId = orderId
        }
        repositoryFuncSet.create = { OrderItem item ->
            orderItemRepository.create(item).get()
        }
        repositoryFuncSet.update = { OrderItem newItem, OrderItem oldItem ->
            if (nonTentative) {
                assert (revisionType != null)
                def revision = toOrderItemRevision(newItem, revisionType)
                oldItem.latestOrderItemRevisionId = revision.getId()
                oldItem.orderItemRevisions.add(revision)
                orderItemRepository.update(oldItem).get()
            } else {
                newItem.id = oldItem.getId()
                newItem.createdBy = oldItem.createdBy
                newItem.createdTime = oldItem.createdTime
                orderItemRepository.update(newItem).get()
            }
            return true
        }
        repositoryFuncSet.delete = { OrderItem item ->
            orderItemRepository.delete(item.getId()).get()
            return true
        }
        def keyFunc = { OrderItem item ->
            return item.offer
        }
        Utils.updateListTypeField(orderItems, getOrderItems(orderId.value), repositoryFuncSet, keyFunc, 'orderItems')

        return Promise.pure(null)
    }

    Promise<Void> saveDiscounts(OrderId orderId, List<Discount> discounts) {
        def repositoryFuncSet = new RepositoryFuncSet()
        discounts.each { Discount discount ->
            discount.orderId = orderId
            if (discount.ownerOrderItem != null) {
                discount.orderItemId = discount.ownerOrderItem.getId()
            }
        }
        repositoryFuncSet.create = { Discount discount ->
            discountRepository.create(discount).get()
        }
        repositoryFuncSet.update = { Discount newDiscount, Discount oldDiscount ->
            newDiscount.id = oldDiscount.getId()
            discountRepository.update(newDiscount).get()
            return true
        }
        repositoryFuncSet.delete = { Discount discount ->
            discountRepository.delete(discount.getId()).get()
            return true
        }
        def keyFunc = { Discount discount ->
            return [discount.orderId, discount.orderItemId]
        }
        Utils.updateListTypeField(discounts, getDiscounts(orderId.value), repositoryFuncSet, keyFunc, 'discounts')

        return Promise.pure(null)
    }

    private Order fillOrderWithRevision(Order order, OrderRevision revision) {
        order.isTaxInclusive = revision.isTaxInclusive
        order.shippingAddress = revision.shippingAddress
        order.shippingMethod = revision.shippingMethod
        order.shippingToName = revision.shippingToName
        order.shippingToPhone = revision.shippingToPhone
        order.totalAmount = revision.totalAmount
        order.totalDiscount = revision.totalDiscount
        order.totalShippingFee = revision.totalShippingFee
        order.totalShippingFeeDiscount = revision.totalShippingFeeDiscount
        order.totalTax = revision.totalTax
        return order
    }

    private OrderItemRevision toOrderItemRevision(OrderItem item, OrderItemRevisionType revisionType) {
        def revision = new OrderItemRevision()
        revision.totalDiscount = item.totalDiscount
        revision.totalAmount = item.totalAmount
        revision.totalTax = item.totalTax
        revision.shippingAddress = item.shippingAddress
        revision.shippingMethod = item.shippingMethod
        revision.quantity = item.quantity
        revision.orderId = item.orderId
        revision.orderItemId = item.getId()
        revision.revisionType = revisionType.name()
        revision.id = idGenerator.nextId(item.orderId.value)
        return revision
    }

    private OrderItem fillOrderItemWithRevision(OrderItem item, OrderItemRevision revision) {
        item.orderId = revision.orderId
        item.quantity = revision.quantity
        item.shippingAddress = revision.shippingAddress
        item.shippingMethod = revision.shippingMethod
        item.totalAmount = revision.totalAmount
        item.totalTax = revision.totalTax
        item.totalDiscount = revision.totalTax
        return item
    }

    private OrderRevision toOrderRevision(Order order) {
        def val = new OrderRevision()
        val.isTaxInclusive = order.isTaxInclusive
        val.orderId = order.getId()
        val.setId(idGenerator.nextId(val.orderId.value))
        val.shippingAddress = order.shippingAddress
        val.shippingMethod = order.shippingMethod
        val.shippingToName = order.shippingToName
        val.shippingToPhone = order.shippingToPhone
        val.totalAmount = order.totalAmount
        val.totalDiscount = order.totalDiscount
        val.totalShippingFee = order.totalShippingFee
        val.totalShippingFeeDiscount = order.totalShippingFeeDiscount
        val.totalTax = order.totalTax
        return val
    }
}
