/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.resource.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.external.casey.*;
import com.junbo.store.spec.model.external.casey.cms.CmsCampaign;
import com.junbo.store.spec.model.external.casey.cms.CmsContent;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The CaseyResource interface.
 */
@Path("/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface CaseyResource {

    @GET
    @Path("search")
    Promise<CaseyResults<JsonNode>> searchOffers(@BeanParam OfferSearchParams params);

    @GET
    @Path("ratings/item/{itemId}")
    Promise<CaseyResults<CaseyAggregateRating>> getRatingByItemId(@PathParam("itemId") String itemId);

    @GET
    @Path("reviews")
    Promise<CaseyResults<CaseyReview>> getReviews(@BeanParam ReviewSearchParams params);

    @POST
    @Path("reviews")
    Promise<CaseyReview> addReview(CaseyReview review);

    @GET
    @Path("cms-campaigns")
    Promise<CaseyResults<CmsCampaign>> getCmsCampaigns();

    @GET
    @Path("cms-contents/{contentId}")
    Promise<CmsContent> getCmsContent(@PathParam("contentId") String contentId);
}
