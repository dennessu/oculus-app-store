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
import com.junbo.common.util.IdFormatter;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
    public Promise<Results<Offer>> getOffers(final OffersGetOptions options) {
        checkRights(options);

        List<Offer> offers = offerService.getOffers(options);
        Results<Offer> results = new Results<>();
        results.setItems(offers);
        Link nextLink = new Link();
        nextLink.setHref(buildNextUrl(options));
        results.setNext(nextLink);
        results.setTotal(options.getTotal());
        return Promise.pure(results);
    }

    private void checkRights(final OffersGetOptions options) {
        boolean isDeveloper = isDeveloper();
        if (isAdmin()) {
            return;
        } else if (!isDeveloper || options.getOwnerId() == null) {
            // If the published status is not provided, use default published=true filter
            if (options.getPublished() == null) {
                options.setPublished(true);
            } else if (!options.getPublished()) {
                // If a developer try to get unpublished offer, the publisherId is required
                // (falling into this branch means the options.getOwnerId() is null)
                if (isDeveloper) {
                    throw AppCommonErrors.INSTANCE.fieldRequired("publisherId").exception();
                    // if a non-developer try to get unpublished offer, throw forbidden exception.
                } else {
                    throw AppCommonErrors.INSTANCE.forbiddenWithMessage("User is not allowed to" +
                            " get offers that have not been published").exception();
                }
            }
            // This is a developer and the publisherId is provided
            // Do the authorization check.
        } else {
            AuthorizeCallback<Offer> callback = offerAuthorizeCallbackFactory.create(options.getOwnerId());
            RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Offer>>() {
                @Override
                public Promise<Offer> apply() {

                    if (!AuthorizeContext.hasRights("draft.read")) {
                        if (options.getPublished() == null) {
                            options.setPublished(true);
                        }

                        if (!options.getPublished()){
                            throw AppCommonErrors.INSTANCE.forbiddenWithMessage("User is not allowed to" +
                                    " get offers that have not been published").exception();
                        }
                    }

                    return Promise.pure(null);
                }
            });
        }
    }

    @Override
    public Promise<Offer> getOffer(final String offerId) {
        final Offer offer = offerService.getEntity(offerId);
        boolean isDeveloper = isDeveloper();
        if (offer.getPublished() != Boolean.TRUE && !isDeveloper) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("offer", offerId).exception();
        } else if (offer.getPublished()) {
            return Promise.pure(offer);
        } else {
            AuthorizeCallback<Offer> callback = offerAuthorizeCallbackFactory.create(offer);
            return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Offer>>() {
                @Override
                public Promise<Offer> apply() {

                    if (!AuthorizeContext.hasRights("draft.read")) {
                        throw AppCommonErrors.INSTANCE.resourceNotFound("offer", offerId).exception();
                    }

                    return Promise.pure(offer);
                }
            });
        }
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
            builder.queryParam("publisherId", IdFormatter.encodeId(options.getOwnerId()));
        }
        builder.queryParam("count", options.getValidSize());
        if (!StringUtils.isEmpty(options.getNextCursor())) {
            builder.queryParam("cursor", options.getNextCursor());
        } else {
            builder.queryParam("cursor", options.nextStart());
        }

        return builder.toTemplate();
    }

    private boolean isAdmin() {
        return AuthorizeContext.hasAnyScope(new String[] {"catalog.admin"});
    }

    private boolean isDeveloper() {
        return AuthorizeContext.hasAnyScope(new String[] {"catalog.developer", "catalog.admin", "catalog.service"});
    }
}
