/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.resource;

import com.junbo.common.id.EmailTemplateId;
import com.junbo.common.model.Results;
import com.junbo.email.spec.model.*;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource of EmailTemplate.
 */
@Path("/email-templates")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface EmailTemplateResource {
    @ApiOperation("Get email templates")
    @GET
    Promise<Results<EmailTemplate>> getEmailTemplates(@BeanParam com.junbo.email.spec.model.QueryParam queryParam,
                                                      @BeanParam Pagination pagination);

    @POST
    Promise<EmailTemplate> postEmailTemplate(EmailTemplate template);

    @GET
    @Path("/{id}")
    Promise<EmailTemplate> getEmailTemplate(@PathParam("id") EmailTemplateId id);

    @PUT
    @Path("/{id}")
    Promise<EmailTemplate> putEmailTemplate(@PathParam("id") EmailTemplateId id, EmailTemplate template);

    @DELETE
    @Path("/{id}")
    Promise<Response> deleteEmailTemplate(@PathParam("id") EmailTemplateId id);
}
