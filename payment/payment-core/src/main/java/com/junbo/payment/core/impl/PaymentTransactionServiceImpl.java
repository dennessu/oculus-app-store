/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.impl;

import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.transaction.AsyncTransactionTemplate;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.PaymentTransactionService;
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
        validateRequest(request, true, true);
        PaymentInstrument pi = getPaymentInstrument(request);
        final PaymentAPI api = PaymentAPI.Auth;
        LOGGER.info("authorize for PI:" + request.getPaymentInstrumentId());
        PaymentTransaction trackingResult = getResultByTrackingUuid(request, api);
        if(trackingResult != null){
            return Promise.pure(trackingResult);
        }
        final PaymentProviderService provider = getPaymentProviderService(pi);
        String merchantRef = getMerchantRef(request, provider.getProviderName());
        request.setPaymentProvider(provider.getProviderName());
        request.setMerchantAccount(merchantRef);
        request.setStatus(PaymentStatus.AUTH_CREATED.toString());
        request.setType(PaymentType.AUTHORIZE.toString());
        PaymentEvent createEvent = createPaymentEvent(request
                , PaymentEventType.AUTH_CREATE, PaymentStatus.AUTH_CREATED, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(request, createEvent);
        //commit the transaction with trackingUuid
        saveAndCommitPayment(request);
        //call braintree.
        return provider.authorize(pi, request).recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
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
                provider.cloneTransactionResult(paymentTransaction, request);
                PaymentEvent authEvent = createPaymentEvent(request,
                        PaymentEventType.AUTHORIZE, authStatus, SUCCESS_EVENT_RESPONSE);
                addPaymentEvent(request, authEvent);
                updatePaymentAndSaveEvent(request, Arrays.asList(authEvent), api, authStatus, true);
                return Promise.pure(request);
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> capture(final Long paymentId, final PaymentTransaction request) {
        validateRequest(request, false, true);
        final PaymentAPI api = PaymentAPI.Capture;
        LOGGER.info("capture for payment:" + paymentId);
        PaymentTransaction trackingResult = getResultByTrackingUuid(request, api);
        if(trackingResult != null){
            return Promise.pure(trackingResult);
        }
        final PaymentTransaction existedTransaction = getPaymentById(paymentId);
        validateCapturable(paymentId, request, existedTransaction);

        CloneRequest(request, existedTransaction);
        PaymentStatus createStatus = PaymentStatus.SETTLE_CREATED;
        existedTransaction.setStatus(createStatus.toString());
        PaymentEvent submitCreateEvent = createPaymentEvent(request,
                PaymentEventType.SUBMIT_SETTLE_CREATE, createStatus, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(existedTransaction, submitCreateEvent);
        //commit the payment event
        updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(submitCreateEvent),
                api, PaymentStatus.SETTLE_CREATED, false);
        PaymentInstrument pi = getPaymentInstrument(existedTransaction);
        final PaymentProviderService provider = getPaymentProviderService(pi);
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
                updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(submitEvent), api, settleStatus, true);
                return Promise.pure(existedTransaction);
            }
        });

    }

    @Override
    public Promise<PaymentTransaction> charge(final PaymentTransaction request) {
        validateRequest(request, true, true);
        PaymentInstrument pi =getPaymentInstrument(request);
        final PaymentAPI api = PaymentAPI.Charge;
        LOGGER.info("charge for PI:" + request.getPaymentInstrumentId());
        PaymentTransaction trackingResult = getResultByTrackingUuid(request, api);
        if(trackingResult != null){
            return Promise.pure(trackingResult);
        }
        final PaymentProviderService provider = getPaymentProviderService(pi);
        String merchantRef = getMerchantRef(request, provider.getProviderName());
        request.setPaymentProvider(provider.getProviderName());
        request.setMerchantAccount(merchantRef);
        request.setStatus(PaymentStatus.SETTLE_CREATED.toString());
        request.setType(PaymentType.CHARGE.toString());
        PaymentEvent event = createPaymentEvent(request
                , PaymentEventType.SUBMIT_SETTLE_CREATE, PaymentStatus.SETTLE_CREATED, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(request, event);
        //commit the transaction
        saveAndCommitPayment(request);
        //call brain tree
        return provider.charge(pi, request).recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Throwable throwable) {
                return handleProviderException(throwable, provider, request, api,
                        PaymentStatus.SETTLEMENT_DECLINED, PaymentEventType.SUBMIT_SETTLE);
            }
        }).then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                PaymentStatus submitStatus = PaymentStatus.SETTLEMENT_SUBMITTED;
                provider.cloneTransactionResult(paymentTransaction, request);
                request.setStatus(submitStatus.toString());
                PaymentEvent submitEvent = createPaymentEvent(request,
                        PaymentEventType.SUBMIT_SETTLE, submitStatus, SUCCESS_EVENT_RESPONSE);
                addPaymentEvent(request, submitEvent);
                updatePaymentAndSaveEvent(request, Arrays.asList(submitEvent), api, submitStatus, true);
                return Promise.pure(request);
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> reverse(final Long paymentId, final PaymentTransaction request) {
        validateRequest(request, false, false);
        final PaymentAPI api = PaymentAPI.Reverse;
        LOGGER.info("reverse for payment:" + paymentId);
        PaymentTransaction trackingResult = getResultByTrackingUuid(request, api);
        if(trackingResult != null){
            return Promise.pure(trackingResult);
        }
        final PaymentTransaction existedTransaction = getPaymentById(paymentId);
        validateReverse(paymentId, request, existedTransaction);
        CloneRequest(request, existedTransaction);
        final PaymentEventType eventType;
        if(PaymentStatus.valueOf(existedTransaction.getStatus()).equals(PaymentStatus.AUTHORIZED)){
            eventType = PaymentEventType.AUTH_REVERSE;
        }else if(PaymentStatus.valueOf(existedTransaction.getStatus()).equals(PaymentStatus.SETTLEMENT_SUBMITTED)){
            eventType = PaymentEventType.SUBMIT_SETTLE_REVERSE;
        }else{
            throw AppServerExceptions.INSTANCE.invalidPaymentStatus(existedTransaction.getStatus()).exception();
        }
        PaymentInstrument pi = getPaymentInstrument(existedTransaction);
        PaymentStatus createStatus = PaymentStatus.REVERSE_CREATED;
        PaymentEvent reverseCreateEvent = createPaymentEvent(existedTransaction,
                PaymentEventType.REVERSE_CREATE, createStatus, SUCCESS_EVENT_RESPONSE);
        existedTransaction.setStatus(createStatus.toString());
        addPaymentEvent(existedTransaction, reverseCreateEvent);
        updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(reverseCreateEvent), api, createStatus, false);
        final PaymentProviderService provider = getPaymentProviderService(pi);
        return provider.reverse(existedTransaction.getExternalToken(), request)
                .recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Throwable throwable) {
                return handleProviderException(throwable, provider, request, api,
                        PaymentStatus.REVERSE_DECLINED, eventType);
            }
        }).then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction aVoid) {
                PaymentStatus reverseStatus = PaymentStatus.REVERSED;
                existedTransaction.setStatus(reverseStatus.toString());
                PaymentEvent reverseEvent = createPaymentEvent(
                        request, eventType, PaymentStatus.REVERSED, SUCCESS_EVENT_RESPONSE);
                addPaymentEvent(existedTransaction, reverseEvent);
                updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(reverseEvent), api, reverseStatus, true);
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
        LOGGER.info("report event for payment:" + event.getPaymentId());
        paymentRepository.updatePayment(event.getPaymentId()
                    , PaymentUtil.getPaymentStatus(event.getStatus()), null);
        paymentRepository.savePaymentEvent(event.getPaymentId(), Arrays.asList(event));
    }

    @Override
    public Promise<PaymentTransaction> refund(Long paymentId, PaymentTransaction request) {
        return null;
    }

    private Promise<PaymentTransaction> handleProviderException(Throwable throwable,
                                         PaymentProviderService provider, PaymentTransaction request, PaymentAPI api,
                                         PaymentStatus status, PaymentEventType event) {
        ProxyExceptionResponse proxyResponse = new ProxyExceptionResponse(throwable);
        LOGGER.error(api.toString() + " declined by " + provider.getProviderName() +
                "; error detail: " + proxyResponse.getBody());
        request.setStatus(status.toString());
        PaymentEvent authDeclined = createPaymentEvent(request, event, status, proxyResponse.getBody());
        addPaymentEvent(request, authDeclined);
        updatePaymentAndSaveEvent(request, Arrays.asList(authDeclined), api, status, false);
        throw AppServerExceptions.INSTANCE.providerProcessError(
                provider.getProviderName(), proxyResponse.getBody()).exception();
    }

    private void validateCapturable(Long paymentId, PaymentTransaction request, PaymentTransaction existed) {
        if(existed == null){
            LOGGER.error("the payment id is invalid for the event." + paymentId);
            throw AppClientExceptions.INSTANCE.invalidPaymentId(paymentId.toString()).exception();
        }
        if(!existed.getBillingRefId().equalsIgnoreCase(request.getBillingRefId())){
            LOGGER.error("the billing ref id is different with the one before for payment: " + paymentId);
        }
        if(request.getChargeInfo() != null && request.getChargeInfo().getAmount() != null &&
                existed.getChargeInfo().getAmount().compareTo(request.getChargeInfo().getAmount()) < 0){
            LOGGER.error("capture amount should not exceed the authorized amount.");
            throw AppClientExceptions.INSTANCE.invalidAmount(
                    request.getChargeInfo().getAmount().toString()).exception();
        }
        if(!PaymentStatus.valueOf(existed.getStatus()).equals(PaymentStatus.AUTHORIZED)){
            LOGGER.error("the payment status is not allowed to be captured.");
            throw AppServerExceptions.INSTANCE.invalidPaymentStatus(existed.getStatus()).exception();
        }
    }


    private PaymentProviderService getPaymentProviderService(PaymentInstrument pi) {
        PaymentProviderService provider = providerRoutingService.getPaymentProvider(
                PaymentUtil.getPIType(pi.getType()));
        if(provider == null){
            throw AppServerExceptions.INSTANCE.providerNotFound(pi.getType()).exception();
        }
        return provider;
    }

    //use new transaction for business data to avoid hibernate cache in the service level transaction
    private PaymentTransaction getResultByTrackingUuid(final PaymentTransaction request, final PaymentAPI api) {
        if(request.getTrackingUuid() == null){
            throw AppClientExceptions.INSTANCE.missingTrackingUuid().exception();
        }
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentTransaction>() {
            public PaymentTransaction doInTransaction(TransactionStatus txnStatus) {
                TrackingUuid  trackingUuid = trackingUuidRepository.getByTrackUuid(
                        request.getUserId(), request.getTrackingUuid());
                if(trackingUuid != null && trackingUuid.getApi().equals(api)){
                    return CommonUtil.parseJson(trackingUuid.getResponse(), PaymentTransaction.class);
                }
                return null;
            }
        });
    }

    private void validateReverse(Long paymentId, PaymentTransaction request, PaymentTransaction existedTransaction){
        if(existedTransaction == null){
            LOGGER.error("the payment id is invalid for the event." + paymentId);
            throw AppClientExceptions.INSTANCE.invalidPaymentId(paymentId.toString()).exception();
        }
        if(!existedTransaction.getBillingRefId().equalsIgnoreCase(request.getBillingRefId())){
            LOGGER.error("the billing ref id is different with the one before for payment: " + paymentId);
        }
    }

    private void CloneRequest(PaymentTransaction request, PaymentTransaction existedTransaction) {
        existedTransaction.setTrackingUuid(request.getTrackingUuid());
        existedTransaction.setUserId(request.getUserId());
        request.setId(existedTransaction.getId());
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
        event.setPaymentId(request.getId());
        event.setType(eventType.toString());
        event.setStatus(status.toString());
        event.setChargeInfo(request.getChargeInfo());
        //TODO: need more detailed json request/response
        event.setRequest(CommonUtil.toJson(request, FILTER));
        event.setResponse(response);
        return event;
    }

    //use new transaction for business data to avoid hibernate cache in the service level transaction
    private PaymentTransaction getPaymentById(final Long paymentId){
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentTransaction>() {
            public PaymentTransaction doInTransaction(TransactionStatus txnStatus) {
                PaymentTransaction existedTransaction = paymentRepository.getByPaymentId(paymentId);
                if(existedTransaction == null){
                    LOGGER.error("the payment id is invalid for the event.");
                    throw AppClientExceptions.INSTANCE.invalidPaymentId(paymentId.toString()).exception();
                }
                return existedTransaction;
            }
        });
    }

    private void saveTrackingUuid(PaymentTransaction request, PaymentAPI api){
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

    private void validateRequest(PaymentTransaction request, boolean needChargeInfo, boolean supportChargeInfo){
        if(request.getUserId() == null){
            throw AppClientExceptions.INSTANCE.missingUserId().exception();
        }
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

    private PaymentInstrument getPaymentInstrument(PaymentTransaction request) {
        if(request.getPaymentInstrumentId() == null){
            throw AppClientExceptions.INSTANCE.missingPaymentInstrumentId().exception();
        }
        PaymentInstrument pi = paymentInstrumentService.getById(null,
                request.getPaymentInstrumentId().getPaymentInstrumentId());
        if(pi == null){
            throw AppClientExceptions.INSTANCE.invalidPaymentInstrumentId(
                    request.getPaymentInstrumentId().toString()).exception();
        }
        if(!pi.getStatus().equalsIgnoreCase(PIStatus.ACTIVE.toString())){
            throw AppServerExceptions.INSTANCE.invalidPIStatus(pi.getStatus()).exception();
        }
        return pi;
    }

    private PaymentTransaction saveAndCommitPayment(final PaymentTransaction request) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentTransaction>() {
            public PaymentTransaction doInTransaction(TransactionStatus txnStatus) {
                paymentRepository.save(request);
                return request;
            }
        });
    }

    private List<PaymentEvent> updatePaymentAndSaveEvent(final PaymentTransaction payment,
        final List<PaymentEvent> events, final PaymentAPI api, final PaymentStatus status, final boolean saveUuid){
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<List<PaymentEvent>>() {
            public List<PaymentEvent> doInTransaction(TransactionStatus txnStatus) {
                if(status != null){
                    paymentRepository.updatePayment(payment.getId()
                            , status, payment.getExternalToken());
                }
                paymentRepository.savePaymentEvent(payment.getId(), events);
                if(saveUuid){
                    saveTrackingUuid(payment, api);
                }
                return events;
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
