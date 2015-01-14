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
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.OrderEvent
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.OrderActionType
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource

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

    @Resource(name='orderValidator')
    OrderValidator orderValidator

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
            case OrderServiceOperation.SETTLE_FREE:
                return Promise.pure(FlowType.FREE_ORDER.name())
            default:
                LOGGER.error('name=Order_Action_Not_Supported, action: {0}', operation.toString())
                throw AppErrors.INSTANCE.orderActionNotSupported(
                        operation.toString()).exception()
        }
    }

    private Promise<String> selectUpdateFlow(OrderServiceContext context) throws AppErrorException {
        def event = context?.orderEvent
        def order = context?.order
        assert (order != null && event != null)
        def action = event.action
        assert (action != null)
        switch (action) {
            case OrderActionType.FULFILL.name():
                if (CoreUtils.isPreorder(order) && CoreUtils.isPendingOnFulfillment(order, event)) {
                    return Promise.pure(FlowType.COMPLETE_PREORDER.name())
                }
                if (CoreUtils.isPendingOnCapture(order)) {
                    return Promise.pure(FlowType.CAPTURE_ORDER.name())
                }
                LOGGER.error('name=Capture_Event_Not_Expected. action: {}, status:{}', event.action, event.status)
                throw AppErrors.INSTANCE.eventNotExpected(event.action, event.status).exception()
            case OrderActionType.CHARGE.name():
                if (CoreUtils.isPendingOnChargeConfirmation(order)) {
                    return selectFlowForChargeEvent(event, context)
                }
                LOGGER.error('name=Charge_Event_Not_Expected. action: {}, status:{}', event.action, event.status)
                throw AppErrors.INSTANCE.eventNotExpected(event.action, event.status).exception()
            default:
                LOGGER.error('name=Event_Not_Support. action: {}, status:{}', event.action, event.status)
                throw AppErrors.INSTANCE.eventNotSupported(event.action, event.status).exception()
        }
    }

    private Promise<String> selectPayInFlow(OrderServiceContext context) throws AppErrorException {

        assert(context != null && context.order != null)

        // select order flow per payment info and product item info
        return orderServiceContextBuilder.getPaymentInstruments(context).then { List<PaymentInstrument> pis ->
            // TODO: do not support multiple payment methods now
            if(CollectionUtils.isEmpty(pis)) {
                orderValidator.notEmpty(pis, 'payments')
            }
            switch (PIType.get(pis[0].type)) {
            // TODO reference to payment instrument type
                case PIType.CREDITCARD:
                    // TODO: do not support mixed order containing both physical item & digital item now
                    if (CoreUtils.isPreorder(context.order)) {
                        return Promise.pure(FlowType.PREORDER_SETTLE.name())
                    }
                    return CoreUtils.hasPhysicalOffer(context.order) ? Promise.pure(FlowType.AUTH_SETTLE.name()) :
                            Promise.pure(FlowType.IMMEDIATE_SETTLE.name())
                case PIType.STOREDVALUE:
                    if (CoreUtils.isPreorder(context.order)) {
                        return Promise.pure(FlowType.PREORDER_SETTLE.name())
                    }
                    return Promise.pure(FlowType.IMMEDIATE_SETTLE.name())
                case PIType.PAYPAL:
                case PIType.OTHERS:
                    return Promise.pure(FlowType.WEB_PAYMENT_CHARGE.name())
                default:
                    LOGGER.error('name=Payment_Instrument_Type_Not_Supported, action: {}', pis[0]?.type)
                    throw AppErrors.INSTANCE.piTypeNotSupported(
                            pis[0]?.type?.toString()).exception()
            }
        }
    }

    private Promise<String> selectFlowForChargeEvent(OrderEvent event, OrderServiceContext context) {
        def order = context.order
        orderServiceContextBuilder.getPaymentInstruments(context).then { List<PaymentInstrument> pis ->
            boolean hasWebPayment = pis.any { PaymentInstrument pi ->
                return PIType.get(pi.type) == PIType.PAYPAL || PIType.get(pi.type) == PIType.OTHERS
            }

            if (hasWebPayment) {
                if (event.status == EventStatus.COMPLETED.name()
                        && CoreUtils.isPendingOnChargeConfirmation(order)) {
                    return Promise.pure(FlowType.WEB_PAYMENT_SETTLE.name())
                }
            } else {
                return selectPayInFlow(context)
            }

            LOGGER.error('name=Charge_Event_Not_Support. action: {}, status:{}, orderStatus:{}',
                    event.action, event.status, context.order.status)
            throw AppErrors.INSTANCE.eventNotSupported(event.action, event.status).exception()
        }
    }
}