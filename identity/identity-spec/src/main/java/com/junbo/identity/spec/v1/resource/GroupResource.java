/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.GroupId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Group;
import com.junbo.identity.spec.v1.option.list.GroupListOptions;
import com.junbo.identity.spec.v1.option.model.GroupGetOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by liangfu on 4/3/14.
 */
@Api("groups")
@RestResource
@InProcessCallable
@Path("/groups")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface GroupResource {

    @ApiOperation("Create a group")
    @RouteBy(value = "group.getOrganizationId()", switchable = true)
    @POST
    Promise<Group> create(Group group);

    @ApiOperation("Update a group")
    @RouteBy(value = "group.getOrganizationId()", switchable = true)
    @PUT
    @Path("/{groupId}")
    Promise<Group> put(@PathParam("groupId") GroupId groupId, Group group);

    @POST
    @Path("/{groupId}")
    Promise<Group> patch(@PathParam("groupId") GroupId groupId, Group group);

    @ApiOperation("Get a group")
    @GET
    @Path("/{groupId}")
    Promise<Group> get(@PathParam("groupId") GroupId groupId, @BeanParam GroupGetOptions getOptions);

    @ApiOperation("Search groups")
    @RouteBy(value = "listOptions.getOrganizationId()", switchable = true)
    @GET
    Promise<Results<Group>> list(@BeanParam GroupListOptions listOptions);

    @ApiOperation("Delete a group")
    @DELETE
    @Path("/{groupId}")
    Promise<Response> delete(@PathParam("groupId") GroupId groupId);
}
