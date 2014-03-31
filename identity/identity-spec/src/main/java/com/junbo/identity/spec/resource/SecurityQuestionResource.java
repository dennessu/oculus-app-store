/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.options.entity.SecurityQuestionGetOptions;
import com.junbo.identity.spec.options.list.SecurityQuestionListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@Api("security-questions")
@RestResource
@Path("/security-questions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface SecurityQuestionResource {
    @ApiOperation("Create a new security question")
    @POST
    @Path("/")
    Promise<SecurityQuestion> create(SecurityQuestion securityQuestion);

    @ApiOperation("Update an existing security question")
    @PUT
    @Path("/{securityQuestionId}")
    Promise<SecurityQuestion> put(@PathParam("securityQuestionId") SecurityQuestionId securityQuestionId,
                                     SecurityQuestion securityQuestion);

    @ApiOperation("Patch update an existing security question")
    @POST
    @Path("/{securityQuestionId}")
    Promise<SecurityQuestion> patch(@PathParam("securityQuestionId") SecurityQuestionId securityQuestionId,
                                    SecurityQuestion securityQuestion);

    @ApiOperation("Get a security question")
    @GET
    @Path("/{securityQuestionId}")
    Promise<SecurityQuestion> get(@PathParam("securityQuestionId") SecurityQuestionId securityQuestionId,
                                  @BeanParam SecurityQuestionGetOptions getOptions);

    @ApiOperation("Search security questions")
    @GET
    @Path("/")
    Promise<Results<SecurityQuestion>> list(@BeanParam SecurityQuestionListOptions listOptions);
}
