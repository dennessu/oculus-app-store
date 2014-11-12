/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.facade.impl;

import com.junbo.common.id.PIType;
import com.junbo.payment.common.CommonUtil;
import com.junbo.payment.db.mapper.PaymentMapperExtension;
import com.junbo.payment.db.repo.CreditCardDetailRepository;
import com.junbo.payment.db.repo.FacebookPaymentAccountRepository;
import com.junbo.payment.db.repo.PaymentInstrumentRepository;
import com.junbo.payment.db.repo.facade.PaymentInstrumentRepositoryFacade;
import com.junbo.payment.spec.internal.FacebookPaymentAccountMapping;
import com.junbo.payment.spec.model.CreditCardDetail;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by minhao on 6/18/14.
 */
public class PaymentInstrumentRepositoryFacadeImpl implements PaymentInstrumentRepositoryFacade {
    private PaymentInstrumentRepository paymentInstrumentRepository;
    private CreditCardDetailRepository creditCardDetailRepository;
    private PaymentMapperExtension paymentMapperExtension;
    private FacebookPaymentAccountRepository fbPaymentAccountRepository;

    @Required
    public void setPaymentInstrumentRepository(PaymentInstrumentRepository paymentInstrumentRepository) {
        this.paymentInstrumentRepository = paymentInstrumentRepository;
    }

    @Required
    public void setCreditCardDetailRepository(CreditCardDetailRepository creditCardDetailRepository) {
        this.creditCardDetailRepository = creditCardDetailRepository;
    }

    @Required
    public void setPaymentMapperExtension(PaymentMapperExtension paymentMapperExtension) {
        this.paymentMapperExtension = paymentMapperExtension;
    }

    @Required
    public void setFbPaymentAccountRepository(FacebookPaymentAccountRepository fbPaymentAccountRepository) {
        this.fbPaymentAccountRepository = fbPaymentAccountRepository;
    }

    public void save(PaymentInstrument paymentInstrument){
        PaymentInstrument saved = paymentInstrumentRepository.create(paymentInstrument).get();
        if (PIType.CREDITCARD.getId().equals(paymentInstrument.getType())) {
            CreditCardDetail creditCardDetail = paymentMapperExtension.toSpecificDetail(paymentInstrument.getTypeSpecificDetails(), PIType.CREDITCARD);
            creditCardDetail.setId(saved.getId());
            creditCardDetail.setLastBillingDate(new Date());
            creditCardDetailRepository.create(creditCardDetail).get();
        }
        paymentInstrument.setId(saved.getId());
    }

    public void delete(Long paymentInstrumentId){
        paymentInstrumentRepository.delete(paymentInstrumentId).get();
    }

    public void update(PaymentInstrument request){
        PaymentInstrument pi = paymentInstrumentRepository.get(request.getId()).get();
        //setup column allowed to be updated:
        pi.setLabel(request.getLabel());
        //pi.setAccountNumber(request.getAccountNumber());
        pi.setUserId(request.getUserId());
        pi.setBillingAddressId(request.getBillingAddressId());
        pi.setEmail(request.getEmail());
        pi.setAccountName(request.getAccountName());
        pi.setPhoneNumber(request.getPhoneNumber());
        pi.setRelationToHolder(request.getRelationToHolder());
        if (request.getIsActive() != null) {
            pi.setIsActive(request.getIsActive());
        }
        paymentInstrumentRepository.update(pi, pi).get();

        if (PIType.CREDITCARD.getId().equals(request.getType())) {
            CreditCardDetail creditCardDetail = paymentMapperExtension.toSpecificDetail(request.getTypeSpecificDetails(), PIType.CREDITCARD);
            CreditCardDetail existing = creditCardDetailRepository.get(pi.getId()).get();
            // setup column allowed to be updated:
            //expire date is immutable and update it actually need to post a new PI.
            // existing.setExpireDate(creditCardDetail.getExpireDate());
            creditCardDetailRepository.update(existing, existing).get();
        }
    }

    public void updateExternalInfo(Long paymentInstrumentId, String externalToken, String label, String num){
        PaymentInstrument pi = paymentInstrumentRepository.get(paymentInstrumentId).get();
        if (!CommonUtil.isNullOrEmpty(externalToken)) {
            pi.setExternalToken(externalToken);
        }
        if (!CommonUtil.isNullOrEmpty(label)) {
            pi.setLabel(pi.getLabel() == null ? label : pi.getLabel() + label);
        }
        if (!CommonUtil.isNullOrEmpty(num)) {
            pi.setAccountNumber(num);
        }
        paymentInstrumentRepository.update(pi, pi).get();
    }

    public PaymentInstrument getByPIId(Long piId){
        PaymentInstrument pi = paymentInstrumentRepository.get(piId).get();
        if (pi == null || (pi.getIsDeleted() != null && pi.getIsDeleted()) ) {
            return null;
        }

        setAdditionalInfo(pi);
        return pi;
    }

    public List<PaymentInstrument> getByUserId(Long userId){
        List<PaymentInstrument> result = new ArrayList<PaymentInstrument>();
        List<PaymentInstrument> piList = paymentInstrumentRepository.getByUserId(userId).get();
        for (PaymentInstrument piItem : piList) {
            setAdditionalInfo(piItem);
            result.add(piItem);
        }
        return result;
    }

    public List<PaymentInstrument> search(Long userId, PaymentInstrumentSearchParam searchParam) {
        List<PaymentInstrument> result = new ArrayList<PaymentInstrument>();
        List<PaymentInstrument> piList = paymentInstrumentRepository.getByUserAndType(userId,
                CommonUtil.isNullOrEmpty(searchParam.getType()) ? null : PIType.valueOf(searchParam.getType())).get();
        for (PaymentInstrument piItem : piList) {
            setAdditionalInfo(piItem);
            result.add(piItem);
        }

        return result;
    }

    @Override
    public String getFacebookPaymentAccount(Long userId) {
        if(userId == null){
            return null;
        }
        List<FacebookPaymentAccountMapping> results = fbPaymentAccountRepository.getByUserId(userId).get();
        if(results != null && results.size() > 0){
            return results.get(0).getFbPaymentAccountId();
        }
        return null;
    }

    @Override
    public String createFBPaymentAccountIfNotExist(FacebookPaymentAccountMapping model) {
        String existingAccount = getFacebookPaymentAccount(model.getUserId());
        if(CommonUtil.isNullOrEmpty(existingAccount)){
            FacebookPaymentAccountMapping result = fbPaymentAccountRepository.create(model).get();
            return result.getFbPaymentAccountId();
        }else{
            return existingAccount;
        }
    }

    private void setAdditionalInfo(PaymentInstrument pi) {
        if (PIType.CREDITCARD.getId().equals(pi.getType())) {
            CreditCardDetail ccDetail = creditCardDetailRepository.get(pi.getId()).get();
            pi.setTypeSpecificDetails(paymentMapperExtension.toTypeSpecificDetails(ccDetail));
        }
    }
}
