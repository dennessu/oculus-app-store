/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.repository;


import com.junbo.oom.core.MappingContext;
import com.junbo.payment.db.dao.payment.TrackingUuidDao;
import com.junbo.payment.db.entity.payment.TrackingUuidEntity;
import com.junbo.payment.db.mapper.PaymentMapper;
import com.junbo.payment.spec.model.TrackingUuid;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

/**
 * Tracking Uuid Repository.
 */
public class TrackingUuidRepository {
    @Autowired
    private TrackingUuidDao trackingUuidDao;
    @Autowired
    private PaymentMapper paymentMapperImpl;

    public TrackingUuid getByTrackUuid(Long userId, UUID trackingUuid){
        List<TrackingUuidEntity> entities = trackingUuidDao.getByTrackingUuid(userId, trackingUuid);
        if(entities != null && !entities.isEmpty()){
            TrackingUuidEntity piEntity = entities.get(0);
            return paymentMapperImpl.toTrackingUuid(piEntity, new MappingContext());
        }
        return null;
    }

    public void saveTrackingUuid(TrackingUuid trackingUuid){
        trackingUuidDao.save(paymentMapperImpl.toTrackingUuidEntity(trackingUuid, new MappingContext()));
    }

    public void updateResponse(TrackingUuid request){
        List<TrackingUuidEntity> entities = trackingUuidDao.getByTrackingUuid(
                request.getUserId(), request.getTrackingUuid());
        entities.get(0).setResponse(request.getResponse());
        trackingUuidDao.update(entities.get(0));
    }
}
