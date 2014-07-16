/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantSearchResult;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Offer repository.
 */
public class OfferRepositoryImpl extends CloudantClient<Offer> implements OfferRepository {

    public Offer create(Offer offer) {
        return cloudantPostSync(offer);
    }

    public Offer get(String offerId) {
        if (offerId == null) {
            return null;
        }
        return cloudantGetSync(offerId);
    }

    public List<Offer> getOffers(OffersGetOptions options) {
        List<Offer> offers = new ArrayList<>();
        if (!CollectionUtils.isEmpty(options.getOfferIds())) {
            for (String offerId : options.getOfferIds()) {
                Offer offer = cloudantGetSync(offerId.toString());
                if (offer == null) {
                    continue;
                }else if (options.getCategory() != null
                        && (offer.getCategories() == null
                            || !offer.getCategories().contains(options.getCategory()))) {
                    continue;
                } else if (options.getPublished() != null && !options.getPublished().equals(offer.getPublished())) {
                    continue;
                } else if (options.getOwnerId() != null
                        && !options.getOwnerId().equals(offer.getOwnerId())) {
                    continue;
                } else {
                    offers.add(offer);
                }
            }
        } else if (!StringUtils.isEmpty(options.getQuery())) {
            CloudantSearchResult<Offer> searchResult =
                    search("search", options.getQuery(), options.getValidSize(), options.getBookmark()).syncGet();
            offers = searchResult.getResults();
            options.setNextBookmark(searchResult.getBookmark());
            options.setStart(null);
        } else if (options.getCategory() != null || options.getPublished() != null || options.getOwnerId() != null) {
            StringBuilder sb = new StringBuilder();
            if (options.getCategory() != null) {
                sb.append("categoryId:'").append(options.getCategory()).append("'");
            }
            if (options.getPublished() != null) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("published:").append(options.getPublished());
            }
            if (options.getOwnerId() != null) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("ownerId:'").append(options.getOwnerId().getValue()).append("'");
            }
            CloudantSearchResult<Offer> searchResult =
                    search("search", sb.toString(), options.getValidSize(), options.getBookmark()).syncGet();
            offers = searchResult.getResults();
            options.setNextBookmark(searchResult.getBookmark());
            options.setStart(null);
        } else {
            offers = queryView("by_offerId", null, options.getValidSize(), options.getValidStart(), false).syncGet();
            options.setNextBookmark(null);
        }

        return offers;
    }

    public List<Offer> getOffers(Collection<String> offerIds) {
        List<Offer> offers = new ArrayList<>();
        for (String offerId : offerIds) {
            Offer offer = cloudantGetSync(offerId);
            if (offer != null) {
                offers.add(offer);
            }
        }

        return offers;
    }

    @Override
    public Offer update(Offer offer, Offer oldOffer) {
        return cloudantPutSync(offer, oldOffer);
    }

    @Override
    public void delete(String offerId) {
        cloudantDeleteSync(offerId);
    }

}
