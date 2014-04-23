/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTeleId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserTeleCode;
import com.junbo.identity.spec.v1.option.list.UserTeleListOptions;
import com.junbo.identity.spec.v1.option.model.UserTeleGetOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/22/14.
 */
@Api(value = "users")
@RestResource
@Path("/users/{userId}/tele")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserTeleResource {
    @ApiOperation("Create one user tele resource")
    @POST
    Promise<UserTeleCode> create(@PathParam("userId") UserId userId, UserTeleCode userTeleCode);

    @ApiOperation("Get one user tele resource")
    @GET
    @Path("/{userTeleId}")
    Promise<UserTeleCode> get(@PathParam("userId") UserId userId,
                          @PathParam("userTeleId") UserTeleId userTeleId,
                          @BeanParam UserTeleGetOptions getOptions);

    @POST
    @Path("/{userTeleId}")
    Promise<UserTeleCode> patch(@PathParam("userId") UserId userId,
                            @PathParam("userTeleId") UserTeleId userTeleId,
                            UserTeleCode userTeleCode);

    @ApiOperation("Update one user tele resource")
    @PUT
    @Path("/{userTeleId}")
    Promise<UserTeleCode> put(@PathParam("userId") UserId userId,
                          @PathParam("userTeleId") UserTeleId userTeleId,
                          UserTeleCode userTeleCode);

    @ApiOperation("Delete one user tele resource")
    @DELETE
    @Path("/{userTeleId}")
    Promise<Void> delete(@PathParam("userId") UserId userId,
                         @PathParam("userTeleId") UserTeleId userTeleId);

    @ApiOperation("Search user tele resource")
    @GET
    Promise<Results<UserTeleCode>> list(@PathParam("userId") UserId userId,
                                    @BeanParam UserTeleListOptions listOptions);
}
