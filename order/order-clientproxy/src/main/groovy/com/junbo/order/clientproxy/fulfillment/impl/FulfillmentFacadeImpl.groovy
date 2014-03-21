/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.clientproxy.fulfillment.impl

import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.fulfilment.spec.resource.FulfilmentResource
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.common.FacadeBuilder
import com.junbo.order.clientproxy.fulfillment.FulfillmentFacade
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by fzhang on 14-2-25.
 */
@CompileStatic
@Component('orderFulfillmentFacade')
class FulfillmentFacadeImpl implements FulfillmentFacade {

    @Resource(name='order.fulfilmentClient')
    private FulfilmentResource fulfilmentResource

    void setFulfilmentResource(FulfilmentResource fulfilmentResource) {
        this.fulfilmentResource = fulfilmentResource
    }

    @Override
    Promise<FulfilmentRequest> postFulfillment(Order order) {
        return fulfilmentResource.fulfill(FacadeBuilder.buildFulfilmentRequest(order))
    }
}