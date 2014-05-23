/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserCommunicationId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserCommunication;
import com.junbo.identity.spec.v1.option.list.UserOptinListOptions;
import com.junbo.identity.spec.v1.option.model.UserOptinGetOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value = "opt-ins")
@RestResource
@InProcessCallable
@Path("/opt-ins")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserCommunicationResource {
    @ApiOperation("Create one user optin")
    @POST
    Promise<UserCommunication> create(UserCommunication userOptin);

    @ApiOperation("Get one user optin")
    @GET
    @Path("/{userOptinId}")
    Promise<UserCommunication> get(@PathParam("userOptinId") UserCommunicationId userOptinId,
                           @BeanParam UserOptinGetOptions getOptions);

    @POST
    @Path("/{userOptinId}")
    Promise<UserCommunication> patch(@PathParam("userOptinId") UserCommunicationId userOptinId,
                              UserCommunication userOptin);

    @ApiOperation("Update one user optin")
    @PUT
    @Path("/{userOptinId}")
    Promise<UserCommunication> put(@PathParam("userOptinId") UserCommunicationId userOptinId,
                            UserCommunication userOptin);

    @ApiOperation("Delete one user optin")
    @DELETE
    @Path("/{userOptinId}")
    Promise<Void> delete(@PathParam("userOptinId") UserCommunicationId userOptinId);

    @ApiOperation("Search user optins")
    @GET
    Promise<Results<UserCommunication>> list(@BeanParam UserOptinListOptions listOptions);
}
