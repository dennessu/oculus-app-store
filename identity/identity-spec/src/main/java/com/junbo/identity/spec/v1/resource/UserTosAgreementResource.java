/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserTosAgreementId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserTosAgreement;
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions;
import com.junbo.identity.spec.v1.option.model.UserTosAgreementGetOptions;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value = "users")
@RestResource
@InProcessCallable
@Path("/tos-agreements")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserTosAgreementResource {
    @ApiOperation("Create one user tos agreement")
    @POST
    Promise<UserTosAgreement> create(UserTosAgreement userTosAgreement);

    @ApiOperation("Get one user tos agreement")
    @GET
    @Path("/{tosAgreementId}")
    Promise<UserTosAgreement> get(@PathParam("tosAgreementId") UserTosAgreementId userTosAgreementId,
                                  @BeanParam UserTosAgreementGetOptions getOptions);

    @POST
    @Path("/{tosAgreementId}")
    Promise<UserTosAgreement> patch(@PathParam("tosAgreementId") UserTosAgreementId userTosAgreementId,
                                    UserTosAgreement userTosAgreement);

    @ApiOperation("Update one user tos agreement")
    @PUT
    @Path("/{tosAgreementId}")
    Promise<UserTosAgreement> put(@PathParam("tosAgreementId") UserTosAgreementId userTosAgreementId,
                                  UserTosAgreement userTosAgreement);

    @ApiOperation("Delete one user tos agreement")
    @DELETE
    @Path("/{tosAgreementId}")
    Promise<Void> delete(@PathParam("tosAgreementId") UserTosAgreementId userTosAgreementId);

    @ApiOperation("Search user tos agreements")
    @GET
    Promise<Results<UserTosAgreement>> list(@BeanParam UserTosAgreementListOptions listOptions);
}
