/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.resource;


import com.junbo.common.id.PaymentId;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.junbo.payment.spec.model.PaymentTransaction;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * payment transaction resource interface.
 */
@Path("/payment-transactions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
@InProcessCallable
public interface PaymentTransactionResource {
    @POST
    @Path("/credit")
    @RouteBy("request.getPaymentInstrumentId()")
    Promise<PaymentTransaction> postPaymentCredit(PaymentTransaction request);

    @POST
    @Path("/authorization")
    @RouteBy("request.getPaymentInstrumentId()")
    Promise<PaymentTransaction> postPaymentAuthorization(PaymentTransaction request);

    @POST
    @Path("/{paymentId}/capture")
    @RouteBy("paymentId")
    Promise<PaymentTransaction> postPaymentCapture(@PathParam("paymentId") PaymentId paymentId,
                                                          PaymentTransaction request);

    @POST
    @Path("/{paymentId}/confirm")
    @RouteBy("paymentId")
    Promise<PaymentTransaction> postPaymentConfirm(@PathParam("paymentId") PaymentId paymentId,
                                               PaymentTransaction request);

    @POST
    @Path("/charge")
    @RouteBy("request.getPaymentInstrumentId()")
    Promise<PaymentTransaction> postPaymentCharge(PaymentTransaction request);

    @PUT
    @Path("/{paymentId}/reverse")
    @RouteBy("paymentId")
    Promise<PaymentTransaction> reversePayment(@PathParam("paymentId") PaymentId paymentId,
                                               PaymentTransaction request);

    @PUT
    @Path("/{paymentId}/refund")
    @RouteBy("paymentId")
    Promise<PaymentTransaction> refundPayment(@PathParam("paymentId") PaymentId paymentId,
                                               PaymentTransaction request);

    @GET
    @Path("/{paymentId}")
    @RouteBy("paymentId")
    Promise<PaymentTransaction> getPayment(@PathParam("paymentId") PaymentId paymentId);

    @POST
    @Path("/{paymentId}/check")
    @RouteBy("paymentId")
    Promise<PaymentTransaction> checkPaymentStatus(@PathParam("paymentId") PaymentId paymentId);
}
