/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.common.cache.CacheFacade;
import com.junbo.catalog.common.util.Callable;
import com.junbo.catalog.common.util.Constants;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.spec.model.offer.CountryProperties;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantQueryResult;
import com.junbo.common.cloudant.model.CloudantSearchResult;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Offer repository.
 */
public class OfferRepositoryImpl extends CloudantClient<Offer> implements OfferRepository {

    public Offer create(Offer offer) {
        Offer createdOffer = cloudantPostSync(offer);
        CacheFacade.OFFER.put(createdOffer.getOfferId(), createdOffer);
        return createdOffer;
    }

    public Offer get(final String offerId) {
        if (offerId == null) {
            return null;
        }
        return CacheFacade.OFFER.get(offerId, new Callable<Offer>() {
            @Override
            public Offer execute() {
                return cloudantGetSync(offerId);
            }
        });
    }

    public List<Offer> getOffers(OffersGetOptions options) {
        List<Offer> offers = new ArrayList<>();
        if (!CollectionUtils.isEmpty(options.getOfferIds())) {
            for (String offerId : options.getOfferIds()) {
                Offer offer = get(offerId);
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
                } else if (options.getCountry() != null && !availableInCountry(options.getCountry(), offer)) {
                    continue;
                } else {
                    offers.add(offer);
                }
            }
            options.setTotal(Long.valueOf(offers.size()));
        } else if (options.getCategory() != null || options.getPublished() != null || options.getOwnerId() != null || options.getCountry() != null) {
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
            if (options.getCountry() != null) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("(availableCountry:('").append(options.getCountry()).append("' OR ").append(Constants.DEFAULT_COUNTRY).append(")");
                sb.append(" AND ").append("-unavailableCountry:'").append(options.getCountry()).append("')");
            }
            CloudantSearchResult<Offer> searchResult =
                    search("search", sb.toString(), options.getValidSize(), options.getCursor()).get();
            offers = searchResult.getResults();
            options.setNextCursor(searchResult.getBookmark());
            options.setTotal(searchResult.getTotal());
        } else if (!StringUtils.isEmpty(options.getQuery())) {
            CloudantSearchResult<Offer> searchResult =
                    search("search", "(" + options.getQuery() + ") AND published:true", options.getValidSize(), options.getCursor()).get();
            offers = searchResult.getResults();
            options.setNextCursor(searchResult.getBookmark());
            options.setTotal(searchResult.getTotal());
        } else {
            CloudantQueryResult queryResult = queryViewSync("by_offerId", null, options.getValidSize(), options.getValidStart(), false, true);
            offers = Utils.getDocs(queryResult.getRows());
            options.setNextCursor(null);
            options.setTotal(queryResult.getTotalRows());
        }

        return offers;
    }

    @Override
    public Offer update(Offer offer, Offer oldOffer) {
        Offer updatedOffer = cloudantPutSync(offer, oldOffer);
        CacheFacade.OFFER.put(updatedOffer.getOfferId(), updatedOffer);
        return updatedOffer;
    }

    @Override
    public void delete(String offerId) {
        cloudantDeleteSync(offerId);
        CacheFacade.OFFER.evict(offerId);
    }

    private boolean availableInCountry(String countryCode, Offer offer) {
        if (offer.getActiveRevision() == null || offer.getActiveRevision().getCountries()==null) {
            return false;
        }
        Map<String, CountryProperties> countries = offer.getActiveRevision().getCountries();
        if (countries.get(countryCode)==null) {
            if (countries.get(Constants.DEFAULT_COUNTRY) != null) {
                return Boolean.TRUE.equals(countries.get(Constants.DEFAULT_COUNTRY).getIsPurchasable());
            }
            return false;
        } else {
            return Boolean.TRUE.equals(countries.get(countryCode).getIsPurchasable());
        }
    }
}
