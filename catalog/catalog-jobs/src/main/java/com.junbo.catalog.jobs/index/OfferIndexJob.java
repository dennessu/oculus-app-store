/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.jobs.index;

import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * OfferIndexJob.
 */
public class OfferIndexJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferIndexJob.class);
    private OfferService offerService;
    private AtomicBoolean running = new AtomicBoolean(false);

    @Required
    public void setOfferService(OfferService offerService) {
        this.offerService = offerService;
    }

    public void execute() {
        if (running.compareAndSet(false, true)) {
            try {
                LOGGER.info("Start OfferIndexJob");
                updateActiveRevision();
                LOGGER.info("End OfferIndex Job");
            } finally {
                running.set(false);
            }
        } else {
            LOGGER.info("Another thread is running.");
        }
    }

    private void updateActiveRevision() {
        OffersGetOptions options = new OffersGetOptions();
        options.setSize(20);
        options.setQuery("scheduledPublish:true AND published:true");
        List<Offer> offers = offerService.getOffers(options);
        while (!CollectionUtils.isEmpty(offers)) {
            Iterator<Offer> iterator = offers.iterator();
            while (iterator.hasNext()) {
                Offer offer = iterator.next();
                if (offer.getCurrentRevisionId() != null && offer.getActiveRevision() != null
                        && offer.getCurrentRevisionId().equals(offer.getActiveRevision().getRevisionId())) {
                    continue;
                } else if (offer.getCurrentRevisionId()==null) {
                    LOGGER.info("No active revision for published offer " + offer.getOfferId());
                    offer.setActiveRevision(null);
                    offerService.updateEntity(offer.getOfferId(), offer);
                    continue;
                }

                LOGGER.info("Refresh active revision for offer: " + offer.getOfferId()
                        + ". Active revision: " + offer.getCurrentRevisionId());
                try {
                    offer.setActiveRevision(offerService.getRevision(offer.getCurrentRevisionId()));
                    offerService.updateEntity(offer.getOfferId(), offer);
                } catch (Exception e) {
                    LOGGER.error("Error updating offer " + offer.getOfferId(), e);
                }

            }

            options.nextPage();
            offers = offerService.getOffers(options);
        }
    }
}
