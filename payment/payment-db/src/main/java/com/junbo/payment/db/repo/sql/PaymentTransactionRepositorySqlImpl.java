/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.sql;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.db.dao.payment.PaymentTransactionDao;
import com.junbo.payment.db.entity.payment.PaymentTransactionEntity;
import com.junbo.payment.db.mapper.PaymentMapperExtension;
import com.junbo.payment.db.repo.PaymentTransactionRepository;
import com.junbo.payment.spec.model.PaymentTransaction;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;

/**
 * Created by minhao on 6/16/14.
 */
public class PaymentTransactionRepositorySqlImpl implements PaymentTransactionRepository {
    private PaymentTransactionDao paymentTransactionDao;
    private PaymentMapperExtension paymentMapperExtension;
    private IdGenerator idGenerator;

    @Required
    public void setPaymentTransactionDao(PaymentTransactionDao paymentTransactionDao) {
        this.paymentTransactionDao = paymentTransactionDao;
    }

    @Required
    public void setPaymentMapperExtension(PaymentMapperExtension paymentMapperExtension) {
        this.paymentMapperExtension = paymentMapperExtension;
    }

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Promise<PaymentTransaction> get(Long id) {
        PaymentTransactionEntity entity = paymentTransactionDao.get(id);
        if (entity != null) {
            return Promise.pure(paymentMapperExtension.toPaymentTransaction(entity));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<PaymentTransaction> create(PaymentTransaction model) {
        if (model.getId() == null) {
            model.setId(idGenerator.nextId(model.getPaymentInstrumentId()));
        }

        PaymentTransactionEntity entity = paymentMapperExtension.toPaymentTransactionEntity(model);
        Long savedId = paymentTransactionDao.save(entity);
        return get(savedId);
    }

    @Override
    public Promise<PaymentTransaction> update(PaymentTransaction model) {
        PaymentTransactionEntity entity = paymentMapperExtension.toPaymentTransactionEntity(model);
        PaymentTransactionEntity updated = paymentTransactionDao.update(entity);
        return get(updated.getId());
    }

    @Override
    public Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Delete on payment transaction not support");
    }
}
