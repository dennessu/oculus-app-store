/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.impl;

import com.junbo.langur.core.client.ClientResponseException;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.transaction.AsyncTransactionTemplate;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.core.exception.AppClientExceptions;
import com.junbo.payment.core.exception.AppServerExceptions;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.provider.ProviderRoutingService;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.db.repository.PITypeRepository;
import com.junbo.payment.spec.enums.PIStatus;
import com.junbo.payment.spec.enums.PIType;
import com.junbo.payment.db.mapper.PaymentAPI;
import com.junbo.payment.db.mapper.TrackingUuid;
import com.junbo.payment.db.repository.PaymentInstrumentRepository;
import com.junbo.payment.db.repository.TrackingUuidRepository;
import com.junbo.payment.spec.model.PageMetaData;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam;
import com.junbo.payment.spec.model.PaymentInstrumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * payment instrument service.
 */
public class PaymentInstrumentServiceImpl implements PaymentInstrumentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentInstrumentServiceImpl.class);
    private ProviderRoutingService providerRoutingService;
    @Autowired
    public void setProviderRoutingService(ProviderRoutingService providerRoutingService) {
        this.providerRoutingService = providerRoutingService;
    }
    @Autowired
    private PaymentInstrumentRepository paymentInstrumentRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private TrackingUuidRepository trackingUuidRepository;
    @Autowired
    private PITypeRepository piTypeRepository;

    @Override
    public Promise<PaymentInstrument> add(final PaymentInstrument request) {
        validateRequest(request);
        if(request.getTrackingUuid() == null){
            throw AppClientExceptions.INSTANCE.missingTrackingUuid().exception();
        }
        TrackingUuid result = trackingUuidRepository.getByTrackUuid(request.getUserId(), request.getTrackingUuid());
        if(result != null && result.getApi().equals(PaymentAPI.AddPI)){
            return Promise.pure(CommonUtil.parseJson(result.getResponse(), PaymentInstrument.class));
        }
        final PaymentProviderService provider = providerRoutingService.getPaymentProvider(
                PaymentUtil.getPIType(request.getType()));
        //call provider and set result
        return provider.add(request).recover(new Promise.Func<Throwable, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(Throwable throwable) {
                return logException(PaymentAPI.AddPI, provider.getProviderName(), throwable);
            }
        }).then(new Promise.Func<PaymentInstrument, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(PaymentInstrument paymentInstrument) {
                request.setAccountNum(paymentInstrument.getAccountNum());
                request.getCreditCardRequest().setExternalToken(
                        paymentInstrument.getCreditCardRequest().getExternalToken());
                request.getCreditCardRequest().setType(paymentInstrument.getCreditCardRequest().getType());
                request.getCreditCardRequest().setCommercial(paymentInstrument.getCreditCardRequest().getCommercial());
                request.getCreditCardRequest().setDebit(paymentInstrument.getCreditCardRequest().getDebit());
                request.getCreditCardRequest().setPrepaid(paymentInstrument.getCreditCardRequest().getPrepaid());
                request.getCreditCardRequest().setIssueCountry(
                        paymentInstrument.getCreditCardRequest().getIssueCountry());
                request.setStatus(PIStatus.ACTIVE.toString());
                if(request.getIsValidated()){
                    request.setLastValidatedTime(new Date());
                }
                saveAndCommitPI(request);
                return Promise.pure(request);
            }
        });
    }

    private Promise<PaymentInstrument> logException(PaymentAPI api, String provider, Throwable throwable){
        String response = null;
        try {
            response = ((ClientResponseException) throwable).getResponse().getResponseBody();
        } catch (IOException e) {

        }
        LOGGER.error(api.toString() + " with error for provider: " + provider+
                ".detail: " + response);
        throw AppServerExceptions.INSTANCE.providerProcessError(provider, response).exception();
    }

    private void saveAndCommitPI(final PaymentInstrument request) {
        AsyncTransactionTemplate template = new AsyncTransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        template.execute(new TransactionCallback<Void>() {
            public Void doInTransaction(TransactionStatus txnStatus) {
                paymentInstrumentRepository.save(request);
                if(CommonUtil.toBool(request.getIsDefault())){
                    paymentInstrumentRepository.setDefault(request.getId());
                }
                saveTrackingUuid(request, PaymentAPI.AddPI);
                return null;
            }
        });
    }

    @Override
    public void delete(Long userId, final Long paymentInstrumentId) {
        getById(userId, paymentInstrumentId);
        paymentInstrumentRepository.delete(paymentInstrumentId);
    }

    @Override
    public void update(PaymentInstrument request) {
        validateRequest(request);
        //no need to support tracking Uuid for put as it should be the same result if call twice
        PaymentInstrument piTarget = getById(request.getUserId(), request.getId());
        //Validate the info:
        if(!piTarget.getUserId().equals(request.getUserId())
                || !piTarget.getType().equals(request.getType())
                || !piTarget.getAccountNum().equals(request.getAccountNum())){
            throw AppClientExceptions.INSTANCE.invalidPaymentInstrumentId(request.getId().toString()).exception();
        }
        if(request.getAddress().getId() == null){
            request.getAddress().setId(piTarget.getAddress().getId());
        }
        if(request.getPhone().getId() == null){
            request.getPhone().setId(piTarget.getPhone().getId());
        }
        if(request.getType().equals(PIType.CREDITCARD.toString())){
            request.getCreditCardRequest().setId(request.getId());
        }
        paymentInstrumentRepository.update(request);
        if(CommonUtil.toBool(request.getIsDefault()) && !CommonUtil.toBool(piTarget.getIsDefault())){
            paymentInstrumentRepository.setDefault(request.getId());
        }
    }

    @Override
    public PaymentInstrument getById(Long userId, Long paymentInstrumentId) {
        PaymentInstrument result = paymentInstrumentRepository.getByPIId(paymentInstrumentId);
        if(result == null){
            throw AppClientExceptions.INSTANCE.resourceNotFound("payment_instrument").exception();
        }
        if(userId != null && !userId.equals(result.getUserId())){
            throw AppClientExceptions.INSTANCE.resourceNotFound("payment_instrument").exception();
        }
        return result;
    }

    @Override
    public List<PaymentInstrument> getByUserId(Long userId) {
        List<PaymentInstrument> results = paymentInstrumentRepository.getByUserId(userId);
        if(results == null || results.isEmpty()){
            throw AppClientExceptions.INSTANCE.resourceNotFound("payment_instrument").exception();
        }
        return results;
    }

    @Override
    public List<PaymentInstrument> searchPi(Long userId, PaymentInstrumentSearchParam searchParam, PageMetaData page) {
        if(userId == null){
            throw AppClientExceptions.INSTANCE.missingUserId().exception();
        }
        List<PaymentInstrument> results = paymentInstrumentRepository.search(userId, searchParam, page);
        if(results == null || results.isEmpty()){
            throw AppClientExceptions.INSTANCE.resourceNotFound("payment_instrument").exception();
        }
        return results;
    }

    @Override
    public PaymentInstrumentType getPIType(String piType) {
        if(CommonUtil.isNullOrEmpty(piType)){
            throw AppClientExceptions.INSTANCE.invalidPIType(piType).exception();
        }
        PaymentInstrumentType result = piTypeRepository.getPITypeByName(piType);
        if(result == null){
            throw AppClientExceptions.INSTANCE.resourceNotFound(piType).exception();
        }
        return result;
    }

    private void saveTrackingUuid(PaymentInstrument request, PaymentAPI api){
        if(request.getId() == null){
            LOGGER.error("payment id should not be empty when store tracking uuid.");
            throw AppServerExceptions.INSTANCE.missingRequiredField("payment_instrument_id").exception();
        }
        TrackingUuid trackingUuid = new TrackingUuid();
        trackingUuid.setTrackingUuid(request.getTrackingUuid());
        trackingUuid.setPaymentInstrumentId(request.getId());
        trackingUuid.setApi(api);
        trackingUuid.setUserId(request.getUserId());
        trackingUuid.setResponse(CommonUtil.toJson(request, null));
        trackingUuidRepository.saveTrackingUuid(trackingUuid);
    }

    private void validateRequest(PaymentInstrument request){
        if(request.getUserId() == null){
            throw AppClientExceptions.INSTANCE.missingUserId().exception();
        }
        if(CommonUtil.isNullOrEmpty(request.getAccountNum())){
            throw AppClientExceptions.INSTANCE.missingAccountName().exception();
        }
        if(CommonUtil.isNullOrEmpty(request.getType())){
            throw AppClientExceptions.INSTANCE.missingPIType().exception();
        }
        PaymentUtil.getPIType(request.getType());
        if(CommonUtil.toBool(request.getIsDefault()) &&
                !request.getType().toString().equalsIgnoreCase(PIType.CREDITCARD.toString())){
            throw AppClientExceptions.INSTANCE.invalidTypeForDefault(request.getType().toString()).exception();
        }
        validateAddress(request);
        validateCreditCard(request);
    }

    private void validateAddress(PaymentInstrument request) {
        if(request.getAddress() != null){
            if(CommonUtil.isNullOrEmpty(request.getAddress().getCountry())){
                throw AppClientExceptions.INSTANCE.missingCountry().exception();
            }
            if(CommonUtil.isNullOrEmpty(request.getAddress().getAddressLine1())){
                throw AppClientExceptions.INSTANCE.missingAddressLine().exception();
            }
            if(CommonUtil.isNullOrEmpty(request.getAddress().getPostalCode())){
                throw AppClientExceptions.INSTANCE.missingPostalCode().exception();
            }
        }
    }

    private void validateCreditCard(PaymentInstrument request){
        if(request.getType().equalsIgnoreCase(PIType.CREDITCARD.toString())){
            if(request.getCreditCardRequest() == null){
                throw AppClientExceptions.INSTANCE.missingExpireDate().exception();
            }
            String expireDate = request.getCreditCardRequest().getExpireDate();
            if (!expireDate.matches("\\d{4}-\\d{2}-\\d{2}")
                    && !expireDate.matches("\\d{4}-\\d{2}")) {
                throw AppClientExceptions.INSTANCE.invalidExpireDateFormat(expireDate).exception();
            }
        }
    }
}
