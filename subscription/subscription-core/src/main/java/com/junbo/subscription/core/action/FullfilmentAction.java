/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.core.action;

import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.subscription.clientproxy.EntitlementGateway;
import com.junbo.subscription.core.SubscriptionAction;
import com.junbo.subscription.db.repository.SubscriptionEntitlementRepository;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.spec.model.SubscriptionEntitlement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * Created by Administrator on 14-5-21.
 */
public class FullfilmentAction implements SubscriptionAction{
    @Autowired
    private SubscriptionEntitlementRepository subscriptionEntitlementRepository;

    @Autowired
    private EntitlementGateway entitlementGateway;

    @Override
    public Subscription execute(Subscription subscription){
        Entitlement entitlement = new Entitlement();
        entitlement.setUserId(subscription.getUserId());
        entitlement.setTrackingUuid(UUID.randomUUID());
        entitlement.setExpirationTime(subscription.getSubsEndDate());
        //entitlement.getEntitlementDefinitionId();

        Long entitlementId = entitlementGateway.grantEntitlement(entitlement);

        SubscriptionEntitlement subscriptionEntitlement = new SubscriptionEntitlement();
        subscriptionEntitlement.setSubscriptionId(subscription.getId());
        subscriptionEntitlement.setEntitlementId(entitlementId);
        subscriptionEntitlement.setEntitlementStatus(0);
        subscriptionEntitlementRepository.insert(subscriptionEntitlement);
        return  subscription;
    }
}
