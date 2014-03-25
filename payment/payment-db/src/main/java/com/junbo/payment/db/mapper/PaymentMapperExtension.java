/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.mapper;


import com.junbo.oom.core.MappingContext;
import com.junbo.payment.db.entity.payment.PaymentEntity;
import com.junbo.payment.db.entity.payment.PaymentEventEntity;
import com.junbo.payment.db.entity.paymentinstrument.PaymentInstrumentEntity;
import com.junbo.payment.db.repository.MerchantAccountRepository;
import com.junbo.payment.db.repository.PaymentProviderRepository;
import com.junbo.payment.spec.model.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * extension for oom mapper.
 */
public class PaymentMapperExtension {
    @Autowired
    private PaymentMapper paymentMapperImpl;
    @Autowired
    private MerchantAccountRepository merchantAccountRepository;
    @Autowired
    private PaymentProviderRepository paymentProviderRepository;

    public PaymentInstrumentEntity toPIEntity(PaymentInstrument piRequest){
        if(piRequest == null){
            return null;
        }
        PaymentInstrumentEntity entity = paymentMapperImpl.toPIEntityRaw(piRequest, new MappingContext());
        entity.setUserId(piRequest.getId().getUserId());
        entity.setId(piRequest.getId().getPaymentInstrumentId());
        return entity;
    }

    public PaymentInstrument toPaymentInstrument(PaymentInstrumentEntity piEntity){
        if(piEntity == null){
            return null;
        }
        PaymentInstrument pi = paymentMapperImpl.toPaymentInstrumentRaw(piEntity, new MappingContext());
        pi.setId(new PIId(piEntity.getUserId(), piEntity.getId()));
        return pi;
    }

    public PaymentEventEntity toPaymentEventEntity(PaymentEvent event){
        if(event == null){
            return null;
        }
        PaymentEventEntity entity = paymentMapperImpl.toPaymentEventEntityRaw(event, new MappingContext());
        entity.setNetAmount(event.getChargeInfo() == null ? null : event.getChargeInfo().getAmount());
        entity.setCurrency(event.getChargeInfo() == null ? null : event.getChargeInfo().getCurrency());
        return entity;
    }

    public PaymentEvent toPaymentEvent(PaymentEventEntity eventEntity){
        if(eventEntity == null){
            return null;
        }
        PaymentEvent event = paymentMapperImpl.toPaymentEventRaw(eventEntity, new MappingContext());
        ChargeInfo chargeInfo = new ChargeInfo();
        chargeInfo.setCurrency(eventEntity.getCurrency());
        chargeInfo.setAmount(eventEntity.getNetAmount());
        event.setChargeInfo(chargeInfo);
        return event;
    }


    public PaymentEntity toPaymentEntity(PaymentTransaction request){
        if(request == null){
            return null;
        }
        PaymentEntity payment = paymentMapperImpl.toPaymentEntityRaw(request, new MappingContext());
        payment.setPaymentInstrumentId(request.getPaymentInstrumentId().getPaymentInstrumentId());
        payment.setCurrency(request.getChargeInfo().getCurrency());
        payment.setNetAmount(request.getChargeInfo().getAmount());
        payment.setCountryCode(request.getChargeInfo().getCountry());
        payment.setBusinessDescriptor(request.getChargeInfo().getBusinessDescriptor());
        payment.setMerchantAccountId(merchantAccountRepository.getMerchantAccountIdByRef(request.getMerchantAccount()));
        payment.setPaymentProviderId(paymentProviderRepository.getProviderId(request.getPaymentProvider()));
        return payment;
    }

    public PaymentTransaction toPayment(PaymentEntity paymentEntity){
        if(paymentEntity == null){
            return null;
        }
        PaymentTransaction request = paymentMapperImpl.toPaymentRaw(paymentEntity, new MappingContext());
        request.setPaymentInstrumentId(new PIId(null, paymentEntity.getPaymentInstrumentId()));
        ChargeInfo chargeInfo = new ChargeInfo();
        chargeInfo.setAmount(paymentEntity.getNetAmount());
        chargeInfo.setBusinessDescriptor(paymentEntity.getBusinessDescriptor());
        chargeInfo.setCurrency(paymentEntity.getCurrency());
        chargeInfo.setCountry(paymentEntity.getCountryCode());
        request.setChargeInfo(chargeInfo);
        return request;
    }
}
