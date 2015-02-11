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
import com.junbo.common.id.OrderId
import com.junbo.entitlement.spec.model.Entitlement
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.clientproxy.model.ItemEntry
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.core.impl.common.*
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.core.impl.orderaction.context.OrderActionContext
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.error.ErrorUtils
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
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
/**
 * Created by chriszhu on 4/1/14.
 */
@CompileStatic
@TypeChecked
@Service('orderInternalService')
class OrderInternalServiceImpl implements OrderInternalService {

    private static final int DEFAULT_NUMBER_AFTER_DECIMAL = 5

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

    @Value('${order.store.offer.snapshot}')
    Boolean storeSnapshot

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderInternalServiceImpl)

    @Override
    Promise<Order> rateOrder(Order order, OrderServiceContext orderServiceContext) throws AppErrorException {
        LOGGER.info('name=internal.rateOrder')
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
                return validatePayments(orderServiceContext).then {
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
                    LOGGER.info('name=order_calculateTax')
                    return facadeContainer.billingFacade.quoteBalance(
                            CoreBuilder.buildBalance(order, BalanceType.DEBIT)).then { Balance balance ->
                        assert (balance != null)
                        CoreBuilder.fillTaxInfo(order, balance)
                        LOGGER.info('name=order_calculateTax_completed')
                        return Promise.pure(order)
                    }
                }
            }
        }.then { Order o ->
            LOGGER.info('name=internal.rateOrder_done')
            return Promise.pure(o)
        }
    }

    @Override
    @Transactional
    Promise<Order> getOrderByOrderId(Long orderId, OrderServiceContext orderServiceContext, Boolean updateOrderStatus) {
        LOGGER.info('name=internal.getOrderByOrderId')
        if (orderId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('orderId').exception()
        }
        // get Order by id
        def order = orderRepository.getOrder(orderId)
        if (order == null) {
            throw AppErrors.INSTANCE.orderNotFound().exception()
        }
        orderServiceContext.order = order
        return completeOrder(order, orderServiceContext, updateOrderStatus)
    }

    @Override
    Promise<Order> refundOrCancelOrder(Order order, OrderServiceContext context) {
        LOGGER.info('name=internal.refundOrCancelOrder')
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
        List<OrderItem> orderItemsBeforeRefund = []
        Promise promise2 = promise1.then { Order o ->
            existingOrder = o
            orderItemsBeforeRefund = CoreUtils.cloneOrderItems(existingOrder)
            return orderServiceContextBuilder.getCurrency(context)
        }

        Order diffOrder = null
        int numberAfterDecimal = DEFAULT_NUMBER_AFTER_DECIMAL
        com.junbo.identity.spec.v1.model.Currency orderCurrency
        Promise promise3 = promise2.then { com.junbo.identity.spec.v1.model.Currency currency ->
            orderCurrency = currency
            if (currency.numberAfterDecimal != null) {
                numberAfterDecimal = currency.numberAfterDecimal
            }
            diffOrder = CoreUtils.diffRefundOrder(existingOrder, order, numberAfterDecimal)
            return orderServiceContextBuilder.getBalances(context)
        }

        Promise promise4 = promise3.then { List<Balance> bls ->
            List<Balance> refundableBalances = bls.findAll { Balance bl ->
                (bl.type == BalanceType.DEBIT.name() || bl.type == BalanceType.MANUAL_CAPTURE.name()) &&
                        (bl.status == BalanceStatus.AWAITING_PAYMENT.name() ||
                                bl.status == BalanceStatus.COMPLETED.name() ||
                                bl.status == BalanceStatus.PENDING_CAPTURE.name() ||
                                bl.status == BalanceStatus.UNCONFIRMED.name() ||
                                bl.status == BalanceStatus.PENDING_RISK_REVIEW.name())
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
                        assert (isRefundable != isCancelable)
                        if (isRefundable) {
                            refundedOrder.status = OrderStatus.REFUNDED.name()
                            refundedOrder.isAudited = false
                            orderRepository.updateOrder(refundedOrder, false, true, OrderItemRevisionType.REFUND)
                            context.refundedOrderItems = CoreUtils.getRefundedOrderItems(orderItemsBeforeRefund, refundedOrder.orderItems, numberAfterDecimal)
                            // todo : need to whether it is the right way to set refundedOrderItems
                            persistBillingHistory(refunded, BillingAction.REQUEST_REFUND, order)
                        } else if (isCancelable) {
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
    Promise<Order> captureOrder(Order order, OrderServiceContext orderServiceContext) {
        LOGGER.info('name=internal.captureOrder')
        assert (order != null)
        return facadeContainer.billingFacade.getBalancesByOrderId(order.getId().value).then { List<Balance> balances ->
            Balance pendingBalance = balances.find { Balance balance ->
                balance.type == BalanceType.MANUAL_CAPTURE.name() &&
                        balance.status == BalanceStatus.PENDING_CAPTURE.name()
            }
            if (pendingBalance == null) {
                LOGGER.error('name=Capture_Failed_No_Balance_To_Capture.')
                throw AppErrors.INSTANCE.orderNotCapturable().exception()
            }
            return facadeContainer.billingFacade.captureBalance(pendingBalance)
                    .then { Balance capturedBalance ->
                if (CoreUtils.isBalanceSettled(capturedBalance)) {
                    persistBillingHistory(capturedBalance, BillingAction.CAPTURE, order)
                    orderRepository.updateOrder(order, true, true, null)
                }
                return getOrderByOrderId(order.getId().value, orderServiceContext, true)
            }
        }
    }

    @Override
    @Transactional
    Promise<List<Order>> getOrdersByUserId(Long userId, OrderServiceContext context, OrderQueryParam orderQueryParam, PageParam pageParam) {
        LOGGER.info('name=internal.getOrdersByUserId')
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
        LOGGER.info('name=internal.completeOrder')
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
        // orderSnapshot
        order.setOrderSnapshot(orderRepository.getSnapshot(order.getId().value))
        // tax
        return orderServiceContextBuilder.refreshBalances(orderServiceContext).then { List<Balance> balances ->
            if (CollectionUtils.isEmpty(balances) && !CoreUtils.isFreeOrder(order)) {
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
                return orderServiceContextBuilder.refreshFulfillmentRequest(orderServiceContext).then {
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
        LOGGER.info('name=internal.refreshOrderStatus')
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
        LOGGER.info('name=internal.markSettlement')
        def latest = orderRepository.getOrder(order.getId().value)
        if (latest == null) {
            throw AppErrors.INSTANCE.orderNotFound().exception()
        }
        if (!latest.tentative) {
            LOGGER.error("name=Already_Non_Tentative. orderId: " + order.getId().value)
            throw AppErrors.INSTANCE.orderAlreadyInSettleProcess().exception()
        } else {
            order.tentative = false
            orderRepository.updateOrder(order, true, false, null)
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void markErrorStatus(Order order) {
        LOGGER.info('name=internal.markErrorStatus')
        def latest = orderRepository.getOrder(order.getId().value)
        if (latest == null) {
            throw AppErrors.INSTANCE.orderNotFound().exception()
        }
        if (latest.status == OrderStatus.ERROR.name()) {
            LOGGER.info("name=Already_In_Error_Status. orderId: " + order.getId().value)
        } else {
            order.status = OrderStatus.ERROR.name()
            orderRepository.updateOrder(order, true, false, null)
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void markTentative(Order order) {
        LOGGER.info('name=internal.markTentative')
        def latest = orderRepository.getOrder(order.getId().value)
        if (latest == null) {
            throw AppErrors.INSTANCE.orderNotFound().exception()
        }
        if (latest.tentative) {
            LOGGER.error("name=Already_Tentative. orderId: " + order.getId().value)
        } else {
            order.tentative = true
            orderRepository.updateOrder(order, true, false, null)
        }
    }

    @Override
    @Transactional
    void persistBillingHistory(Balance balance, BillingAction action, Order order) {
        LOGGER.info('name=internal.persistBillingHistory')
        def billingHistory
        if (action == BillingAction.CHARGE) {
            billingHistory = BillingEventHistoryBuilder.buildBillingHistoryForImmediateSettle(balance)
        } else {
            billingHistory = BillingEventHistoryBuilder.buildBillingHistory(balance)
        }
        if (action != null) {
            billingHistory.billingEvent = action.name()
            if (action == BillingAction.REQUEST_REFUND) {
                billingHistory.note = order.note
            }
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

    @Transactional
    void persistOrderSnapshot(Order order) {
        LOGGER.info('name=internal.persistOrderSnapshot')
        if (!storeSnapshot || CollectionUtils.isEmpty(order.orderSnapshot)) {
            return
        }
        def offerSnapshots = []
        order.orderSnapshot.each { OfferSnapshot snapshot ->
            snapshot.orderId = order.getId().value
            def savedOfferSnapshot = orderRepository.createOfferSnapshot(snapshot)
            offerSnapshots << savedOfferSnapshot
        }
        order.orderSnapshot = offerSnapshots
    }

    @Override
    OrderEvent checkOrderEventStatus(Order order, OrderEvent event, List<Balance> balances) {
        LOGGER.info('name=internal.checkOrderEventStatus')
        if (event.action == OrderActionType.CHARGE.name()) {
            switch (event.status) {
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
        LOGGER.info('name=internal.auditTax')
        return facadeContainer.billingFacade.getBalancesByOrderId(order.getId().value).recover { Throwable throwable ->
            LOGGER.error('name=Tax_Audit_Fail', throwable)
            throw AppErrors.INSTANCE.billingAuditFailed('billing returns error: ' + throwable.message).exception()
        }.then { List<Balance> balances ->
            def balancesToBeAudited = balances.findAll { Balance balance ->
                CoreUtils.isBalanceSettled(balance) && TaxStatus.TAXED.name() == balance.taxStatus
            }
            if (CollectionUtils.isEmpty(balancesToBeAudited)) {
                LOGGER.error('name=No_Balance_Can_Be_Audit, orderId = {}', order.getId().value)
                throw AppErrors.INSTANCE.billingAuditFailed('no balance can be audited.').exception()
            }
            if (order.status == OrderStatus.REFUNDED.name()) {
                def hasRefundedBalance = balancesToBeAudited.any { Balance b ->
                    BalanceType.REFUND.name() == b.type
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
        LOGGER.info('name=internal.validatePayments')
        // validate payments
        if (CollectionUtils.isEmpty(orderServiceContext.order.payments)) {
            return Promise.pure(null)
        }
        return orderServiceContextBuilder.getPaymentInstruments(
                orderServiceContext).then { List<PaymentInstrument> pis ->
            // TODO: need double confirm whether this is the way to validate pi
            // TODO: validate pi after the pi status design is locked down.
            pis.each { PaymentInstrument pi ->
                orderValidator.validatePaymentInstrument(pi, orderServiceContext.order)
            }
            return Promise.pure(pis)
        }
    }

    Promise<PaymentInstrument> validateRefundPaymentInstrument(OrderServiceContext orderServiceContext) {
        LOGGER.info('name=internal.validateRefundPaymentInstrument')
        if (orderServiceContext.order.refundPaymentInstrument == null) {
            return Promise.pure(null)
        }
        Long piId = orderServiceContext.order.refundPaymentInstrument.value
        return facadeContainer.paymentFacade.getPaymentInstrument(piId).syncThen { PaymentInstrument pi ->
            orderValidator.validatePaymentInstrument(pi, orderServiceContext.order)
            return pi
        }
    }

    Promise<UserPersonalInfo> validateUserPersonalInfo(OrderServiceContext context) {
        LOGGER.info('name=internal.validateUserPersonalInfo')
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

    @Override
    Promise<Order> validateDuplicatePurchase(Order order, Offer offer, int quantity) {
        LOGGER.info('name=internal.validateDuplicatePurchase')
        if (offer.items.any { ItemEntry itemEntry ->
            !['APP', 'DOWNLOADED_ADDITION', 'PERMANENT_UNLOCK'].contains(itemEntry.item.type)
        }) {
            LOGGER.info("name=skip_validateDuplicatePurchase")
            return Promise.pure(order)
        }
        if (quantity > 1) {
            LOGGER.info("name=dup_purchase. offerId: {}, quantity: {}", offer.getId(), quantity)
            throw AppErrors.INSTANCE.duplicatePurchase().exception()
        }
        LOGGER.info("name=validateDuplicatePurchase_start_get_entitlements_by_user, userId={}", order.user.value)
        return facadeContainer.entitlementFacade.getEntitlements(order.user, offer)
                .syncThen { List<Entitlement> entitlements ->
            LOGGER.info("name=validateDuplicatePurchase_check_entitlement")
            if (entitlements == null || entitlements.size() == 0) {
                return order
            }
            def isDup = offer.items.every() { ItemEntry itemEntry ->
                return entitlements.any() { Entitlement entitlement ->
                    entitlement.itemId == itemEntry.item.itemId
                }
            }
            LOGGER.info("name=validateDuplicatePurchase_Complete, isDup={}", isDup)
            if (isDup) {
                LOGGER.info("name=dup_purchase. offerId: {}", offer.getId())
                throw AppErrors.INSTANCE.duplicatePurchase().exception()
            }
            return Promise.pure(order)
        }
    }

    @Override
    @Transactional
    Promise<Boolean> reverseFulfillment(Order order) {
        return facadeContainer.fulfillmentFacade.reverseFulfillment(order).then { FulfilmentRequest fr ->
            if (fr == null || CollectionUtils.isEmpty(fr.items)) {
                return Promise.pure(false)
            }
            def completed = true
            fr.items.each { FulfilmentItem fulfilmentItem ->
                def orderItem = order.orderItems?.find { OrderItem item ->
                    item.getId()?.value == fulfilmentItem.itemReferenceId
                }

                def revokeItems = orderItem?.orderItemRevisions?.findAll { OrderItemRevision oir ->
                    oir.revisionType == OrderItemRevisionType.REFUND.name() && !oir.revoked
                }

                if (CollectionUtils.isEmpty(revokeItems)) {
                    return
                }

                def fulfillmentHistory = FulfillmentEventHistoryBuilder.buildRevokeFulfillmentHistory(fr, fulfilmentItem)
                def fulfillmentEventStatus = FulfillmentEventHistoryBuilder.getRevokeFulfillmentEventStatus(fulfilmentItem)
                if (fulfillmentEventStatus != EventStatus.COMPLETED) {
                    LOGGER.error('name=Order_Failed_to_revoke_fulfillment. orderId: ' + order.getId().value)
                    completed = false
                }

                // update to revoked
                if (completed) {
                    revokeItems?.each { OrderItemRevision oir ->
                        oir.revoked = true
                    }
                    orderRepository.updateOrder(order, false, false, null)
                }

                // save fulfillment history
                if (fulfillmentHistory.fulfillmentEvent != null) {
                    def savedHistory = orderRepository.createFulfillmentHistory(fulfillmentHistory)
                    if (orderItem.fulfillmentHistories == null) {
                        orderItem.fulfillmentHistories = [savedHistory]
                    } else {
                        orderItem.fulfillmentHistories.add(savedHistory)
                    }
                }
            }
            return Promise.pure(completed)
        }
    }

    @Override
    Promise<com.junbo.langur.core.webflow.action.ActionResult> immediateSettle(Order order, OrderActionContext context) {
        String actionResultStr = null
        ActionResult actionResult = null
        return Promise.pure().then {
            return facadeContainer.billingFacade.createBalance(CoreBuilder.buildBalance(order, BalanceType.DEBIT), false)
        }.recover { Throwable ex ->
            // prepare action result for order event
            def appError = ErrorUtils.processBillingError(ex)
            if (appError.error().code == AppErrors.INSTANCE.billingConnectionError().error().code) {
                LOGGER.error('name=Order_Unexpected_Billing_Error_ImmediateSettle', ex)
                actionResultStr = 'ERROR'
                actionResult = CoreBuilder.buildActionResultForOrderEventAwareAction(context, EventStatus.ERROR, actionResultStr, appError)
                markErrorStatus(order)
            } else {
                LOGGER.info('name=Order_ImmediateSettle_Declined. reason: {}', ex.getMessage())
                actionResultStr = 'ROLLBACK'
                actionResult = CoreBuilder.buildActionResultForOrderEventAwareAction(context, EventStatus.FAILED, actionResultStr, appError)
                markTentative(order)
            }
            return Promise.pure(null)
        }.then { Balance balance ->
            if (balance == null) {
                // processed in recover
                assert actionResult != null
                return Promise.pure(actionResult)
            }

            if (balance.status == BalanceStatus.PENDING_RISK_REVIEW.name()) {
                LOGGER.info("name=Order_Pending_Risk_Review, orderId={}, balanceId={}", order.getId(), balance.getId())
                createOrderPendingAction(order.getId(), OrderPendingActionType.RISK_REVIEW)
            }

            EventStatus status = BillingEventHistoryBuilder.buildEventStatusFromImmediateSettle(balance)
            switch (status) {
                case EventStatus.COMPLETED:
                    LOGGER.info('name=Order_ImmediateSettle_Success. BalanceStatus: {}', balance.status)
                    actionResultStr = 'SUCCESS'
                    break
                case EventStatus.FAILED:
                    LOGGER.info('name=Order_ImmediateSettle_Declined. orderId: {}', order.getId().value)
                    actionResultStr = 'ROLLBACK'
                    markTentative(order)
                    break
                case EventStatus.ERROR:
                default:
                    LOGGER.error('name=Order_ImmediateSettle_UnexpectedStatus.BalanceStatus: {}', balance.status)
                    actionResultStr = 'ERROR'
                    markErrorStatus(order)
                    break
            }
            context.orderServiceContext.isAsyncCharge = balance.isAsyncCharge
            CoreBuilder.fillTaxInfo(order, balance)
            persistBillingHistory(balance, BillingAction.CHARGE, order)
            return orderServiceContextBuilder.refreshBalances(context.orderServiceContext).syncThen {
                return CoreBuilder.buildActionResultForOrderEventAwareAction(
                        context,
                        status,
                        actionResultStr)
            }
        }.recover { Throwable ex ->
            transactionHelper.executeInNewTransaction {
                // catch any exception during processing the balance result
                LOGGER.error('name=Order_Unexpected_Error_ImmediateSettle', ex)
                actionResultStr = 'ERROR'
                markErrorStatus(order)
                return Promise.pure(CoreBuilder.buildActionResultForOrderEventAwareAction(
                        context,
                        EventStatus.ERROR,
                        actionResultStr,
                        AppErrors.INSTANCE.billingConnectionError(ex.message)))
            }
        }
    }

    @Override
    OrderPendingAction createOrderPendingAction(OrderId orderId, OrderPendingActionType type) {
        orderRepository.createOrderPendingAction(new OrderPendingAction(
                orderId: orderId,
                completed: false,
                actionType: type.name()
        ))
    }
}
