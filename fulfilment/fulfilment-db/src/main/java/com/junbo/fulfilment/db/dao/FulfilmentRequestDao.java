/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.dao;

import com.junbo.fulfilment.db.entity.FulfilmentRequestEntity;

/**
 * FulfilmentRequestDao.
 */
public interface FulfilmentRequestDao extends BaseDao<FulfilmentRequestEntity> {
    FulfilmentRequestEntity findByTrackingGuid(Long userId, String trackingGuid);

    FulfilmentRequestEntity findByOrderId(Long billingOrderId);
}
