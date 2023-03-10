/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.provider.impl;

import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.common.exception.AppServerExceptions;
import com.junbo.payment.core.provider.PaymentProvider;
import com.junbo.payment.core.provider.PaymentProviderService;
import com.junbo.payment.core.provider.ProviderRoutingService;
import com.junbo.payment.db.repository.PaymentProviderRepository;
import com.junbo.payment.spec.internal.ProviderCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * payment provider routing implementation.
 */
public class ProviderRoutingServiceImpl implements ProviderRoutingService{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderRoutingServiceImpl.class);

    private PaymentProviderRepository paymentProviderRepository;

    @Override
    public PaymentProviderService getPaymentProvider(ProviderCriteria criteria) {
        String provider = paymentProviderRepository.getProviderName(criteria);
        return getProviderByName(provider);
    }

    @Override
    public PaymentProviderService getProviderByName(String provider) {
        if(CommonUtil.isNullOrEmpty(provider)){
            LOGGER.error("provider is empty");
            throw AppServerExceptions.INSTANCE.providerNotFound(provider).exception();
        }
        PaymentProvider paymentProvider = null;
        try{
            paymentProvider = PaymentProvider.valueOf(provider);
        }catch(Exception ex){
            LOGGER.error("provider routing failed:", ex);
            throw AppServerExceptions.INSTANCE.providerNotFound(provider).exception();
        }
        PaymentProviderService result = PaymentProviderRegistry.getPaymentProviderService(paymentProvider);
        if(result == null){
            LOGGER.error("provider service is not available for:" + provider);
            throw AppServerExceptions.INSTANCE.providerNotFound(provider).exception();
        }
        return result;
    }

    @Override
    public void updatePaymentConfiguration() {

    }

    public void setPaymentProviderRepository(PaymentProviderRepository paymentProviderRepository) {
        this.paymentProviderRepository = paymentProviderRepository;
    }
}
