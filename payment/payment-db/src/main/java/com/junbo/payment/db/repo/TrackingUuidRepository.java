/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.spec.model.TrackingUuid;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.UUID;

/**
 * Created by minhao on 6/16/14.
 */
public interface TrackingUuidRepository extends BaseRepository<TrackingUuid, Long> {
    @ReadMethod
    Promise<TrackingUuid> getByTrackingUuid(Long userId, UUID trackingUuid);
}
