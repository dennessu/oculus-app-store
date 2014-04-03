/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosAgreementId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserTosAgreement;
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions;
import com.junbo.identity.spec.v1.option.model.UserTosAgreementGetOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value = "tosAgreements")
@RestResource
@Path("/users/{userId}/tos-agreements")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserTosAgreementResource {
    @ApiOperation("Create one user tos agreement")
    @POST
    @Path("/")
    Promise<UserTosAgreement> create(@PathParam("userId") UserId userId, UserTosAgreement userTosAgreement);

    @ApiOperation("Get one user tos agreement")
    @GET
    @Path("/{tosAgreementId}")
    Promise<UserTosAgreement> get(@PathParam("userId") UserId userId,
                                  @PathParam("tosAgreementId") UserTosAgreementId userTosAgreementId,
                                  UserTosAgreementGetOptions getOptions);

    @ApiOperation("Partial update one user tos agreement")
    @POST
    @Path("/{tosAgreementId}")
    Promise<UserTosAgreement> patch(@PathParam("userId") UserId userId,
                                    @PathParam("tosAgreementId") UserTosAgreementId userTosAgreementId,
                                    UserTosAgreement userTosAgreement);

    @ApiOperation("Update one user tos agreement")
    @PUT
    @Path("/{tosAgreementId}")
    Promise<UserTosAgreement> put(@PathParam("userId") UserId userId,
                                  @PathParam("tosAgreementId") UserTosAgreementId userTosAgreementId,
                                  UserTosAgreement userTosAgreement);

    @ApiOperation("Delete one user tos agreement")
    @DELETE
    @Path("/{tosAgreementId}")
    Promise<Void> delete(@PathParam("userId") UserId userId,
                         @PathParam("tosAgreementId") UserTosAgreementId userTosAgreementId);

    @ApiOperation("Search user tos agreements")
    @GET
    @Path("/")
    Promise<Results<UserTosAgreement>> list(@PathParam("userId") UserId userId,
                                            UserTosAgreementListOptions listOptions);
}
