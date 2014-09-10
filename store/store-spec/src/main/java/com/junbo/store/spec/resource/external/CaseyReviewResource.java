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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The CaseyReviewResource class.
 */
@Path("/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface CaseyReviewResource {

    @GET
    @Path("ratings/item/{itemId}")
    Promise<CaseyResults<CaseyAggregateRating>> getRatingByItemId(@PathParam("itemId") String itemId);

    @GET
    @Path("reviews")
    Promise<CaseyResults<CaseyReview>> getReviews(@BeanParam ReviewSearchParams params);

    @POST
    @Path("reviews")
    Promise<CaseyReview> addReview(CaseyReview review);
}
