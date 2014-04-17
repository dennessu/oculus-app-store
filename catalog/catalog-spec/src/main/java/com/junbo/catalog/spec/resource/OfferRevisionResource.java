/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;
import com.junbo.common.id.OfferRevisionId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Offer revisions resource definition.
 */
@Api("offer-revisions")
@Path("offer-revisions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface OfferRevisionResource {
    @ApiOperation("Get or search offer revisions")
    @GET
    @Path("/")
    Promise<Results<OfferRevision>> getOfferRevisions(@BeanParam OfferRevisionsGetOptions options);

    @ApiOperation("Get an offer revision")
    @GET
    @Path("/{revisionId}")
    Promise<OfferRevision> getOfferRevision(@PathParam("revisionId") OfferRevisionId revisionId);

    @ApiOperation("Create an offer revision")
    @POST
    @Path("/")
    Promise<OfferRevision> createOfferRevision(OfferRevision offerRevision);

    @ApiOperation("Put an offer revision")
    @PUT
    @Path("/{revisionId}")
    Promise<OfferRevision> updateOfferRevision(@PathParam("revisionId") OfferRevisionId revisionId,
                                               OfferRevision offerRevision);

    @ApiOperation("Delete an item revision")
    @DELETE
    @Path("/{revisionId}")
    Promise<Response> delete(@PathParam("revisionId") OfferRevisionId revisionId);
}
