/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.payment.db.repo.cloudant;

import com.junbo.langur.core.promise.Promise;
import com.junbo.payment.db.repo.TrackingUuidRepository;
import com.junbo.payment.spec.model.TrackingUuid;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;
import groovy.lang.Closure;

import java.util.List;
import java.util.UUID;

/**
 * Created by haomin on 14-6-19.
 */
public class TrackingUuidRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<TrackingUuid, Long> implements TrackingUuidRepository {
    @Override
    public Promise<TrackingUuid> getByTrackingUuid(Long userId, UUID trackingUuid) {
        return super.queryView("by_tracking_uuid", trackingUuid.toString()).then(new Closure<Promise<TrackingUuid>>(this, this) {
            public Promise<TrackingUuid> doCall(List<TrackingUuid> list) {
                return Promise.pure(list.get(0));
            }

        });
    }

}
