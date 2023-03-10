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
import com.junbo.payment.core.provider.PaymentProvider;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.spec.enums.PaymentAPI;
import com.junbo.payment.spec.enums.PaymentEventType;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.enums.PaymentType;
import com.junbo.payment.spec.internal.CallbackParams;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * payment transaction service.
 */
public class PaymentTransactionServiceImpl extends AbstractPaymentTransactionServiceImpl{
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentTransactionServiceImpl.class);

    @Override
    public Promise<PaymentTransaction> credit(final PaymentTransaction request) {
        validateRequest(request, true, true);
        PaymentInstrument pi = getPaymentInstrument(request);
        final PaymentAPI api = PaymentAPI.Credit;
        LOGGER.info("credit for PI:" + request.getPaymentInstrumentId());
        PaymentTransaction trackingResult = getResultByTrackingUuid(request, api);
        if(trackingResult != null){
            return Promise.pure(trackingResult);
        }
        final PaymentProviderService provider = getPaymentProviderService(pi);
        request.setPaymentProvider(provider.getProviderName());
        request.setMerchantAccount(getMerchantRef(pi, request, provider.getProviderName()));
        request.setStatus(PaymentStatus.CREDIT_CREATED.toString());
        request.setType(PaymentType.CREDIT.toString());
        PaymentEvent createEvent = createPaymentEvent(request
                , PaymentEventType.CREDIT_CREATE, PaymentStatus.CREDIT_CREATED, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(request, createEvent);
        //commit the transaction with trackingUuid
        saveAndCommitPayment(request);
        return provider.credit(pi, request).recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Throwable throwable) {
                return handleProviderException(throwable, provider, request, api,
                        PaymentStatus.CREDIT_DECLINED, PaymentEventType.CREDIT);
            }
        }).then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                provider.cloneTransactionResult(paymentTransaction, request);
                PaymentStatus authStatus = PaymentStatus.valueOf(request.getStatus());
                PaymentEvent authEvent = createPaymentEvent(request,
                        PaymentEventType.CREDIT, authStatus, SUCCESS_EVENT_RESPONSE);
                addPaymentEvent(request, authEvent);
                updatePaymentAndSaveEvent(request, Arrays.asList(authEvent), api, authStatus, true);
                return Promise.pure(request);
            }
        });
    }

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
        request.setPaymentProvider(provider.getProviderName());
        request.setMerchantAccount(getMerchantRef(pi, request, provider.getProviderName()));
        request.setStatus(PaymentStatus.AUTH_CREATED.toString());
        request.setType(PaymentType.AUTHORIZE.toString());
        PaymentEvent createEvent = createPaymentEvent(request
                , PaymentEventType.AUTH_CREATE, PaymentStatus.AUTH_CREATED, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(request, createEvent);
        //commit the transaction with trackingUuid
        saveAndCommitPayment(request);
        LOGGER.info("start to call provider authorize");
        return provider.authorize(pi, request).recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Throwable throwable) {
                return handleProviderException(throwable, provider, request, api,
                        PaymentStatus.AUTH_DECLINED, PaymentEventType.AUTHORIZE);
            }
        }).then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                LOGGER.info("call provider authorize successfully");
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
        final PaymentProviderService provider = getProviderByName(existedTransaction.getPaymentProvider());
        LOGGER.info("start to call provider capture");
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
                LOGGER.info("call provider capture successfully");
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
        if(existedTransaction.getStatus().equalsIgnoreCase(PaymentStatus.SETTLED.toString())){
            LOGGER.error("the trasnaction has already been settled:" + paymentId);
            throw AppServerExceptions.INSTANCE.invalidPaymentStatus(PaymentStatus.SETTLED.toString()).exception();
        }
        CloneRequest(request, existedTransaction);
        PaymentStatus createStatus = PaymentStatus.SETTLE_CREATED;
        existedTransaction.setStatus(createStatus.toString());
        PaymentEvent submitCreateEvent = createPaymentEvent(request,
                PaymentEventType.IMMEDIATE_SETTLE_CREATE, createStatus, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(existedTransaction, submitCreateEvent);
        //commit the payment event
        updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(submitCreateEvent),
                api, PaymentStatus.SETTLE_CREATED, false);
        final PaymentProviderService provider = getProviderByName(existedTransaction.getPaymentProvider());
        CallbackParams properties = paymentRepositoryFacade.getPaymentProperties(paymentId);
        request.setCallbackParams(properties);
        LOGGER.info("start to call provider confirm");
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
                LOGGER.info("call provider confirm successfully");
                if(paymentTransaction == null){
                    return Promise.pure(existedTransaction);
                }
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
        request.setPaymentProvider(provider.getProviderName());
        request.setMerchantAccount(getMerchantRef(pi, request, provider.getProviderName()));
        request.setStatus(PaymentStatus.SETTLEMENT_SUBMIT_CREATED.toString());
        request.setType(PaymentType.CHARGE.toString());
        PaymentEvent event = createPaymentEvent(request, PaymentEventType.SUBMIT_SETTLE_CREATE,
                 PaymentStatus.SETTLEMENT_SUBMIT_CREATED, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(request, event);
        //commit the transaction
        saveAndCommitPayment(request);
        LOGGER.info("start to call provider charge");
        return provider.charge(pi, request).recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Throwable throwable) {
                return handleProviderException(throwable, provider, request, api,
                        PaymentStatus.SETTLEMENT_SUBMIT_DECLINED, PaymentEventType.SUBMIT_SETTLE);
            }
        }).then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction paymentTransaction) {
                LOGGER.info("call provider charge successfully");
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
        }else{
            eventType = PaymentEventType.SUBMIT_SETTLE_REVERSE;
        }
        PaymentStatus createStatus = PaymentStatus.REVERSE_CREATED;
        PaymentEvent reverseCreateEvent = createPaymentEvent(existedTransaction,
                PaymentEventType.REVERSE_CREATE, createStatus, SUCCESS_EVENT_RESPONSE);
        final String currentStatus = existedTransaction.getStatus();
        existedTransaction.setStatus(createStatus.toString());
        addPaymentEvent(existedTransaction, reverseCreateEvent);
        updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(reverseCreateEvent), api, createStatus, false);
        final PaymentProviderService provider = getProviderByName(existedTransaction.getPaymentProvider());
        LOGGER.info("start to call provider reverse");
        return provider.reverse(existedTransaction.getExternalToken(), request)
                .recover(new Promise.Func<Throwable, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(Throwable throwable) {
                //if any exception, do not affect the current status:
                try{
                    handleProviderException(throwable, provider, request, api,PaymentStatus.REVERSE_DECLINED, eventType);
                }catch(Exception ex){
                    updatePayment(existedTransaction, PaymentStatus.valueOf(currentStatus), null);
                    throw ex;
                }
                return Promise.pure(null);
            }
        }).then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction aVoid) {
                LOGGER.info("call provider reverse successfully");
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
    public Promise<PaymentTransaction> getTransaction(Long paymentId) {
        final PaymentTransaction result = getPaymentAndEvents(paymentId);
        return Promise.pure(result);
    }

    @Override
    public Promise<PaymentTransaction> getUpdatedTransaction(Long paymentId) {
        final PaymentTransaction result = getPaymentAndEvents(paymentId);
        if(!isOpenStatus(result.getStatus())){
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
                                boolean needUpdate = false;
                                //update the external token only
                                if(CommonUtil.isNullOrEmpty(result.getExternalToken()) &&
                                        !CommonUtil.isNullOrEmpty(paymentTransaction.getExternalToken())){
                                    needUpdate = true;
                                }
                                PaymentStatus paymentStatus = PaymentStatus.valueOf(paymentTransaction.getStatus());
                                if(!paymentStatus.toString().equalsIgnoreCase(result.getStatus())){
                                    needUpdate = true;
                                }
                                //report a event as status changed.
                                if(needUpdate){
                                    PaymentEvent reportEvent = createPaymentEvent(result
                                            , PaymentEventType.REPORT_EVENT, paymentStatus, SUCCESS_EVENT_RESPONSE);
                                    reportPaymentEvent(reportEvent, paymentTransaction, null);
                                    result.setStatus(paymentStatus.toString());
                                    result.getPaymentEvents().add(reportEvent);
                                }
                                return Promise.pure(result);
                            }
                        }
                    });
        }

    }

    @Override
    public Promise<PaymentTransaction> getProviderTransaction(Long paymentId) {
        PaymentTransaction payment = paymentRepositoryFacade.getByPaymentId(paymentId);
        String externalToken = payment.getExternalToken();
        if(CommonUtil.isNullOrEmpty(externalToken)){
            CallbackParams properties = paymentRepositoryFacade.getPaymentProperties(paymentId);
            if(properties != null){
                payment.setCallbackParams(properties);
            }
        }
        final PaymentProviderService provider = getProviderByName(payment.getPaymentProvider());
        return provider.getByTransactionToken(payment);
    }

    @Override
    public Promise<PaymentTransaction> reportPaymentEvent(PaymentEvent event, PaymentTransaction paymentNew,
                                                          CallbackParams paymentCallbackParams) {
        if(event.getPaymentId() == null){
            LOGGER.error("the payment id is missing for the event.");
            throw AppClientExceptions.INSTANCE.paymentInstrumentNotFound("null paymentId").exception();
        }
        PaymentTransaction payment = getPaymentById(event.getPaymentId());
        PaymentStatus status = PaymentUtil.getPaymentStatus(event.getStatus());
        if(paymentNew != null){
            if(!CommonUtil.isNullOrEmpty(paymentNew.getExternalToken())){
                payment.setExternalToken(paymentNew.getExternalToken());
            }
            if(!CommonUtil.isNullOrEmpty(paymentNew.getStatus())){
                status = PaymentUtil.getPaymentStatus(paymentNew.getStatus());
            }
        }
        payment.setStatus(status.toString());
        LOGGER.info("report event for payment:" + event.getPaymentId());
        updatePaymentAndSaveEvent(payment, Arrays.asList(event), PaymentAPI.ReportEvent,status, false);
        payment.setPaymentEvents(Arrays.asList(event));
        if(paymentCallbackParams != null){
            final PaymentProviderService provider = getProviderByName(payment.getPaymentProvider());
            LOGGER.info("start to call provider confirmNotify");
            return provider.confirmNotify(payment, paymentCallbackParams);
        }
        return Promise.pure(payment);
    }

    @Override
    public Promise<PaymentTransaction> processNotification(PaymentProvider provider, String request) {
        LOGGER.info("receive notification from " + provider.name() + ": " + request);
        PaymentProviderService providerService = providerRoutingService.getProviderByName(provider.name());
        return providerService.processNotify(request)
                .then(new Promise.Func<PaymentTransaction, Promise<PaymentTransaction>>() {
            @Override
            public Promise<PaymentTransaction> apply(PaymentTransaction payment) {
                LOGGER.info("call provider processNotification successfully");
                if(payment == null){
                    return Promise.pure(null);
                }
                if(CommonUtil.isNullOrEmpty(payment.getStatus())){
                    return Promise.pure(null);
                }
                PaymentTransaction existingTrx = paymentRepositoryFacade.getByPaymentId(payment.getId());
                if(existingTrx == null){
                    //Credit Card Auth use PIID as reference so no transaction would be found:
                    LOGGER.warn("cannot find payment transaction:" + payment.getId());
                    return Promise.pure(null);
                }
                PaymentEvent reportEvent = createPaymentEvent(payment
                        , PaymentEventType.NOTIFY, PaymentStatus.valueOf(payment.getStatus()), SUCCESS_EVENT_RESPONSE);
                reportPaymentEvent(reportEvent, payment, null);
                return Promise.pure(null);
            }
        });
    }

    @Override
    public Promise<PaymentTransaction> refund(Long paymentId, final PaymentTransaction request) {
        validateRequest(request, false, true);
        final PaymentAPI api = PaymentAPI.Refund;
        PaymentTransaction trackingResult = getResultByTrackingUuid(request, api);
        if(trackingResult != null){
            return Promise.pure(trackingResult);
        }
        final PaymentTransaction existedTransaction = getPaymentById(paymentId);
        validateTransactionRequest(paymentId, request, existedTransaction);
        if(PaymentStatus.CHARGE_BACK.equals(PaymentStatus.valueOf(existedTransaction.getStatus()))){
            LOGGER.error("the payment status CHARGE_BACK is not allowed to be refunded.");
            throw AppServerExceptions.INSTANCE.invalidPaymentStatus(PaymentStatus.CHARGE_BACK.toString()).exception();
        }

        CloneRequest(request, existedTransaction);
        PaymentStatus createStatus = PaymentStatus.REFUND_CREATED;
        existedTransaction.setStatus(createStatus.toString());
        PaymentEvent submitCreateEvent = createPaymentEvent(request,
                PaymentEventType.REFUND_CREATE, createStatus, SUCCESS_EVENT_RESPONSE);
        addPaymentEvent(existedTransaction, submitCreateEvent);
        //commit the payment event
        updatePaymentAndSaveEvent(existedTransaction, Arrays.asList(submitCreateEvent),
                api, createStatus, false);
        LOGGER.info("start to call provider refund");
        final PaymentProviderService provider = getProviderByName(existedTransaction.getPaymentProvider());
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
                LOGGER.info("call provider refund successfully");
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
        LOGGER.error(api.toString() + " declined by " + provider.getProviderName() +
                "; error detail: ", throwable);
        request.setStatus(status.toString());
        PaymentEvent authDeclined = createPaymentEvent(request, event, status, CommonUtil.toJson(throwable.getMessage(), null));
        addPaymentEvent(request, authDeclined);
        updatePaymentAndSaveEvent(request, Arrays.asList(authDeclined), api, status, false);
        return Promise.throwing(throwable);
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
            throw AppClientExceptions.INSTANCE.paymentInstrumentNotFound(paymentId.toString()).exception();
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

    private void validateReverse(Long paymentId, PaymentTransaction request, PaymentTransaction existedTransaction){
        if(existedTransaction == null){
            LOGGER.error("the payment id is invalid for the event." + paymentId);
            throw AppClientExceptions.INSTANCE.paymentInstrumentNotFound(paymentId.toString()).exception();
        }
        if(!existedTransaction.getBillingRefId().equalsIgnoreCase(request.getBillingRefId())){
            LOGGER.error("the billing ref id is different with the one before for payment: " + paymentId);
            throw AppClientExceptions.INSTANCE.invalidBIllingRef(request.getBillingRefId()).exception();
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
}
