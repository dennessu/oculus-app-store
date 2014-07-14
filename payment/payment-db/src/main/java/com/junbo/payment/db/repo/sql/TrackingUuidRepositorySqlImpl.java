/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.sql;

import com.junbo.langur.core.promise.Promise;
import com.junbo.oom.core.MappingContext;
import com.junbo.payment.db.dao.payment.TrackingUuidDao;
import com.junbo.payment.db.entity.payment.TrackingUuidEntity;
import com.junbo.payment.db.mapper.PaymentMapper;
import com.junbo.payment.db.repo.TrackingUuidRepository;
import com.junbo.payment.spec.model.TrackingUuid;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by minhao on 6/16/14.
 */
public class TrackingUuidRepositorySqlImpl implements TrackingUuidRepository {
    private TrackingUuidDao trackingUuidDao;
    private PaymentMapper paymentMapper;
    private IdGenerator idGenerator;

    @Required
    public void setTrackingUuidDao(TrackingUuidDao trackingUuidDao) {
        this.trackingUuidDao = trackingUuidDao;
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
    public Promise<TrackingUuid> getByTrackingUuid(Long userId, UUID trackingUuid) {
        List<TrackingUuidEntity> list = trackingUuidDao.getByTrackingUuid(userId, trackingUuid);
        List<TrackingUuid> trackingUuidRecords = new ArrayList<>();
        for (TrackingUuidEntity entity : list) {
            TrackingUuid trackingUuidRecord = paymentMapper.toTrackingUuid(entity, new MappingContext());
            if (trackingUuid != null) {
                trackingUuidRecords.add(trackingUuidRecord);
            }
        }

        if (trackingUuidRecords.isEmpty()) {
            return Promise.pure(null);
        }
        else {
            return Promise.pure(trackingUuidRecords.get(0));
        }
    }

    @Override
    public Promise<TrackingUuid> get(Long id) {
        TrackingUuidEntity entity = trackingUuidDao.get(id);
        if (entity != null) {
            return Promise.pure(paymentMapper.toTrackingUuid(entity, new MappingContext()));
        }

        return Promise.pure(null);
    }

    @Override
    public Promise<TrackingUuid> create(TrackingUuid model) {
        if (model.getId() == null) {
            if(model.getPaymentInstrumentId() != null){
                model.setId(idGenerator.nextId(model.getPaymentInstrumentId()));
            }
            else if(model.getPaymentId() != null){
                model.setId(idGenerator.nextId(model.getPaymentId()));
            }
            else if(model.getUserId() != null){
                model.setId(idGenerator.nextId(model.getUserId()));
            }
        }

        TrackingUuidEntity entity = paymentMapper.toTrackingUuidEntity(model, new MappingContext());
        Long savedId = trackingUuidDao.save(entity);
        return get(savedId);
    }

    @Override
    public Promise<TrackingUuid> update(TrackingUuid model) {
        TrackingUuidEntity entity = paymentMapper.toTrackingUuidEntity(model, new MappingContext());
        TrackingUuidEntity updated = trackingUuidDao.update(entity);
        return get(updated.getId());
    }

    @Override
    public Promise<Void> delete(Long id) {
        throw new UnsupportedOperationException("Delete on tracking uuid not support");
    }
}
