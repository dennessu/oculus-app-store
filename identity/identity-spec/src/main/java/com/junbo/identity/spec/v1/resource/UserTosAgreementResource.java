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
import com.junbo.langur.core.routing.RouteBy;
import com.junbo.langur.core.routing.RouteByAccessToken;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value = "tos-agreements")
@RestResource
@InProcessCallable
@Path("/tos-agreements")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserTosAgreementResource {
    @ApiOperation("Create one user tos agreement")
    @RouteBy(value = "userTosAgreement.getUserId()", switchable = true)
    @POST
    Promise<UserTosAgreement> create(UserTosAgreement userTosAgreement);

    @ApiOperation("Get one user tos agreement")
    @RouteByAccessToken(switchable = true)
    @GET
    @Path("/{tosAgreementId}")
    Promise<UserTosAgreement> get(@PathParam("tosAgreementId") UserTosAgreementId userTosAgreementId,
                                  @BeanParam UserTosAgreementGetOptions getOptions);

    @ApiOperation("Search user tos agreements")
    @RouteByAccessToken(switchable = true)
    @GET
    Promise<Results<UserTosAgreement>> list(@BeanParam UserTosAgreementListOptions listOptions);
}
