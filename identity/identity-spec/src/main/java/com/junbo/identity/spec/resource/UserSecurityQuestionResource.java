/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserSecurityQuestion;
import com.junbo.identity.spec.options.entity.UserSecurityQuestionGetOptions;
import com.junbo.identity.spec.options.list.UserSecurityQuestionListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@Api("userSecurityQuestion")
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

    @ApiOperation("Get one existing user security question")
    @GET
    @Path("/{userSecurityQuestionId}")
    Promise<UserSecurityQuestion> get(@PathParam("userId") UserId userId,
                                  @PathParam("userSecurityQuestionId") UserSecurityQuestionId userSecurityQuestionId,
                                  @BeanParam UserSecurityQuestionGetOptions getOptions);

    @ApiOperation("Search one user's security questions")
    @GET
    @Path("/")
    Promise<Results<UserSecurityQuestion>> list(@PathParam("userId") UserId userId,
                                  @BeanParam UserSecurityQuestionListOptions listOptions);

}
