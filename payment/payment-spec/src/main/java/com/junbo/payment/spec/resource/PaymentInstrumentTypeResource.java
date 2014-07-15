/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.resource;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentInstrumentType;

import javax.ws.rs.*;

import java.util.List;

//comment the rest resource here as we have cloudant ones:
/*
@Api("payment-instrument-types")
@Path("/payment-instrument-types")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource(sync = true)
*/
/**
 * payment instrument type resource interface.
 */
public interface PaymentInstrumentTypeResource {
    /*
    @ApiOperation("Get payment instrument type info")
    @GET
    @Path("/{paymentInstrumentTypeId}")*/
    Promise<PaymentInstrumentType> getById(@PathParam("paymentInstrumentTypeId") String paymentInstrumentTypeId);

    /*@ApiOperation("Get all the payment instrument types info")
    @GET*/
    Promise<List<PaymentInstrumentType>> getAllTypes();
}
