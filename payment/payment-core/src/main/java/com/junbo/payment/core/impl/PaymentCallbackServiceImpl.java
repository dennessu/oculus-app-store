/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.impl;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.PaymentCallbackService;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.db.repo.facade.PaymentRepositoryFacade;
import com.junbo.payment.spec.enums.PaymentEventType;
import com.junbo.payment.spec.internal.CallbackParams;
import com.junbo.payment.spec.internal.CallbackParamsFactory;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


/**
 * payment callback service implementation.
 */
public class PaymentCallbackServiceImpl implements PaymentCallbackService{
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentCallbackServiceImpl.class);
    protected static final String SUCCESS_EVENT_RESPONSE = "{\"result\": \"OK\"}";
    private PaymentTransactionService paymentTransactionService;
    @Autowired
    private PaymentRepositoryFacade paymentRepositoryFacade;
    @Override
    public Promise<Void> addPaymentProperties(String request) {
        request = CommonUtil.urlDecode(request);
        //get results
        Map<String, String> notifies = new HashMap<>();
        String[] requests = request.split("&");
        for(String field : requests){
            //signature end with "=" which would confuse the split
            if(field.startsWith("merchantSig=")){
                notifies.put("merchantSig", field.replace("merchantSig=", ""));
            }else{
                String[] results = field.split("=");
                notifies.put(results[0], results.length > 1 ? results[1] : null);
            }
        }
        final CallbackParams properties = CallbackParamsFactory.getCallbackParams(notifies);
        if(properties == null){
            throw AppServerExceptions.INSTANCE.providerNotFound(request).exception();
        }
        final Long paymentId = properties.getPaymentId();
        PaymentTransaction existedTransaction = paymentRepositoryFacade.getByPaymentId(paymentId);
        if(existedTransaction == null){
            LOGGER.error("the payment id is invalid:" + paymentId);
            throw AppClientExceptions.INSTANCE.paymentInstrumentNotFound(paymentId.toString()).exception();
        }
        //report a payment notify event to notify corresponding partner
        PaymentEvent event = new PaymentEvent();
        event.setPaymentId(paymentId);
        event.setType(PaymentEventType.NOTIFY.toString());
        event.setStatus(existedTransaction.getStatus());
        event.setRequest(CommonUtil.toJson(properties, null));
        event.setResponse(SUCCESS_EVENT_RESPONSE);
        return paymentTransactionService.reportPaymentEvent(event, null, properties)
                .then(new Promise.Func<PaymentTransaction, Promise<Void>>() {
                    @Override
                    public Promise<Void> apply(PaymentTransaction paymentTransaction) {
                        paymentRepositoryFacade.addPaymentProperties(paymentId, properties);
                        return Promise.pure(null);
                    }
                });
    }

    public void setPaymentTransactionService(PaymentTransactionService paymentTransactionService) {
        this.paymentTransactionService = paymentTransactionService;
    }
}
