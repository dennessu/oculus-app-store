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
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

/**
 * Offer revision resource implementation.
 */
public class OfferRevisionResourceImpl implements OfferRevisionResource {
    @Autowired
    private OfferService offerService;

    @Autowired
    private OfferAuthorizeCallbackFactory offerAuthorizeCallbackFactory;

    @Autowired
    private AuthorizeService authorizeService;

    @Override
    public Promise<Results<OfferRevision>> getOfferRevisions(OfferRevisionsGetOptions options) {
        List<OfferRevision> revisions = offerService.getRevisions(options);
        Results<OfferRevision> results = new Results<>();
        results.setItems(revisions);
        return Promise.pure(results);
    }

    private String buildNextUrl(OfferRevisionsGetOptions options) {
        if (!CollectionUtils.isEmpty(options.getOfferIds()) || !CollectionUtils.isEmpty(options.getRevisionIds())) {
            return null;
        }

        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("offer-revisions");
        if (options.getStatus() != null) {
            builder.queryParam("status", options.getStatus().toUpperCase());
        }
        builder.queryParam("size", options.getValidSize());
        if (!StringUtils.isEmpty(options.getNextBookmark())) {
            builder.queryParam("bookmark", options.getNextBookmark());
        } else {
            builder.queryParam("start", options.nextStart());
        }

        return builder.toTemplate();
    }

    @Override
    public Promise<OfferRevision> getOfferRevision(String revisionId, OfferRevisionGetOptions options) {
        return Promise.pure(offerService.getRevision(revisionId));
    }

    @Override
    public Promise<OfferRevision> createOfferRevision(final OfferRevision offerRevision) {
        AuthorizeCallback<Offer> callback = offerAuthorizeCallbackFactory.create(offerRevision.getOfferId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<OfferRevision>>() {
            @Override
            public Promise<OfferRevision> apply() {

                if (!AuthorizeContext.hasRights("create")) {
                    throw AppErrors.INSTANCE.accessDenied().exception();
                }

                return Promise.pure(offerService.createRevision(offerRevision));
            }
        });
    }

    @Override
    public Promise<OfferRevision> updateOfferRevision(final String revisionId, final OfferRevision offerRevision) {
        AuthorizeCallback<Offer> callback = offerAuthorizeCallbackFactory.create(offerRevision.getOfferId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<OfferRevision>>() {
            @Override
            public Promise<OfferRevision> apply() {

                if (!AuthorizeContext.hasRights("update")) {
                    throw AppErrors.INSTANCE.accessDenied().exception();
                }

                return Promise.pure(offerService.updateRevision(revisionId, offerRevision));
            }
        });
    }

    @Override
    public Promise<Response> delete(final String revisionId) {
        final OfferRevision offerRevision = offerService.getRevision(revisionId);
        if (offerRevision == null) {
            throw AppErrors.INSTANCE.notFound("offer-revision", Utils.encodeId(revisionId)).exception();
        }

        AuthorizeCallback<Offer> callback = offerAuthorizeCallbackFactory.create(offerRevision.getOfferId());
        return RightsScope.with(authorizeService.authorize(callback), new Promise.Func0<Promise<Response>>() {
            @Override
            public Promise<Response> apply() {

                if (!AuthorizeContext.hasRights("delete")) {
                    throw AppErrors.INSTANCE.accessDenied().exception();
                }

                offerService.deleteRevision(revisionId);
                return Promise.pure(Response.status(204).build());
            }
        });
    }
}
