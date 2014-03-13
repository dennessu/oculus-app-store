/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource.v1.resource;

import com.junbo.common.id.GroupMembershipId;
import com.junbo.common.id.SecurityQuestionId;
import com.junbo.identity.spec.v1.model.group.GroupMembership;
import com.junbo.identity.spec.v1.model.domainData.SecurityQuestion;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/identity/group/{groupId}/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface GroupMembershipResource {
    @POST
    @Path("/members")
    Promise<GroupMembership> post(GroupMembership groupMembership);

    @PUT
    @Path("/members/{memberId}")
    Promise<SecurityQuestion> put(@PathParam("memberId") GroupMembershipId groupMembershipId,
                                  GroupMembership groupMembership);

    @GET
    @Path("/members")
    Promise<SecurityQuestion> get(@PathParam("memberId") SecurityQuestionId securityQuestionId);
}
