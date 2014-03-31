/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.core.service;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.Price;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.subscription.clientproxy.CatalogGateway;
import com.junbo.subscription.clientproxy.EntitlementGateway;
import com.junbo.subscription.common.exception.SubscriptionExceptions;
import com.junbo.subscription.core.SubscriptionService;
import com.junbo.subscription.db.entity.SubscriptionStatus;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.db.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * subscription service implement.
 */
public class SubscriptionServiceImpl implements SubscriptionService {
    private static final String NOT_START = "NOT_START";
    private static final String EXPIRED = "EXPIRED";
    private static final String SUBSCRIPTION = "SUBSCRIPTION";
    //private static final Long SUBSCRIPTION = 1L;


    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private CatalogGateway catalogGateway;

    @Autowired
    private EntitlementGateway entitlementGateway;

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
//        Subscription result = subscriptionRepository.getByTrackingUuid(subscription.getTrackingUuid());
//        if(result != null){
//            return result;
//        }


        //TODO: set property
        Offer subsOffer = catalogGateway.getOffer(subscription.getOfferId());
        List<ItemEntry> itemEntryList= subsOffer.getItems();
        Item subsItem = catalogGateway.getItem(itemEntryList.get(0).getItemId());

        if (subsItem.getType() != SUBSCRIPTION) {
            throw SubscriptionExceptions.INSTANCE.subscriptionTypeError().exception();
        }

        //check if free subs? if not, throw exception now and will call billing later.

        if (!isFreeSubscrption(subsOffer)){
            throw SubscriptionExceptions.INSTANCE.subscriptionTypeError().exception();
        }

        //TODO: create entitlement for subscription
        Entitlement entitlement = new Entitlement();
        //entitlementGateway.grant(entitlement);

        subscription.setStatus(SubscriptionStatus.ENABLED.toString());

        return subscriptionRepository.insert(subscription);
    }

    @Override
    public Subscription getSubsByTrackingUuid(UUID trackingUuid) {
        return subscriptionRepository.getByTrackingUuid(trackingUuid);
    }

    private boolean isFreeSubscrption(Offer offer) {

        Map<String, Price> priceMap = offer.getPrices();
        if(!offer.getPriceType().equals("Free")){
            return false;
        }
        return true;
    }
}
