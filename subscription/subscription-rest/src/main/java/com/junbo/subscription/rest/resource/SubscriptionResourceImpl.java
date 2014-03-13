/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.rest.resource;

import com.junbo.common.id.SubscriptionId;
import com.junbo.subscription.core.SubscriptionService;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.spec.resource.SubscriptionResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.HttpHeaders;

/**
 * subscription resource implement.
 */
public class SubscriptionResourceImpl implements SubscriptionResource {
    @Autowired
    private SubscriptionService service;

    @Override
    public Promise<Subscription> getSubscription(HttpHeaders httpHeaders, SubscriptionId subscriptionId){
        Subscription entitlement = service.getsubscription(subscriptionId.getValue());
        return Promise.pure(entitlement);
    }

}
