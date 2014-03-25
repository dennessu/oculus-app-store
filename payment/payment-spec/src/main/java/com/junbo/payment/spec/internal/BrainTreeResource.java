/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.internal;

import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * brain tree resource interface.
 */
@Path("/braintree")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface BrainTreeResource {
    @POST
    @Path("/payment-instruments")
    Promise<PaymentInstrument> addPaymentInstrument(PaymentInstrument request);

    @DELETE
    @Path("/payment-instruments/{paymentInstrumentId}")
    Promise<Response> deletePaymentInstrument(@PathParam("paymentInstrumentId") String externalToken);

    @POST
    @Path("/payment/{paymentInstrumentId}/authorization")
    Promise<PaymentTransaction> postAuthorization(@PathParam("paymentInstrumentId") String piToken,
                                                  PaymentTransaction request);

    @POST
    @Path("/payment/{paymentId}/capture")
    Promise<PaymentTransaction> postCapture(@PathParam("paymentId") String transactionToken,
                                            PaymentTransaction request);

    @POST
    @Path("/payment/{paymentInstrumentId}/charge")
    Promise<PaymentTransaction> postCharge(@PathParam("paymentInstrumentId") String piToken,
                                           PaymentTransaction request);

    @PUT
    @Path("/payment/{paymentId}/reverse")
    Promise<PaymentTransaction> reverse(@PathParam("paymentId") String transactionToken,
                                        PaymentTransaction request);

    @GET
    @Path("/payment/{paymentId}")
    Promise<PaymentTransaction> getById(@PathParam("paymentId") String transactionToken);

    @GET
    @Path("/payment/search")
    Promise<Results<PaymentTransaction>> getByOrderId(@QueryParam("orderId") String orderId);
}
