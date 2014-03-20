/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.resource;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentInstrumentType;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * payment instrument type resource interface.
 */
@Api("payment-instrument-types")
@Path("/payment-instrument-types")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface PaymentInstrumentTypeResource {
    @ApiOperation("Get payment instrument type info")
    @GET
    @Path("/{paymentInstrumentType}")
    Promise<PaymentInstrumentType> getById(@PathParam("paymentInstrumentType") String paymentInstrumentType);
}
