/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;
import com.junbo.common.filter.annotations.CacheMaxAge;
import com.junbo.common.model.Results;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

/**
 * Offer revisions resource definition.
 */
@Api("offer-revisions")
@Path("offer-revisions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
@InProcessCallable
public interface OfferRevisionResource {
    @CacheMaxAge(duration = 5, unit = TimeUnit.MINUTES)
    @ApiOperation("Get or search offer revisions")
    @GET
    @Path("/")
    Promise<Results<OfferRevision>> getOfferRevisions(@BeanParam OfferRevisionsGetOptions options);

    @CacheMaxAge(duration = 5, unit = TimeUnit.MINUTES)
    @ApiOperation("Get an offer revision")
    @GET
    @Path("/{revisionId}")
    Promise<OfferRevision> getOfferRevision(@PathParam("revisionId") String revisionId,
                                            @BeanParam OfferRevisionGetOptions options);

    @ApiOperation("Create an offer revision")
    @POST
    @Path("/")
    Promise<OfferRevision> createOfferRevision(OfferRevision offerRevision);

    @ApiOperation("Put an offer revision")
    @PUT
    @Path("/{revisionId}")
    Promise<OfferRevision> updateOfferRevision(@PathParam("revisionId") String revisionId,
                                               OfferRevision offerRevision);

    @ApiOperation("Delete an item revision")
    @DELETE
    @Path("/{revisionId}")
    Promise<Response> delete(@PathParam("revisionId") String revisionId);
}
