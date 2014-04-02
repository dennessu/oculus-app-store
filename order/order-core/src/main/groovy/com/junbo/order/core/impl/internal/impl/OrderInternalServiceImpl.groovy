/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.core.impl.internal.impl
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.common.*
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderQueryParam
import com.junbo.order.spec.model.PageParam
import com.junbo.rating.spec.model.request.OrderRatingRequest
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

import javax.annotation.Resource

/**
 * Created by chriszhu on 4/1/14.
 */
@CompileStatic
@TypeChecked
@Service('orderInternalService')
class OrderInternalServiceImpl implements OrderInternalService {

    @Resource(name = 'orderFacadeContainer')
    FacadeContainer facadeContainer
    @Autowired
    OrderRepository orderRepository
    @Qualifier('orderTransactionHelper')
    @Autowired
    TransactionHelper transactionHelper
    @Qualifier('orderValidator')
    @Autowired
    OrderValidator orderValidator
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderInternalServiceImpl)

    @Override
    Promise<Order> rateOrder(Order order) {
        return facadeContainer.ratingFacade.rateOrder(order).recover { Throwable throwable ->
            LOGGER.error('name=Order_Rating_Error', throwable)
            // TODO parse the rating error
            throw AppErrors.INSTANCE.ratingConnectionError().exception()
        }.then { OrderRatingRequest ratingResult ->
            // todo handle rating violation
            if (ratingResult == null) {
                // TODO: log order charge action error?
                LOGGER.error('name=Rating_Result_Null')
                throw AppErrors.INSTANCE.ratingResultInvalid().exception()
            }
            CoreBuilder.fillRatingInfo(order, ratingResult)
            LOGGER.error('name=Rating_Result_Successfully')
            // no need to log order event for rating
            // call billing to calculate tax
            if (order.totalAmount == 0) {
                LOGGER.info('name=Skip_Calculate_Tax_Zero_Total_Amount')
                return Promise.pure(order)
            }
            if (CoreUtils.hasPhysicalOffer(order)) {
                // check whether the shipping address id are there
                if (order.shippingAddressId == null) {
                    if (order.tentative) {
                        LOGGER.info('name=Skip_Calculate_Tax_Without_shippingAddressId')
                        return Promise.pure(order)
                    }
                    LOGGER.error('name=Missing_shippingAddressId_To_Calculate_Tax')
                    throw AppErrors.INSTANCE.missingParameterField('shippingAddressId').exception()
                }
            } else {
                // check pi is there
                if (CollectionUtils.isEmpty(order.paymentInstruments)) {
                    if (order.tentative) {
                        LOGGER.info('name=Skip_Calculate_Tax_Without_PI')
                        return Promise.pure(order)
                    }
                    LOGGER.error('name=Missing_paymentInstruments_To_Calculate_Tax')
                    throw AppErrors.INSTANCE.missingParameterField('paymentInstruments').exception()
                }
            }
            return facadeContainer.billingFacade.quoteBalance(
                    CoreBuilder.buildBalance(order, BalanceType.DEBIT)).syncRecover {
                Throwable throwable ->
                    LOGGER.error('name=Fail_To_Calculate_Tax', throwable)
                    // TODO parse the tax error
                    throw AppErrors.INSTANCE.billingConnectionError().exception()
            }.then { Balance balance ->
                if (balance == null) {
                    // TODO: log order charge action error?
                    LOGGER.info('name=Fail_To_Calculate_Tax_Balance_Not_Found')
                    throw AppErrors.INSTANCE.balanceNotFound().exception()
                } else {
                    CoreBuilder.fillTaxInfo(order, balance)
                }
                return Promise.pure(order)
            }
        }
    }

    @Override
    Promise<Order> getOrderByOrderId(Long orderId) {
        if (orderId == null) {
            throw AppErrors.INSTANCE.fieldInvalid('orderId', 'orderId cannot be null').exception()
        }
        // get Order by id
        def order = orderRepository.getOrder(orderId)
        if (order == null) {
            throw AppErrors.INSTANCE.orderNotFound().exception()
        }
        return Promise.pure(completeOrder(order))
    }

    @Override
    Promise<List<Order>> getOrdersByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam) {

        if (userId == null) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', 'userId cannot be null').exception()
        }
        // get Orders by userId
        def orders = orderRepository.getOrdersByUserId(userId,
                ParamUtils.processOrderQueryParam(orderQueryParam),
                ParamUtils.processPageParam(pageParam))
        orders.each { Order order ->
            completeOrder(order)
        }
        return Promise.pure(orders)
    }

    @Override
    Order getOrderByTrackingUuid(UUID trackingUuid) {
        if (trackingUuid == null) {
            return null
        }
        def order = orderRepository.getOrderByTrackingUuid(trackingUuid)
        if (order != null) {
            completeOrder(order)
        }
        return order
    }

    private Order completeOrder(Order order) {
        // order items
        order.orderItems = orderRepository.getOrderItems(order.id.value)
        if (order.orderItems == null) {
            throw AppErrors.INSTANCE.orderItemNotFound().exception()
        }
        // payment instrument
        order.setPaymentInstruments(orderRepository.getPaymentInstrumentIds(order.id.value))
        // discount
        order.setDiscounts(orderRepository.getDiscounts(order.id.value))
        refreshOrderStatus(order)
        return order
    }

    @Override
    void refreshOrderStatus(Order order) {
        transactionHelper.executeInTransaction {
            def status = OrderStatusBuilder.buildOrderStatus(order,
                    orderRepository.getOrderEvents(order.id.value, null))
            if (status != order.status) {
                order.status = status
                orderRepository.updateOrder(order, true)
            }
        }
    }
}
