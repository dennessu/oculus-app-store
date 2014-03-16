/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.impl;

import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.transaction.AsyncTransactionTemplate;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.PaymentTransactionService;
import com.junbo.payment.core.exception.*;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.provider.ProviderRoutingService;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.core.util.ProxyExceptionResponse;
import com.junbo.payment.db.mapper.*;
import com.junbo.payment.db.repository.*;
import com.junbo.payment.spec.enums.PIStatus;
import com.junbo.payment.spec.enums.PaymentEventType;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.enums.PaymentType;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * payment transaction service.
 */
public class PaymentTransactionServiceImpl implements PaymentTransactionService{
    private static final String[] FILTER = new String[]{"paymentId", "paymentProvider", "merchantAccount", "status",
                        "externalToken", "type","paymentEvents" };
    private static final String SUCCESS_EVENT_RESPONSE = "{\"result\": \"OK\"}";
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentTransactionServiceImpl.class);

    private PaymentInstrumentService paymentInstrumentService;
    private ProviderRoutingService providerRoutingService;
    @Autowired
    public void setPaymentInstrumentService(PaymentInstrumentService paymentInstrumentService) {
        this.paymentInstrumentService = paymentInstrumentService;
    }

    @Autowired
    public void setProviderRoutingService(ProviderRoutingService providerRoutingService) {
        this.providerRoutingService = providerRoutingService;
    }

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MerchantAccountRepository merchantAccountRepository;
    @Autowired
    private PaymentProviderRepository paymentProviderRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private TrackingUuidRepository trackingUuidRepository;

    @Override
    public Promise<PaymentTransaction> authorize(final PaymentTransaction request) {
        PaymentInstrument pi = validateAndGetPI(request);
        final PaymentAPI api = PaymentAPI.Auth;
        PaymentTransaction trackingResult = getResultByTrackingUuid(request, api);
        if(trackingResult != null){
            return Promise.pure(trackingResult);
        }
        final PaymentProviderService provider = providerRoutingService.getPaymentProvider(
                PaymentUtil.getPIType(pi.getType()));
        String merchantRef = getMerchantRef(request, provider.getProviderName());
        request.setPaymentProvider(provider.getProviderName());
        request.setMerchantAccount(merchantRef);
        request.setStatus(PaymentStatus.AUTH_CREATED.toString());
        request.setType(PaymentType.AUTHORIZE.toString());
        PaymentEvent createEvent = createPaymentEvent(request
                , PaymentEventType.AUTH_CREATE, PaymentStatus.AUTH_CREATED, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(request, createEvent);
        //commit the transaction with trackingUuid
        saveAndCommitPayment(request, api);
        //call braintree.
        return provider.authorize(pi.getCreditCardRequest().getExternalToken(), request)
                .recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Throwable throwable) {
                return handleProviderException(throwable, provider, request, api,
                        PaymentStatus.AUTH_DECLINED, PaymentEventType.AUTHORIZE);
            }
        }).then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                PaymentStatus authStatus = PaymentStatus.AUTHORIZED;
                request.setStatus(authStatus.toString());
                request.setExternalToken(paymentTransaction.getExternalToken());
                PaymentEvent authEvent = createPaymentEvent(request,
                        PaymentEventType.AUTHORIZE, authStatus, SUCCESS_EVENT_RESPONSE);
                addPaymentEvent(request, authEvent);
                updatePaymentAndSaveEvent(request, Arrays.asList(authEvent), api, authStatus);
                return Promise.pure(request);
            }
        });
    }

    private Promise<PaymentTransaction> handleProviderException(Throwable throwable,
             PaymentProviderService provider, PaymentTransaction request, PaymentAPI api,
             PaymentStatus status, PaymentEventType event) {
        ProxyExceptionResponse proxyResponse = new ProxyExceptionResponse(throwable);
        LOGGER.error(api.toString() + " declined by" + provider.getProviderName() +
                "; error detail: " + proxyResponse.getBody());
        request.setStatus(status.toString());
        PaymentEvent authDeclined = createPaymentEvent(request, event, status, proxyResponse.getBody());
        addPaymentEvent(request, authDeclined);
        updatePaymentAndSaveEvent(request, Arrays.asList(authDeclined), api, status);
        throw AppServerExceptions.INSTANCE.providerProcessError(
                provider.getProviderName(), proxyResponse.getBody()).exception();
    }

    @Override
    public Promise<PaymentTransaction> capture(final Long paymentId, final PaymentTransaction request) {
        final PaymentAPI api = PaymentAPI.Capture;
        request.setPaymentId(paymentId);
        final PaymentTransaction existedTransaction = getPaymentById(paymentId);
        if(existedTransaction == null){
            LOGGER.error("the payment id is invalid for the event.");
            throw AppClientExceptions.INSTANCE.invalidPaymentId(paymentId.toString()).exception();
        }
        if(request.getChargeInfo() != null && request.getChargeInfo().getAmount() != null &&
          existedTransaction.getChargeInfo().getAmount().compareTo(request.getChargeInfo().getAmount()) < 0){
            throw AppClientExceptions.INSTANCE.invalidAmount(
                    request.getChargeInfo().getAmount().toString()).exception();
        }
        if(!PaymentStatus.valueOf(existedTransaction.getStatus()).equals(PaymentStatus.AUTHORIZED)){
            throw AppServerExceptions.INSTANCE.invalidPaymentStatus(
                    existedTransaction.getStatus().toString()).exception();
        }
        PaymentTransaction trackingResult = getResultByTrackingUuid(request, api);
        if(trackingResult != null){
            return Promise.pure(trackingResult);
        }
        PaymentStatus createStatus = PaymentStatus.SETTLE_CREATED;
        existedTransaction.setStatus(createStatus.toString());
        PaymentEvent submitCreateEvent = createPaymentEvent(request,
                PaymentEventType.SUBMIT_SETTLE_CREATE, createStatus, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(existedTransaction, submitCreateEvent);
        //commit the payment event
        updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(submitCreateEvent),
                api, PaymentStatus.SETTLE_CREATED);
        PaymentInstrument pi = paymentInstrumentService.getById(null, existedTransaction.getPaymentInstrumentId());
        final PaymentProviderService provider = providerRoutingService.getPaymentProvider(
                PaymentUtil.getPIType(pi.getType()));
        return provider.capture(existedTransaction.getExternalToken(), request).
                recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Throwable throwable) {
                return handleProviderException(throwable, provider, request, api,
                        PaymentStatus.SETTLEMENT_DECLINED, PaymentEventType.SUBMIT_SETTLE);
            }
        }).then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                PaymentStatus settleStatus = PaymentStatus.SETTLEMENT_SUBMITTED;
                existedTransaction.setStatus(settleStatus.toString());
                PaymentEvent submitEvent = createPaymentEvent(request, PaymentEventType.SUBMIT_SETTLE,
                         settleStatus, SUCCESS_EVENT_RESPONSE);
                addPaymentEvent(existedTransaction, submitEvent);
                updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(submitEvent), api, settleStatus);
                return Promise.pure(existedTransaction);
            }
        });

    }

    @Override
    public Promise<PaymentTransaction> charge(final PaymentTransaction request) {
        PaymentInstrument pi = validateAndGetPI(request);
        final PaymentAPI api = PaymentAPI.Charge;
        PaymentTransaction trackingResult = getResultByTrackingUuid(request, api);
        if(trackingResult != null){
            return Promise.pure(trackingResult);
        }
        final PaymentProviderService provider = providerRoutingService.getPaymentProvider(
                PaymentUtil.getPIType(pi.getType()));
        String merchantRef = getMerchantRef(request, provider.getProviderName());
        request.setPaymentProvider(provider.getProviderName());
        request.setMerchantAccount(merchantRef);
        request.setStatus(PaymentStatus.SETTLE_CREATED.toString());
        request.setType(PaymentType.CHARGE.toString());
        PaymentEvent event = createPaymentEvent(request
                , PaymentEventType.SUBMIT_SETTLE_CREATE, PaymentStatus.SETTLE_CREATED, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(request, event);
        //commit the transaction
        saveAndCommitPayment(request, api);
        //call brain tree
        return provider.charge(pi.getCreditCardRequest().getExternalToken(), request)
                .recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Throwable throwable) {
                return handleProviderException(throwable, provider, request, api,
                        PaymentStatus.SETTLEMENT_DECLINED, PaymentEventType.SUBMIT_SETTLE);
            }
        }).then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                PaymentStatus submitStatus = PaymentStatus.SETTLEMENT_SUBMITTED;
                request.setExternalToken(paymentTransaction.getExternalToken());
                request.setStatus(submitStatus.toString());
                PaymentEvent submitEvent = createPaymentEvent(request,
                        PaymentEventType.SUBMIT_SETTLE, submitStatus, SUCCESS_EVENT_RESPONSE);
                addPaymentEvent(request, submitEvent);
                updatePaymentAndSaveEvent(request, Arrays.asList(submitEvent), api, submitStatus);
                return Promise.pure(request);
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> reverse(final Long paymentId) {
        final PaymentAPI api = PaymentAPI.Reverse;
        final PaymentTransaction existedTransaction = getPaymentById(paymentId);
        final PaymentEventType eventType;
        if(PaymentStatus.valueOf(existedTransaction.getStatus()).equals(PaymentStatus.AUTHORIZED)){
            eventType = PaymentEventType.AUTH_REVERSE;
        }else if(PaymentStatus.valueOf(existedTransaction.getStatus()).equals(PaymentStatus.AUTHORIZED)){
            eventType = PaymentEventType.SUBMIT_SETTLE_REVERSE;
        }else{
            throw AppServerExceptions.INSTANCE.invalidPaymentStatus(
                    existedTransaction.getStatus().toString()).exception();
        }
        PaymentTransaction trackingResult = getResultByTrackingUuid(existedTransaction, api);
        if(trackingResult != null){
            return Promise.pure(trackingResult);
        }
        PaymentInstrument pi = paymentInstrumentService.getById(null, existedTransaction.getPaymentInstrumentId());
        PaymentStatus createStatus = PaymentStatus.REVERSE_CREATED;
        PaymentEvent reverseCreateEvent = createPaymentEvent(existedTransaction,
                PaymentEventType.REVERSE_CREATE, createStatus, SUCCESS_EVENT_RESPONSE);
        existedTransaction.setStatus(createStatus.toString());
        addPaymentEvent(existedTransaction, reverseCreateEvent);
        updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(reverseCreateEvent), api, createStatus);
        final PaymentProviderService provider =
                providerRoutingService.getPaymentProvider(PaymentUtil.getPIType(pi.getType()));
        return provider.reverse(existedTransaction.getExternalToken())
                .recover(new Promise.Func<Throwable, Promise<Void>>() {
            @Override
            public Promise<Void> apply(Throwable throwable) {
                handleProviderException(throwable, provider, existedTransaction, api,
                        PaymentStatus.REVERSE_DECLINED, eventType);
                return null;
            }
        }).then(new Promise.Func<Void, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Void aVoid) {
                PaymentStatus reverseStatus = PaymentStatus.REVERSED;
                existedTransaction.setStatus(reverseStatus.toString());
                PaymentEvent reverseEvent = createPaymentEvent(
                        existedTransaction, eventType, PaymentStatus.REVERSED, SUCCESS_EVENT_RESPONSE);
                addPaymentEvent(existedTransaction, reverseEvent);
                updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(reverseEvent), api, reverseStatus);
                return Promise.pure(existedTransaction);
            }
        });
    }

    @Override
    public PaymentTransaction getById(Long paymentId) {
        PaymentTransaction request = paymentRepository.getByPaymentId(paymentId);
        if(request == null){
            throw AppClientExceptions.INSTANCE.resourceNotFound("payment_transaction").exception();
        }
        List<PaymentEvent> events = paymentRepository.getPaymentEventsByPaymentId(paymentId);
        request.setPaymentEvents(events);
        return request;
    }

    @Override
    public void reportPaymentEvent(PaymentEvent event) {
        if(event.getPaymentId() == null){
            LOGGER.error("the payment id is missing for the event.");
            throw AppClientExceptions.INSTANCE.invalidPaymentId(event.getPaymentId().toString()).exception();
        }
        paymentRepository.updatePayment(event.getPaymentId()
                    , PaymentUtil.getPaymentStatus(event.getStatus()), null);
        paymentRepository.savePaymentEvent(event.getPaymentId(), Arrays.asList(event));
    }

    @Override
    public Promise<PaymentTransaction> refund(Long paymentId, PaymentTransaction request) {
        return null;
    }

    private PaymentTransaction getResultByTrackingUuid(PaymentTransaction request, PaymentAPI api){
        if(request.getTrackingUuid() == null){
            throw AppClientExceptions.INSTANCE.missingTrackingUuid().exception();
        }
        TrackingUuid trackingUuid = trackingUuidRepository.getByTrackUuid(
                request.getUserId(), request.getTrackingUuid());
        if(trackingUuid != null && trackingUuid.getApi().equals(api)){
            return CommonUtil.parseJson(trackingUuid.getResponse(), PaymentTransaction.class);
        }
        return null;
    }

    private void addPaymentEvent(PaymentTransaction request, PaymentEvent event){
        if(request.getPaymentEvents() == null){
            request.setPaymentEvents(new ArrayList<PaymentEvent>());
        }
        request.getPaymentEvents().add(event);
    }

    private PaymentEvent createPaymentEvent(PaymentTransaction request,
             PaymentEventType eventType, PaymentStatus status, String response) {
        PaymentEvent event = new PaymentEvent();
        event.setPaymentId(request.getPaymentId());
        event.setType(eventType.toString());
        event.setStatus(status.toString());
        event.setChargeInfo(request.getChargeInfo());
        //TODO: need more detailed json request/response
        event.setRequest(CommonUtil.toJson(request, FILTER));
        event.setResponse(response);
        return event;
    }

    private PaymentTransaction getPaymentById(Long paymentId){
        PaymentTransaction existedTransaction = paymentRepository.getByPaymentId(paymentId);
        if(existedTransaction == null){
            LOGGER.error("the payment id is invalid for the event.");
            throw AppClientExceptions.INSTANCE.invalidPaymentId(paymentId.toString()).exception();
        }
        return existedTransaction;
    }

    private void saveOrUpdateTrackingUuid(PaymentTransaction request, PaymentAPI api){
        if(request.getPaymentId() == null){
            throw AppServerExceptions.INSTANCE.missingRequiredField("payment transaction id").exception();
        }
        TrackingUuid trackingUuid = new TrackingUuid();
        trackingUuid.setPaymentId(request.getPaymentId());
        trackingUuid.setApi(api);
        trackingUuid.setTrackingUuid(request.getTrackingUuid());
        trackingUuid.setUserId(request.getUserId());
        trackingUuid.setResponse(CommonUtil.toJson(request, null));
        TrackingUuid existing = trackingUuidRepository.getByTrackUuid(request.getUserId(), request.getTrackingUuid());
        if(existing == null){
            trackingUuidRepository.saveTrackingUuid(trackingUuid);
        }else{
            if(existing.getPaymentId() == null || !existing.getPaymentId().equals(request.getPaymentId()) ||
                    !existing.getApi().toString().equalsIgnoreCase(api.toString())){
                throw AppClientExceptions.INSTANCE.duplicatedTrackingUuid(
                        request.getTrackingUuid().toString()).exception();
            }
            trackingUuidRepository.updateResponse(trackingUuid);
        }
    }

    private PaymentInstrument validateAndGetPI(PaymentTransaction request) {
        if(request.getPaymentInstrumentId() == null){
            throw AppClientExceptions.INSTANCE.missingPaymentInstrumentId().exception();
        }
        if(request.getUserId() == null){
            throw AppClientExceptions.INSTANCE.missingUserId().exception();
        }
        if(request.getChargeInfo() == null){
            throw AppClientExceptions.INSTANCE.missingAmount().exception();
        }else{
            if(request.getChargeInfo().getAmount() == null){
                throw AppClientExceptions.INSTANCE.missingAmount().exception();
            }
            if(CommonUtil.isNullOrEmpty(request.getChargeInfo().getCurrency())){
                throw AppClientExceptions.INSTANCE.missingCurrency().exception();
            }
        }
        PaymentInstrument pi = paymentInstrumentService.getById(null, request.getPaymentInstrumentId());
        if(pi == null){
            throw AppClientExceptions.INSTANCE.invalidPaymentInstrumentId(
                    request.getPaymentInstrumentId().toString()).exception();
        }
        if(!pi.getStatus().equalsIgnoreCase(PIStatus.ACTIVE.toString())){
            throw AppServerExceptions.INSTANCE.invalidPIStatus(pi.getStatus()).exception();
        }
        return pi;
    }

    private PaymentTransaction saveAndCommitPayment(final PaymentTransaction request, final PaymentAPI api) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentTransaction>() {
            public PaymentTransaction doInTransaction(TransactionStatus txnStatus) {
                paymentRepository.save(request);
                saveOrUpdateTrackingUuid(request, api);
                return request;
            }
        });
    }

    private List<PaymentEvent> updatePaymentAndSaveEvent(final PaymentTransaction payment,
              final List<PaymentEvent> request, final PaymentAPI api, final PaymentStatus status){
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<List<PaymentEvent>>() {
            public List<PaymentEvent> doInTransaction(TransactionStatus txnStatus) {
                if(status != null){
                    paymentRepository.updatePayment(payment.getPaymentId()
                            , status, payment.getExternalToken());
                }
                paymentRepository.savePaymentEvent(payment.getPaymentId(), request);
                saveOrUpdateTrackingUuid(payment, api);
                return request;
            }
        });
    }

    private String getMerchantRef(PaymentTransaction request, String providerName){
        String merchantRef = merchantAccountRepository.getMerchantAccountRef(
                paymentProviderRepository.getProviderId(providerName)
                , request.getChargeInfo().getCurrency());
        if(CommonUtil.isNullOrEmpty(merchantRef)){
            throw AppServerExceptions.INSTANCE.merchantRefNotAvailable(
                    request.getChargeInfo().getCurrency()).exception();
        }
        return merchantRef;
    }

}
