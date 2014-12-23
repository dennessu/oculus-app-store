/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.resource.external;

import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.external.sewer.casey.CaseyReview;

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
    @POST
    @Path("reviews")
    // This annotation is added since we need to specify self generated access token through the header.
    @AuthorizationNotRequired
    Promise<CaseyReview> addReview(@HeaderParam("Authorization") String authorization, CaseyReview review);

    @PUT
    @Path("reviews/{reviewId}")
    // This annotation is added since we need to specify self generated access token through the header.
    @AuthorizationNotRequired
    Promise<CaseyReview> putReview(@HeaderParam("Authorization") String authorization, @PathParam("reviewId") String reviewId, CaseyReview review);
}
