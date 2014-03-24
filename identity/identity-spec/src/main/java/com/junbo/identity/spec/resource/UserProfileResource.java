/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserProfileId;
import com.junbo.common.model.Results;
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
    Promise<UserProfile> postUserProfile(@PathParam("key1") UserId userId,
            UserProfile userProfile);

    @GET
    Promise<Results<UserProfile>> getUserProfiles(@PathParam("key1") UserId userId,
                                     @QueryParam("type") String type,
                                     @QueryParam("cursor") Integer cursor,
                                     @QueryParam("count") Integer count);

    @GET
    @Path("/{key2}")
    Promise<UserProfile> getUserProfile(@PathParam("key1") UserId userId,
                               @PathParam("key2") UserProfileId profileId);

    @PUT
    @Path("/{key2}")
    Promise<UserProfile> updateUserProfile(@PathParam("key1") UserId userId,
                                  @PathParam("key2") UserProfileId profileId,
                                  UserProfile userProfile);

    @DELETE
    @Path("/{key2}")
    Promise<Void> deleteUserProfile(@PathParam("key1") UserId userId,
                           @PathParam("key2") UserProfileId profileId);
}
