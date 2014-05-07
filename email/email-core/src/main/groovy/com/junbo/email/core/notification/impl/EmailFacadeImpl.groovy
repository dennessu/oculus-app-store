/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.notification.impl

import com.junbo.email.core.notification.EmailFacade
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.resource.EmailResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Impl of EmailFacade.
 */
@CompileStatic
class EmailFacadeImpl implements EmailFacade {
    @Resource(name = 'emailClient')
    private EmailResource emailResource

    @Override
    Promise<Email> sendEmail(Email email) {
        return emailResource.sendEmail(email)
    }
}
