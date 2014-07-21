/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.sql;

import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import com.junbo.payment.db.dao.payment.PaymentPropertyDao;
import com.junbo.payment.db.entity.payment.PaymentPropertyEntity;
import com.junbo.payment.db.mapper.PaymentMapper;
import com.junbo.payment.db.repo.PaymentPropertyRepository;
import com.junbo.payment.spec.model.PaymentProperty;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by minhao on 6/16/14.
 */
public class PaymentPropertyRepositorySqlImpl implements PaymentPropertyRepository {
    private PaymentPropertyDao paymentPropertyDao;
    private PaymentMapper paymentMapper;
    private IdGenerator idGenerator;

    @Required
    public void setPaymentPropertyDao(PaymentPropertyDao paymentPropertyDao) {
        this.paymentPropertyDao = paymentPropertyDao;
    }

    @Required
    public void setPaymentMapper(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Promise<List<PaymentProperty>> getByPaymentId(Long paymentId) {
        List<PaymentPropertyEntity> entities = paymentPropertyDao.getByPaymentId(paymentId);
        List<PaymentProperty> results = new ArrayList<PaymentProperty>();
        for(PaymentPropertyEntity entity : entities){
            PaymentProperty result = paymentMapper.toPaymentProperty(entity, new MappingContext());
            results.add(result);
        }
        return Promise.pure(results);
    }

    @Override
    public Promise<PaymentProperty> get(Long id) {
        PaymentPropertyEntity entity = paymentPropertyDao.get(id);
        if (entity != null) {
            return Promise.pure(paymentMapper.toPaymentProperty(entity, new MappingContext()));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<PaymentProperty> create(PaymentProperty model) {
        if (model.getId() == null) {
            model.setId(idGenerator.nextId(model.getPaymentId()));
        }

        PaymentPropertyEntity entity = paymentMapper.toPaymentPropertyEntity(model, new MappingContext());
        Long savedId = paymentPropertyDao.save(entity);
        return get(savedId);
    }

    @Override
    public Promise<PaymentProperty> update(PaymentProperty model, PaymentProperty oldModel) {
        PaymentPropertyEntity entity = paymentMapper.toPaymentPropertyEntity(model, new MappingContext());
        PaymentPropertyEntity updated = paymentPropertyDao.update(entity);
        return get(updated.getId());
    }

    @Override
    public Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Delete on payment property not support");
    }
}
