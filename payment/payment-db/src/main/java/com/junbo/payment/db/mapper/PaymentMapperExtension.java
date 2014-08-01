/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.mapper;


import com.junbo.common.id.PIType;
import com.junbo.oom.core.MappingContext;
import com.junbo.payment.db.entity.payment.PaymentTransactionEntity;
import com.junbo.payment.db.entity.payment.PaymentEventEntity;
import com.junbo.payment.db.entity.paymentinstrument.PaymentInstrumentEntity;
import com.junbo.payment.spec.model.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * extension for oom mapper.
 */
public class PaymentMapperExtension {
    @Autowired
    private PaymentMapper paymentMapperImpl;


    public PaymentInstrumentEntity toPIEntity(PaymentInstrument piRequest){
        if(piRequest == null){
            return null;
        }
        PaymentInstrumentEntity entity = paymentMapperImpl.toPIEntityRaw(piRequest, new MappingContext());
        //entity.setUserId(piRequest.getId().getUserId());
        //entity.setId(piRequest.getId().getPaymentInstrumentId());
        return entity;
    }

    public PaymentInstrument toPaymentInstrument(PaymentInstrumentEntity piEntity){
        if(piEntity == null){
            return null;
        }
        PaymentInstrument pi = paymentMapperImpl.toPaymentInstrumentRaw(piEntity, new MappingContext());
        //pi.setId(new PIId(piEntity.getUserId(), piEntity.getId()));
        pi.setIsValidated(pi.getLastValidatedTime() != null);
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


    public PaymentTransactionEntity toPaymentTransactionEntity(PaymentTransaction request){
        if(request == null){
            return null;
        }
        PaymentTransactionEntity payment = paymentMapperImpl.toPaymentEntityRaw(request, new MappingContext());
        // payment.setPaymentInstrumentId(request.getPaymentInstrumentId());
        payment.setCurrency(request.getChargeInfo().getCurrency());
        payment.setNetAmount(request.getChargeInfo().getAmount());
        payment.setCountryCode(request.getChargeInfo().getCountry());
        payment.setBusinessDescriptor(request.getChargeInfo().getBusinessDescriptor());
        // payment.setMerchantAccountId(merchantAccountRepository.getMerchantAccountIdByRef(request.getMerchantAccount()));
        // payment.setPaymentProviderId(paymentProviderRepository.getProviderId(request.getPaymentProvider()));
        return payment;
    }

    public PaymentTransaction toPaymentTransaction(PaymentTransactionEntity paymentTransactionEntity){
        if(paymentTransactionEntity == null){
            return null;
        }
        PaymentTransaction request = paymentMapperImpl.toPaymentRaw(paymentTransactionEntity, new MappingContext());
        ChargeInfo chargeInfo = new ChargeInfo();
        chargeInfo.setAmount(paymentTransactionEntity.getNetAmount());
        chargeInfo.setBusinessDescriptor(paymentTransactionEntity.getBusinessDescriptor());
        chargeInfo.setCurrency(paymentTransactionEntity.getCurrency());
        chargeInfo.setCountry(paymentTransactionEntity.getCountryCode());
        request.setChargeInfo(chargeInfo);
        return request;
    }

    public TypeSpecificDetails toTypeSpecificDetails(Object specificDetail){
        TypeSpecificDetails result = new TypeSpecificDetails();
        if(specificDetail instanceof CreditCardDetail){
            CreditCardDetail generalDetail = (CreditCardDetail)specificDetail;
            result.setId(generalDetail.getId());
            result.setExpireDate(generalDetail.getExpireDate());
            result.setIssuerIdentificationNumber(generalDetail.getIssuerIdentificationNumber());
            result.setCreditCardType(generalDetail.getType());
            result.setEncryptedCvmCode(generalDetail.getEncryptedCvmCode());
            result.setLastBillingDate(generalDetail.getLastBillingDate());
            result.setCommercial(generalDetail.getIsCommercial());
            result.setDebit(generalDetail.getIsDebit());
            result.setIssueCountry(generalDetail.getIssueCountry());
            result.setPrepaid(generalDetail.getIsPrepaid());
        }else if(specificDetail instanceof WalletDetail){
            WalletDetail generalDetail = (WalletDetail)specificDetail;
            result.setStoredValueCurrency(generalDetail.getCurrency());
            result.setWalletType(generalDetail.getType());
        }else{
            return null;
        }
        return result;
    }

    public <T> T toSpecificDetail(final TypeSpecificDetails generalDetail, PIType type){
        switch (type){
            case CREDITCARD:
                return (T) new CreditCardDetail(){
                    {
                        setId(generalDetail.getId());
                        setExpireDate(generalDetail.getExpireDate());
                        setIssuerIdentificationNumber(generalDetail.getIssuerIdentificationNumber());
                        setType(generalDetail.getCreditCardType());
                        setEncryptedCvmCode(generalDetail.getEncryptedCvmCode());
                        setLastBillingDate(generalDetail.getLastBillingDate());
                        setIsCommercial(generalDetail.getCommercial());
                        setIsDebit(generalDetail.getDebit());
                        setIssueCountry(generalDetail.getIssueCountry());
                        setIsPrepaid(generalDetail.getPrepaid());
                    }
                };
            case STOREDVALUE:
                return (T) new WalletDetail(){
                    {
                        setCurrency(generalDetail.getStoredValueCurrency());
                        setType(generalDetail.getWalletType());
                    }
                };
            default:
                return null;
        }
    }
}
