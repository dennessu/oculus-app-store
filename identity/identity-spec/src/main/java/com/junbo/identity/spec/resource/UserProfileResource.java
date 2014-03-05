/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.resource;

import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.user.UserProfile;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Java cod for UserProfileResource.
 */

@Path("/users/{key1}/profiles")
@RestResource
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserProfileResource {
    @POST
    Promise<UserProfile> postUserProfile(@PathParam("key1") Long userId,
            UserProfile userProfile);

    @GET
    Promise<ResultList<UserProfile>> getUserProfiles(@PathParam("key1") Long userId,
                                     @QueryParam("type") String type,
                                     @QueryParam("cursor") Integer cursor,
                                     @QueryParam("count") Integer count);

    @GET
    @Path("/{key2}")
    Promise<UserProfile> getUserProfile(@PathParam("key1") Long userId,
                               @PathParam("key2") Long profileId);

    @PUT
    @Path("/{key2}")
    Promise<UserProfile> updateUserProfile(@PathParam("key1") Long userId,
                                  @PathParam("key2") Long profileId,
                                  UserProfile userProfile);

    @DELETE
    @Path("/{key2}")
    Promise<Void> deleteUserProfile(@PathParam("key1") Long userId,
                           @PathParam("key2") Long profileId);
}
