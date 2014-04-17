/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.GroupId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Group;
import com.junbo.identity.spec.v1.option.model.GroupGetOptions;
import com.junbo.identity.spec.v1.option.list.GroupListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api("groups")
@RestResource
@Path("/groups")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface GroupResource {

    @ApiOperation("Create a group")
    @POST
    Promise<Group> create(Group group);

    @ApiOperation("Update a group")
    @PUT
    @Path("/{groupId}")
    Promise<Group> put(@PathParam("groupId") GroupId groupId, Group group);

    @ApiOperation("Partial update a group")
    @POST
    @Path("/{groupId}")
    Promise<Group> patch(@PathParam("groupId") GroupId groupId, Group group);

    @ApiOperation("Get a group")
    @GET
    @Path("/{groupId}")
    Promise<Group> get(@PathParam("groupId") GroupId groupId, @BeanParam GroupGetOptions getOptions);

    @ApiOperation("Search groups")
    @GET
    Promise<Results<Group>> list(@BeanParam GroupListOptions listOptions);
}
