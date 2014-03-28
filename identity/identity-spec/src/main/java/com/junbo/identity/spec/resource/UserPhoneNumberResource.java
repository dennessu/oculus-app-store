/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPhoneNumberId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserPhoneNumber;
import com.junbo.identity.spec.options.entity.UserPhoneNumberGetOptions;
import com.junbo.identity.spec.options.list.UserPhoneNumberListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@Api("userPhoneNumber")
@RestResource
@Path("/users/{userId}/phone-numbers")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserPhoneNumberResource {

    @ApiOperation("Create one user phone number")
    @POST
    @Path("/")
    Promise<UserPhoneNumber> create(@PathParam("userId") UserId userId,
                                    UserPhoneNumber userPhoneNumber);

    @ApiOperation("Update one existing user phone number")
    @PUT
    @Path("/{userPhoneNumberId}")
    Promise<UserPhoneNumber> put(@PathParam("userId") UserId userId,
                                 @PathParam("userPhoneNumberId") UserPhoneNumberId userPhoneNumberId,
                                 UserPhoneNumber userPhoneNumber);

    @ApiOperation("Patch update one existing user phone number")
    @POST
    @Path("/{userPhoneNumberId}")
    Promise<UserPhoneNumber> patch(@PathParam("userId") UserId userId,
                                   @PathParam("userPhoneNumberId") UserPhoneNumberId userPhoneNumberId,
                                   UserPhoneNumber userPhoneNumber);

    @ApiOperation("Delete one existing user phone number")
    @DELETE
    @Path("/{userPhoneNumberId}")
    Promise<UserPhoneNumber> delete(@PathParam("userId") UserId userId,
                                    @PathParam("userPhoneNumberId") UserPhoneNumberId userPhoneNumberId);

    @ApiOperation("Get one existing user phone number")
    @GET
    @Path("/{userPhoneNumberId}")
    Promise<UserPhoneNumber> get(@PathParam("userId") UserId userId,
                                 @PathParam("userPhoneNumberId") UserPhoneNumberId userPhoneNumberId,
                                 @BeanParam UserPhoneNumberGetOptions getOptions);

    @ApiOperation("Search one user's phone numbers")
    @GET
    @Path("/")
    Promise<Results<UserPhoneNumber>> list(@PathParam("userId") UserId userId,
                                              @BeanParam UserPhoneNumberListOptions listOptions);
}
