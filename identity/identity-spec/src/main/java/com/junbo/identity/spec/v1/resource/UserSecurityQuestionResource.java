/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserSecurityQuestion;
import com.junbo.identity.spec.v1.option.list.UserSecurityQuestionListOptions;
import com.junbo.identity.spec.v1.option.model.UserSecurityQuestionGetOptions;
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
@Api(value = "users")
@RestResource
@InProcessCallable
@Path("/users/{userId}/security-questions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserSecurityQuestionResource {
    @ApiOperation("Create one user security question")
    @RouteBy(value = "userId", switchable = true)
    @POST
    Promise<UserSecurityQuestion> create(@PathParam("userId") UserId userId,
                                         UserSecurityQuestion userSecurityQuestion);

    @ApiOperation("Get one user security question")
    @RouteBy(value = "userId", switchable = true)
    @GET
    @Path("/{userSecurityQuestionId}")
    Promise<UserSecurityQuestion> get(@PathParam("userId") UserId userId,
            @PathParam("userSecurityQuestionId") UserSecurityQuestionId userSecurityQuestionId,
            @BeanParam UserSecurityQuestionGetOptions getOptions);

    @RouteBy(value = "userId", switchable = true)
    @POST
    @Path("/{userSecurityQuestionId}")
    Promise<UserSecurityQuestion> patch(@PathParam("userId") UserId userId,
            @PathParam("userSecurityQuestionId") UserSecurityQuestionId userSecurityQuestionId,
            UserSecurityQuestion userSecurityQuestion);

    @ApiOperation("Update one user security question")
    @RouteBy(value = "userId", switchable = true)
    @PUT
    @Path("/{userSecurityQuestionId}")
    Promise<UserSecurityQuestion> put(@PathParam("userId") UserId userId,
            @PathParam("userSecurityQuestionId") UserSecurityQuestionId userSecurityQuestionId,
            UserSecurityQuestion userSecurityQuestion);

    @ApiOperation("Delete one user security question")
    @RouteBy(value = "userId", switchable = true)
    @DELETE
    @Path("/{userSecurityQuestionId}")
    Promise<Response> delete(@PathParam("userId") UserId userId,
            @PathParam("userSecurityQuestionId") UserSecurityQuestionId userSecurityQuestionId);

    @ApiOperation("Search user security questions")
    @RouteBy(value = "userId", switchable = true)
    @GET
    Promise<Results<UserSecurityQuestion>> list(@PathParam("userId") UserId userId,
            @BeanParam UserSecurityQuestionListOptions listOptions);
}
