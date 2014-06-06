/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.impl;

import com.junbo.common.id.PIType;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.UserInfoFacade;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.provider.ProviderRoutingService;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.core.util.ProxyExceptionResponse;
import com.junbo.payment.db.repository.PITypeRepository;
import com.junbo.payment.db.mapper.PaymentAPI;
import com.junbo.payment.db.mapper.TrackingUuid;
import com.junbo.payment.db.repository.PaymentInstrumentRepository;
import com.junbo.payment.db.repository.TrackingUuidRepository;
import com.junbo.payment.spec.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
    private TrackingUuidRepository trackingUuidRepository;
    @Autowired
    private PITypeRepository piTypeRepository;
    protected UserInfoFacade userInfoFacade;

    @Override
    public Promise<PaymentInstrument> add(final PaymentInstrument request) {
        validateRequest(request);
        if(request.getTrackingUuid() != null){
            TrackingUuid result = trackingUuidRepository.getByTrackUuid(request.getUserId(),
                    request.getTrackingUuid());
            if(result != null && result.getApi().equals(PaymentAPI.AddPI)){
                return Promise.pure(CommonUtil.parseJson(result.getResponse(), PaymentInstrument.class));
            }
        }
        final PaymentProviderService provider = providerRoutingService.getPaymentProvider(
                PIType.get(request.getType()));
        if(provider == null){
            throw AppServerExceptions.INSTANCE.providerNotFound(PIType.get(request.getType()).toString()).exception();
        }
        return provider.add(request).recover(new Promise.Func<Throwable, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(Throwable throwable) {
                return logException(PaymentAPI.AddPI, provider.getProviderName(), throwable);
            }
        }).then(new Promise.Func<PaymentInstrument, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(PaymentInstrument paymentInstrument) {
                provider.clonePIResult(paymentInstrument, request);
                request.setIsActive(true);
                if(request.getLastValidatedTime() != null){
                    request.setLastValidatedTime(new Date());
                    request.setIsValidated(true);
                }
                saveAndCommitPI(request);
                return Promise.pure(request);
            }
        });
    }

    private Promise<PaymentInstrument> logException(PaymentAPI api, String provider, Throwable throwable){
        ProxyExceptionResponse proxyResponse = new ProxyExceptionResponse(throwable);
        LOGGER.error(api.toString() + " with error for provider: " + provider +
                ".detail: " + proxyResponse.getBody());
        throw AppServerExceptions.INSTANCE.providerProcessError(provider, proxyResponse.getBody()).exception();
    }

    private void saveAndCommitPI(final PaymentInstrument request) {
        paymentInstrumentRepository.save(request);
        saveTrackingUuid(request, PaymentAPI.AddPI);
    }

    @Override
    public void delete(final Long paymentInstrumentId) {
        getById(paymentInstrumentId);
        paymentInstrumentRepository.delete(paymentInstrumentId);
    }

    @Override
    public void update(PaymentInstrument request) {
        validateRequest(request);
        if(request.getRev() == null){
            throw AppClientExceptions.INSTANCE.missingRevision().exception();
        }
        PaymentInstrument piTarget = getPaymentInstrument(request.getId());
        if(!request.getRev().equals(piTarget.getRev())){
            throw AppClientExceptions.INSTANCE.invalidRevision().exception();
        }
        //Validate the info:
        if(!piTarget.getUserId().equals(request.getUserId())
                || !piTarget.getType().equals(request.getType())
                || !piTarget.getAccountNum().equals(request.getAccountNum())){
            throw AppClientExceptions.INSTANCE.invalidPaymentInstrumentId(request.getId().toString()).exception();
        }
        if(PIType.get(request.getType()).equals(PIType.CREDITCARD)){
            request.getTypeSpecificDetails().setId(request.getId());
        }
        //TODO: need re-validate the PI according to the lastValidatedTime
        paymentInstrumentRepository.update(request);
    }

    @Override
    public Promise<PaymentInstrument> getById(Long paymentInstrumentId) {
        final PaymentInstrument result = getPaymentInstrument(paymentInstrumentId);
        //if(userId != null && !userId.equals(result.getUserId())){
        //    throw AppClientExceptions.INSTANCE.resourceNotFound("payment_instrument").exception();
        //}
        final PaymentProviderService provider = providerRoutingService.getPaymentProvider(
                PIType.get(result.getType()));
        if(provider == null){
            throw AppServerExceptions.INSTANCE.providerNotFound(PIType.get(result.getType()).toString()).exception();
        }
        return provider.getByInstrumentToken(result.getExternalToken())
                .then(new Promise.Func<PaymentInstrument, Promise<PaymentInstrument>>() {
            @Override
            public Promise<PaymentInstrument> apply(PaymentInstrument paymentInstrument) {
                if(paymentInstrument != null){
                    provider.clonePIResult(paymentInstrument, result);
                }
                return Promise.pure(result);
            }
        });
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
    public Promise<List<PaymentInstrument>> searchPi(Long userId, PaymentInstrumentSearchParam searchParam, PageMetaData page) {
        if(userId == null){
            throw AppClientExceptions.INSTANCE.missingUserId().exception();
        }
        if(!CommonUtil.isNullOrEmpty(searchParam.getType())){
            PaymentUtil.getPIType(searchParam.getType());
        }
        final List<PaymentInstrument> results = paymentInstrumentRepository.search(userId, searchParam, page);
        final List<PaymentInstrument> detailedResults = new ArrayList<PaymentInstrument>();
        return Promise.each(results.iterator(), new Promise.Func<PaymentInstrument, Promise>() {
            @Override
            public Promise apply(PaymentInstrument paymentInstrument) {
                return getById(paymentInstrument.getId()).then(new Promise.Func<PaymentInstrument, Promise<Void>>() {
                    @Override
                    public Promise<Void> apply(PaymentInstrument paymentInstrument) {
                        detailedResults.add(paymentInstrument);
                        return Promise.pure(null);
                    }
                });
            }
        }).then(new Promise.Func<Void, Promise<List<PaymentInstrument>>>() {
            @Override
            public Promise<List<PaymentInstrument>> apply(Void aVoid) {
                return Promise.pure(detailedResults);
            }
        });
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
        if(request.getTrackingUuid() == null){
            return;
        }
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
        UserInfo user = userInfoFacade.getUserInfo(request.getUserId()).get();
        if(user == null){
            throw AppClientExceptions.INSTANCE.invalidUserId(request.getUserId().toString()).exception();
        }
        request.setUserInfo(user);
        if(request.getType() == null){
            throw AppClientExceptions.INSTANCE.missingPIType().exception();
        }
        PaymentUtil.getPIType(request.getType());
        validateCreditCard(request);
    }

    private void validateCreditCard(PaymentInstrument request){
        if(PaymentUtil.getPIType(request.getType()).equals(PIType.CREDITCARD)){
            if(CommonUtil.isNullOrEmpty(request.getAccountName())){
                throw AppClientExceptions.INSTANCE.missingAccountName().exception();
            }
            if(CommonUtil.isNullOrEmpty(request.getAccountNum())){
                throw AppClientExceptions.INSTANCE.missingAccountNum().exception();
            }
            if(request.getTypeSpecificDetails() == null){
                throw AppClientExceptions.INSTANCE.missingExpireDate().exception();
            }
            String expireDate = request.getTypeSpecificDetails().getExpireDate();
            if(CommonUtil.isNullOrEmpty(expireDate)){
                throw AppClientExceptions.INSTANCE.missingExpireDate().exception();
            }
            if (!expireDate.matches("\\d{4}-\\d{2}-\\d{2}")
                    && !expireDate.matches("\\d{4}-\\d{2}")) {
                throw AppClientExceptions.INSTANCE.invalidExpireDateFormat(expireDate).exception();
            }
        }
    }

    private PaymentInstrument getPaymentInstrument(Long paymentInstrumentId){
        PaymentInstrument result = paymentInstrumentRepository.getByPIId(paymentInstrumentId);
        if(result == null){
            throw AppClientExceptions.INSTANCE.resourceNotFound("payment_instrument").exception();
        }
        return result;
    }

    public void setUserInfoFacade(UserInfoFacade userInfoFacade) {
        this.userInfoFacade = userInfoFacade;
    }
}
