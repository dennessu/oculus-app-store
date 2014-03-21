/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.rest.resource;

import com.junbo.email.core.EmailTemplateService;
import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.email.spec.model.Paging;
import com.junbo.email.spec.model.Results;
import com.junbo.email.spec.resource.EmailTemplateResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Impl of EmailTemplateResource.
 */
@Provider
@Component
@Scope("prototype")
public class EmailTemplateResourceImpl implements EmailTemplateResource{
    @Autowired
    private EmailTemplateService templateService;

    public Promise<EmailTemplate> postEmailTemplate(EmailTemplate template) {
        return templateService.postEmailTemplate(template);
    }
    public Promise<EmailTemplate> getEmailTemplate(Long id) {
        return templateService.getEmailTemplate(id);
    }
    public Promise<EmailTemplate> putEmailTemplate(Long id, EmailTemplate template) {
        return templateService.putEmailTemplate(id, template);
    }
    public Promise<Response> deleteEmailTemplate(Long id) {
        templateService.deleteEmailTemplate(id);
        return Promise.pure(Response.status(204).build());
    }
    public Promise<Results<EmailTemplate>> getEmailTemplates(Paging paging) {
        return templateService.getEmailTemplates(paging);
    }


}
