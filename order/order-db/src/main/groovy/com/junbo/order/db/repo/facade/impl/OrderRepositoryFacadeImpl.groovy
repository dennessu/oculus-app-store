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
import com.junbo.order.spec.model.*
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

    @Override
    Order createOrder(Order order) {
        return ((Promise<Order>)orderRepository.create(order).then { Order savedOrder ->
            return saveOrderItems(savedOrder.getId(), order.orderItems).then {
                return saveDiscounts(savedOrder.getId(), order.discounts);
            };
        }.then {
            return Promise.pure(order);
        }).get();
    }

    @Override
    Order updateOrder(Order order, boolean updateOnlyOrder) {
        return ((Promise<Order>)orderRepository.update(order).then { Order savedOrder ->
            if (!updateOnlyOrder) {
                return saveOrderItems(savedOrder.getId(), order.orderItems).then {
                    return saveDiscounts(savedOrder.getId(), order.discounts)
                }.then {
                    return Promise.pure(order);
                };
            } else {
                return Promise.pure(order);
            }
        }).get()
    }

    @Override
    Order getOrder(Long orderId) {
        return orderRepository.get(new OrderId(orderId)).get();
    }

    @Override
    List<Order> getOrdersByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam) {
        return orderRepository.getByUserId(userId, orderQueryParam, pageParam).get()
    }

    @Override
    List<Order> getOrdersByStatus(Object shardKey, List<String> statusList,
                                  boolean updatedByAscending, PageParam pageParam) {
        return orderRepository.getByStatus(shardKey, statusList, updatedByAscending, pageParam).get();
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
        return orderItemRepository.getByOrderId(orderId).get();
    }

    @Override
    OrderItem getOrderItem(Long orderItemId) {
        return orderItemRepository.get(new OrderItemId(orderItemId)).get();
    }

    @Override
    List<Discount> getDiscounts(Long orderId) {
        return discountRepository.getByOrderId(orderId).get();
    }

    @Override
    List<PaymentInfo> getPayments(Long orderId) {
        // TODO: this should be removed. PaymentInfo always loads with the order
        return getOrder(orderId).getPayments()
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

    private Promise<Void> saveOrderItems(OrderId orderId, List<OrderItem> orderItems) {
        def repositoryFuncSet = new RepositoryFuncSet()
        orderItems.each { OrderItem item ->
            item.orderId = orderId
        }
        repositoryFuncSet.create = { OrderItem item ->
            orderItemRepository.create(item).get()
        }
        repositoryFuncSet.update = { OrderItem newItem, OrderItem oldItem ->
            newItem.id = oldItem.getId()
            newItem.createdBy = oldItem.createdBy
            newItem.createdTime = oldItem.createdTime
            orderItemRepository.update(newItem).get()
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
}
