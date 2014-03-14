/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.core.service;

import com.junbo.subscription.common.exception.SubscriptionExceptions;
import com.junbo.subscription.core.SubscriptionService;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.db.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

/**
 * subscription service implement.
 */
public class SubscriptionServiceImpl implements SubscriptionService {
    public static final String NOT_START = "NOT_START";
    public static final String EXPIRED = "EXPIRED";
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public Subscription getsubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.get(subscriptionId);
        if (subscription == null) {
            //throw new AppErrorException();
        }
        return subscription;
    }

    @Override
    @Transactional
    public Subscription addsubscription(Subscription subscription) {
        if(subscription.getTrackingUuid() == null){
            throw SubscriptionExceptions.INSTANCE.missingTrackingUuid().exception();
        }
        Subscription result = subscriptionRepository.getByTrackingUuid(subscription.getTrackingUuid());
        if(result != null){
            return result;
        }


        //TODO: set property

        Date currentDate = new Date();

        subscription.setCreatedBy("DEFAULT");
        subscription.setCreatedTime(currentDate);
        subscription.setModifiedBy("DEFAULT");
        subscription.setModifiedTime(currentDate);

        //TODO: create entitlement for subscription

        return subscriptionRepository.insert(subscription);
    }

    @Override
    public Subscription getSubsByTrackingUuid(UUID trackingUuid) {
        return subscriptionRepository.getByTrackingUuid(trackingUuid);
    }

}
