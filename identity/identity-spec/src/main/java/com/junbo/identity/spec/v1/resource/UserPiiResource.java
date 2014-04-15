/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserPiiId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserPii;
import com.junbo.identity.spec.v1.option.list.UserPiiListOptions;
import com.junbo.identity.spec.v1.option.model.UserPiiGetOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value = "pii")
@RestResource
@Path("/pii")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserPiiResource {
    @ApiOperation("Create user's pii information")
    @POST
    @Path("/")
    Promise<UserPii> create(UserPii userPii);

    @ApiOperation("Get user's pii information")
    @GET
    @Path("/{userPiiId}")
    Promise<UserPii> get(@PathParam("userPiiId") UserPiiId userPiiId,
                         @BeanParam  UserPiiGetOptions getOptions);

    @ApiOperation("Partial update user's pii information")
    @POST
    @Path("/{userPiiId}")
    Promise<UserPii> patch(@PathParam("userPiiId") UserPiiId userPiiId,
                             UserPii userOptin);

    @ApiOperation("Update user's pii information")
    @PUT
    @Path("/{userPiiId}")
    Promise<UserPii> put(@PathParam("userPiiId") UserPiiId userPiiId,
                         UserPii userPii);

    @ApiOperation("Delete user's pii information")
    @DELETE
    @Path("/{userPiiId}")
    Promise<Void> delete(@PathParam("userPiiId") UserPiiId userPiiId);

    @ApiOperation("Se")
    @GET
    @Path("/")
    Promise<Results<UserPii>> list(@BeanParam UserPiiListOptions listOptions);
}
