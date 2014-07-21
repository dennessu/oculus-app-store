/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.rest.resource;

import com.junbo.authorization.AuthorizeContext;
import com.junbo.common.error.AppCommonErrors;
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
    private static final String FULFILMENT_SERVICE_SCOPE = "fulfilment.service";
    private static final String FULFILMENT_CSR_SCOPE = "fulfilment.csr";

    @Autowired
    private FulfilmentService service;

    @POST
    public Promise<FulfilmentRequest> fulfill(@Valid FulfilmentRequest request) {
        authorize();
        FulfilmentRequest result = service.fulfill(request);
        return Promise.pure(result);
    }

    @GET
    public Promise<FulfilmentRequest> getByOrderId(@QueryParam("orderId") OrderId orderId) {
        authorize();
        return Promise.pure(service.retrieveRequestByOrderId(orderId.getValue()));
    }

    @Override
    public Promise<FulfilmentItem> getByFulfilmentId(@PathParam("fulfilmentId") FulfilmentId fulfilmentId) {
        authorize();
        return Promise.pure(service.retrieveFulfilmentItem(fulfilmentId.getValue()));
    }

    private static void authorize() {
        if (!AuthorizeContext.hasScopes(FULFILMENT_SERVICE_SCOPE)
                && !AuthorizeContext.hasScopes(FULFILMENT_CSR_SCOPE)) {
            throw AppCommonErrors.INSTANCE.insufficientScope().exception();
        }
    }
}
