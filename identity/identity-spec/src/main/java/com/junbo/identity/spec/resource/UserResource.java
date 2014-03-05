/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.resource;

import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.user.User;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Java cod for UserResource.
 */

@Path("users")
@RestResource
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserResource {
    @POST
    Promise<User> postUser(User user);

    @GET
    Promise<ResultList<User>> getUsers(@QueryParam("userName") String userName,
                        @QueryParam("userNamePrefix") String userNamePrefix,
                        @QueryParam("cursor") Integer cursor,
                        @QueryParam("count") Integer count);

    @GET
    @Path("/{key}")
    Promise<User> getUser(@PathParam("key") Long id);

    @PUT
    @Path("/{key}")
    Promise<User> putUser(@PathParam("key") Long id,
                 User user);

    @POST
    @Path("/authenticate-user")
    Promise<User> authenticateUser(@QueryParam("userName") String userName,
                                   @QueryParam("password") String password);

    @POST
    @Path("/{key}/update-password")
    Promise<User> updatePassword(@PathParam("key") Long id,
                        @QueryParam("oldPassword") String oldPassword,
                        @QueryParam("newPassword") String newPassword);

    @POST
    @Path("/{key}/reset-password")
    Promise<User> restPassword(@PathParam("key") Long id,
                      @QueryParam("newPassword") String newPassword);
}
