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

        /*
        FacebookCreditCard fbCard = new FacebookCreditCard();
        switch (fbCreditCard.getToken()){
            case "4111117711552927":
                fbCard.setId("Y3JlZGl0X2NhcmRfMjk4NjI2NzgzNjAxMzYw");
                fbCard.setLast4("2927");
                break;
            case "4111114869229598":
                fbCard.setId("Y3JlZGl0X2NhcmRfMjk4NjI2NzkwMjY4MDI2");
                fbCard.setLast4("9598");
                break;
            case "4111119315405122":
                fbCard.setId("Y3JlZGl0X2NhcmRfMjk4NjI2NzkzNjAxMzU5");
                fbCard.setLast4("5122");
                break;
            case "4111110448424155":
                fbCard.setId("Y3JlZGl0X2NhcmRfMjk4NjI2Nzk2OTM0Njky");
                fbCard.setLast4("4155");
                break;
            case "4111119818229052":
                fbCard.setId("Y3JlZGl0X2NhcmRfMjk4NjI2ODAzNjAxMzU4");
                fbCard.setLast4("9052");
                break;
            case "4111118096366644":
                throw new RuntimeException("invalid card to auth");
            default:
                throw new RuntimeException("invalid card");
        }
        return Promise.pure(fbCard);
        */
    }

    @Override
    public Promise<FacebookCreditCard> getCreditCard(String accessToken, String creditcardId) {
        FacebookCreditCard fakeCard = new FacebookCreditCard();
        fakeCard.setId(creditcardId);
        fakeCard.setLast4("1234");
        fakeCard.setFirst6("123456");
        fakeCard.setExpiryYear("2016");
        fakeCard.setExpiryMonth("06");
        return Promise.pure(fakeCard);
    }

    @Override
    public Promise<FacebookPayment> addPayment(String accessToken, String paymentAccountId, FacebookPayment fbPayment) {
        return facebookPaymentApi.addPayment(accessToken, paymentAccountId, fbPayment);
    }

    @Override
    public Promise<FacebookPayment> modifyPayment(String accessToken, String paymentAccountId, FacebookPayment fbPayment) {
        return facebookPaymentApi.modifyPayment(accessToken, paymentAccountId, fbPayment);
    }

    @Override
    public Promise<FacebookRiskPayment> getPaymentField(String accessToken, String paymentId) {
        return Promise.pure(null);
    }
}
