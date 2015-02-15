/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.rest.resource;

import com.junbo.common.id.EmailId;
import com.junbo.common.model.Results;
import com.junbo.email.core.EmailService;
import com.junbo.email.spec.model.Email;
import com.junbo.email.spec.model.EmailSearchOption;
import com.junbo.email.spec.resource.EmailResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Impl of EmailResource.
 */
@Provider
@Component
public class EmailResourceImpl implements EmailResource {

    @Autowired
    private EmailService emailService;

    @Override
    public Promise<Email> postEmail(Email email) {
        return emailService.postEmail(email);
    }

    @Override
    public Promise<Email> getEmail(EmailId id) {
        return emailService.getEmail(id.getValue());
    }

    @Override
    public Promise<Email> putEmail(EmailId id, Email email) {
        return emailService.updateEmail(id.getValue(), email);
    }

    @Override
    public Promise<Results<Email>> search(EmailSearchOption emailSearchOption) {
        return emailService.searchEmail(emailSearchOption);
    }

    @Override
    public Promise<Response> deleteEmail(EmailId id) {
        return emailService.deleteEmail(id.getValue()).then(new Promise.Func<Void, Promise<Response>>() {
            @Override
            public Promise<Response> apply(Void aVoid) {
                return Promise.pure(Response.status(204).build());
            }
        });
    }
}
