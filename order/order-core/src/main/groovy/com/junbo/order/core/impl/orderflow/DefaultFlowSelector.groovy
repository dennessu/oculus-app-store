/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.orderflow

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.PIType
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.FlowType
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.OrderActionType
import com.junbo.order.spec.model.enums.OrderStatus
import com.junbo.order.spec.error.AppErrors
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
@TypeChecked
@Component('defaultFlowSelector')
class DefaultFlowSelector implements FlowSelector {

    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultFlowSelector)

    @Autowired
    OrderServiceContextBuilder orderServiceContextBuilder

    @Override
    Promise<String> select(OrderServiceContext context, OrderServiceOperation operation) throws AppErrorException {
        assert(context != null && operation != null)
        switch (operation) {
            case OrderServiceOperation.CREATE:
            case OrderServiceOperation.SETTLE_TENTATIVE:
                if (CoreUtils.isFreeOrder(context.order)) {
                    return Promise.pure(FlowType.FREE_SETTLE.name())
                }
                return selectPayInFlow(context)
            case OrderServiceOperation.CREATE_TENTATIVE:
                return Promise.pure(FlowType.RATE_ORDER.name())
            case OrderServiceOperation.UPDATE_TENTATIVE:
                return Promise.pure(FlowType.UPDATE_TENTATIVE.name())
            case OrderServiceOperation.UPDATE_NON_TENTATIVE:
                return Promise.pure(FlowType.UPDATE_NON_TENTATIVE.name())
            case OrderServiceOperation.GET:
                return Promise.pure(FlowType.GET_ORDER.name())
            case OrderServiceOperation.UPDATE:
                return selectUpdateFlow(context)
            case OrderServiceOperation.REFUND:
                return Promise.pure(FlowType.REFUND_ORDER.name())
            default:
                LOGGER.error('name=Order_Action_Not_Supported, action: {0}', operation.toString())
                throw AppErrors.INSTANCE.orderActionNotSupported(
                        operation.toString()).exception()
        }
    }

    private Promise<String> selectUpdateFlow(OrderServiceContext context) throws AppErrorException {
        def event = context?.orderEvent
        assert (event != null)
        def action = event.action
        assert (action != null)
        switch (action) {
            case OrderActionType.FULFILL.name():
                if (event.status == EventStatus.COMPLETED.name()
                        && CoreUtils.hasPhysicalOffer(context.order)
                        && context.order.status == OrderStatus.PENDING_FULFILL.name()) {
                    LOGGER.info('name=Complete_Charge_Order. orderId: {}', event.order.value)
                    return Promise.pure(FlowType.COMPLETE_CHARGE.name())
                }
                LOGGER.error('name=Fulfillment_Event_Not_Support. action: {}, status:{}', event.action, event.status)
                throw AppErrors.INSTANCE.eventNotSupported(event.action, event.status).exception()

            case OrderActionType.CHARGE.name():
                if (event.status == EventStatus.COMPLETED.name()
                        && context.order.status == OrderStatus.PENDING_CHARGE.name()) {
                    return Promise.pure(FlowType.WEB_PAYMENT_SETTLE.name())
                }
                LOGGER.error('name=Charge_Event_Not_Support. action: {}, status:{}', event.action, event.status)
                throw AppErrors.INSTANCE.eventNotSupported(event.action, event.status).exception()

            case OrderActionType.CANCEL.name():
                if (event.status == EventStatus.COMPLETED.name()
                        && context.order.status == OrderStatus.PENDING_CHARGE.name()) {
                    return Promise.pure(FlowType.CANCEL_ORDER.name())
                }
                LOGGER.error('name=Cancel_Event_Not_Support. action: {}, status:{}', event.action, event.status)
                throw AppErrors.INSTANCE.eventNotSupported(event.action, event.status).exception()
            default:
                LOGGER.error('name=Event_Not_Support. action: {}, status:{}', event.action, event.status)
                throw AppErrors.INSTANCE.eventNotSupported(event.action, event.status).exception()
        }
    }

    private Promise<String> selectPayInFlow(OrderServiceContext context) throws AppErrorException {

        assert(context != null && context.order != null)

        if (CollectionUtils.isEmpty(context.order.payments)) {
            return Promise.pure(FlowType.FREE_SETTLE.name())
        }
        // select order flow per payment info and product item info
        return orderServiceContextBuilder.getPaymentInstruments(context).then { List<PaymentInstrument> pis ->
            // TODO: do not support multiple payment methods now
            assert(!CollectionUtils.isEmpty(pis))
            switch (PIType.get(pis[0].type)) {
                // TODO reference to payment instrument type
                case PIType.CREDITCARD:
                    // TODO: do not support mixed order containing both physical item & digital item now
                    if (CoreUtils.isPreorder(context.order)) {
                        return FlowType.PHYSICAL_SETTLE.name()
                    }
                    return CoreUtils.hasPhysicalOffer(context.order) ? Promise.pure(FlowType.AUTH_SETTLE.name()) :
                            Promise.pure(FlowType.IMMEDIATE_SETTLE.name())
                case PIType.STOREDVALUE:
                    return Promise.pure(FlowType.IMMEDIATE_SETTLE.name())
                case PIType.PAYPAL:
                    return Promise.pure(FlowType.WEB_PAYMENT_CHARGE.name())
                default:
                    LOGGER.error('name=Payment_Instrument_Type_Not_Supported, action: {}', pis[0]?.type)
                    throw AppErrors.INSTANCE.piTypeNotSupported(
                            pis[0]?.type?.toString()).exception()
            }
        }
    }
}