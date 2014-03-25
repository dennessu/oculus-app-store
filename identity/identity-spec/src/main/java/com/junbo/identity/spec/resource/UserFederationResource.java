/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserFederationId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.user.UserFederation;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Java cod for UserOptInResource.
 */

@Path("/users/{key1}/federations")
@RestResource
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserFederationResource {
    @POST
    Promise<UserFederation> postUserFederation(@PathParam("key1") UserId userId,
                                      UserFederation userFederation);

    @GET
    Promise<Results<UserFederation>> getUserFederations(@PathParam("key1") UserId userId,
                                            @QueryParam("type") String type,
                                            @QueryParam("cursor") Integer cursor,
                                            @QueryParam("count") Integer count);

    @GET
    @Path("/{key2}")
    Promise<UserFederation> getUserFederation(@PathParam("key1") UserId userId,
                                     @PathParam("key2") UserFederationId federationId);

    @PUT
    @Path("/{key2}")
    Promise<UserFederation> updateUserFederation(@PathParam("key1") UserId userId,
                                        @PathParam("key2") UserFederationId federationId,
                                        UserFederation userFederation);

    @DELETE
    @Path("/{key2}")
    Promise<Void> deleteUserFederation(@PathParam("key1") UserId userId,
                              @PathParam("key2") UserFederationId federationId);
}
