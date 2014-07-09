/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.rest.resource;

import com.junbo.authorization.AuthorizeCallback;
import com.junbo.authorization.AuthorizeContext;
import com.junbo.authorization.AuthorizeService;
import com.junbo.authorization.RightsScope;
import com.junbo.catalog.auth.OfferAuthorizeCallbackFactory;
import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
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

    @Autowired
    private OfferAuthorizeCallbackFactory offerAuthorizeCallbackFactory;

    @Autowired
    private AuthorizeService authorizeService;

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
    public Promise<Offer> getOffer(String offerId) {
        return Promise.pure(offerService.getEntity(offerId));
    }

    @Override
    public Promise<Offer> create(final Offer offer) {
        AuthorizeCallback<Offer> callback = offerAuthorizeCallbackFactory.create(offer);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Offer>>() {
            @Override
            public Promise<Offer> apply() {

                if (!AuthorizeContext.hasRights("create")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                return Promise.pure(offerService.createEntity(offer));
            }
        });
    }

    @Override
    public Promise<Offer> update(final String offerId, final Offer offer) {
        AuthorizeCallback<Offer> callback = offerAuthorizeCallbackFactory.create(offer);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Offer>>() {
            @Override
            public Promise<Offer> apply() {

                if (!AuthorizeContext.hasRights("update")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                return Promise.pure(offerService.updateEntity(offerId, offer));
            }
        });
    }

    @Override
    public Promise<Response> delete(final String offerId) {
        final Offer offer = offerService.getEntity(offerId);
        if (offer == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("Offer", offerId).exception();
        }


        AuthorizeCallback<Offer> callback = offerAuthorizeCallbackFactory.create(offer);
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Response>>() {
            @Override
            public Promise<Response> apply() {

                if (!AuthorizeContext.hasRights("delete")) {
                    throw AppCommonErrors.INSTANCE.forbidden().exception();
                }

                offerService.deleteEntity(offerId);
                return Promise.pure(Response.status(204).build());
            }
        });

    }

    private String buildNextUrl(OffersGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getOfferIds()) || options.getItemId() != null) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("offers");
        if (!StringUtils.isEmpty(options.getCategory())) {
            builder.queryParam("category", options.getCategory());
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
