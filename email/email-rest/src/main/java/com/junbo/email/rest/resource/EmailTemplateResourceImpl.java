/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.rest.resource;

import com.junbo.authorization.AuthorizeContext;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.EmailTemplateId;
import com.junbo.common.model.Results;
import com.junbo.email.core.EmailTemplateService;
import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.email.spec.model.QueryParam;
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
public class EmailTemplateResourceImpl implements EmailTemplateResource{
    private static final String EMAIL_ADMIN_SCOPE = "email.admin";
    private static final String EMAIL_SERVICE_SCOPE = "email.service";
    @Autowired
    private EmailTemplateService templateService;

    public Promise<EmailTemplate> postEmailTemplate(EmailTemplate template) {
        if (!AuthorizeContext.hasScopes(EMAIL_ADMIN_SCOPE)) {
            throw AppCommonErrors.INSTANCE.insufficientScope().exception();
        }

        return templateService.postEmailTemplate(template);
    }
    public Promise<EmailTemplate> getEmailTemplate(EmailTemplateId id) {
        if (!AuthorizeContext.hasScopes(EMAIL_ADMIN_SCOPE) && !AuthorizeContext.hasScopes(EMAIL_SERVICE_SCOPE)) {
            throw AppCommonErrors.INSTANCE.insufficientScope().exception();
        }

        return templateService.getEmailTemplate(id.getValue());
    }
    public Promise<EmailTemplate> putEmailTemplate(EmailTemplateId id, EmailTemplate template) {
        if (!AuthorizeContext.hasScopes(EMAIL_ADMIN_SCOPE)) {
            throw AppCommonErrors.INSTANCE.insufficientScope().exception();
        }

        return templateService.putEmailTemplate(id.getValue(), template);
    }
    public Promise<Response> deleteEmailTemplate(EmailTemplateId id) {
        if (!AuthorizeContext.hasScopes(EMAIL_ADMIN_SCOPE)) {
            throw AppCommonErrors.INSTANCE.insufficientScope().exception();
        }

        return templateService.deleteEmailTemplate(id.getValue()).then(new Promise.Func<Void, Promise<Response>>() {
            @Override
            public Promise<Response> apply(Void aVoid) {
                return Promise.pure(Response.status(204).build());
            }
        });
    }

    public Promise<Results<EmailTemplate>> getEmailTemplates(QueryParam queryParam) {
        if (!AuthorizeContext.hasScopes(EMAIL_ADMIN_SCOPE) && !AuthorizeContext.hasScopes(EMAIL_SERVICE_SCOPE)) {
            throw AppCommonErrors.INSTANCE.insufficientScope().exception();
        }

        return templateService.getEmailTemplates(queryParam);
    }
}
