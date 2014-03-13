/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.mapper;

import com.junbo.payment.db.repository.MerchantAccountRepository;
import com.junbo.payment.db.repository.PaymentProviderRepository;
import com.junbo.payment.spec.enums.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;


/**
 * common mapper for oom.
 */
public class CommonMapper {
    @Autowired
    private MerchantAccountRepository merchantAccountRepository;
    @Autowired
    private PaymentProviderRepository paymentProviderRepository;

    public CreditCardType toCreditCardTypeEnum(String creditCardType) {
        return CreditCardType.valueOf(creditCardType.replace(" ", "").toUpperCase());
    }

    public String toCreditCardType(CreditCardType creditCardType) {
        return creditCardType.toString();
    }

    public PIStatus toPIStatusEnum(String status){
        return PIStatus.valueOf(status.toUpperCase());
    }

    public String toPIStatus(PIStatus status){
        return status.toString();
    }

    public PIType toPITypeEnum(String piType){
        return PIType.valueOf(piType.toUpperCase());
    }

    public String toPIType(PIType piType){
        return piType.toString();
    }

    public PhoneType toPhoneTypeEnum(String phoneType){
        return PhoneType.valueOf(phoneType.toUpperCase());
    }

    public String toPhoneType(PhoneType phoneType){
        return phoneType.toString();
    }

    public UUID toUUID(UUID uuid){
        return uuid;
    }

    public String toPaymentType(PaymentType type){
        return type.toString();
    }

    public PaymentType toPaymentEnum(String type){
        return PaymentType.valueOf(type.toUpperCase());
    }

    public String toPaymentStatus(PaymentStatus status){
        return status.toString();
    }

    public PaymentStatus toPaymentStatusEnum(String status){
        return PaymentStatus.valueOf(status.toUpperCase());
    }

    public String toPaymentEventType(PaymentEventType type){
        return type.toString();
    }

    public PaymentEventType toPaymentEventTypeEnum(String type){
        return PaymentEventType.valueOf(type.toUpperCase());
    }

    public PaymentAPI toPaymentAPIEunm(String api){
        return PaymentAPI.valueOf(api.toUpperCase());
    }

    public String toPaymentAPI(PaymentAPI api){
        return api.toString();
    }

    public PaymentAPI toPaymentAPIEunm(PaymentAPI api){
        return api;
    }

    public Integer explicitMethod_toMerchantId(String merchant){
        return merchantAccountRepository.getMerchantAccountIdByRef(merchant);
    }

    public Integer explicitMethod_toProviderId(String provider){
        return paymentProviderRepository.getProviderId(provider);
    }

    public String explicitMethod_toMerchantName(Integer merchantId){
        return merchantAccountRepository.getMerchantAccountRef(merchantId);
    }

    public String explicitMethod_toProviderName(Integer providerId){
        return paymentProviderRepository.getProviderName(providerId);
    }
}
