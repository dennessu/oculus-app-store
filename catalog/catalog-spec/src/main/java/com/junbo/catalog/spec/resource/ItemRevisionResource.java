/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionGetOptions;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import com.junbo.common.filter.annotations.CacheMaxAge;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

/**
 * Item revisions resource definition.
 */
@Api("item-revisions")
@Path("item-revisions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface ItemRevisionResource {
    @CacheMaxAge(duration = 5, unit = TimeUnit.MINUTES)
    @ApiOperation("Get or search item revisions")
    @GET
    @Path("/")
    Promise<Results<ItemRevision>> getItemRevisions(@BeanParam ItemRevisionsGetOptions options);

    @CacheMaxAge(duration = 5, unit = TimeUnit.MINUTES)
    @ApiOperation("Get an item revision")
    @GET
    @Path("/{revisionId}")
    Promise<ItemRevision> getItemRevision(@PathParam("revisionId") String revisionId, @BeanParam ItemRevisionGetOptions options);

    @ApiOperation("Create an item revision")
    @POST
    @Path("/")
    Promise<ItemRevision> createItemRevision(ItemRevision offerRevision);

    @ApiOperation("Put an item revision")
    @PUT
    @Path("/{revisionId}")
    Promise<ItemRevision> updateItemRevision(@PathParam("revisionId") String revisionId,
                                              ItemRevision offerRevision);

    @ApiOperation("Delete an item revision")
    @DELETE
    @Path("/{revisionId}")
    Promise<Response> delete(@PathParam("revisionId") String revisionId);
}
