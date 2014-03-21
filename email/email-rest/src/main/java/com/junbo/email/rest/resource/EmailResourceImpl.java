/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.rest.resource;

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
@Scope("prototype")
public class EmailResourceImpl implements EmailResource {

    @Autowired
    private EmailService emailService;

    @Override
    public Promise<Email> postEmail(Email request) {
        return emailService.send(request);
    }

    @Override
    public Promise<Email> getEmail(Long id) {
        return emailService.getEmail(id);
    }

    @Override
    public Promise<Email> putEmail(Long id, Email email) {
        return emailService.updateEmail(id, email);
    }

    @Override
    public Promise<Response> deleteEmail(Long id) {
        emailService.deleteEmail(id);
        return Promise.pure(Response.status(204).build());
    }
}
