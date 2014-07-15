/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.clientproxy.impl;

import com.junbo.langur.core.promise.SyncModeScope;
import com.junbo.rating.spec.model.subscription.SubsRatingRequest;
import com.junbo.rating.spec.resource.RatingResource;
import com.junbo.subscription.clientproxy.RatingGateway;
import com.junbo.subscription.common.exception.SubscriptionExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by Administrator on 14-5-20.
 */
public class RatingGatewayImpl implements RatingGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentGatewayImpl.class);

    @Autowired
    @Qualifier("ratingClient")
    private RatingResource ratingResource;

    public SubsRatingRequest subsRating(SubsRatingRequest request){
        try (SyncModeScope scope = new SyncModeScope()) {
            SubsRatingRequest response = ratingResource.subsRating(request).syncGet();

            return response;
        }catch (Exception e) {
            LOGGER.error("Error occurred during calling [Rating] component.", e);
            throw SubscriptionExceptions.INSTANCE.gatewayFailure("Rating").exception();
        }

    }

}
