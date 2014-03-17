/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.domaindata.SecurityQuestion;
import com.junbo.identity.spec.model.options.DomainDataGetOption;
import com.junbo.identity.spec.options.SecurityQuestionGetOptions;
import com.junbo.identity.spec.options.SecurityQuestionListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/security-questions")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface SecurityQuestionResource {

    @POST
    @Path("/")
    Promise<SecurityQuestion> create(SecurityQuestion securityQuestion);

    @PUT
    @Path("/{securityQuestionId}")
    Promise<SecurityQuestion> put(@PathParam("securityQuestionId") SecurityQuestionId securityQuestionId,
                                     SecurityQuestion securityQuestion);

    @POST
    @Path("/{securityQuestionId}")
    Promise<SecurityQuestion> patch(@PathParam("securityQuestionId") SecurityQuestionId securityQuestionId,
                                    SecurityQuestion securityQuestion);

    @GET
    @Path("/{securityQuestionId}")
    Promise<SecurityQuestion> get(@PathParam("securityQuestionId") SecurityQuestionId securityQuestionId,
                                  @BeanParam SecurityQuestionGetOptions getOptions);

    @GET
    @Path("/")
    Promise<ResultList<SecurityQuestion>> list(@BeanParam SecurityQuestionListOptions listOptions);
}
