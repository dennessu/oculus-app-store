/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.resource;

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
@Path("/payment-instruments")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface PaymentInstrumentResource {
    @POST
    Promise<PaymentInstrument> postPaymentInstrument(PaymentInstrument request);

    @GET
    @Path("/{paymentInstrumentId}")
    Promise<PaymentInstrument> getById(@PathParam("paymentInstrumentId") Long paymentInstrumentId);

    @DELETE
    @Path("/{paymentInstrumentId}")
    Promise<Response> delete(@PathParam("paymentInstrumentId") Long paymentInstrumentId);

    @PUT
    @Path("/{paymentInstrumentId}")
    Promise<PaymentInstrument> update(@PathParam("paymentInstrumentId") Long paymentInstrumentId,
                                             PaymentInstrument request);

    @GET
    @Path("/search")
    Promise<ResultList<PaymentInstrument>> searchPaymentInstrument(
            @BeanParam PaymentInstrumentSearchParam searchParam,
            @BeanParam PageMetaData pageMetadata);

}
