/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.resource;

import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.email.spec.model.Paging;
import com.junbo.email.spec.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

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
    @GET
    Promise<Results<EmailTemplate>> getEmailTemplates(@BeanParam Paging paging);

    @POST
    Promise<EmailTemplate> postEmailTemplate(EmailTemplate template);

    @GET
    @Path("/{id}")
    Promise<EmailTemplate> getEmailTemplate(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    Promise<EmailTemplate> putEmailTemplate(@PathParam("id") Long id, EmailTemplate template);

    @DELETE
    @Path("/{id}")
    Promise<Response> deleteEmailTemplate(@PathParam("id") Long id);
}
