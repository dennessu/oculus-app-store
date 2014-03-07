/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.resource;

import com.junbo.common.id.PasswordRuleId;
import com.junbo.identity.spec.model.password.PasswordRule;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
/**
 * Java cod for password rule resource.
 */
@RestResource
@Path("/password-rules")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface PasswordResource {
    @GET
    @Path("/{passwordRuleId}")
    Promise<PasswordRule> getPasswordRule(@PathParam("passwordRuleId") PasswordRuleId passwordRuleId);

    @POST
    @Path("/")
    Promise<PasswordRule> postPasswordRule(PasswordRule passwordRule);

    @PUT
    @Path("/{appId}")
    Promise<PasswordRule> updatePasswordRule(@PathParam("passwordRuleId") PasswordRuleId passwordRuleId,
                                             PasswordRule passwordRule);
}
