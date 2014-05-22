/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.adyen;

import com.adyen.services.common.Amount;
import com.adyen.services.payment.*;
import com.adyen.services.recurring.RecurringDetail;
import com.junbo.common.util.PromiseFacade;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.sharding.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * adyen credit card provider service implementation.
 */
public class AdyenCCProivderServiceImpl extends AdyenProviderServiceImpl{
    private static final Logger LOGGER = LoggerFactory.getLogger(AdyenCCProivderServiceImpl.class);
    private static final String PROVIDER_NAME = "AdyenCreditCard";
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public Promise<PaymentInstrument> add(final PaymentInstrument request) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentInstrument>() {
            @Override
            public PaymentInstrument call() throws Exception {
                //TODO: get the billing address country Code and find default currency & minAuthAmount
                String defaultCurrency = "USD";
                long minAuthAmount = 100L;
                Long piId = null;
                if(request.getId() == null){
                    piId = idGenerator.nextId(request.getUserId());
                }
                request.setId(piId);
                PaymentRequest adyenRequest = new PaymentRequest();
                adyenRequest.setMerchantAccount(getMerchantAccount());
                adyenRequest.setRecurring(new Recurring(RECURRING, null));
                //TODO: need calculate amount according to currency ISO, hard code * 100 first
                adyenRequest.setAmount(new Amount(defaultCurrency, minAuthAmount));
                adyenRequest.setReference(piId.toString());
                adyenRequest.setShopperEmail(TEMP_EMAIL);
                adyenRequest.setShopperReference(piId.toString());
                adyenRequest.setShopperInteraction("ContAuth");
                AnyType2AnyTypeMapEntry encryptedInfo = new AnyType2AnyTypeMapEntry();
                encryptedInfo.setKey("card.encrypted.json");
                //encrypted account number
                encryptedInfo.setValue(request.getAccountNum());
                adyenRequest.setAdditionalData((AnyType2AnyTypeMapEntry[]) Arrays.asList(encryptedInfo).toArray());
                PaymentResult result = null;
                try {
                    result = service.authorise(adyenRequest);
                } catch (RemoteException e) {
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, e.toString()).exception();
                }
                if(result != null && result.getResultCode().equals("Authorised")){
                    RecurringDetail recurringDetail = getRecurringReference(piId);
                    if(recurringDetail != null && recurringDetail.getCard() != null){
                        request.setExternalToken(recurringDetail.getRecurringDetailReference());
                        request.setAccountNum(recurringDetail.getCard().getNumber());
                        request.getTypeSpecificDetails().setCreditCardType(
                                PaymentUtil.getCreditCardType(recurringDetail.getVariant()).toString());
                        request.getTypeSpecificDetails().setExpireDate(recurringDetail.getCard().getExpiryYear()
                                + "-" + recurringDetail.getCard().getExpiryMonth());
                    }else{
                        throw AppServerExceptions.INSTANCE.providerProcessError(
                                PROVIDER_NAME, "Invalid Card to add").exception();
                    }
                }else{
                    throw AppServerExceptions.INSTANCE.providerProcessError(
                        PROVIDER_NAME, result == null ? "No Result" : result.getRefusalReason()).exception();
                }
                return request;
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> authorize(PaymentInstrument pi, PaymentTransaction paymentRequest) {
        PaymentResult adyenResult = doReferenceCharge(pi.getExternalToken(), paymentRequest);
        paymentRequest.setWebPaymentInfo(null);
        paymentRequest.setStatus(PaymentStatus.AUTHORIZED.toString());
        paymentRequest.setExternalToken(adyenResult.getPspReference());
        return Promise.pure(paymentRequest);
    }

    @Override
    public Promise<PaymentTransaction> capture(final String transactionId, final PaymentTransaction paymentRequest) {
        return PromiseFacade.PAYMENT.decorate(new Callable<PaymentTransaction>() {
            @Override
            public PaymentTransaction call() throws Exception {
                ModificationRequest request = new ModificationRequest();
                request.setMerchantAccount(getMerchantAccount());
                //TODO: need calculate amount according to currency ISO, hard code * 100 first
                request.setModificationAmount(new Amount(paymentRequest.getChargeInfo().getCurrency(),
                        paymentRequest.getChargeInfo().getAmount().longValue() * 100));
                request.setOriginalReference(transactionId);
                ModificationResult result = null;
                try {
                    result = service.capture(request);
                } catch (RemoteException e) {
                    throw AppServerExceptions.INSTANCE.providerProcessError(PROVIDER_NAME, e.toString()).exception();
                }
                if(result != null && result.getResponse().equals(CAPTURE_STATE)){
                    paymentRequest.setWebPaymentInfo(null);
                    paymentRequest.setStatus(PaymentStatus.SETTLEMENT_SUBMITTED.toString());
                    //update external token in capture as Adyen has new ref and cancel/refund would use this one
                    paymentRequest.setExternalToken(result.getPspReference());
                    return paymentRequest;
                }
                return paymentRequest;
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> charge(final PaymentInstrument pi, final PaymentTransaction paymentRequest) {
        return authorize(pi, paymentRequest)
                .then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                        return capture(paymentTransaction.getExternalToken(), paymentRequest);
                    }
                });
    }
}
