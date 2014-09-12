/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.resource;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * payment instrument resource interface.
 */
@Api("payment-instruments")
@Path("/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
@InProcessCallable
public interface PaymentInstrumentResource {

    @ApiOperation("Create a payment instrument")
    @POST
    @Path("payment-instruments")
    @RouteBy("request.getUserId()")
    Promise<PaymentInstrument> postPaymentInstrument(PaymentInstrument request);

    @ApiOperation("Get a payment instrument")
    @GET
    @Path("payment-instruments/{paymentInstrumentId}")
    @RouteBy("paymentInstrumentId")
    Promise<PaymentInstrument> getById(@PathParam("paymentInstrumentId") PaymentInstrumentId paymentInstrumentId);

    @ApiOperation("Delete a payment instrument")
    @DELETE
    @Path("payment-instruments/{paymentInstrumentId}")
    @RouteBy("paymentInstrumentId")
    Promise<Void> delete(@PathParam("paymentInstrumentId") PaymentInstrumentId paymentInstrumentId);

    @ApiOperation("Put a payment instrument")
    @PUT
    @Path("payment-instruments/{paymentInstrumentId}")
    @RouteBy("paymentInstrumentId")
    Promise<PaymentInstrument> update(@PathParam("paymentInstrumentId") PaymentInstrumentId paymentInstrumentId,
                                             PaymentInstrument request);

    @ApiOperation("Search a payment instrument")
    @GET
    @Path("payment-instruments")
    @RouteBy("searchParam.getUserId()")
    Promise<Results<PaymentInstrument>> searchPaymentInstrument(@BeanParam PaymentInstrumentSearchParam searchParam);

}
