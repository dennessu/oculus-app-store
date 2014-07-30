/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.rest.resource;

import com.junbo.common.id.EmailTemplateId;
import com.junbo.common.model.Results;
import com.junbo.email.core.EmailTemplateService;
import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.email.spec.model.QueryParam;
import com.junbo.email.spec.resource.EmailTemplateResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Impl of EmailTemplateResource.
 */
@Provider
@Component
public class EmailTemplateResourceImpl implements EmailTemplateResource{
    @Autowired
    private EmailTemplateService templateService;

    public Promise<EmailTemplate> postEmailTemplate(EmailTemplate template) {
        return templateService.postEmailTemplate(template);
    }

    public Promise<EmailTemplate> getEmailTemplate(EmailTemplateId id) {
        return templateService.getEmailTemplate(id.getValue());
    }

    public Promise<EmailTemplate> putEmailTemplate(EmailTemplateId id, EmailTemplate template) {
        return templateService.putEmailTemplate(id.getValue(), template);
    }

    public Promise<Response> deleteEmailTemplate(EmailTemplateId id) {
        return templateService.deleteEmailTemplate(id.getValue()).then(new Promise.Func<Void, Promise<Response>>() {
            @Override
            public Promise<Response> apply(Void aVoid) {
                return Promise.pure(Response.status(204).build());
            }
        });
    }

    public Promise<Results<EmailTemplate>> getEmailTemplates(QueryParam queryParam) {
        return templateService.getEmailTemplates(queryParam);
    }
}
