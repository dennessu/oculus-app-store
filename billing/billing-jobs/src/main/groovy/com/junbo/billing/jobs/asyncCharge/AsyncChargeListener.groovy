/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.jobs.asyncCharge

import com.junbo.billing.jobs.clientproxy.BillingFacade
import com.junbo.billing.jobs.clientproxy.OrderFacade
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.model.Balance
import com.junbo.common.id.BalanceId
import com.junbo.langur.core.promise.Promise
import com.junbo.notification.core.BaseListener
import com.junbo.order.spec.model.OrderEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by xmchen on 14-4-21.
 */
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
        balance.setBalanceId(new BalanceId(balanceId))

        billingFacade.processAsyncBalance(balance).recover { Throwable throwable ->
            LOGGER.error('Error in processing async charge balance', throwable)
            throw throwable
        }.then { Balance processedBalance ->
            LOGGER.info('The processed balance status is ' + processedBalance.status + 'for balance id: ' +
                    processedBalance.balanceId.value)

            OrderEvent orderEvent = generateOrderEvent(processedBalance)
            if (orderEvent != null) {
                orderFacade.postOrderEvent(orderEvent)
            }

            return Promise.pure(processedBalance)
        }
    }

    private OrderEvent generateOrderEvent(Balance balance) {
        OrderEvent orderEvent = new OrderEvent()
        orderEvent.setOrder(balance.orderId)

        if (balance.type == BalanceType.DEBIT.name()) {
            orderEvent.setAction('CHARGE')
        } else if (balance.type == BalanceType.MANUAL_CAPTURE.name()) {
            orderEvent.setAction('AUTHORIZE')
        } else if (balance.type == BalanceType.REFUND.name()) {
            orderEvent.setAction('REFUND')
        } else {
            LOGGER.error('unsupported balance type to post order event: ' + balance.type)
            return null
        }

        switch (balance.status) {
            case BalanceStatus.PENDING_CAPTURE:
            case BalanceStatus.AWAITING_PAYMENT:
            case BalanceStatus.COMPLETED:
                orderEvent.setStatus('COMPLETED')
                break
            case BalanceStatus.ERROR:
                orderEvent.setStatus('ERROR')
                break
            case BalanceStatus.FAILED:
                orderEvent.setStatus('FAILED')
                break
            default:
                LOGGER.error('unsupported balance status to post order event: ' + balance.status)
                return null
        }
        return orderEvent
    }
}
