/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.resource;

import com.junbo.common.id.GroupId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.csr.spec.model.CsrUser;
import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by haomin on 14-7-14.
 */
@Path("/csr-users")
@Produces({MediaType.APPLICATION_JSON})
@RestResource
public interface CsrUserResource {
    @GET
    Promise<Results<CsrUser>> list();

    @POST
    @Path("/join")
    Promise<Response> join(@FormParam("userId")UserId userId, @FormParam("groupId")GroupId groupId);

    @POST
    @Path("/invite")
    Promise<Response> inviteCsr(@FormParam("locale") String locale,
                                @FormParam("email") String email,
                                @FormParam("groupId") GroupId groupId,
                                @Context ContainerRequestContext requestContext);

    @GET
    @Path("/invite")
    @AuthorizationNotRequired
    Promise<Response> confirmCsrInvitation(@QueryParam("code") String code);
}
