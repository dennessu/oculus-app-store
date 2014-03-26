/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.mapper;

import com.junbo.subscription.db.entity.SubscriptionEntity;
import com.junbo.subscription.db.entity.SubscriptionStatus;
import com.junbo.subscription.spec.model.Subscription;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * subscription mapper.
 */
@Component
public class SubscriptionMapper {
    public Subscription toSubscription(SubscriptionEntity subscriptionEntity) {
        if (subscriptionEntity == null){
            return null;
        }
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(subscriptionEntity.getSubscriptionId());
        subscription.setUserId(subscriptionEntity.getUserId());
        subscription.setStatus(subscriptionEntity.getStatusId().toString()); //subscriptionEntity.getStatusId()
        return subscription;
    }

    public SubscriptionEntity toSubscriptionEntity(Subscription subscription) {
        SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setSubscriptionId(subscription.getSubscriptionId());
        subscriptionEntity.setUserId(subscription.getUserId());
        subscriptionEntity.setItemId(subscription.getOfferId().toString());
        subscriptionEntity.setStatusId(SubscriptionStatus.valueOf(subscription.getStatus()));
        return subscriptionEntity;
    }


    public List<Subscription> toSubscriptionList(List<SubscriptionEntity> subscriptionEntities) {
        List<Subscription> subscriptions = new ArrayList<Subscription>(subscriptionEntities.size());
        for (SubscriptionEntity subscriptionEntity : subscriptionEntities) {
            subscriptions.add(toSubscription(subscriptionEntity));
        }
        return subscriptions;
    }



}
