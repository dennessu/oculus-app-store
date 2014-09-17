/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.facebook;

import com.junbo.common.error.AppCommonErrors;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.facebook.FacebookCreditCard;
import com.junbo.payment.clientproxy.facebook.FacebookPaymentAccount;
import com.junbo.payment.clientproxy.facebook.FacebookPaymentApi;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.core.provider.AbstractPaymentProviderService;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Facebook Payment Service Impl.
 */
public class FacebookCCProviderServiceImpl extends AbstractPaymentProviderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FacebookCCProviderServiceImpl.class);
    private static final String PROVIDER_NAME = "FacebookCC";

    private String oculusAppId;
    private FacebookPaymentUtils facebookPaymentUtils;
    private FacebookPaymentApi facebookPaymentApi;
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public void clonePIResult(PaymentInstrument source, PaymentInstrument target) {

    }

    @Override
    public void cloneTransactionResult(PaymentTransaction source, PaymentTransaction target) {

    }

    @Override
    public Promise<PaymentInstrument> add(final PaymentInstrument request) {
        validateFacebookCC(request);
        String[] tokens = request.getTypeSpecificDetails().getExpireDate().split("-");
        if (tokens == null || tokens.length < 2) {
            throw AppCommonErrors.INSTANCE.fieldInvalid("expire_date",
                    "only accept format: yyyy-MM or yyyy-MM-dd").exception();
        }
        return facebookPaymentUtils.getAccessToken().then(new Promise.Func<String, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(String s) {
                final String accessToken = s;
                FacebookPaymentAccount fbPaymentAccount = new FacebookPaymentAccount();
                fbPaymentAccount.setPayerId(request.getUserId().toString());
                return facebookPaymentApi.createAccount(s, oculusAppId, fbPaymentAccount).then(new Promise.Func<String, Promise<PaymentInstrument>>() {
                    @Override
                    public Promise<PaymentInstrument> apply(String s) {
                        FacebookCreditCard fbCreditCard = new FacebookCreditCard();
                        return facebookPaymentApi.addCreditCard(accessToken, s, fbCreditCard).then(new Promise.Func<FacebookCreditCard, Promise<PaymentInstrument>>() {
                            @Override
                            public Promise<PaymentInstrument> apply(FacebookCreditCard facebookCreditCard) {
                                request.setExternalToken(facebookCreditCard.getId());
                                request.getTypeSpecificDetails().setIssuerIdentificationNumber(facebookCreditCard.getFirst6());
                                request.getTypeSpecificDetails().setExpireDate(facebookCreditCard.getExpiryYear() + "-" + facebookCreditCard.getExpiryMonth());
                                request.setAccountNumber(facebookCreditCard.getLast4());
                                return Promise.pure(request);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public Promise<Response> delete(PaymentInstrument pi) {
        return null;
    }

    @Override
    public Promise<PaymentInstrument> getByInstrumentToken(String token) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> authorize(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> capture(String transactionId, PaymentTransaction paymentRequest) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> charge(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> reverse(String transactionId, PaymentTransaction paymentRequest) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> refund(String transactionId, PaymentTransaction request) {
        return null;
    }

    @Override
    public List<PaymentTransaction> getByBillingRefId(String orderId) {
        return null;
    }

    @Override
    public Promise<PaymentTransaction> getByTransactionToken(PaymentTransaction paymentRequest) {
        return null;
    }
    private void validateFacebookCC(PaymentInstrument request){
        if(request.getTypeSpecificDetails() == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("expire_date").exception();
        }
        String expireDate = request.getTypeSpecificDetails().getExpireDate();
        if(CommonUtil.isNullOrEmpty(expireDate)){
            throw AppCommonErrors.INSTANCE.fieldRequired("expire_date").exception();
        }
    }

    @Required
    public void setOculusAppId(String oculusAppId) {
        this.oculusAppId = oculusAppId;
    }

    @Required
    public void setFacebookPaymentUtils(FacebookPaymentUtils facebookPaymentUtils) {
        this.facebookPaymentUtils = facebookPaymentUtils;
    }
}
