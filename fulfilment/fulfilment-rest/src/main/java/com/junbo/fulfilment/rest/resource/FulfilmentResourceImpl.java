/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.rest.resource;

import com.junbo.common.id.FulfilmentId;
import com.junbo.common.id.OrderId;
import com.junbo.fulfilment.core.FulfilmentService;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.fulfilment.spec.resource.FulfilmentResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * FulfilmentResourceImpl.
 */
@Path("fulfilments")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class FulfilmentResourceImpl implements FulfilmentResource {
    @Autowired
    private FulfilmentService service;

    @POST
    public Promise<FulfilmentRequest> fulfill(@Valid FulfilmentRequest request) {
        FulfilmentRequest result = service.fulfill(request);
        return Promise.pure(result);
    }

    @GET
    public Promise<FulfilmentRequest> getByBillingOrderId(@QueryParam("orderId") OrderId orderId) {
        return Promise.pure(service.retrieveRequestByBillingOrderId(orderId.getValue()));
    }

    @Override
    public Promise<FulfilmentItem> getByFulfilmentId(@PathParam("fulfilmentId") FulfilmentId fulfilmentId) {
        return Promise.pure(service.retrieveFulfilmentItem(fulfilmentId.getValue()));
    }
}
