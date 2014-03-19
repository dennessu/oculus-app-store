/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.user.User;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Java cod for UserResource.
 */

@Api(value= "users")
@Path("/users")
@RestResource
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserResource {
    @ApiOperation("Create a new user")
    @POST
    Promise<User> postUser(User user);

    @ApiOperation("Get or search users")
    @GET
    Promise<ResultList<User>> getUsers(@QueryParam("userName") String userName,
                        @QueryParam("userNamePrefix") String userNamePrefix,
                        @QueryParam("cursor") Integer cursor,
                        @QueryParam("count") Integer count);

    @ApiOperation("Get a user")
    @GET
    @Path("/{key}")
    Promise<User> getUser(@PathParam("key") UserId id);

    @ApiOperation("Put a user")
    @PUT
    @Path("/{key}")
    Promise<User> putUser(@PathParam("key") UserId id,
                 User user);

    @ApiOperation("Authenticate user")
    @POST
    @Path("/authenticate-user")
    Promise<User> authenticateUser(@QueryParam("userName") String userName,
                                   @QueryParam("password") String password);

    @ApiOperation("Update password")
    @POST
    @Path("/{key}/update-password")
    Promise<User> updatePassword(@PathParam("key") UserId id,
                        @QueryParam("oldPassword") String oldPassword,
                        @QueryParam("newPassword") String newPassword);

    @ApiOperation("Reset password")
    @POST
    @Path("/{key}/reset-password")
    Promise<User> restPassword(@PathParam("key") UserId id,
                      @QueryParam("newPassword") String newPassword);
}
