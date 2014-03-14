/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserSecurityQuestionId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.v1.model.options.UserSecurityQuestionGetOption;
import com.junbo.identity.spec.v1.model.users.UserSecurityQuestion;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/users/{userId}/security-questions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserSecurityQuestionResource {
    @POST
    @Path("/")
    Promise<UserSecurityQuestion> create(@PathParam("userId")UserId userId,
                                       UserSecurityQuestion userSecurityQuestion);

    @PUT
    @Path("/{userSecurityQuestionId}")
    Promise<UserSecurityQuestion> update(@PathParam("userId")UserId userId,
                                     @PathParam("userSecurityQuestionId")UserSecurityQuestionId userSecurityQuestionId,
                                     UserSecurityQuestion userSecurityQuestion);

    @POST
    @Path("/{userSecurityQuestionId}")
    Promise<UserSecurityQuestion> patch(@PathParam("userId")UserId userId,
                                    @PathParam("userSecurityQuestionId")UserSecurityQuestionId userSecurityQuestionId,
                                    UserSecurityQuestion userSecurityQuestion);

    @GET
    @Path("/{userSecurityQuestionId}")
    Promise<UserSecurityQuestion> get(@PathParam("userId")UserId userId,
                                  @PathParam("userSecurityQuestionId")UserSecurityQuestionId userSecurityQuestionId);

    @DELETE
    @Path("/{userSecurityQuestionId}")
    Promise<UserSecurityQuestion> delete(@PathParam("userId")UserId userId,
                                  @PathParam("userSecurityQuestionId")UserSecurityQuestionId userSecurityQuestionId);

    @GET
    @Path("/")
    Promise<ResultList<UserSecurityQuestion>> get(@PathParam("userId")UserId userId,
                                             @BeanParam UserSecurityQuestionGetOption getOption);
}
