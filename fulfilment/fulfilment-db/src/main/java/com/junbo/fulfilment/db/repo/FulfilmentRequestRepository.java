/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.repo;

import com.junbo.fulfilment.db.dao.FulfilmentRequestDao;
import com.junbo.fulfilment.db.entity.FulfilmentRequestEntity;
import com.junbo.fulfilment.db.mapper.Mapper;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * FulfilmentRequestRepository.
 */
public class FulfilmentRequestRepository {
    @Autowired
    private FulfilmentRequestDao fulfilmentRequestDao;

    public FulfilmentRequest create(FulfilmentRequest request) {
        request.setRequestId(fulfilmentRequestDao.create(Mapper.map(request)));
        return request;
    }

    public FulfilmentRequest get(Long id) {
        return Mapper.map(fulfilmentRequestDao.get(id));
    }

    public Long existBillingOrderId(Long billingOrderId) {
        FulfilmentRequestEntity entity = fulfilmentRequestDao.findByOrderId(billingOrderId);
        return entity == null ? null : entity.getId();
    }

    public Long existTrackingUuid(Long userId, String trackingUuid) {
        FulfilmentRequestEntity entity = fulfilmentRequestDao.findByTrackingUuid(userId, trackingUuid);
        return entity == null ? null : entity.getId();
    }
}
