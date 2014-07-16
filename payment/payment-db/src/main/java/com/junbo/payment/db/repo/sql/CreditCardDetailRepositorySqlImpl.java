/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.sql;

import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import com.junbo.payment.db.dao.paymentinstrument.CreditCardPaymentInstrumentDao;
import com.junbo.payment.db.entity.paymentinstrument.CreditCardPaymentInstrumentEntity;
import com.junbo.payment.db.mapper.PaymentMapper;
import com.junbo.payment.db.repo.CreditCardDetailRepository;
import com.junbo.payment.spec.model.CreditCardDetail;
import org.springframework.beans.factory.annotation.Required;


/**
 * Created by minhao on 6/16/14.
 */
public class CreditCardDetailRepositorySqlImpl implements CreditCardDetailRepository {
    private CreditCardPaymentInstrumentDao creditCardPaymentInstrumentDao;
    private PaymentMapper paymentMapper;

    @Required
    public void setCreditCardPaymentInstrumentDao(CreditCardPaymentInstrumentDao creditCardPaymentInstrumentDao) {
        this.creditCardPaymentInstrumentDao = creditCardPaymentInstrumentDao;
    }

    @Required
    public void setPaymentMapper(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    @Override
    public Promise<CreditCardDetail> get(Long id) {
        CreditCardPaymentInstrumentEntity entity = creditCardPaymentInstrumentDao.get(id);
        if (entity != null) {
            return Promise.pure(paymentMapper.toCreditCardDetail(entity, new MappingContext()));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<CreditCardDetail> create(CreditCardDetail model) {
        if (model.getId() == null) {
            throw new RuntimeException("credit card detail id is null, please set id as payment instrument id before create");
        }

        CreditCardPaymentInstrumentEntity entity = paymentMapper.toCreditCardEntity(model, new MappingContext());
        Long savedId = creditCardPaymentInstrumentDao.save(entity);
        return get(savedId);
    }

    @Override
    public Promise<CreditCardDetail> update(CreditCardDetail model, CreditCardDetail oldModel) {
        CreditCardPaymentInstrumentEntity entity = paymentMapper.toCreditCardEntity(model, new MappingContext());
        CreditCardPaymentInstrumentEntity updated = creditCardPaymentInstrumentDao.update(entity);
        return get(updated.getId());
    }

    @Override
    public Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Delete on creditcard detail not support");
    }
}
