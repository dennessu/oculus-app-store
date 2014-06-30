/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.core.impl.internal.impl
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.enums.PropertyKey
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.EntitlementId
import com.junbo.common.id.OfferId
import com.junbo.fulfilment.spec.constant.FulfilmentActionType
import com.junbo.fulfilment.spec.constant.FulfilmentStatus
import com.junbo.fulfilment.spec.model.FulfilmentAction
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.common.*
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.*
import com.junbo.order.spec.model.enums.BillingAction
import com.junbo.order.spec.model.enums.OrderItemRevisionType
import com.junbo.order.spec.model.enums.OrderStatus
import com.junbo.rating.spec.model.priceRating.RatingRequest
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
    OrderRepositoryFacade orderRepository
    @Qualifier('orderTransactionHelper')
    @Autowired
    TransactionHelper transactionHelper
    @Qualifier('orderValidator')
    @Autowired
    OrderValidator orderValidator
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderInternalServiceImpl)

    @Override
    Promise<Order> rateOrder(Order order) throws AppErrorException {
        LOGGER.info('name=OrderInternalServiceImpl_Rate_Order')
        return facadeContainer.ratingFacade.rateOrder(order).then { RatingRequest ratingResult ->
            // todo handle rating violation
            assert (ratingResult != null)
            CoreBuilder.fillRatingInfo(order, ratingResult)
            LOGGER.info('name=OrderInternalServiceImpl_Get_Rating_Result_Successfully')
            // call billing to calculate tax
            if (CoreUtils.isFreeOrder(order)) {
                LOGGER.info('name=Skip_Calculate_Tax_Zero_Total_Amount')
                return Promise.pure(order)
            }
            if (CoreUtils.hasStoredValueOffer(order)) {
                LOGGER.info('name=Skip_Calculate_Tax_Credit_Stored_Value')
                return Promise.pure(order)
            }
            // validate the tax calculation precondition
            // check pi is there, it means the billing address is there.
            if (CollectionUtils.isEmpty(order.payments)) {
                if (order.tentative) {
                    LOGGER.info('name=Skip_Calculate_Tax_Without_PI')
                    return Promise.pure(order)
                }
                LOGGER.error('name=Missing_paymentInstruments_To_Calculate_Tax')
                throw AppErrors.INSTANCE.missingParameterField('paymentInstruments').exception()
            }
            if (CoreUtils.hasPhysicalOffer(order)) {
                // check whether the shipping address id are there
                if (order.shippingAddress == null) {
                    if (order.tentative) {
                        LOGGER.info('name=Skip_Calculate_Tax_Without_shippingAddressId')
                        return Promise.pure(order)
                    }
                    LOGGER.error('name=Missing_shippingAddressId_To_Calculate_Tax')
                    throw AppErrors.INSTANCE.missingParameterField('shippingAddressId').exception()
                }
            }
            // calculateTax
            return facadeContainer.billingFacade.quoteBalance(
                    CoreBuilder.buildBalance(order, BalanceType.DEBIT)).then { Balance balance ->
                assert (balance != null)
                CoreBuilder.fillTaxInfo(order, balance)
                return Promise.pure(order)
            }
        }
    }

    @Override
    @Transactional
    Promise<Order> getOrderByOrderId(Long orderId) {
        if (orderId == null) {
            throw AppErrors.INSTANCE.fieldInvalid('orderId', 'orderId cannot be null').exception()
        }
        // get Order by id
        def order = orderRepository.getOrder(orderId)
        if (order == null) {
            throw AppErrors.INSTANCE.orderNotFound().exception()
        }
        return completeOrder(order)
    }

    @Override
    @Transactional
    Promise<Order> cancelOrder(Order order) {

        assert(order != null)

        def isCancelable = CoreUtils.checkOrderStatusCancelable(order)
        if (!isCancelable) {
            LOGGER.info('name=Order_Is_Not_Cancelable, orderId = {}, orderStatus={}', order.getId().value, order.status)
            throw AppErrors.INSTANCE.orderNotCancelable().exception()
        }
        LOGGER.info('name=Order_Is_Cancelable, orderId = {}, orderStatus={}', order.getId().value, order.status)

        order.status = OrderStatus.CANCELED.name()
        orderRepository.updateOrder(order, false, true, OrderItemRevisionType.CANCEL)

        // TODO: reverse authorize if physical goods

        // TODO: cancel paypal confirm

        if (order.status == OrderStatus.PREORDERED.name()) {
            return refundDeposit(order).then { Order o ->
                return Promise.pure(o)
            }
        }
        return Promise.pure(order)
    }

    @Override
    @Transactional
    Promise<Order> refundOrder(Order order) {

        assert (order != null)

        def isRefundable = CoreUtils.checkOrderStatusRefundable(order)
        if (!isRefundable) {
            LOGGER.info('name=Order_Is_Not_Refundable, orderId = {}, orderStatus={}', order.getId().value, order.status)
            throw AppErrors.INSTANCE.orderNotRefundable().exception()
        }
        LOGGER.info('name=Order_Is_Refundable, orderId = {}, orderStatus={}', order.getId().value, order.status)

        return getOrderByOrderId(order.getId().value).then { Order existingOrder ->

            return facadeContainer.identityFacade.getCurrency(existingOrder.currency.value).then {
                com.junbo.identity.spec.v1.model.Currency currency ->

                    Order diffOrder = CoreUtils.diffRefundOrder(existingOrder, order, currency.numberAfterDecimal)
                    return facadeContainer.billingFacade.getBalancesByOrderId(order.getId().value).syncRecover {
                        Throwable ex ->
                    }.then { List<Balance> bls ->

                        List<Balance> refundableBalances = bls.findAll { Balance bl ->
                            (bl.type == BalanceType.DEBIT.name() || bl.type == BalanceType.MANUAL_CAPTURE.name()) &&
                                    (bl.status == BalanceStatus.AWAITING_PAYMENT.name() ||
                                            bl.status == BalanceStatus.COMPLETED.name())
                        }.toList()

                        // preorder: deposit+deposit
                        // physical goods: manual_capture/deposit
                        // digital: deposit
                        List<Balance> refundBalances = CoreBuilder.buildRefundBalances(refundableBalances, diffOrder)
                        if (CollectionUtils.isEmpty(refundBalances)) {
                            throw AppErrors.INSTANCE.orderNotRefundable().exception()
                        }
                        assert (refundBalances.size() <= 2)

                        return Promise.each(refundBalances) { Balance refundBalance ->
                            return facadeContainer.billingFacade.createBalance(refundBalance, true).syncRecover {
                                Throwable ex ->
                                    LOGGER.error('name=Refund_Failed', ex)
                                    throw AppErrors.INSTANCE.
                                            billingRefundFailed('billing returns error: ' + ex.message).exception()
                            }.then { Balance refunded ->
                                if (refunded == null) {
                                    throw AppErrors.INSTANCE.billingRefundFailed('billing returns null balance').exception()
                                }
                                def refundedOrder = CoreUtils.calcRefundedOrder(existingOrder, refunded, diffOrder)
                                refundedOrder.status = OrderStatus.REFUNDED.name()
                                orderRepository.updateOrder(refundedOrder, false, true, OrderItemRevisionType.REFUND)
                                // TODO handel multiple balances
                                persistBillingHistory(refunded, BillingAction.REFUND, order)
                                return Promise.pure(null)
                            }
                        }
                    }
            }
        }.then {
            return getOrderByOrderId(order.getId().value)
        }
    }

    @Override
    Promise<Void> refundDeposit(Order order) {
        if (CoreUtils.isFreeOrder(order)) {
            return Promise.pure(null)
        }
        BillingHistory bh = CoreUtils.getLatestBillingHistory(order)
        assert(bh != null)
        if (bh.billingEvent == BillingAction.DEPOSIT) {
            return facadeContainer.billingFacade.getBalancesByOrderId(order.getId().value).syncRecover {
                Throwable ex ->
            }.then { List<Balance> bls ->
                if (CoreUtils.checkDepositOrderRefundable(order, bls)) {
                    return facadeContainer.billingFacade.createBalance(
                            CoreBuilder.buildRefundDepositBalance(bls[0]), false).syncRecover { Throwable ex ->
                    }.then { Balance refunded ->
                        if (refunded != null) {
                            throw AppErrors.INSTANCE.billingChargeFailed().exception()
                        }
                        return Promise.pure(null)
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    Promise<List<Balance>> getBalancesByOrderId(Long orderId) {
        return facadeContainer.billingFacade.getBalancesByOrderId(orderId).syncRecover { Throwable throwable ->
            LOGGER.error('name=Get_Balances_Error', throwable)
            throw facadeContainer.billingFacade.convertError(throwable).exception()
        }.then { List<Balance> balances ->
            return Promise.pure(balances)
        }
    }

    @Override
    @Transactional
    Promise<List<Order>> getOrdersByUserId(Long userId, OrderQueryParam orderQueryParam, PageParam pageParam) {

        if (userId == null) {
            throw AppErrors.INSTANCE.fieldInvalid('userId', 'userId cannot be null').exception()
        }
        // get Orders by userId
        def orders = orderRepository.getOrdersByUserId(userId,
                ParamUtils.processOrderQueryParam(orderQueryParam),
                ParamUtils.processPageParam(pageParam))
        return Promise.each(orders) { Order order ->
            return completeOrder(order)
        }.then {
            return Promise.pure(orders)
        }
    }

    Promise<Order> completeOrder(Order order) {
        // order items
        def hasFulfillment = false
        order.orderItems = orderRepository.getOrderItems(order.getId().value)
        if (!CollectionUtils.isEmpty(order.orderItems)) {
            order.orderItems.each { OrderItem orderItem ->
                List<PreorderInfo> preorderInfoList = orderRepository.getPreorderInfo(orderItem.getId().value)
                if (preorderInfoList?.size() > 0) {
                    orderItem.preorderInfo = preorderInfoList[0]
                }
                orderItem.fulfillmentHistories = orderRepository.getFulfillmentHistories(orderItem.getId().value)
                if (!CollectionUtils.isEmpty(orderItem.fulfillmentHistories)) {
                    hasFulfillment = true
                }
            }
        }
        // discount
        order.setDiscounts(orderRepository.getDiscounts(order.getId().value))
        // tax
        return facadeContainer.billingFacade.getBalancesByOrderId(order.getId().value).then { List<Balance> balances ->
            def taxedBalances = balances.findAll { Balance balance ->
                balance.balanceItems?.taxItems?.size() > 0
            }.toList()
            if (!CollectionUtils.isEmpty(taxedBalances)) {
                order.orderItems?.each { OrderItem item ->
                    CoreBuilder.mergeTaxInfo(taxedBalances, item)
                }
            }
            order.setBillingHistories(orderRepository.getBillingHistories(order.getId().value))
            if (!CollectionUtils.isEmpty(order.billingHistories)) {
                // fill balance info
                order.billingHistories.each { BillingHistory bh ->
                    def balance = balances.find() { Balance ba ->
                        ba.getId().value == Long.parseLong(bh.balanceId)
                    }
                    assert (balance != null)
                    bh.payments = []
                    def payment = new BillingPaymentInfo()
                    payment.paymentAmount = balance.totalAmount
                    payment.paymentInstrument = balance.piId
                    bh.payments << payment

                    if (balance.type == BalanceType.REFUND.name()) {
                        bh.refundedOrderItems = []
                        balance.balanceItems?.each { BalanceItem bi ->
                            def roi = new RefundOrderItem()
                            roi.orderItemId = bi.orderItemId.value
                            roi.refundedAmount = 0G - bi.amount
                            roi.refundedTax = 0G - bi.taxAmount
                            // fill quantity and offer id
                            roi.offer = new OfferId(bi.propertySet.get(PropertyKey.OFFER_ID.name()))
                            roi.quantity = Integer.parseInt(bi.propertySet.get(PropertyKey.ITEM_QUANTITY.name()))
                            bh.refundedOrderItems << roi
                        }
                        payment.paymentAmount = 0G - balance.totalAmount
                    }
                }
            }
            return Promise.pure(order)
        }.then {
            if (!hasFulfillment) {
                return Promise.pure(order)
            }
            // fill fulfillment histories
            return facadeContainer.fulfillmentFacade.getFulfillment(order.getId()).then { FulfilmentRequest fr ->
                if (fr == null) {
                    return Promise.pure(order)
                }
                fr.items?.collect() { FulfilmentItem fi ->
                    def fulfillOrderItem = order.orderItems?.find() { OrderItem oi ->
                        oi.getId().value == fi.orderItemId
                    }
                    if (fulfillOrderItem != null) {
                        fulfillOrderItem.fulfillmentHistories.collect() { FulfillmentHistory fh ->
                            fh.coupons = []
                            fh.walletAmount = 0G
                            fh.shippingDetails = []
                            fh.entitlements = []
                            // TODO: parse shippingDetails and coupons
                            def entitlementAction = fi.actions?.find() { FulfilmentAction fa ->
                                fa.type == FulfilmentActionType.GRANT_ENTITLEMENT &&
                                        fa.status != FulfilmentStatus.FAILED
                            }
                            if (entitlementAction != null) {
                                entitlementAction.result?.entitlementIds?.each { String eid ->
                                    fh.entitlements << new EntitlementId(eid)
                                }
                            }
                            def walletAction = fi.actions?.find() { FulfilmentAction fa ->
                                fa.type == FulfilmentActionType.CREDIT_WALLET &&
                                        fa.status != FulfilmentStatus.FAILED
                            }
                            if (walletAction != null) {
                                fh.walletAmount = walletAction.result.amount
                            }
                        }
                    }
                }
                return Promise.pure(order)
            }
        }
    }

    @Override
    Order refreshOrderStatus(Order order, boolean updateOrder) {
        transactionHelper.executeInTransaction {
            def oldOrder = orderRepository.getOrder(order.getId().value)
            if (oldOrder == null) {
                throw AppErrors.INSTANCE.orderNotFound().exception()
            }
            order.status = OrderStatusBuilder.buildOrderStatus(oldOrder,
                    orderRepository.getOrderEvents(order.getId().value, null))
            if (updateOrder && order.status != oldOrder.status) {
                oldOrder.status = order.status
                orderRepository.updateOrder(oldOrder, true, false, null)
            }
        }
        return order
    }

    @Override
    @Transactional
    void markSettlement(Order order) {

        def latest = orderRepository.getOrder(order.getId().value)
        if (latest == null) {
            throw AppErrors.INSTANCE.orderNotFound().exception()
        }

        if (!latest?.tentative) {
            LOGGER.info("name=Already_Tentative")
        } else {
            order.tentative = false
            orderRepository.updateOrder(order, true, false, null)
        }
    }

    @Override
    @Transactional
    void persistBillingHistory(Balance balance, BillingAction action, Order order) {
        def billingHistory = BillingEventHistoryBuilder.buildBillingHistory(balance)
        if (billingHistory.billingEvent != null) {
            def savedHistory = orderRepository.createBillingHistory(order.getId().value, billingHistory)
            if (order.billingHistories == null) {
                order.billingHistories = [savedHistory]
            }
            else {
                order.billingHistories.add(savedHistory)
            }
        }
    }
}
