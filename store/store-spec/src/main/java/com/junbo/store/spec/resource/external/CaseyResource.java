/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.resource.external;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating;
import com.junbo.store.spec.model.external.casey.CaseyResults;
import com.junbo.store.spec.model.external.casey.CaseyReview;
import com.junbo.store.spec.model.external.casey.ReviewSearchParams;
import com.junbo.store.spec.model.external.casey.cms.CmsCampaign;
import com.junbo.store.spec.model.external.casey.cms.CmsContent;
import com.junbo.store.spec.model.external.casey.cms.CmsPage;
import com.junbo.store.spec.model.external.casey.cms.CmsPageGetParams;
import com.junbo.store.spec.model.external.casey.cms.*;
import com.junbo.store.spec.model.external.casey.search.CaseyOffer;
import com.junbo.store.spec.model.external.casey.search.OfferSearchParams;

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
    Promise<CaseyResults<CaseyOffer>> searchOffers(@BeanParam OfferSearchParams params);

    @GET
    @Path("cms-campaigns")
    Promise<CaseyResults<CmsCampaign>> getCmsCampaigns(@BeanParam CmsCampaignGetParam campaignGetParam);

    @GET
    @Path("cms-pages")
    Promise<CaseyResults<CmsPage>> getCmsPages(@BeanParam CmsPageGetParams pageGetParams);

    @GET
    @Path("cms-pages/{pageId}/cms-schedule")
    Promise<CmsSchedule> getCmsSchedules(@PathParam("pageId") String pageId, @BeanParam CmsScheduleGetParams cmsScheduleGetParams);

    @GET
    @Path("cms-contents/{contentId}")
    Promise<CmsContent> getCmsContent(@PathParam("contentId") String contentId);

    @GET
    @Path("ratings/item/{itemId}")
    Promise<CaseyResults<CaseyAggregateRating>> getRatingByItemId(@PathParam("itemId") String itemId);

    @GET
    @Path("reviews")
    Promise<CaseyResults<CaseyReview>> getReviews(@BeanParam ReviewSearchParams params);
}
