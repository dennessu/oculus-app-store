/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core.impl.orderflow

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.common.error.AppErrorException
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.FlowSelector
import com.junbo.order.core.FlowType
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.entity.enums.ItemType
import com.junbo.order.db.entity.enums.OrderType
import com.junbo.payment.spec.enums.PIType
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
@Component('defaultFlowSelector')
class DefaultFlowSelector implements FlowSelector {

    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultFlowSelector)

    @Autowired
    OrderServiceContextBuilder orderServiceContextBuilder

    @Override
    Promise<FlowType> select(OrderServiceContext context, OrderServiceOperation operation) throws AppErrorException {
        assert(context != null && operation != null)
        switch (operation) {
            case OrderServiceOperation.CREATE:
            case OrderServiceOperation.SETTLE_TENTATIVE:
                return selectSettleOrderFlow(context)
            case OrderServiceOperation.CREATE_TENTATIVE:
                return Promise.pure(FlowType.RATE_ORDER)
            case OrderServiceOperation.UPDATE_TENTATIVE:
                return Promise.pure(FlowType.UPDATE_TENTATIVE)
            case OrderServiceOperation.GET:
                return Promise.pure(FlowType.GET_ORDER)
            default:
                LOGGER.error('name=Order_Action_Not_Supported, action: {0}', operation.toString())
                throw com.junbo.order.spec.error.AppErrors.INSTANCE.orderActionNotSupported(
                        operation.toString()).exception()
        }
    }

    private Promise<FlowType> selectSettleOrderFlow(OrderServiceContext expOrder) throws AppErrorException {
        def type = expOrder?.order?.type
        assert (type != null)
        switch (type) {
            case OrderType.PAY_IN.toString():
                return selectPayInFlow(expOrder)
            default:
                LOGGER.error('name=Order_Type_Not_Supported, action: {0}', type.toString())
                throw com.junbo.order.spec.error.AppErrors.INSTANCE.orderTypeNotSupported(type.toString()).exception()
        }
    }

    private Promise<FlowType> selectPayInFlow(OrderServiceContext context) throws AppErrorException {

        assert(context != null && context.order != null)

        if (CollectionUtils.isEmpty(context.order.paymentInstruments)) {
            return Promise.pure(FlowType.FREE_SETTLE)
        }
        // select order flow per payment info and product item info
        orderServiceContextBuilder.getPaymentInstruments(context).then { List<PaymentInstrument> pis ->
            // TODO: do not support multiple payment methods now
            assert(!CollectionUtils.isEmpty(pis))
            switch (pis[0].type) {
                // TODO reference to payment instrument type
                case PIType.CREDITCARD.toString():
                    // TODO: do not support mixed order containing both physical item & digital item now
                    return orderServiceContextBuilder.getOffers(context).then { List<Offer> ofs ->
                        Boolean isPhysical = ofs.any { Offer of ->
                            CoreUtils.getOfferType(of) == ItemType.PHYSICAL
                        }
                        if (isPhysical) {
                            return Promise.pure(FlowType.AUTH_SETTLE)
                        }
                        return Promise.pure(FlowType.IMMEDIATE_SETTLE)
                    }
                default:
                    LOGGER.error('name=Payment_Instrument_Type_Not_Supported, action: {}', pis[0]?.type)
                    throw com.junbo.order.spec.error.AppErrors.INSTANCE.piTypeNotSupported(
                            pis[0]?.type.toString()).exception()
            }
        }
    }
}