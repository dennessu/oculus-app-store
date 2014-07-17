/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.sql;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.db.dao.payment.PaymentEventDao;
import com.junbo.payment.db.entity.payment.PaymentEventEntity;
import com.junbo.payment.db.mapper.PaymentMapperExtension;
import com.junbo.payment.db.repo.PaymentEventRepository;
import com.junbo.payment.spec.model.PaymentEvent;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by minhao on 6/16/14.
 */
public class PaymentEventRepositorySqlImpl implements PaymentEventRepository {
    private PaymentEventDao paymentEventDao;
    private PaymentMapperExtension paymentMapperExtension;
    private IdGenerator idGenerator;

    @Required
    public void setPaymentEventDao(PaymentEventDao paymentEventDao) {
        this.paymentEventDao = paymentEventDao;
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
    public Promise<PaymentEvent> get(Long id) {
        PaymentEventEntity entity = paymentEventDao.get(id);
        if (entity != null) {
            return Promise.pure(paymentMapperExtension.toPaymentEvent(entity));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<PaymentEvent> create(PaymentEvent model) {
        if (model.getId() == null) {
            model.setId(idGenerator.nextId(model.getPaymentId()));
        }

        PaymentEventEntity entity = paymentMapperExtension.toPaymentEventEntity(model);
        Long savedId = paymentEventDao.save(entity);
        return get(savedId);
    }

    @Override
    public Promise<PaymentEvent> update(PaymentEvent model, PaymentEvent oldModel) {
        PaymentEventEntity entity = paymentMapperExtension.toPaymentEventEntity(model);
        PaymentEventEntity updated = paymentEventDao.update(entity);
        return get(updated.getId());
    }

    @Override
    public Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Delete on payment event not support");
    }

    @Override
    public Promise<List<PaymentEvent>> getByPaymentId(Long paymentId) {
        List<PaymentEventEntity> list = paymentEventDao.getByPaymentId(paymentId);
        List<PaymentEvent> paymentEvents = new ArrayList<>();
        for (PaymentEventEntity entity : list) {
            PaymentEvent paymentEvent = paymentMapperExtension.toPaymentEvent(entity);
            if (paymentEvent != null) {
                paymentEvents.add(paymentEvent);
            }
        }

        return Promise.pure(paymentEvents);
    }
}
