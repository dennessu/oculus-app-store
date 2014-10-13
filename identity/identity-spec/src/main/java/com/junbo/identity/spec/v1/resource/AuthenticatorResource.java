/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserAuthenticatorId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserAuthenticator;
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions;
import com.junbo.identity.spec.v1.option.model.AuthenticatorGetOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.junbo.langur.core.routing.RouteByAccessToken;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value= "authenticators")
@RestResource
@InProcessCallable
@Path("/authenticators")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface AuthenticatorResource {

    @ApiOperation("Create an user authenticator")
    @RouteBy(value = "userAuthenticator.getUserId()", switchable = true)
    @POST
    Promise<UserAuthenticator> create(UserAuthenticator userAuthenticator);

    @ApiOperation("Update an existing user authenticator")
    @RouteBy(value = "userAuthenticator.getUserId()", switchable = true)
    @PUT
    @Path("/{authenticatorId}")
    Promise<UserAuthenticator> put(@PathParam("authenticatorId") UserAuthenticatorId userAuthenticatorId,
                                   UserAuthenticator userAuthenticator);

    @RouteByAccessToken(switchable = true)
    @POST
    @Path("/{authenticatorId}")
    Promise<UserAuthenticator> patch(@PathParam("authenticatorId") UserAuthenticatorId userAuthenticatorId,
                                       UserAuthenticator userAuthenticator);

    @ApiOperation("Get an existing user authenticator")
    @RouteByAccessToken(switchable = true)
    @GET
    @Path("/{authenticatorId}")
    Promise<UserAuthenticator> get(@PathParam("authenticatorId") UserAuthenticatorId userAuthenticatorId,
                                   @BeanParam AuthenticatorGetOptions getOptions);

    @ApiOperation("Search user authenticator")
    @RouteByAccessToken(switchable = true)
    @GET
    Promise<Results<UserAuthenticator>> list(@BeanParam AuthenticatorListOptions listOptions);

    @ApiOperation("Delete user authenticator")
    @RouteByAccessToken(switchable = true)
    @DELETE
    @Path("/{authenticatorId}")
    Promise<Response> delete(@PathParam("authenticatorId") UserAuthenticatorId userAuthenticatorId);
}

