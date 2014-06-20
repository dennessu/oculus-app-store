/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.fulfillment.impl

import com.junbo.common.error.AppError
import com.junbo.common.id.OrderId
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.fulfilment.spec.resource.FulfilmentResource
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.common.FacadeBuilder
import com.junbo.order.clientproxy.fulfillment.FulfillmentFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.error.ErrorUtils
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    @Override
    Promise<FulfilmentRequest> postFulfillment(Order order) {
        return fulfilmentResource.fulfill(FacadeBuilder.buildFulfilmentRequest(order)).recover { Throwable ex ->
            LOGGER.error('name=FulfillmentFacadeImpl_Create_Fulfillment_Error', ex)
            throw convertError(ex).exception()
        }.then { FulfilmentRequest f ->
            if (f == null) {
                LOGGER.error('name=FulfillmentFacadeImpl_Create_Fulfillment_Null')
                throw AppErrors.INSTANCE.billingResultInvalid('Create balance response is null').exception()
            }
            LOGGER.info('name=FulfillmentFacadeImpl_Create_Fulfillment_Success')
            return Promise.pure(f)
        }
    }

    @Override
    Promise<FulfilmentRequest> getFulfillment(OrderId orderId) {
        return fulfilmentResource.getByOrderId(orderId).recover { Throwable ex ->
            LOGGER.error('name=FulfillmentFacadeImpl_Get_Fulfillment_Error', ex)
            throw convertError(ex).exception()
        }.then { FulfilmentRequest f ->
            return Promise.pure(f)
        }
    }

    private static AppError convertError(Throwable error) {
        AppError e = ErrorUtils.toAppError(error)
        if (e != null) {
            return AppErrors.INSTANCE.fulfilmentConnectionError(e)
        }
        return AppErrors.INSTANCE.fulfillmentConnectionError(error.message)
    }
}