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
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.core.util.ProxyExceptionResponse;
import com.junbo.payment.db.mapper.*;
import com.junbo.payment.spec.enums.*;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * payment transaction service.
 */
public class PaymentTransactionServiceImpl extends AbstractPaymentTransactionServiceImpl{
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentTransactionServiceImpl.class);

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
        String merchantRef = getMerchantRef(pi, request, provider.getProviderName());
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
                provider.cloneTransactionResult(paymentTransaction, request);
                PaymentStatus authStatus = PaymentStatus.valueOf(request.getStatus());
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
        PaymentStatus createStatus = PaymentStatus.SETTLEMENT_SUBMIT_CREATED;
        existedTransaction.setStatus(createStatus.toString());
        PaymentEvent submitCreateEvent = createPaymentEvent(request,
                PaymentEventType.SUBMIT_SETTLE_CREATE, createStatus, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(existedTransaction, submitCreateEvent);
        //commit the payment event
        updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(submitCreateEvent),
                api, createStatus, false);
        PaymentInstrument pi = getPaymentInstrument(existedTransaction);
        final PaymentProviderService provider = getPaymentProviderService(pi);
        return provider.capture(existedTransaction.getExternalToken(), request).
                recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Throwable throwable) {
                return handleProviderException(throwable, provider, request, api,
                        PaymentStatus.SETTLEMENT_SUBMIT_DECLINED, PaymentEventType.SUBMIT_SETTLE);
            }
        }).then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                provider.cloneTransactionResult(paymentTransaction, existedTransaction);
                PaymentStatus settleStatus = PaymentStatus.valueOf(existedTransaction.getStatus());
                PaymentEvent submitEvent = createPaymentEvent(request, PaymentEventType.SUBMIT_SETTLE,
                         settleStatus, SUCCESS_EVENT_RESPONSE);
                addPaymentEvent(existedTransaction, submitEvent);
                updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(submitEvent), api, settleStatus, true);
                return Promise.pure(existedTransaction);
            }
        });

    }

    @Override
    public Promise<PaymentTransaction> confirm(Long paymentId, final PaymentTransaction request) {
        validateRequest(request, false, true);
        final PaymentAPI api = PaymentAPI.Confirm;
        LOGGER.info("Confirm for payment:" + paymentId);
        PaymentTransaction trackingResult = getResultByTrackingUuid(request, api);
        if(trackingResult != null){
            return Promise.pure(trackingResult);
        }
        final PaymentTransaction existedTransaction = getPaymentById(paymentId);

        CloneRequest(request, existedTransaction);
        PaymentStatus createStatus = PaymentStatus.SETTLE_CREATED;
        existedTransaction.setStatus(createStatus.toString());
        PaymentEvent submitCreateEvent = createPaymentEvent(request,
                PaymentEventType.IMMEDIATE_SETTLE_CREATE, createStatus, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(existedTransaction, submitCreateEvent);
        //commit the payment event
        updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(submitCreateEvent),
                api, PaymentStatus.SETTLE_CREATED, false);
        PaymentInstrument pi = getPaymentInstrument(existedTransaction);
        final PaymentProviderService provider = getPaymentProviderService(pi);
        Map<PropertyField, String> properties = paymentRepository.getPaymentProperties(paymentId);
        if(!properties.isEmpty()){
            request.getWebPaymentInfo().setToken(properties.get(PropertyField.EXTERNAL_ACCESS_TOKEN));
            request.getWebPaymentInfo().setPayerId(properties.get(PropertyField.EXTERNAL_PAYER_ID));
        }
        return provider.confirm(existedTransaction.getExternalToken(), request).
                recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(Throwable throwable) {
                        return handleProviderException(throwable, provider, request, api,
                                PaymentStatus.SETTLE_DECLINED, PaymentEventType.IMMEDIATE_SETTLE);
                    }
                }).then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                provider.cloneTransactionResult(paymentTransaction, existedTransaction);
                PaymentStatus settleStatus = PaymentStatus.valueOf(existedTransaction.getStatus());
                PaymentEvent submitEvent = createPaymentEvent(request, PaymentEventType.IMMEDIATE_SETTLE,
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
        String merchantRef = getMerchantRef(pi, request, provider.getProviderName());
        request.setPaymentProvider(provider.getProviderName());
        request.setMerchantAccount(merchantRef);
        request.setStatus(PaymentStatus.SETTLEMENT_SUBMIT_CREATED.toString());
        request.setType(PaymentType.CHARGE.toString());
        PaymentEvent event = createPaymentEvent(request, PaymentEventType.SUBMIT_SETTLE_CREATE,
                 PaymentStatus.SETTLEMENT_SUBMIT_CREATED, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(request, event);
        //commit the transaction
        saveAndCommitPayment(request);
        //call brain tree
        return provider.charge(pi, request).recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Throwable throwable) {
                return handleProviderException(throwable, provider, request, api,
                        PaymentStatus.SETTLEMENT_SUBMIT_DECLINED, PaymentEventType.SUBMIT_SETTLE);
            }
        }).then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                provider.cloneTransactionResult(paymentTransaction, request);
                PaymentStatus submitStatus = PaymentStatus.valueOf(request.getStatus());
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
    public Promise<PaymentTransaction> getUpdatedTransaction(Long paymentId) {
        final PaymentTransaction result = paymentRepository.getByPaymentId(paymentId);
        if(result == null){
            throw AppClientExceptions.INSTANCE.resourceNotFound("payment_transaction").exception();
        }
        final List<PaymentEvent> events = paymentRepository.getPaymentEventsByPaymentId(paymentId);
        result.setPaymentEvents(events);
        if(result.getStatus().equalsIgnoreCase(PaymentStatus.SETTLED.toString()) ||
                result.getStatus().equalsIgnoreCase(PaymentStatus.SETTLE_DECLINED.toString())){
            return Promise.pure(result);
        }else{
            return getProviderTransaction(paymentId)
                    .recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
                        @Override
                        public Promise<PaymentTransaction> apply(Throwable throwable) {
                            LOGGER.warn("get error when get provider status");
                            return Promise.pure(result);
                        }
                    })
                    .then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
                        @Override
                        public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                            if (paymentTransaction == null) {
                                return Promise.pure(result);
                            } else {
                                PaymentStatus paymentStatus = PaymentStatus.valueOf(paymentTransaction.getStatus());
                                PaymentEvent reportEvent = createPaymentEvent(result
                                        , PaymentEventType.REPORT_EVENT, paymentStatus, SUCCESS_EVENT_RESPONSE);
                                reportPaymentEvent(reportEvent);
                                result.setStatus(paymentStatus.toString());
                                result.getPaymentEvents().add(reportEvent);
                                return Promise.pure(result);
                            }
                        }
                    });
        }

    }

    @Override
    public Promise<PaymentTransaction> getProviderTransaction(Long paymentId) {
        PaymentTransaction payment = paymentRepository.getByPaymentId(paymentId);
        PaymentInstrument pi = getPaymentInstrument(payment);
        final PaymentProviderService provider = getPaymentProviderService(pi);
        String externalToken = payment.getExternalToken();
        if(CommonUtil.isNullOrEmpty(externalToken)){
            Map<PropertyField, String> properties = paymentRepository.getPaymentProperties(paymentId);
            if(!properties.isEmpty()){
                externalToken = properties.get(PropertyField.EXTERNAL_ACCESS_TOKEN);
            }
        }
        if(CommonUtil.isNullOrEmpty(externalToken)){
            throw AppServerExceptions.INSTANCE.noExternalTokenFoundForPayment(paymentId.toString()).exception();
        }
        return provider.getByTransactionToken(externalToken)
                .recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(Throwable throwable) {
                        ProxyExceptionResponse proxyResponse = new ProxyExceptionResponse(throwable);
                        LOGGER.error("error get transaction for" + provider.getProviderName() +
                                "; error detail: " + proxyResponse.getBody());
                        throw AppServerExceptions.INSTANCE.providerProcessError(
                                provider.getProviderName(), proxyResponse.getBody()).exception();
                    }
                });
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
    public Promise<PaymentTransaction> refund(Long paymentId, final PaymentTransaction request) {
        validateRequest(request, false, true);
        final PaymentAPI api = PaymentAPI.Refund;
        LOGGER.info("capture for payment:" + paymentId);
        PaymentTransaction trackingResult = getResultByTrackingUuid(request, api);
        if(trackingResult != null){
            return Promise.pure(trackingResult);
        }
        final PaymentTransaction existedTransaction = getPaymentById(paymentId);
        validateTransactionRequest(paymentId, request, existedTransaction);

        CloneRequest(request, existedTransaction);
        PaymentStatus createStatus = PaymentStatus.REFUND_CREATED;
        existedTransaction.setStatus(createStatus.toString());
        PaymentEvent submitCreateEvent = createPaymentEvent(request,
                PaymentEventType.REFUND_CREATE, createStatus, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(existedTransaction, submitCreateEvent);
        //commit the payment event
        updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(submitCreateEvent),
                api, createStatus, false);
        PaymentInstrument pi = getPaymentInstrument(existedTransaction);
        final PaymentProviderService provider = getPaymentProviderService(pi);
        return provider.refund(existedTransaction.getExternalToken(), request).
                recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
                    @Override
                    public Promise<PaymentTransaction> apply(Throwable throwable) {
                        return handleProviderException(throwable, provider, request, api,
                                PaymentStatus.REFUND_DECLINED, PaymentEventType.REFUND);
                    }
                }).then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                provider.cloneTransactionResult(paymentTransaction, existedTransaction);
                PaymentStatus settleStatus = PaymentStatus.valueOf(existedTransaction.getStatus());
                PaymentEvent submitEvent = createPaymentEvent(request, PaymentEventType.REFUND,
                        settleStatus, SUCCESS_EVENT_RESPONSE);
                addPaymentEvent(existedTransaction, submitEvent);
                updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(submitEvent), api, settleStatus, true);
                return Promise.pure(existedTransaction);
            }
        });

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
        validateTransactionRequest(paymentId, request, existed);
        if(!PaymentStatus.valueOf(existed.getStatus()).equals(PaymentStatus.AUTHORIZED)){
            LOGGER.error("the payment status is not allowed to be captured.");
            throw AppServerExceptions.INSTANCE.invalidPaymentStatus(existed.getStatus()).exception();
        }
    }

    private void validateTransactionRequest(Long paymentId, PaymentTransaction request, PaymentTransaction existed){
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
    }

    private PaymentProviderService getPaymentProviderService(PaymentInstrument pi) {
        PaymentProviderService provider = providerRoutingService.getPaymentProvider(
                PaymentUtil.getPIType(pi.getType()));
        if(provider == null){
            throw AppServerExceptions.INSTANCE.providerNotFound(pi.getType()).exception();
        }
        return provider;
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
}
