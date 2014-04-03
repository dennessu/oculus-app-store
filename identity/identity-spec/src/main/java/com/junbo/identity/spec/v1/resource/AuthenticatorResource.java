/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserAuthenticatorId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserAuthenticator;
import com.junbo.identity.spec.v1.option.model.AuthenticatorGetOptions;
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value= "authenticators")
@RestResource
@Path("/authenticators")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface AuthenticatorResource {

    @ApiOperation("Create an user authenticator")
    @POST
    @Path("/")
    Promise<UserAuthenticator> create(UserAuthenticator userAuthenticator);

    @ApiOperation("Update an existing user authenticator")
    @PUT
    @Path("/{authenticatorId}")
    Promise<UserAuthenticator> put(@PathParam("authenticatorId") UserAuthenticatorId userAuthenticatorId,
                                   UserAuthenticator userAuthenticator);

    @ApiOperation("Partial update an existing user authenticator")
    @POST
    @Path("/{authenticatorId}")
    Promise<UserAuthenticator> patch(@PathParam("authenticatorId") UserAuthenticatorId userAuthenticatorId,
                                       UserAuthenticator userAuthenticator);

    @ApiOperation("Get an existing user authenticator")
    @GET
    @Path("/{authenticatorId}")
    Promise<UserAuthenticator> get(@PathParam("authenticatorId") UserAuthenticatorId userAuthenticatorId,
                                   @BeanParam AuthenticatorGetOptions getOptions);

    @ApiOperation("Search user authenticator")
    @GET
    @Path("/")
    Promise<Results<UserAuthenticator>> list(@BeanParam AuthenticatorListOptions listOptions);
}

