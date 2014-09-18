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
    @GET
    @Path("oauth/access_token")
    Promise<String> getAccessToken(@BeanParam FacebookTokenRequest fbTokenRequest);

    @POST
    @Path("{oculus-app-id}/payment_accounts")
    Promise<String> createAccount(@QueryParam("access_token") String accessToken,
                    @PathParam("oculus-app-id") String oculusAppId, FacebookPaymentAccount fbPaymentAccount);

    @POST
    @Path("{payment-account-id}/credit_cards")
    Promise<FacebookCreditCard> addCreditCard(@QueryParam("access_token") String accessToken,
                    @PathParam("payment-account-id") String paymentAccountId, FacebookCreditCard fbCreditCard);

    @POST
    @Path("{payment-account-id}/payments")
    Promise<FacebookPayment> addPayment(@QueryParam("access_token") String accessToken,
                                              @PathParam("payment-account-id") String paymentAccountId, FacebookPayment fbPayment);

    @POST
    @Path("{payment-account-id}/payments")
    Promise<FacebookPayment> modifyPayment(@QueryParam("access_token") String accessToken,
                                        @PathParam("payment-id") String paymentAccountId, FacebookPayment fbPayment);
}
