/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.resource;

import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.model.promotion.PromotionRevisionsGetOptions;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Promotion revisions resource definition.
 */
//@Api("promotion-revisions")
@Path("promotion-revisions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource(sync = true)
public interface PromotionRevisionResource {
    //@ApiOperation("Get or search promotion revisions")
    @GET
    @Path("/")
    Promise<Results<PromotionRevision>> getPromotionRevisions(@BeanParam PromotionRevisionsGetOptions options);

    //@ApiOperation("Get an promotion revision")
    @GET
    @Path("/{revisionId}")
    Promise<PromotionRevision> getPromotionRevision(@PathParam("revisionId") String revisionId);

    //@ApiOperation("Create an promotion revision")
    @POST
    @Path("/")
    Promise<PromotionRevision> createPromotionRevision(PromotionRevision promotionRevision);

    //@ApiOperation("Put an promotion revision")
    @PUT
    @Path("/{revisionId}")
    Promise<PromotionRevision> updatePromotionRevision(@PathParam("revisionId") String revisionId,
                                               PromotionRevision promotionRevision);
}
