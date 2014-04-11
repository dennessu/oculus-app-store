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
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value = "securityQuestions")
@RestResource
@Path("/users/{userId}/security-questions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserSecurityQuestionResource {
    @ApiOperation("Create one user security question")
    @POST
    @Path("/")
    Promise<UserSecurityQuestion> create(@PathParam("userId") UserId userId,
                                         UserSecurityQuestion userSecurityQuestion);

    @ApiOperation("Get one user security question")
    @GET
    @Path("/{userSecurityQuestionId}")
    Promise<UserSecurityQuestion> get(@PathParam("userId") UserId userId,
            @PathParam("userSecurityQuestionId") UserSecurityQuestionId userSecurityQuestionId,
            @BeanParam UserSecurityQuestionGetOptions getOptions);

    @ApiOperation("Partial update one user security question")
    @POST
    @Path("/{userSecurityQuestionId}")
    Promise<UserSecurityQuestion> patch(@PathParam("userId") UserId userId,
            @PathParam("userSecurityQuestionId") UserSecurityQuestionId userSecurityQuestionId,
            UserSecurityQuestion userSecurityQuestion);

    @ApiOperation("Update one user security question")
    @PUT
    @Path("/{userSecurityQuestionId}")
    Promise<UserSecurityQuestion> put(@PathParam("userId") UserId userId,
            @PathParam("userSecurityQuestionId") UserSecurityQuestionId userSecurityQuestionId,
            UserSecurityQuestion userSecurityQuestion);

    @ApiOperation("Delete one user security question")
    @DELETE
    @Path("/{userSecurityQuestionId}")
    Promise<Void> delete(@PathParam("userId") UserId userId,
            @PathParam("userSecurityQuestionId") UserSecurityQuestionId userSecurityQuestionId);

    @ApiOperation("Search user security questions")
    @GET
    @Path("/")
    Promise<Results<UserSecurityQuestion>> list(@PathParam("userId") UserId userId,
            @BeanParam UserSecurityQuestionListOptions listOptions);
}
