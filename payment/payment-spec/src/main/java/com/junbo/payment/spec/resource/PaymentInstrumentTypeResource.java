/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.resource;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentInstrumentType;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * payment instrument type resource interface.
 */
@Path("/payment-instrument-types")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface PaymentInstrumentTypeResource {
    @GET
    @Path("/{paymentInstrumentType}")
    Promise<PaymentInstrumentType> getById(@PathParam("paymentInstrumentType") String paymentInstrumentType);
}
