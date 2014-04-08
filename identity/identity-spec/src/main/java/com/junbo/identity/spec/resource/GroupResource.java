/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.GroupId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserGroup;
import com.junbo.identity.spec.options.entity.GroupGetOptions;
import com.junbo.identity.spec.options.list.GroupListOptions;
import com.junbo.identity.spec.options.list.UserGroupListOptions;
import com.junbo.identity.spec.v1.model.Group;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/groups")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface GroupResource {

    @POST
    @Path("/")
    Promise<Group> create(Group group);

    @PUT
    @Path("/{groupId}")
    Promise<Group> put(@PathParam("groupId") GroupId groupId, Group group);

    @POST
    @Path("/{groupId}")
    Promise<Group> patch(@PathParam("groupId") GroupId groupId, Group group);

    @GET
    @Path("/{groupId}")
    Promise<Group> get(@PathParam("groupId") GroupId groupId, @BeanParam GroupGetOptions getOptions);

    @GET
    @Path("/")
    Promise<Results<Group>> list(@BeanParam GroupListOptions listOptions);

    @GET
    @Path("/{groupId}/users")
    Promise<Results<UserGroup>> listUserGroups(@PathParam("groupId") GroupId groupId,
                                                  @BeanParam UserGroupListOptions listOptions);
}
