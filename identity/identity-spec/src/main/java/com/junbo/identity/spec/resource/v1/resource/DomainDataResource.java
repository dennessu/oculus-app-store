/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource.v1.resource;

import com.junbo.common.id.SecurityQuestionId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.v1.model.domainData.SecurityQuestion;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/identity/domain-data")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface DomainDataResource {
    @POST
    @Path("/securityQuestions")
    Promise<SecurityQuestion> post(SecurityQuestion securityQuestion);

    @PUT
    @Path("/securityQuestions/{securityQuestionId}")
    Promise<SecurityQuestion> put(@PathParam("securityQuestionId") SecurityQuestionId securityQuestionId,
                                  SecurityQuestion securityQuestion);

    @GET
    @Path("/securityQuestions/{securityQuestionId}")
    Promise<SecurityQuestion> get(@PathParam("securityQuestionId") SecurityQuestionId securityQuestionId);

    // This is list operation
    @GET
    @Path("/securityQuestions")
    Promise<ResultList<SecurityQuestion>> get();
    // Won't support DELETE operation in security Question, it may have some reference in other places.
}
