/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions;
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value = "userPersonalInfo")
@RestResource
@Path("/personal-info")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserPersonalInfoResource {
    @ApiOperation("Create user's personalInfo information")
    @POST
    Promise<UserPersonalInfo> create(UserPersonalInfo userPii);

    @ApiOperation("Get user's personalInfo information")
    @GET
    @Path("/{userPiiId}")
    Promise<UserPersonalInfo> get(@PathParam("userPiiId") UserPersonalInfoId userPiiId,
                         @BeanParam  UserPersonalInfoGetOptions getOptions);

    @POST
    @Path("/{userPiiId}")
    Promise<UserPersonalInfo> patch(@PathParam("userPiiId") UserPersonalInfoId userPiiId,
                             UserPersonalInfo userPersonalInfo);

    @ApiOperation("Update user's personalInfo information")
    @PUT
    @Path("/{userPiiId}")
    Promise<UserPersonalInfo> put(@PathParam("userPiiId") UserPersonalInfoId userPiiId,
                         UserPersonalInfo userPersonalInfo);

    @ApiOperation("Delete user's personalInfo information")
    @DELETE
    @Path("/{userPiiId}")
    Promise<Void> delete(@PathParam("userPiiId") UserPersonalInfoId userPiiId);

    @ApiOperation("Search user person information.")
    @GET
    Promise<Results<UserPersonalInfo>> list(@BeanParam UserPersonalInfoListOptions listOptions);
}
