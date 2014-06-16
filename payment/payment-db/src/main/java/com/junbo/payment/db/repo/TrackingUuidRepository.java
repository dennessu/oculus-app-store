/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo;

import com.junbo.payment.db.mapper.TrackingUuid;

import java.util.UUID;

/**
 * Created by minhao on 6/16/14.
 */
public interface TrackingUuidRepository {
    // payment internal use
    TrackingUuid getByTrackUuid(Long userId, UUID trackingUuid);

    // payment internal use
    void saveTrackingUuid(TrackingUuid trackingUuid);
}
