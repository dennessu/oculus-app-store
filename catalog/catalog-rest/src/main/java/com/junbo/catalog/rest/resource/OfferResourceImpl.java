/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.common.util.IdFormatter;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.BeanParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

/**
 * Offer resource implementation.
 */
public class OfferResourceImpl implements OfferResource {
    @Autowired
    private OfferService offerService;

    @Override
    public Promise<Results<Offer>> getOffers(@BeanParam OffersGetOptions options) {
        List<Offer> offers = offerService.getOffers(options);
        Results<Offer> results = new Results<>();
        results.setItems(offers);
        Link nextLink = new Link();
        nextLink.setHref(buildNextUrl(options));
        results.setNext(nextLink);
        return Promise.pure(results);
    }

    @Override
    public Promise<Offer> getOffer(OfferId offerId) {
        return Promise.pure(offerService.getEntity(offerId.getValue()));
    }

    @Override
    public Promise<Offer> create(Offer offer) {
        return Promise.pure(offerService.createEntity(offer));
    }

    @Override
    public Promise<Offer> update(OfferId offerId, Offer offer) {
        return Promise.pure(offerService.updateEntity(offerId.getValue(), offer));
    }

    @Override
    public Promise<Response> delete(OfferId offerId) {
        offerService.deleteEntity(offerId.getValue());
        return Promise.pure(Response.status(204).build());
    }

    private String buildNextUrl(OffersGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getOfferIds()) || options.getItemId() != null) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("offers");
        if (options.getCategory() != null) {
            builder.queryParam("category", IdFormatter.encodeId(options.getCategory()));
        }
        if (options.getPublished() != null) {
            builder.queryParam("published", options.getPublished());
        }
        if (options.getOwnerId() != null) {
            builder.queryParam("publisherId", options.getOwnerId());
        }
        builder.queryParam("size", options.getValidSize());
        if (!StringUtils.isEmpty(options.getNextBookmark())) {
            builder.queryParam("bookmark", options.getNextBookmark());
        } else {
            builder.queryParam("start", options.nextStart());
        }

        return builder.toTemplate();
    }
}
