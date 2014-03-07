/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserOptInId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.user.UserOptIn;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Java cod for UserOptInResource.
 */

@Path("/users/{key1}/opt-ins")
@RestResource
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserOptInResource {
    @POST
    Promise<UserOptIn> postUserOptIn(@PathParam("key1") UserId userId,
                            UserOptIn userOptIn);

    @GET
    Promise<ResultList<UserOptIn>> getUserOptIns(@PathParam("key1") UserId userId,
                                 @QueryParam("type") String type,
                                 @QueryParam("cursor") Integer cursor,
                                 @QueryParam("count") Integer count);

    @GET
    @Path("/{key2}")
    Promise<UserOptIn> getUserOptIn(@PathParam("key1") UserId userId,
                           @PathParam("key2") UserOptInId optInId);

    @PUT
    @Path("/{key2}")
    Promise<UserOptIn> updateUserOptIn(@PathParam("key1") UserId userId,
                              @PathParam("key2") UserOptInId optInId,
                              UserOptIn userOptIn);

    @DELETE
    @Path("/{key2}")
    Promise<Void> deleteUserOptIn(@PathParam("key1") UserId userId,
                         @PathParam("key2") UserOptInId optInId);
}
