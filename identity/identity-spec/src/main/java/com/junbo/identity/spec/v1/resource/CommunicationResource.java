/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.CommunicationId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Communication;
import com.junbo.identity.spec.v1.option.list.CommunicationListOptions;
import com.junbo.identity.spec.v1.option.model.CommunicationGetOptions;
import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by xiali_000 on 4/21/2014.
 */
@Api(value = "communications")
@RestResource
@InProcessCallable
@Path("/communications")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface CommunicationResource {


    @ApiOperation("Create one Communication info")
    @POST
    Promise<Communication> create(Communication communication);

    @ApiOperation("Update one communication info")
    @PUT
    @Path("/{communicationId}")
    Promise<Communication> put(@PathParam("communicationId") CommunicationId communicationId,
                               Communication communication);

    @POST
    @Path("/{communicationId}")
    Promise<Communication> patch(@PathParam("communicationId") CommunicationId communicationId,
                                 Communication communication);

    @ApiOperation("Get one communication info")
    @GET
    @Path("/{communicationId}")
    @AuthorizationNotRequired
    Promise<Communication> get(@PathParam("communicationId") CommunicationId communicationId,
                               @BeanParam CommunicationGetOptions getOptions);

    @ApiOperation("Search communication info")
    @GET
    @AuthorizationNotRequired
    Promise<Results<Communication>> list(@BeanParam CommunicationListOptions listOptions);

    @ApiOperation("Delete communication info")
    @DELETE
    @Path("/{communicationId}")
    Promise<Void> delete(@PathParam("communicationId") CommunicationId communicationId);
}
