/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserDeviceProfileId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.user.UserDeviceProfile;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Java cod for UserDeviceProfileResource.
 */

@Path("/users/{key1}/device-profiles")
@RestResource
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserDeviceProfileResource {
    @POST
    Promise<UserDeviceProfile> postUserDeviceProfile(@PathParam("key1") UserId userId,
                                UserDeviceProfile userProfile);

    @GET
    Promise<ResultList<UserDeviceProfile>> getUserDeviceProfiles(@PathParam("key1") UserId userId,
                                                  @QueryParam("type") String type,
                                                  @QueryParam("cursor") Integer cursor,
                                                  @QueryParam("count") Integer count);

    @GET
    @Path("/{key2}")
    Promise<UserDeviceProfile> getUserDeviceProfile(@PathParam("key1") UserId userId,
                                           @PathParam("key2") UserDeviceProfileId deviceProfileId);

    @PUT
    @Path("/{key2}")
    Promise<UserDeviceProfile> updateUserDeviceProfile(@PathParam("key1") UserId userId,
                                              @PathParam("key2") UserDeviceProfileId deviceProfileId,
                                              UserDeviceProfile userDeviceProfile);

    @DELETE
    @Path("/{key2}")
    Promise<Void> deleteUserDeviceProfile(@PathParam("key1") UserId userId,
                                 @PathParam("key2") UserDeviceProfileId deviceProfileId);
}
