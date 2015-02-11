/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.listener.asyncCharge

import com.junbo.billing.listener.clientproxy.BillingFacade
import com.junbo.billing.listener.clientproxy.OrderFacade
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.common.id.BalanceId
import com.junbo.common.id.OrderId
import com.junbo.langur.core.promise.Promise
import com.junbo.notification.core.BaseListener
import com.junbo.order.spec.model.OrderEvent
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by xmchen on 14-4-21.
 */
@CompileStatic
class AsyncChargeListener extends BaseListener {

    @Autowired
    BillingFacade billingFacade

    @Autowired
    OrderFacade orderFacade

    static final Logger LOGGER = LoggerFactory.getLogger(AsyncChargeListener.class)

    protected void onMessage(final String eventId, final String content) {
        LOGGER.info('Receive a message with event id: ' + eventId)
        Long balanceId
        try {
            balanceId = Long.parseLong(content)
        } catch(NumberFormatException ex) {
            throw ex
        }

        LOGGER.info('Sending async charge process request for balance id: ' + balanceId)
        Balance balance = new Balance()
        balance.setId(new BalanceId(balanceId))

        billingFacade.processAsyncBalance(balance).recover { Throwable throwable ->
            LOGGER.error('Error in processing async charge balance', throwable)
            throw throwable
        }.then { Balance processedBalance ->
            LOGGER.info('The processed balance status is ' + processedBalance.status + 'for balance id: ' +
                    processedBalance.getId().toString())

            List<OrderEvent> orderEvents = generateOrderEvents(processedBalance)
            if (orderEvents != null) {
                for (OrderEvent orderEvent : orderEvents) {
                    orderFacade.postOrderEvent(orderEvent)
                }
            }

            return Promise.pure(processedBalance)
        }
    }

    private List<OrderEvent> generateOrderEvents(Balance balance) {
        String action = null
        String status = null
        if (balance.type == BalanceType.DEBIT.name()) {
            action = 'CHARGE'
        } else if (balance.type == BalanceType.MANUAL_CAPTURE.name()) {
            action = 'AUTHORIZE'
        } else if (balance.type == BalanceType.REFUND.name()) {
            action = 'REFUND'
        } else {
            LOGGER.error('unsupported balance type to post order event: ' + balance.type)
            return null
        }

        switch (balance.status) {
            case BalanceStatus.PENDING_CAPTURE.name():
            case BalanceStatus.AWAITING_PAYMENT.name():
            case BalanceStatus.COMPLETED.name():
                status = 'COMPLETED'
                break
            case BalanceStatus.ERROR.name():
                status = 'ERROR'
                break
            case BalanceStatus.FAILED.name():
                status = 'FAILED'
                break
            default:
                LOGGER.error('unsupported balance status to post order event: ' + balance.status)
                return null
        }

        List<OrderEvent> orderEvents = new ArrayList<>()

        if (balance.orderIds == null || balance.orderIds.size() == 0) {
            LOGGER.error('there is no order in this balance: ' + balance.getId().toString())
            return null
        }

        for (OrderId orderId : balance.orderIds) {
            OrderEvent orderEvent = new OrderEvent()
            orderEvent.setOrder(orderId)
            orderEvent.setAction(action)
            orderEvent.setStatus(status)
            orderEvent.billingInfo = new OrderEvent.BillingInfo()
            orderEvent.billingInfo.balance = balance.getId()
            orderEvents.add(orderEvent)
        }

        return orderEvents
    }
}
