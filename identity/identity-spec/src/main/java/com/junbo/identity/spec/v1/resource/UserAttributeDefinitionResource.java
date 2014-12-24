/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserAttributeDefinitionId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserAttributeDefinition;
import com.junbo.identity.spec.v1.option.list.UserAttributeDefinitionListOptions;
import com.junbo.identity.spec.v1.option.model.UserAttributeDefinitionGetOptions;
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
@Api(value = "user-attribute-definitions")
@RestResource
@InProcessCallable
@Path("/user-attribute-definitions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserAttributeDefinitionResource {
    @ApiOperation("Create an user attribute definition")
    @POST
    Promise<UserAttributeDefinition> create(UserAttributeDefinition userAttributeDefinition);

    @ApiOperation("Update an user attribute definition")
    @PUT
    @Path("/{userAttributeDefinitionId}")
    Promise<UserAttributeDefinition> put(
            @PathParam("userAttributeDefinitionId") UserAttributeDefinitionId userAttributeDefinitionId,
            UserAttributeDefinition userAttributeDefinition);

    @ApiOperation("Get an user attribute definition")
    @GET
    @AuthorizationNotRequired
    @Path("/{userAttributeDefinitionId}")
    Promise<UserAttributeDefinition> get(
            @PathParam("userAttributeDefinitionId") UserAttributeDefinitionId userAttributeDefinitionId,
            @BeanParam UserAttributeDefinitionGetOptions getOptions);

    @ApiOperation("Search user attribute definition info")
    @GET
    @AuthorizationNotRequired
    Promise<Results<UserAttributeDefinition>> list(@BeanParam UserAttributeDefinitionListOptions listOptions);

    @ApiOperation("Delete user attribute definition info")
    @DELETE
    @Path("/{userAttributeDefinitionId}")
    Promise<Response> delete(@PathParam("userAttributeDefinitionId") UserAttributeDefinitionId userAttributeDefinitionId);
}
