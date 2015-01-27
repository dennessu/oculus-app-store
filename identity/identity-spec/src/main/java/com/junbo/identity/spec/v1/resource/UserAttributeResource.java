/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserAttributeId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserAttribute;
import com.junbo.identity.spec.v1.option.list.UserAttributeListOptions;
import com.junbo.identity.spec.v1.option.model.UserAttributeGetOptions;
import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by xiali_000 on 2014/12/19.
 */
@Api(value = "user-attributes")
@RestResource
@InProcessCallable
@Path("/user-attributes")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserAttributeResource {
    @ApiOperation("Create an user attribute")
    @POST
    Promise<UserAttribute> create(UserAttribute userAttribute);

    @ApiOperation("Update an user attribute")
    @PUT
    @Path("/{userAttributeId}")
    Promise<UserAttribute> put(
            @PathParam("userAttributeId") UserAttributeId userAttributeId, UserAttribute userAttribute);

    @ApiOperation("Get an user attribute")
    @GET
    @Path("/{userAttributeId}")
    @AuthorizationNotRequired
    Promise<UserAttribute> get(
            @PathParam("userAttributeId") UserAttributeId userAttributeId, @BeanParam UserAttributeGetOptions getOptions);

    @ApiOperation("Search user attribute info")
    @GET
    @AuthorizationNotRequired
    Promise<Results<UserAttribute>> list(@BeanParam UserAttributeListOptions listOptions);

    @ApiOperation("Delete user attribute info")
    @DELETE
    @Path("/{userAttributeId}")
    Promise<Response> delete(@PathParam("userAttributeId") UserAttributeId userAttributeId);
}
