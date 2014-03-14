/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.user.UserTosAcceptance;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Java cod for UserTosAcceptanceResource.
 */

@Path("/users/{key1}/tos-acceptances")
@RestResource
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserTosAcceptanceResource {
    @POST
    Promise<UserTosAcceptance> postUserTosAcceptance(@PathParam("key1") UserId userId,
                                            UserTosAcceptance userTosAcceptance);

    @GET
    Promise<ResultList<UserTosAcceptance>> getUserTosAcceptances(@PathParam("key1") UserId userId,
                                                  @QueryParam("tos") String tos,
                                                  @QueryParam("cursor") Integer cursor,
                                                  @QueryParam("count") Integer count);

    @GET
    @Path("/{key2}")
    Promise<UserTosAcceptance> getUserTosAcceptance(@PathParam("key1") UserId userId,
                                           @PathParam("key2") UserTosId tosAcceptanceId);

    @PUT
    @Path("/{key2}")
    Promise<UserTosAcceptance> updateUserTosAcceptance(@PathParam("key1") UserId userId,
                                              @PathParam("key2") UserTosId tosAcceptanceId,
                                              UserTosAcceptance userTosAcceptance);

    @DELETE
    @Path("/{key2}")
    Promise<Void> deleteUserTosAcceptance(@PathParam("key1") UserId userId,
                                 @PathParam("key2") UserTosId tosAcceptanceId);
}
