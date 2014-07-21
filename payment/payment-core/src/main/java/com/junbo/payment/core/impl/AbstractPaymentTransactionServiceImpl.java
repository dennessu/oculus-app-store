/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.impl;

import com.junbo.common.id.PIType;
import com.junbo.langur.core.transaction.AsyncTransactionTemplate;
import com.junbo.payment.clientproxy.UserInfoFacade;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.provider.ProviderRoutingService;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.spec.model.TrackingUuid;
import com.junbo.payment.db.repository.MerchantAccountRepository;
import com.junbo.payment.db.repository.PaymentProviderRepository;
import com.junbo.payment.db.repo.facade.PaymentRepositoryFacade;
import com.junbo.payment.db.repo.TrackingUuidRepository;
import com.junbo.payment.spec.enums.PaymentAPI;
import com.junbo.payment.spec.enums.PaymentEventType;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.payment.spec.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

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
    protected PaymentRepositoryFacade paymentRepositoryFacade;
    @Autowired
    protected MerchantAccountRepository merchantAccountRepository;
    @Autowired
    protected PaymentProviderRepository paymentProviderRepository;
    @Autowired
    protected PlatformTransactionManager transactionManager;
    @Autowired
    protected TrackingUuidRepository trackingUuidRepository;
    protected UserInfoFacade userInfoFacade;

    protected void validateRequest(PaymentTransaction request, boolean needChargeInfo, boolean supportChargeInfo){
        if(request.getUserId() == null){
            throw AppClientExceptions.INSTANCE.missingUserId().exception();
        }
        UserInfo user = userInfoFacade.getUserInfo(request.getUserId()).get();
        if(user == null){
            throw AppClientExceptions.INSTANCE.invalidUserId(request.getUserId().toString()).exception();
        }
        request.setUserInfo(user);
        if(request.getTrackingUuid() == null){
            throw AppClientExceptions.INSTANCE.missingTrackingUuid().exception();
        }
        if(CommonUtil.isNullOrEmpty(request.getBillingRefId())){
            throw AppClientExceptions.INSTANCE.missingBillingRefId().exception();
        }
        if(needChargeInfo){
            if(request.getChargeInfo() == null){
                throw AppClientExceptions.INSTANCE.missingAmount().exception();
            }
            if(request.getChargeInfo().getAmount() == null){
                throw AppClientExceptions.INSTANCE.missingAmount().exception();
            }
            if(CommonUtil.isNullOrEmpty(request.getChargeInfo().getCurrency())){
                throw AppClientExceptions.INSTANCE.missingCurrency().exception();
            }
        }
        if(!supportChargeInfo && request.getChargeInfo() != null){
            throw AppClientExceptions.INSTANCE.fieldNotNeeded("chargeInfo").exception();
        }
    }

    //use new transaction for business data to avoid hibernate cache in the service level transaction
    protected PaymentTransaction getResultByTrackingUuid(final PaymentTransaction request, final PaymentAPI api) {
        if(request.getTrackingUuid() == null){
            throw AppClientExceptions.INSTANCE.missingTrackingUuid().exception();
        }
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentTransaction>() {
            public PaymentTransaction doInTransaction(TransactionStatus txnStatus) {
                TrackingUuid trackingUuid = trackingUuidRepository.getByTrackingUuid(
                        request.getUserId(), request.getTrackingUuid()).get();
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
        trackingUuid.setApi(api.toString());
        trackingUuid.setTrackingUuid(request.getTrackingUuid());
        trackingUuid.setUserId(request.getUserId());
        trackingUuid.setResponse(CommonUtil.toJson(request, null));
        trackingUuidRepository.create(trackingUuid).get();
    }

    protected PaymentInstrument getPaymentInstrument(final PaymentTransaction request){
        if(request.getPaymentInstrumentId() == null){
            throw AppClientExceptions.INSTANCE.missingPaymentInstrumentId().exception();
        }
        PaymentInstrument pi = null;
        try{
            AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
            template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
            pi = template.execute(new TransactionCallback<PaymentInstrument>() {
                public PaymentInstrument doInTransaction(TransactionStatus txnStatus) {
                    return paymentInstrumentService.getById(request.getPaymentInstrumentId()).get();
                }
            });
        }catch(Exception ex){
            throw AppServerExceptions.INSTANCE.invalidPI().exception();
        }
        if(pi == null){
            throw AppClientExceptions.INSTANCE.invalidPaymentInstrumentId(
                    request.getPaymentInstrumentId().toString()).exception();
        }
        if(!pi.getIsActive()){
            throw AppServerExceptions.INSTANCE.invalidPI().exception();
        }
        return pi;
    }

    protected String getMerchantRef(PaymentInstrument pi, PaymentTransaction request, String providerName){
        if(PIType.get(pi.getType()).equals(PIType.CREDITCARD)){
            String merchantRef = merchantAccountRepository.getMerchantAccountRef(
                    paymentProviderRepository.getProviderId(providerName), request.getChargeInfo().getCurrency());
            if(CommonUtil.isNullOrEmpty(merchantRef)){
                throw AppServerExceptions.INSTANCE.merchantRefNotAvailable(
                        request.getChargeInfo().getCurrency()).exception();
            }
            return merchantRef;
        }
        return null;
    }

    //use new transaction for business data to avoid hibernate cache in the service level transaction
    protected PaymentTransaction getPaymentById(final Long paymentId){
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentTransaction>() {
            public PaymentTransaction doInTransaction(TransactionStatus txnStatus) {
                PaymentTransaction existedTransaction = paymentRepositoryFacade.getByPaymentId(paymentId);
                if(existedTransaction == null){
                    LOGGER.error("the payment id is invalid for the event.");
                    throw AppClientExceptions.INSTANCE.invalidPaymentId(paymentId.toString()).exception();
                }
                return existedTransaction;
            }
        });
    }

    protected PaymentTransaction getPaymentAndEvents(final Long paymentId) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentTransaction>() {
            public PaymentTransaction doInTransaction(TransactionStatus txnStatus) {
                final PaymentTransaction result = paymentRepositoryFacade.getByPaymentId(paymentId);
                if(result == null){
                    throw AppClientExceptions.INSTANCE.resourceNotFound("payment_transaction").exception();
                }
                final List<PaymentEvent> events = paymentRepositoryFacade.getPaymentEventsByPaymentId(paymentId);
                result.setPaymentEvents(events);
                return result;
            }
        });
    }

    protected PaymentTransaction saveAndCommitPayment(final PaymentTransaction request) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentTransaction>() {
            public PaymentTransaction doInTransaction(TransactionStatus txnStatus) {
                paymentRepositoryFacade.save(request);
                return request;
            }
        });
    }

    protected List<PaymentEvent> updatePaymentAndSaveEvent(final PaymentTransaction payment,
                                                         final List<PaymentEvent> events, final PaymentAPI api, final PaymentStatus status, final boolean saveUuid){
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<List<PaymentEvent>>() {
            public List<PaymentEvent> doInTransaction(TransactionStatus txnStatus) {
                if(status != null){
                    paymentRepositoryFacade.updatePayment(payment.getId()
                            , status, payment.getExternalToken());
                }
                paymentRepositoryFacade.savePaymentEvent(payment.getId(), events);
                if(saveUuid){
                    saveTrackingUuid(payment, api);
                }
                return events;
            }
        });
    }

    protected PaymentEvent createPaymentEvent(PaymentTransaction request,
                                            PaymentEventType eventType, PaymentStatus status, String response) {
        PaymentEvent event = new PaymentEvent();
        event.setPaymentId(request.getId());
        event.setType(eventType.toString());
        event.setStatus(status.toString());
        event.setChargeInfo(request.getChargeInfo());
        //TODO: need more detailed json request/response
        event.setRequest(CommonUtil.toJson(request, FILTER));
        event.setResponse(response);
        return event;
    }

    protected PaymentProviderService getPaymentProviderService(PaymentInstrument pi) {
        PaymentProviderService provider = providerRoutingService.getPaymentProvider(
                PaymentUtil.getPIType(pi.getType()));
        if(provider == null){
            throw AppServerExceptions.INSTANCE.providerNotFound(
                    PaymentUtil.getPIType(pi.getType()).toString()).exception();
        }
        return provider;
    }

    protected PaymentProviderService getProviderByName(String provider){
        PaymentProviderService service = providerRoutingService.getProviderByName(provider);
        if(service == null){
            throw AppServerExceptions.INSTANCE.providerNotFound(provider).exception();
        }
        return service;
    }

    public void setUserInfoFacade(UserInfoFacade userInfoFacade) {
        this.userInfoFacade = userInfoFacade;
    }
}
