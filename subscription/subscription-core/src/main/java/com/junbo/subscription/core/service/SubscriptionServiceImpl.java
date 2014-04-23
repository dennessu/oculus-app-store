/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.core.service;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.subscription.clientproxy.CatalogGateway;
import com.junbo.subscription.clientproxy.EntitlementGateway;
import com.junbo.subscription.common.exception.SubscriptionExceptions;
import com.junbo.subscription.core.SubscriptionService;
import com.junbo.subscription.db.entity.SubscriptionStatus;
import com.junbo.subscription.db.repository.SubscriptionEntitlementRepository;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.db.repository.SubscriptionRepository;
import com.junbo.subscription.spec.model.SubscriptionEntitlement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * subscription service implement.
 */
public class SubscriptionServiceImpl implements SubscriptionService {
    private static final String NOT_START = "NOT_START";
    private static final String EXPIRED = "EXPIRED";
    private static final String SUBSCRIPTION = "SUBSCRIPTION";


    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionEntitlementRepository subscriptionEntitlementRepository;

    @Autowired
    private CatalogGateway catalogGateway;

    @Autowired
    private EntitlementGateway entitlementGateway;

    @Override
    public Subscription getSubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.get(subscriptionId);
        if (subscription == null) {
            //throw new AppErrorException();
        }
        return subscription;
    }

    @Override
    @Transactional
    public Subscription addSubscription(Subscription subscription) {

        //get and verify account and PI

        //get and verify offer.
        validateOffer(subscription, subscription.getOfferId());
        subscription.setStatus(SubscriptionStatus.ENABLED.toString());
        subscription = subscriptionRepository.insert(subscription);

        grantEntitlement(subscription);

        subscription.setStatus(SubscriptionStatus.ENABLED.toString());
        //subscriptionRepository.update(subscription);
        return subscription;
    }

    @Override
    public Subscription getSubsByTrackingUuid(Long userId, UUID trackingUuid) {
        if(trackingUuid == null){
            throw SubscriptionExceptions.INSTANCE.missingTrackingUuid().exception();
        }
        return subscriptionRepository.getByTrackingUuid(userId, trackingUuid);
    }

    private void validateOffer(Subscription subs, Long offerId){
        if (offerId == null){
            throw SubscriptionExceptions.INSTANCE.missingOfferId().exception();
        }

        Offer subsOffer = catalogGateway.getOffer(offerId);
        OfferRevision subsOfferRev = catalogGateway.getOfferRev(subsOffer.getCurrentRevisionId());
        List<ItemEntry> itemEntryList= subsOfferRev.getItems();
        Item subsItem = catalogGateway.getItem(itemEntryList.get(0).getItemId());

        if (subsItem.getType() != SUBSCRIPTION) {
            throw SubscriptionExceptions.INSTANCE.subscriptionTypeError().exception();
        }

        //check if free subs? if not, throw exception now and will call billing later.
        if (!isFreeSubscrption(subsOfferRev)){
            throw SubscriptionExceptions.INSTANCE.subscriptionTypeError().exception();
        }

        //TODO: need get subs length from offer.
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        subs.setSubsStartDate(instance.getTime());
        subs.setAnniversaryDay(instance.get(Calendar.DAY_OF_MONTH));
        instance.add(Calendar.YEAR, 1);
        subs.setSubsEndDate(instance.getTime());
    }

    private boolean isFreeSubscrption(OfferRevision offerRev) {

        Price price = offerRev.getPrice();
        if(!price.getPriceType().equals(Price.FREE)){
            return false;
        }
        return true;
    }

    private void grantEntitlement(Subscription subscription) {
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

    }
}
