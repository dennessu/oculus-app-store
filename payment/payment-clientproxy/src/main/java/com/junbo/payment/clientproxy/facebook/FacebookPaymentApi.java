/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;

/**
 * Facebook payment Api.
 */
@RestResource
@Path("/")
public interface FacebookPaymentApi {

    @POST
    @Path("{oculus-app-id}/payment_accounts")
    Promise<FacebookPaymentAccount> createAccount(@QueryParam("access_token") String accessToken,
                    @PathParam("oculus-app-id") String oculusAppId, @BeanParam FacebookPaymentAccount fbPaymentAccount);

    @POST
    @Path("{payment-account-id}/credit_cards")
    Promise<FacebookCreditCard> addCreditCard(@QueryParam("access_token") String accessToken,
                    @PathParam("payment-account-id") String paymentAccountId, @BeanParam FacebookCreditCard fbCreditCard);


    @GET
    @Path("{creditcard-id}")
    Promise<FacebookCreditCard> getCreditCard(@QueryParam("access_token") String accessToken, @PathParam("creditcard-id") String creditcardId);

    @POST
    @Path("{payment-account-id}/payments")
    Promise<FacebookPayment> addPayment(@QueryParam("access_token") String accessToken,
                                              @PathParam("payment-account-id") String paymentAccountId, @BeanParam FacebookPayment fbPayment);

    @POST
    @Path("{payment-id}")
    Promise<FacebookPayment> modifyPayment(@QueryParam("access_token") String accessToken,
                                        @PathParam("payment-id") String paymentId, @BeanParam FacebookPayment fbPayment);
}
