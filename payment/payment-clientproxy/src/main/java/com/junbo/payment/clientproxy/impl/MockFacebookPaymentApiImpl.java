/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.impl;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.facebook.*;

import javax.ws.rs.BeanParam;

/**
 * Created by wenzhu on 10/17/14.
 */
public class MockFacebookPaymentApiImpl implements FacebookPaymentApi {
    public void setFacebookPaymentApi(FacebookPaymentApi facebookPaymentApi) {
        this.facebookPaymentApi = facebookPaymentApi;
    }

    private FacebookPaymentApi facebookPaymentApi;

    @Override
    public Promise<FacebookPaymentAccount> createAccount(String accessToken, String oculusAppId, @BeanParam FacebookPaymentAccount fbPaymentAccount) {
        FacebookPaymentAccount account = new FacebookPaymentAccount();
        account.setEnv("test");
        account.setId("ZXh0X3BheW1lbnRfYWNjb3VudF8yOTk2NDYyNjM1NzQ5NDc");
        return Promise.pure(account);
    }

    @Override
    public Promise<FacebookCreditCard> addCreditCard(String accessToken, String paymentAccountId, FacebookCreditCard fbCreditCard) {
        return facebookPaymentApi.addCreditCard(accessToken, paymentAccountId, fbCreditCard);
    }

    @Override
    public Promise<FacebookPayment> addPayment(String accessToken, String paymentAccountId, FacebookPayment fbPayment) {
        return facebookPaymentApi.addPayment(accessToken, paymentAccountId, fbPayment);
    }

    @Override
    public Promise<FacebookPayment> modifyPayment(String accessToken, String paymentAccountId, FacebookPayment fbPayment) {
        return facebookPaymentApi.modifyPayment(accessToken, paymentAccountId, fbPayment);
    }
}
