/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.impl;

import com.junbo.langur.core.transaction.AsyncTransactionTemplate;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.core.provider.ProviderRoutingService;
import com.junbo.payment.db.mapper.PaymentAPI;
import com.junbo.payment.db.mapper.TrackingUuid;
import com.junbo.payment.db.repository.MerchantAccountRepository;
import com.junbo.payment.db.repository.PaymentProviderRepository;
import com.junbo.payment.db.repository.PaymentRepository;
import com.junbo.payment.db.repository.TrackingUuidRepository;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Abstract Payment Transaction Service Implementation.
 */
public abstract class AbstractPaymentTransactionServiceImpl implements PaymentTransactionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPaymentTransactionServiceImpl.class);
    protected static final String[] FILTER = new String[]{"paymentId", "paymentProvider", "merchantAccount", "status",
            "externalToken", "type","paymentEvents" };
    protected static final String SUCCESS_EVENT_RESPONSE = "{\"result\": \"OK\"}";
    protected PaymentInstrumentService paymentInstrumentService;
    protected ProviderRoutingService providerRoutingService;
    @Autowired
    public void setPaymentInstrumentService(PaymentInstrumentService paymentInstrumentService) {
        this.paymentInstrumentService = paymentInstrumentService;
    }

    @Autowired
    public void setProviderRoutingService(ProviderRoutingService providerRoutingService) {
        this.providerRoutingService = providerRoutingService;
    }

    @Autowired
    protected PaymentRepository paymentRepository;
    @Autowired
    protected MerchantAccountRepository merchantAccountRepository;
    @Autowired
    protected PaymentProviderRepository paymentProviderRepository;
    @Autowired
    protected PlatformTransactionManager transactionManager;
    @Autowired
    protected TrackingUuidRepository trackingUuidRepository;

    //use new transaction for business data to avoid hibernate cache in the service level transaction
    protected PaymentTransaction getResultByTrackingUuid(final PaymentTransaction request, final PaymentAPI api) {
        if(request.getTrackingUuid() == null){
            throw AppClientExceptions.INSTANCE.missingTrackingUuid().exception();
        }
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentTransaction>() {
            public PaymentTransaction doInTransaction(TransactionStatus txnStatus) {
                TrackingUuid trackingUuid = trackingUuidRepository.getByTrackUuid(
                        request.getUserId(), request.getTrackingUuid());
                if(trackingUuid != null && trackingUuid.getApi().equals(api)){
                    return CommonUtil.parseJson(trackingUuid.getResponse(), PaymentTransaction.class);
                }
                return null;
            }
        });
    }

    protected void saveTrackingUuid(PaymentTransaction request, PaymentAPI api){
        if(request.getId() == null){
            LOGGER.error("payment transaction id should not be empty when store tracking uuid.");
            throw AppServerExceptions.INSTANCE.missingRequiredField("payment transaction id").exception();
        }
        TrackingUuid trackingUuid = new TrackingUuid();
        trackingUuid.setPaymentId(request.getId());
        trackingUuid.setApi(api);
        trackingUuid.setTrackingUuid(request.getTrackingUuid());
        trackingUuid.setUserId(request.getUserId());
        trackingUuid.setResponse(CommonUtil.toJson(request, null));
        trackingUuidRepository.saveTrackingUuid(trackingUuid);
    }

    protected PaymentInstrument getPaymentInstrument(PaymentTransaction request) {
        if(request.getPaymentInstrumentId() == null){
            throw AppClientExceptions.INSTANCE.missingPaymentInstrumentId().exception();
        }
        PaymentInstrument pi = paymentInstrumentService.getById(request.getPaymentInstrumentId());
        if(pi == null){
            throw AppClientExceptions.INSTANCE.invalidPaymentInstrumentId(
                    request.getPaymentInstrumentId().toString()).exception();
        }
        if(!pi.getIsActive()){
            throw AppServerExceptions.INSTANCE.invalidPI().exception();
        }
        return pi;
    }
}
