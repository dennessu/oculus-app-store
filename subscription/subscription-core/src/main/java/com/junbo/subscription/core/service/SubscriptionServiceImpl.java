/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.core.service;

import com.junbo.catalog.spec.enums.PriceType;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.subscription.clientproxy.CatalogGateway;
import com.junbo.subscription.core.SubscriptionService;
import com.junbo.subscription.core.event.SubscriptionCreateEvent;
import com.junbo.subscription.db.entity.SubscriptionStatus;
import com.junbo.subscription.db.repository.SubscriptionRepository;
import com.junbo.subscription.spec.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//import com.junbo.subscription.db.entity.SubscriptionStatus;

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
    private CatalogGateway catalogGateway;

    @Autowired
    private SubscriptionCreateEvent subscriptionCreateEvent;

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

        //create subscription;
        subscription.setStatus(SubscriptionStatus.CREATED.toString());
        subscription = subscriptionRepository.insert(subscription);

        Subscription subs = subscriptionCreateEvent.execute(subscription);

        subscription.setStatus(SubscriptionStatus.ENABLED.toString());
        subs = subscriptionRepository.update(subs);

        return  subs;
    }

    @Override
    public Subscription getSubsByTrackingUuid(Long userId, UUID trackingUuid) {
        if(trackingUuid == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("tracking_uuid").exception();
        }
        return null;
        //return subscriptionRepository.getByTrackingUuid(userId, trackingUuid);
    }

    private void validateOffer(Subscription subs, String offerId){
        if (offerId == null){
            throw AppCommonErrors.INSTANCE.fieldRequired("offer_id").exception();
        }

        Offer subsOffer = catalogGateway.getOffer(offerId);
        OfferRevision subsOfferRev = catalogGateway.getOfferRev(subsOffer.getCurrentRevisionId());
        List<ItemEntry> itemEntryList= subsOfferRev.getItems();
        Item subsItem = catalogGateway.getItem(itemEntryList.get(0).getItemId());

        if (subsItem.getType() != SUBSCRIPTION) {
            throw AppCommonErrors.INSTANCE.fieldInvalid("offer_id", "This is not a subscription offer.").exception();
        }

        //check if free subs? if not, throw exception now and will call billing later.
        if (!isFreeSubscrption(subsOfferRev)){
            throw AppCommonErrors.INSTANCE.fieldInvalid("offer_id", "This is not a free subscription offer.").exception();
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
        if(!price.getPriceType().equals(PriceType.FREE.name())){
            return false;
        }
        return true;
    }


}
