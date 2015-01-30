/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.fulfillment.impl

import com.junbo.common.id.OrderId
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.fulfilment.spec.resource.FulfilmentResource
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.clientproxy.common.FacadeBuilder
import com.junbo.order.clientproxy.fulfillment.FulfillmentFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * Created by fzhang on 14-2-25.
 */
@CompileStatic
@TypeChecked
@Component('orderFulfillmentFacade')
class FulfillmentFacadeImpl implements FulfillmentFacade {

    @Resource(name='order.fulfilmentClient')
    private FulfilmentResource fulfilmentResource

    private static final Logger LOGGER = LoggerFactory.getLogger(FulfillmentFacadeImpl)

    void setFulfilmentResource(FulfilmentResource fulfilmentResource) {
        this.fulfilmentResource = fulfilmentResource
    }

    @Qualifier('orderTransactionHelper')
    @Autowired
    TransactionHelper transactionHelper

    @Override
    Promise<FulfilmentRequest> postFulfillment(Order order) {
        return transactionHelper.executeInNewTransaction {
            return fulfilmentResource.fulfill(FacadeBuilder.buildFulfilmentRequest(order)).then { FulfilmentRequest f ->
                if (f == null) {
                    LOGGER.error('name=FulfillmentFacadeImpl_Create_Fulfillment_Null')
                    throw AppErrors.INSTANCE.fulfillmentConnectionError('Create fulfillment response is null').exception()
                }
                LOGGER.info('name=FulfillmentFacadeImpl_Create_Fulfillment_Success')
                return Promise.pure(f)
            }
        }
    }

    @Override
    Promise<FulfilmentRequest> reverseFulfillment(Order order) {
        return transactionHelper.executeInNewTransaction {
            def request = FacadeBuilder.buildRevokeFulfilmentRequest(order)
            if (CollectionUtils.isEmpty(request.items)) {
                return Promise.pure(request)
            }
            return fulfilmentResource.revoke(request).then { FulfilmentRequest f ->
                if (f == null) {
                    LOGGER.error('name=FulfillmentFacadeImpl_Revoke_Fulfillment_Null')
                    throw AppErrors.INSTANCE.fulfillmentConnectionError('Revoke fulfillment response is null').exception()
                }
                LOGGER.info('name=FulfillmentFacadeImpl_Revoke_Fulfillment_Success')
                return Promise.pure(f)
            }
        }
    }

    @Override
    Promise<FulfilmentRequest> getFulfillment(OrderId orderId) {
        return transactionHelper.executeInNewTransaction {
            return fulfilmentResource.getByOrderId(orderId).then { FulfilmentRequest f ->
                return Promise.pure(f)
            }
        }
    }
}