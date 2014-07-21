/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.rest.resource;

import com.junbo.authorization.AuthorizeContext;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.id.EmailId;
import com.junbo.email.core.EmailService;
import com.junbo.email.spec.model.Email;
import com.junbo.email.spec.resource.EmailResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Impl of EmailResource.
 */
@Provider
@Component
public class EmailResourceImpl implements EmailResource {
    private static final String EMAIL_SERVICE_SCOPE = "email.service";

    @Autowired
    private EmailService emailService;

    @Override
    public Promise<Email> postEmail(Email email) {
        authorize();
        return emailService.postEmail(email);
    }

    @Override
    public Promise<Email> getEmail(EmailId id) {
        authorize();
        return emailService.getEmail(id.getValue());
    }

    @Override
    public Promise<Email> putEmail(EmailId id, Email email) {
        authorize();
        return emailService.updateEmail(id.getValue(), email);
    }

    @Override
    public Promise<Response> deleteEmail(EmailId id) {
        authorize();
        return emailService.deleteEmail(id.getValue()).then(new Promise.Func<Void, Promise<Response>>() {
            @Override
            public Promise<Response> apply(Void aVoid) {
                return Promise.pure(Response.status(204).build());
            }
        });
    }

    private static void authorize() {
        if (!AuthorizeContext.hasScopes(EMAIL_SERVICE_SCOPE)) {
            throw AppCommonErrors.INSTANCE.insufficientScope().exception();
        }
    }
}
