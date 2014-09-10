/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.impl;

import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.PIType;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.clientproxy.UserInfoFacade;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.PaymentInstrumentService;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.provider.ProviderRoutingService;
import com.junbo.payment.core.util.PaymentUtil;
import com.junbo.payment.db.repo.TrackingUuidRepository;
import com.junbo.payment.db.repo.facade.PaymentInstrumentRepositoryFacade;
import com.junbo.payment.db.repository.PITypeRepository;
import com.junbo.payment.spec.enums.PaymentAPI;
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
    private PaymentInstrumentRepositoryFacade paymentInstrumentRepositoryFacade;
    @Autowired
    private TrackingUuidRepository trackingUuidRepository;
    @Autowired
    private PITypeRepository piTypeRepository;
    protected UserInfoFacade userInfoFacade;

    @Override
    public Promise<PaymentInstrument> add(final PaymentInstrument request) {
        validateRequest(request);
        if(request.getTrackingUuid() != null){
            TrackingUuid result = trackingUuidRepository.getByTrackingUuid(request.getUserId(), request.getTrackingUuid()).get();
            if(result != null && result.getApi().equals(PaymentAPI.AddPI)){
                return Promise.pure(CommonUtil.parseJson(result.getResponse(), PaymentInstrument.class));
            }
        }
        final PaymentProviderService provider = providerRoutingService.getPaymentProvider(
                PIType.get(request.getType()));
        if(provider == null){
            throw AppServerExceptions.INSTANCE.providerNotFound(PIType.get(request.getType()).toString()).exception();
        }
        return provider.add(request).then(new Promise.Func<PaymentInstrument, Promise<PaymentInstrument>>() {
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

    private void saveAndCommitPI(final PaymentInstrument request) {
        paymentInstrumentRepositoryFacade.save(request);
        saveTrackingUuid(request, PaymentAPI.AddPI);
    }

    @Override
    public void delete(final Long paymentInstrumentId) {
        paymentInstrumentRepositoryFacade.delete(paymentInstrumentId);
    }

    @Override
    public void update(PaymentInstrument request) {
        validateRequest(request);

        PaymentInstrument piExisting = getPaymentInstrument(request.getId());
        //Validate the info:
        if(!piExisting.getUserId().equals(request.getUserId())){
            throw AppClientExceptions.INSTANCE.invalidPaymentInstrumentId(request.getId().toString()).exception();
        }
        if(!request.getAccountNumber().equalsIgnoreCase(piExisting.getAccountNumber())){
            throw AppClientExceptions.INSTANCE.updateNotAllowed("accountNumber").exception();
        }
        if(!request.getType().equals(piExisting.getType())){
            throw AppClientExceptions.INSTANCE.updateNotAllowed("type").exception();
        }
        if(PIType.get(request.getType()).equals(PIType.CREDITCARD)){
            if(request.getTypeSpecificDetails() == null){
                throw AppClientExceptions.INSTANCE.invalidPaymentInstrumentId(request.getId().toString()).exception();
            }
            request.getTypeSpecificDetails().setId(request.getId());
            if(request.getTypeSpecificDetails().getExpireDate() == null){
                if(piExisting.getTypeSpecificDetails().getExpireDate() != null){
                    throw AppClientExceptions.INSTANCE.updateNotAllowed("expireDate").exception();
                }
            }else if(!request.getTypeSpecificDetails().getExpireDate().equalsIgnoreCase(
                    piExisting.getTypeSpecificDetails().getExpireDate())){
                throw AppClientExceptions.INSTANCE.updateNotAllowed("expireDate").exception();
            }
            if(request.getTypeSpecificDetails().getIssuerIdentificationNumber() == null){
                if(piExisting.getTypeSpecificDetails().getIssuerIdentificationNumber() != null){
                    throw AppClientExceptions.INSTANCE.updateNotAllowed("issuerIdentificationNumber").exception();
                }
            }else if(!request.getTypeSpecificDetails().getIssuerIdentificationNumber().equalsIgnoreCase(
                    piExisting.getTypeSpecificDetails().getIssuerIdentificationNumber())){
                throw AppClientExceptions.INSTANCE.updateNotAllowed("issuerIdentificationNumber").exception();
            }
            if(request.getTypeSpecificDetails().getCreditCardType() == null){
                if(piExisting.getTypeSpecificDetails().getCreditCardType() != null){
                    throw AppClientExceptions.INSTANCE.updateNotAllowed("creditCardType").exception();
                }
            }else if(!request.getTypeSpecificDetails().getCreditCardType().equalsIgnoreCase(
                    piExisting.getTypeSpecificDetails().getCreditCardType())){
                throw AppClientExceptions.INSTANCE.updateNotAllowed("creditCardType").exception();
            }
        }
        //TODO: need re-validate the PI according to the lastValidatedTime
        paymentInstrumentRepositoryFacade.update(request);
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
            LOGGER.error("the provider is not available for pi type:" + result.getType());
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
        List<PaymentInstrument> results = paymentInstrumentRepositoryFacade.getByUserId(userId);
        if(results == null || results.isEmpty()){
            throw AppClientExceptions.INSTANCE.paymentInstrumentNotFound("userId : " + userId.toString()).exception();
        }
        return results;
    }

    @Override
    public Promise<List<PaymentInstrument>> searchPi(Long userId, PaymentInstrumentSearchParam searchParam) {
        if(userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("user_id").exception();
        }
        if(!CommonUtil.isNullOrEmpty(searchParam.getType())) {
            PaymentUtil.getPIType(searchParam.getType());
        }
        final List<PaymentInstrument> results = paymentInstrumentRepositoryFacade.search(userId, searchParam);
        final List<PaymentInstrument> detailedResults = new ArrayList<PaymentInstrument>();
        return Promise.each(results.iterator(), new Promise.Func<PaymentInstrument, Promise>() {
            @Override
            public Promise apply(PaymentInstrument paymentInstrument) {
                return getById(paymentInstrument.getId()).then(new Promise.Func<PaymentInstrument, Promise<Void>>() {
                    @Override
                    public Promise<Void> apply(PaymentInstrument paymentInstrument) {
                        if(!CommonUtil.isNullOrEmpty(paymentInstrument.getExternalToken())){
                            detailedResults.add(paymentInstrument);
                        }
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
            throw AppCommonErrors.INSTANCE.fieldRequired("piType").exception();
        }
        PaymentInstrumentType result = piTypeRepository.getPITypeByName(piType);
        if(result == null){
            throw AppClientExceptions.INSTANCE.invalidPIType(piType).exception();
        }
        return result;
    }

    private void saveTrackingUuid(PaymentInstrument request, PaymentAPI api){
        if(request.getTrackingUuid() == null){
            return;
        }
        if(request.getId() == null){
            LOGGER.error("payment id should not be empty when store tracking uuid.");
            throw AppCommonErrors.INSTANCE.fieldRequired("id").exception();
        }
        TrackingUuid trackingUuid = new TrackingUuid();
        trackingUuid.setTrackingUuid(request.getTrackingUuid());
        trackingUuid.setPaymentInstrumentId(request.getId());
        trackingUuid.setApi(api.toString());
        trackingUuid.setUserId(request.getUserId());
        trackingUuid.setResponse(CommonUtil.toJson(request, null));
        trackingUuidRepository.create(trackingUuid).get();
    }

    private void validateRequest(PaymentInstrument request){
        if(request.getUserId() == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("user_id").exception();
        }
        UserInfo user = userInfoFacade.getUserInfo(request.getUserId()).get();
        if(user == null){
            throw AppClientExceptions.INSTANCE.userNotFound(request.getUserId().toString()).exception();
        }
        if(user.getAnonymous() != null && user.getAnonymous()){
            throw AppClientExceptions.INSTANCE.userNotAllowed(request.getUserId().toString()).exception();
        }
        request.setUserInfo(user);
        if(request.getType() == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("payment_instrument_type").exception();
        }
        PaymentUtil.getPIType(request.getType());
        validateCreditCard(request);
    }

    private void validateCreditCard(PaymentInstrument request){
        if(PaymentUtil.getPIType(request.getType()).equals(PIType.CREDITCARD)){
            if(CommonUtil.isNullOrEmpty(request.getAccountName())){
                throw AppCommonErrors.INSTANCE.fieldRequired("account_name").exception();
            }
            if(CommonUtil.isNullOrEmpty(request.getAccountNumber())){
                throw AppCommonErrors.INSTANCE.fieldRequired("account_number").exception();
            }
            /* expire date is now client-immutable. Expected to be encrypted and pass to providers
            if(request.getTypeSpecificDetails() == null){
                throw AppCommonErrors.INSTANCE.fieldRequired("expire_date").exception();
            }
            String expireDate = request.getTypeSpecificDetails().getExpireDate();
            if(CommonUtil.isNullOrEmpty(expireDate)){
                throw AppCommonErrors.INSTANCE.fieldRequired("expire_date").exception();
            }
            if (!expireDate.matches("\\d{4}-\\d{2}-\\d{2}")
                    && !expireDate.matches("\\d{4}-\\d{2}")) {
                throw AppCommonErrors.INSTANCE.fieldInvalid("expire_date",
                        "only accept format: yyyy-MM or yyyy-MM-dd").exception();
            }*/
        }
    }

    private PaymentInstrument getPaymentInstrument(Long paymentInstrumentId){
        PaymentInstrument result = paymentInstrumentRepositoryFacade.getByPIId(paymentInstrumentId);
        if(result == null){
            throw AppClientExceptions.INSTANCE.paymentInstrumentNotFound(paymentInstrumentId.toString()).exception();
        }
        return result;
    }

    public void setUserInfoFacade(UserInfoFacade userInfoFacade) {
        this.userInfoFacade = userInfoFacade;
    }
}
