/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.spec.resource;

import com.junbo.common.id.FulfilmentId;
import com.junbo.common.id.OrderId;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * FulfilmentResource.
 */
@Path("/fulfilments")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource(sync = true)
@InProcessCallable
public interface FulfilmentResource {
    @POST
    @Path("/")
    Promise<FulfilmentRequest> fulfill(@Valid FulfilmentRequest request);

    @GET
    @Path("/")
    Promise<FulfilmentRequest> getByOrderId(@QueryParam("orderId") OrderId orderId);

    @GET
    @Path("/{fulfilmentId}")
    Promise<FulfilmentItem> getByFulfilmentId(@PathParam("fulfilmentId") FulfilmentId fulfilmentId);
}
