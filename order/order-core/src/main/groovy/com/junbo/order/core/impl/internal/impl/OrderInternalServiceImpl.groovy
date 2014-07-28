/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.core.impl.internal.impl
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.enums.TaxStatus
import com.junbo.billing.spec.model.Balance
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.PIType
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.common.*
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.*
import com.junbo.order.spec.model.enums.*
import com.junbo.payment.spec.model.PaymentInstrument
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
    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder orderServiceContextBuilder

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderInternalServiceImpl)

    @Override
    Promise<Order> rateOrder(Order order, OrderServiceContext orderServiceContext) throws AppErrorException {
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
                throw AppCommonErrors.INSTANCE.parameterRequired('paymentInstruments').exception()
            } else {
                // calculate tax
                validatePayments(orderServiceContext).then {
                    if (CoreUtils.hasPhysicalOffer(order)) {
                        // check whether the shipping address id are there
                        if (order.shippingAddress == null) {
                            if (order.tentative) {
                                LOGGER.info('name=Skip_Calculate_Tax_Without_shippingAddressId')
                                return Promise.pure(order)
                            }
                            LOGGER.error('name=Missing_shippingAddressId_To_Calculate_Tax')
                            throw AppCommonErrors.INSTANCE.parameterRequired('shippingAddressId').exception()
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
        }
    }

    @Override
    @Transactional
    Promise<Order> getOrderByOrderId(Long orderId, OrderServiceContext orderServiceContext, Boolean updateOrderStatus) {
        if (orderId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('orderId').exception()
        }
        // get Order by id
        def order = orderRepository.getOrder(orderId)
        if (order == null) {
            throw AppErrors.INSTANCE.orderNotFound().exception()
        }
        orderServiceContext.order = order
        completeOrder(order, orderServiceContext, updateOrderStatus)
    }

    @Override
    Promise<Order> refundOrCancelOrder(Order order, OrderServiceContext context) {

        assert (order != null)

        Boolean isRefundable = CoreUtils.isRefundable(order)
        Boolean isCancelable = CoreUtils.isCancellable(order)

        if (!isRefundable && !isCancelable) {
            LOGGER.info('name=Order_Is_Not_Refundable_Or_Cancelable, orderId = {}, orderStatus={}', order.getId().value, order.status)
            throw AppErrors.INSTANCE.orderNotRefundable().exception()
        }
        LOGGER.info('name=Order_Is_Refundable_Or_Cancelable, orderId = {}, orderStatus={}', order.getId().value, order.status)

        // @Transactional
        Promise promise1 = getOrderByOrderId(order.getId().value, context, false)

        Order existingOrder = null
        Promise promise2 = promise1.then { Order o ->
            existingOrder = o
            return orderServiceContextBuilder.getCurrency(context)
        }

        Order diffOrder = null
        com.junbo.identity.spec.v1.model.Currency orderCurrency
        Promise promise3 = promise2.then { com.junbo.identity.spec.v1.model.Currency currency ->
            orderCurrency = currency
            diffOrder = CoreUtils.diffRefundOrder(existingOrder, order, currency.numberAfterDecimal)
            return orderServiceContextBuilder.getBalances(context)
        }

        Promise promise4 = promise3.then { List<Balance> bls ->
            List<Balance> refundableBalances = bls.findAll { Balance bl ->
                (bl.type == BalanceType.DEBIT.name() || bl.type == BalanceType.MANUAL_CAPTURE.name()) &&
                        (bl.status == BalanceStatus.AWAITING_PAYMENT.name() ||
                                bl.status == BalanceStatus.COMPLETED.name() ||
                                bl.status == BalanceStatus.PENDING_CAPTURE.name() ||
                                bl.status == BalanceStatus.UNCONFIRMED.name())
            }.toList()
            // preorder: deposit+deposit
            // physical goods: manual_capture/deposit
            // digital: deposit
            List<Balance> refundBalances = CoreBuilder.buildRefundBalances(refundableBalances, diffOrder)
            if (CollectionUtils.isEmpty(refundBalances)) {
                throw AppErrors.INSTANCE.orderNotRefundable().exception()
            }
            assert (refundBalances.size() <= 2)
            return Promise.pure(refundBalances)
        }

        return promise4.then { List<Balance> refundBalances ->
            return Promise.each(refundBalances) { Balance refundBalance ->
                return transactionHelper.executeInTransaction {
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
                        assert(isRefundable != isCancelable)
                        if(isRefundable) {
                            refundedOrder.status = OrderStatus.REFUNDED.name()
                            refundedOrder.isAudited = false
                            orderRepository.updateOrder(refundedOrder, false, true, OrderItemRevisionType.REFUND)
                            context.refundedOrderItems = diffOrder.orderItems // todo : need to whether it is the right way to set refundedOrderItems
                            persistBillingHistory(refunded, BillingAction.REQUEST_REFUND, order)
                        } else if(isCancelable) {
                            refundedOrder.status = OrderStatus.CANCELED.name()
                            orderRepository.updateOrder(refundedOrder, false, true, OrderItemRevisionType.CANCEL)
                            persistBillingHistory(refunded, BillingAction.REQUEST_CANCEL, order)
                        }
                        return Promise.pure(null)
                    }
                }
            }
        }.then {
            return getOrderByOrderId(order.getId().value, context, true)
        }
    }

    @Override
    @Transactional
    Promise<List<Order>> getOrdersByUserId(Long userId, OrderServiceContext context, OrderQueryParam orderQueryParam, PageParam pageParam) {

        if (userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }
        // get Orders by userId
        def orders = orderRepository.getOrdersByUserId(userId,
                ParamUtils.processOrderQueryParam(orderQueryParam),
                ParamUtils.processPageParam(pageParam))
        return Promise.each(orders) { Order order ->
            context.order = order
            return completeOrder(order, context, false)
        }.then {
            return Promise.pure(orders)
        }
    }

    // readonly
    private Promise<Order> completeOrder(Order order, OrderServiceContext orderServiceContext, Boolean updateOrderStatus) {
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
        return orderServiceContextBuilder.refreshBalances(orderServiceContext).then { List<Balance> balances ->
            if (CollectionUtils.isEmpty(balances)) {
                return Promise.pure(order)
            }
            def taxedBalances = balances.findAll { Balance balance ->
                balance.balanceItems?.taxItems?.size() > 0
            }.toList()
            if (!CollectionUtils.isEmpty(taxedBalances)) {
                order.orderItems?.each { OrderItem item ->
                    CoreBuilder.mergeTaxInfo(taxedBalances, item)
                }
            }
            order.setBillingHistories(orderRepository.getBillingHistories(order.getId().value))
            CoreUtils.refreshBillingHistories(order, balances)
            if (hasFulfillment) {
                return orderServiceContextBuilder.getFulfillmentRequest(orderServiceContext).then {
                    FulfilmentRequest fr ->
                        CoreUtils.refreshFulfillmentHistories(order, fr)
                        return Promise.pure(order)
                }
            }
            return Promise.pure(order)
        }.then {
            if (updateOrderStatus) {
                return Promise.pure(refreshOrderStatus(order, true))
            }
            return Promise.pure(order)
        }
    }

    @Override
    @Transactional
    Order refreshOrderStatus(Order order, boolean updateOrder) {
        def oldStatus = order.status
        order.status = OrderStatusBuilder.buildOrderStatus(order)
        if (updateOrder && order.status != oldStatus) {
            orderRepository.updateOrder(order, true, false, null)
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
        if(action != null) {
            billingHistory.billingEvent = action.name()
        }
        if (billingHistory.billingEvent != null) {
            def savedHistory = orderRepository.createBillingHistory(order.getId().value, billingHistory)
            if (order.billingHistories == null) {
                order.billingHistories = [savedHistory]
            } else {
                order.billingHistories.add(savedHistory)
            }
        }
    }

    @Override
    OrderEvent checkOrderEventStatus(Order order, OrderEvent event, List<Balance> balances) {
        if (event.action == OrderActionType.CHARGE.name()) {
            switch(event.status) {
                case EventStatus.COMPLETED.name():
                    if (!CoreUtils.isChargeCompleted(balances)) {
                        throw AppErrors.INSTANCE.orderEvenStatusNotMatch().exception()
                    }
                    return event
                case EventStatus.FAILED.name():
                    if (!CoreUtils.isChargeFailed(balances)) {
                        throw AppErrors.INSTANCE.orderEvenStatusNotMatch().exception()
                    }
                    def failedBalance = balances.find { Balance b ->
                        b.status = BalanceStatus.FAILED.name() && b.type == BalanceType.DEBIT.name()
                    }
                    // persist charge failed billing event
                    persistBillingHistory(failedBalance, BillingAction.CHARGE, order)
                    return event
                default:
                    throw AppErrors.INSTANCE.eventNotSupported(event.action, event.status).exception()
            }
        }
        if (event.action == OrderActionType.FULFILL.name()) {
            // TODO
        }
        return event
    }

    @Override
    @Transactional
    Promise<Order> auditTax(Order order) {
        return facadeContainer.billingFacade.getBalancesByOrderId(order.getId().value).recover { Throwable throwable ->
            LOGGER.error('name=Tax_Audit_Fail', throwable)
            throw AppErrors.INSTANCE.billingAuditFailed('billing returns error: ' + throwable.message).exception()
        }.then { List<Balance> balances ->
            def balancesToBeAudited = balances.findAll { Balance balance ->
                (BalanceStatus.COMPLETED.name() == balance.status ||
                        BalanceStatus.AWAITING_PAYMENT.name() == balance.status) &&
                        TaxStatus.TAXED.name() == balance.taxStatus
            }
            if (CollectionUtils.isEmpty(balancesToBeAudited)) {
                LOGGER.error('name=No_Balance_Can_Be_Audit, orderId = {}', order.getId().value)
                throw AppErrors.INSTANCE.billingAuditFailed('no balance can be audited.').exception()
            }
            if (order.status == OrderStatus.REFUNDED.name()) {
                def hasRefundedBalance = balancesToBeAudited.any { Balance b ->
                    b.type == BalanceType.REFUND.name()
                }
                if (!hasRefundedBalance) {
                    LOGGER.error('name=No_Refund_Balance_Can_Be_Audit, orderId={}', order.getId().value)
                    throw AppErrors.INSTANCE.billingAuditFailed('no refund balance can be audited.').exception()
                }
            }
            def auditedBalances = []
            return Promise.each(balancesToBeAudited) { Balance balanceToBeAudited ->
                return facadeContainer.billingFacade.auditBalance(balanceToBeAudited).recover { Throwable throwable ->
                    LOGGER.error('name=Tax_Audit_Fail', throwable)
                }.then { Balance auditedBalance ->
                    if (TaxStatus.AUDITED.name() == auditedBalance.taxStatus) {
                        auditedBalances << auditedBalance
                        return Promise.pure(null)
                    }
                }
            }.then {
                if (balancesToBeAudited?.size() == auditedBalances.size()) {
                    order.isAudited = true
                    orderRepository.updateOrder(order, true, true, null)
                }
                return Promise.pure(order)
            }
        }
    }

    Promise<List<PaymentInstrument>> validatePayments(OrderServiceContext orderServiceContext) {
        // validate payments
        if (CollectionUtils.isEmpty(orderServiceContext.order.payments)) {
            return Promise.pure(null)
        }
        return orderServiceContextBuilder.getPaymentInstruments(
                orderServiceContext).then { List<PaymentInstrument> pis ->
            // TODO: need double confirm whether this is the way to validate pi
            // TODO: validate pi after the pi status design is locked down.
            pis.each { PaymentInstrument pi ->
                if (pi.userId != null && orderServiceContext.order.user.value != pi.userId) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid(
                            'payments', 'do not belong to this user').exception()
                }
                if (PIType.get(pi.type) == PIType.CREDITCARD) {
                    Date expireDate = CoreBuilder.DATE_FORMATTER.get().parse(pi.typeSpecificDetails.expireDate)
                    Date now = new Date()
                    if (expireDate.before(now)) {
                        throw AppCommonErrors.INSTANCE.fieldInvalid(
                                'payments', 'PI expired').exception()
                    }
                }

            }
            return Promise.pure(pis)
        }
    }

    Promise<UserPersonalInfo> validateUserPersonalInfo(OrderServiceContext context) {
        def order = context.order
        return facadeContainer.identityFacade.getUserPersonalInfo(order.shippingAddress)
                .then { UserPersonalInfo shippingAddressInfo ->
            if (shippingAddressInfo != null && order.user != shippingAddressInfo.userId) {
                throw AppCommonErrors.INSTANCE.fieldInvalid(
                        'shippingAddressInfo', 'do not belong to this user').exception()
            }
            return facadeContainer.identityFacade.getUserPersonalInfo(order.shippingToName)
                    .then { UserPersonalInfo shippingToNameInfo ->
                if (shippingToNameInfo != null && order.user != shippingToNameInfo.userId) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid(
                            'shippingToName', 'do not belong to this user').exception()
                }
                return facadeContainer.identityFacade.getUserPersonalInfo(order.shippingToPhone)
                        .then { UserPersonalInfo shippingToPhoneInfo ->
                    if (shippingToPhoneInfo != null && order.user != shippingToPhoneInfo.userId) {
                        throw AppCommonErrors.INSTANCE.fieldInvalid(
                                'shippingToPhone', 'do not belong to this user').exception()
                    }
                    return Promise.pure(null)
                }
            }
        }
    }
}
