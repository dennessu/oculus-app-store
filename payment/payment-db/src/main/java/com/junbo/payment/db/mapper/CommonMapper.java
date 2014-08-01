/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.mapper;

import com.junbo.common.util.EnumRegistry;
import com.junbo.payment.db.repository.MerchantAccountRepository;
import com.junbo.payment.db.repository.PaymentProviderRepository;
import com.junbo.payment.spec.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.UUID;


/**
 * common mapper for oom.
 */
public class CommonMapper {
    @Autowired
    private MerchantAccountRepository merchantAccountRepository;
    @Autowired
    private PaymentProviderRepository paymentProviderRepository;

    public Long fromStringToLong(String source) {
        return source == null ? null : Long.parseLong(source);
    }

    public String fromLongToString(Long source) {
        return source == null ? null : source.toString();
    }

    public UUID toUUID(UUID uuid) {
        return uuid;
    }

    public Short explicitMethod_convertPaymentType(String type) {
        if(!StringUtils.isEmpty(type)) {
            PaymentType paymentType = PaymentType.valueOf(type);
            return paymentType.getId();
        }
        return null;
    }

    public String explicitMethod_convertPaymentType(Short typeId) {
        PaymentType paymentType = EnumRegistry.resolve(typeId, PaymentType.class);
        return paymentType.toString();
    }

    public Short explicitMethod_convertPaymentStatus(String status) {
        if(!StringUtils.isEmpty(status)) {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status);
            return paymentStatus.getId();
        }
        return null;
    }

    public String explicitMethod_convertPaymentStatus(Short statusId) {
        PaymentStatus paymentStatus = EnumRegistry.resolve(statusId, PaymentStatus.class);
        return paymentStatus.toString();
    }

    public Short explicitMethod_convertPaymentEventType(String type) {
        if(!StringUtils.isEmpty(type)) {
            PaymentEventType paymentEventType = PaymentEventType.valueOf(type);
            return paymentEventType.getId();
        }
        return null;
    }

    public String explicitMethod_convertPaymentEventType(Short typeId) {
        PaymentEventType paymentEventType = EnumRegistry.resolve(typeId, PaymentEventType.class);
        return paymentEventType.toString();
    }

    public Short explicitMethod_convertPaymentAPI(String api) {
        if(!StringUtils.isEmpty(api)) {
            PaymentAPI paymentAPI = PaymentAPI.valueOf(api);
            return paymentAPI.getId();
        }
        return null;
    }

    public String explicitMethod_convertPaymentAPI(Short apiId) {
        PaymentAPI paymentAPI = EnumRegistry.resolve(apiId, PaymentAPI.class);
        return paymentAPI.toString();
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
