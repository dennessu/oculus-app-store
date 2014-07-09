/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.rest.resource;

import com.junbo.authorization.AuthorizeCallback;
import com.junbo.authorization.AuthorizeContext;
import com.junbo.authorization.AuthorizeService;
import com.junbo.authorization.RightsScope;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.SubscriptionId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.subscription.auth.SubscriptionAuthorizeCallbackFactory;
import com.junbo.subscription.core.SubscriptionService;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.spec.resource.SubscriptionResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * subscription resource implement.
 */
public class SubscriptionResourceImpl implements SubscriptionResource {
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private SubscriptionAuthorizeCallbackFactory authorizeCallbackFactory;

    @Override
    public Promise<Subscription> getSubscriptionById(SubscriptionId subscriptionId){
        final Subscription subscription = subscriptionService.getSubscription(subscriptionId.getValue());
        AuthorizeCallback callback = authorizeCallbackFactory.create(subscription);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Subscription>>() {
            @Override
            public Promise<Subscription> apply() {
                if (!AuthorizeContext.hasRights("read")) {
                    //throw new AppErrorException();
                }
                return Promise.pure(subscription);
            }
        });
    }

    @Override
    public Promise<Subscription> postSubscription(final Subscription subscription){
        AuthorizeCallback callback = authorizeCallbackFactory.create(subscription);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Subscription>>() {
            @Override
            public Promise<Subscription> apply() {
                if (!AuthorizeContext.hasRights("create")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                UUID trackingUuid = subscription.getTrackingUuid();
                if (trackingUuid != null) {
                    Subscription existingSubscription =
                            subscriptionService.getSubsByTrackingUuid(subscription.getUserId(), trackingUuid);
                    if (existingSubscription != null) {
                        return Promise.pure(existingSubscription);
                    }
                }
                subscription.setTrackingUuid(UUID.randomUUID());

                return Promise.pure(subscriptionService.addSubscription(subscription));
            }
        });
    }



}
