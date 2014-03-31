/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.GroupId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.Group;
import com.junbo.identity.spec.model.users.UserGroup;
import com.junbo.identity.spec.options.entity.GroupGetOptions;
import com.junbo.identity.spec.options.list.GroupListOptions;
import com.junbo.identity.spec.options.list.UserGroupListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@Api(value= "groups")
@RestResource
@Path("/groups")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface GroupResource {

    @ApiOperation("Create a new Group")
    @POST
    @Path("/")
    Promise<Group> create(Group group);

    @ApiOperation("Update an existing Group")
    @PUT
    @Path("/{groupId}")
    Promise<Group> put(@PathParam("groupId") GroupId groupId, Group group);

    @ApiOperation("Patch update an existing Group")
    @POST
    @Path("/{groupId}")
    Promise<Group> patch(@PathParam("groupId") GroupId groupId, Group group);

    @ApiOperation("Get a group")
    @GET
    @Path("/{groupId}")
    Promise<Group> get(@PathParam("groupId") GroupId groupId, @BeanParam GroupGetOptions getOptions);

    @ApiOperation("Search groups")
    @GET
    @Path("/")
    Promise<Results<Group>> list(@BeanParam GroupListOptions listOptions);

    @ApiOperation("Search users in one group")
    @GET
    @Path("/{groupId}/users")
    Promise<Results<UserGroup>> listUserGroups(@PathParam("groupId") GroupId groupId,
                                                  @BeanParam UserGroupListOptions listOptions);
}
