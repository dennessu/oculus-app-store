/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Item revisions resource definition.
 */
@Api("item-revisions")
@Path("item-revisions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface ItemRevisionResource {
    @ApiOperation("Get or search item revisions")
    @GET
    @Path("/")
    Promise<Results<ItemRevision>> getItemRevisions(@QueryParam("itemId") ItemId itemId);

    @ApiOperation("Get an item revision")
    @GET
    @Path("/{revisionId}")
    Promise<ItemRevision> getItemRevision(@PathParam("revisionId") ItemRevisionId revisionId);

    @ApiOperation("Create an item revision")
    @POST
    @Path("/")
    Promise<ItemRevision> createItemRevision(ItemRevision offerRevision);

    @ApiOperation("Put an item revision")
    @PUT
    @Path("/{revisionId}")
    Promise<ItemRevision> updateItemRevision(@PathParam("revisionId") ItemRevisionId revisionId,
                                              ItemRevision offerRevision);
}
