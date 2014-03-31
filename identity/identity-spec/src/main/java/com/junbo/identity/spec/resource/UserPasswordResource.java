/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPasswordId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserPassword;
import com.junbo.identity.spec.options.entity.UserPasswordGetOptions;
import com.junbo.identity.spec.options.list.UserPasswordListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@Api("users")
@RestResource
@Path("/users/{userId}/passwords")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserPasswordResource {
    @ApiOperation("Create one user password")
    @POST
    @Path("/")
    Promise<UserPassword> create(@PathParam("userId") UserId userId,
                                 UserPassword userPassword);

    @ApiOperation("Get one user password information(no user password return)")
    @GET
    @Path("/{userPasswordId}")
    Promise<UserPassword> get(@PathParam("userId") UserId userId,
                              @PathParam("userPasswordId") UserPasswordId userPasswordId,
                              @BeanParam UserPasswordGetOptions getOptions);

    @ApiOperation("Search one user's password history information")
    @GET
    @Path("/")
    Promise<Results<UserPassword>> list(@PathParam("userId") UserId userId,
                                           @BeanParam UserPasswordListOptions listOptions);

}
