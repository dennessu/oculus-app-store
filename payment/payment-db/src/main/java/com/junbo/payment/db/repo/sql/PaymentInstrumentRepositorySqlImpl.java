/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.sql;

import com.junbo.common.id.PIType;
import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.db.dao.paymentinstrument.PaymentInstrumentDao;
import com.junbo.payment.db.entity.paymentinstrument.PaymentInstrumentEntity;
import com.junbo.payment.db.mapper.PaymentMapperExtension;
import com.junbo.payment.db.repo.PaymentInstrumentRepository;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by minhao on 6/16/14.
 */
public class PaymentInstrumentRepositorySqlImpl implements PaymentInstrumentRepository {
    private PaymentInstrumentDao paymentInstrumentDao;
    private PaymentMapperExtension paymentMapperExtension;
    private IdGenerator idGenerator;

    @Required
    public void setPaymentInstrumentDao(PaymentInstrumentDao paymentInstrumentDao) {
        this.paymentInstrumentDao = paymentInstrumentDao;
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
    public Promise<List<PaymentInstrument>> getByUserId(Long userId) {
        List<PaymentInstrumentEntity> list = paymentInstrumentDao.getByUserId(userId);
        List<PaymentInstrument> paymentInstruments = new ArrayList<>();
        for (PaymentInstrumentEntity entity : list) {
            PaymentInstrument paymentInstrument = paymentMapperExtension.toPaymentInstrument(entity);
            if (paymentInstrument != null) {
                paymentInstruments.add(paymentInstrument);
            }
        }

        return Promise.pure(paymentInstruments);
    }

    @Override
    public Promise<List<PaymentInstrument>> getByUserAndType(Long userId, PIType piType) {
        List<PaymentInstrumentEntity> list = paymentInstrumentDao.getByUserAndType(userId, piType);
        List<PaymentInstrument> paymentInstruments = new ArrayList<>();
        for (PaymentInstrumentEntity entity : list) {
            PaymentInstrument paymentInstrument = paymentMapperExtension.toPaymentInstrument(entity);
            if (paymentInstrument != null) {
                paymentInstruments.add(paymentInstrument);
            }
        }

        return Promise.pure(paymentInstruments);
    }

    @Override
    public Promise<PaymentInstrument> get(Long id) {
        PaymentInstrumentEntity entity = paymentInstrumentDao.get(id);
        if (entity != null) {
            return Promise.pure(paymentMapperExtension.toPaymentInstrument(entity));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<PaymentInstrument> create(PaymentInstrument model) {
        if (model.getId() == null) {
            model.setId(idGenerator.nextId(model.getUserId()));
        }

        PaymentInstrumentEntity entity = paymentMapperExtension.toPIEntity(model);
        Long savedId = paymentInstrumentDao.save(entity);
        return get(savedId);
    }

    @Override
    public Promise<PaymentInstrument> update(PaymentInstrument model) {
        PaymentInstrumentEntity entity = paymentMapperExtension.toPIEntity(model);
        PaymentInstrumentEntity updated = paymentInstrumentDao.update(entity);
        return get(updated.getId());
    }

    @Override
    public Promise<Void> delete(Long id) {
        paymentInstrumentDao.delete(id);
        return Promise.pure(null);
    }
}
