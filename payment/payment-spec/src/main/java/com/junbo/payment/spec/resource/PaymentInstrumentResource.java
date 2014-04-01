/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.resource;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PageMetaData;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * payment instrument resource interface.
 */
@Api("payment-instruments")
@Path("/users/{userId}/payment-instruments")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface PaymentInstrumentResource {

    @ApiOperation("Create a payment instrument")
    @POST
    Promise<PaymentInstrument> postPaymentInstrument(@PathParam("userId")UserId userId, PaymentInstrument request);

    @ApiOperation("Get a payment instrument")
    @GET
    @Path("/{paymentInstrumentId}")
    Promise<PaymentInstrument> getById(@PathParam("userId")UserId userId,
            @PathParam("paymentInstrumentId") PaymentInstrumentId paymentInstrumentId);

    @ApiOperation("Delete a payment instrument")
    @DELETE
    @Path("/{paymentInstrumentId}")
    Promise<Response> delete(@PathParam("userId")UserId userId,
            @PathParam("paymentInstrumentId") PaymentInstrumentId paymentInstrumentId);

    @ApiOperation("Put a payment instrument")
    @PUT
    @Path("/{paymentInstrumentId}")
    Promise<PaymentInstrument> update(@PathParam("userId")UserId userId,
            @PathParam("paymentInstrumentId") PaymentInstrumentId paymentInstrumentId,
                                             PaymentInstrument request);

    @ApiOperation("Search a payment instrument")
    @GET
    Promise<Results<PaymentInstrument>> searchPaymentInstrument(
            @PathParam("userId")UserId userId,
            @BeanParam PaymentInstrumentSearchParam searchParam,
            @BeanParam PageMetaData pageMetadata);

}
