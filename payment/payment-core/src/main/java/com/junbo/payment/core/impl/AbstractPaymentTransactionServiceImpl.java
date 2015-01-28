/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.impl;

import com.junbo.common.error.AppCommonErrors;
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
import com.junbo.payment.db.repo.TrackingUuidRepository;
import com.junbo.payment.db.repo.facade.PaymentRepositoryFacade;
import com.junbo.payment.db.repository.MerchantAccountRepository;
import com.junbo.payment.db.repository.PaymentProviderRepository;
import com.junbo.payment.spec.enums.PaymentAPI;
import com.junbo.payment.spec.enums.PaymentEventType;
import com.junbo.payment.spec.enums.PaymentStatus;
import com.junbo.payment.spec.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract Payment Transaction Service Implementation.
 */
public abstract class AbstractPaymentTransactionServiceImpl implements PaymentTransactionService {
    private static final PaymentStatus[] OPEN_STATUS = new PaymentStatus[]{
            PaymentStatus.AUTH_CREATED, PaymentStatus.AUTHORIZED,PaymentStatus.UNCONFIRMED,
            PaymentStatus.SETTLEMENT_SUBMIT_CREATED, PaymentStatus.SETTLEMENT_SUBMITTED,
            PaymentStatus.SETTLE_CREATED, PaymentStatus.SETTLED, PaymentStatus.SETTLING,
            PaymentStatus.REVERSE_CREATED, PaymentStatus.REFUND_CREATED, PaymentStatus.CREDIT_CREATED,
            PaymentStatus.RISK_PENDING};
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
        if(request == null || request.getUserId() == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("user_id").exception();
        }
        UserInfo user = userInfoFacade.getUserInfo(request.getUserId()).get();
        if(user == null){
            throw AppClientExceptions.INSTANCE.userNotFound(request.getUserId().toString()).exception();
        }
        request.setUserInfo(user);
        if(request.getTrackingUuid() == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("tracking_uuid").exception();
        }
        if(CommonUtil.isNullOrEmpty(request.getBillingRefId())){
            throw AppCommonErrors.INSTANCE.fieldRequired("billingRefId").exception();
        }
        if(needChargeInfo){
            if(request.getChargeInfo() == null){
                throw AppCommonErrors.INSTANCE.fieldRequired("amount").exception();
            }
            if(request.getChargeInfo().getAmount() == null){
                throw AppCommonErrors.INSTANCE.fieldRequired("amount").exception();
            }
            if(CommonUtil.isNullOrEmpty(request.getChargeInfo().getCurrency())){
                throw AppCommonErrors.INSTANCE.fieldRequired("currency").exception();
            }
        }
        if(!supportChargeInfo && request.getChargeInfo() != null){
            throw AppCommonErrors.INSTANCE.fieldNotWritable("chargeInfo").exception();
        }
    }

    //use new transaction for business data to avoid hibernate cache in the service level transaction
    protected PaymentTransaction getResultByTrackingUuid(final PaymentTransaction request, final PaymentAPI api) {
        if(request.getTrackingUuid() == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("tracking_uuid").exception();
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
            throw AppCommonErrors.INSTANCE.fieldRequired("id").exception();
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
            throw AppCommonErrors.INSTANCE.fieldRequired("payment_instrument_id").exception();
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
            LOGGER.error("error get payment instrument:", ex);
            throw AppServerExceptions.INSTANCE.invalidPI().exception();
        }
        if(pi == null){
            LOGGER.error("PI does not exist:" + request.getPaymentInstrumentId());
            throw AppClientExceptions.INSTANCE.invalidPaymentInstrumentId(
                    request.getPaymentInstrumentId().toString()).exception();
        }
        if(!pi.getIsActive()){
            LOGGER.error("PI is not active:" + request.getPaymentInstrumentId());
            throw AppServerExceptions.INSTANCE.invalidPI().exception();
        }
        return pi;
    }

    protected String getMerchantRef(PaymentInstrument pi, PaymentTransaction request, String providerName){
        if(PIType.get(pi.getType()).equals(PIType.CREDITCARD)){
            String merchantRef = merchantAccountRepository.getMerchantAccountRef(
                    paymentProviderRepository.getProviderId(providerName), request.getChargeInfo().getCurrency());
            if(CommonUtil.isNullOrEmpty(merchantRef)){
                LOGGER.error("merchant reference is not available for provider:" + providerName);
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
                    throw AppClientExceptions.INSTANCE.paymentInstrumentNotFound(paymentId.toString()).exception();
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
                    LOGGER.error("the payment is not available:" + paymentId);
                    throw AppClientExceptions.INSTANCE.paymentInstrumentNotFound(paymentId.toString()).exception();
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
        final String externalToken = CommonUtil.isNullOrEmpty(payment.getExternalToken()) ? null : payment.getExternalToken();
        return template.execute(new TransactionCallback<List<PaymentEvent>>() {
            public List<PaymentEvent> doInTransaction(TransactionStatus txnStatus) {
                if(status != null){
                    paymentRepositoryFacade.updatePayment(payment.getId(), status, externalToken);
                }
                paymentRepositoryFacade.savePaymentEvent(payment.getId(), events);
                if(saveUuid){
                    saveTrackingUuid(payment, api);
                }
                return events;
            }
        });
    }

    protected PaymentTransaction updatePayment(final PaymentTransaction payment,final PaymentStatus status, final String token){
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return template.execute(new TransactionCallback<PaymentTransaction>() {
            public PaymentTransaction doInTransaction(TransactionStatus txnStatus) {
                if(status != null){
                    paymentRepositoryFacade.updatePayment(payment.getId(), status, token);
                }
                return payment;
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
        if(CommonUtil.isNullOrEmpty(pi.getPaymentProvider())){
            LOGGER.error("provider is not available for pi:" + pi.getId());
            throw AppServerExceptions.INSTANCE.providerNotFound(pi.getId().toString()).exception();
        }
        return getProviderByName(pi.getPaymentProvider());
    }

    protected PaymentProviderService getProviderByName(String provider){
        PaymentProviderService service = providerRoutingService.getProviderByName(provider);
        if(service == null){
            LOGGER.error("provider service is not available for provider:" + provider);
            throw AppServerExceptions.INSTANCE.providerNotFound(provider).exception();
        }
        return service;
    }

    public boolean isOpenStatus(String paymentStatus){
        return Arrays.asList(OPEN_STATUS).contains(PaymentStatus.valueOf(paymentStatus));
    }

    public void setUserInfoFacade(UserInfoFacade userInfoFacade) {
        this.userInfoFacade = userInfoFacade;
    }
}
