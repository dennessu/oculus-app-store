/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.resource;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PageMetaData;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam;
import com.junbo.payment.spec.model.ResultList;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * payment instrument resource interface.
 */
@Path("/users/{userId}/payment-instruments")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface PaymentInstrumentResource {
    @POST
    Promise<PaymentInstrument> postPaymentInstrument(@PathParam("userId")UserId userId, PaymentInstrument request);

    @GET
    @Path("/{paymentInstrumentId}")
    Promise<PaymentInstrument> getById(@PathParam("userId")UserId userId,
            @PathParam("paymentInstrumentId") PaymentInstrumentId paymentInstrumentId);

    @DELETE
    @Path("/{paymentInstrumentId}")
    Promise<Response> delete(@PathParam("userId")UserId userId,
            @PathParam("paymentInstrumentId") PaymentInstrumentId paymentInstrumentId);

    @PUT
    @Path("/{paymentInstrumentId}")
    Promise<PaymentInstrument> update(@PathParam("userId")UserId userId,
            @PathParam("paymentInstrumentId") PaymentInstrumentId paymentInstrumentId,
                                             PaymentInstrument request);

    @GET
    @Path("/search")
    Promise<ResultList<PaymentInstrument>> searchPaymentInstrument(
            @PathParam("userId")UserId userId,
            @BeanParam PaymentInstrumentSearchParam searchParam,
            @BeanParam PageMetaData pageMetadata);

}
