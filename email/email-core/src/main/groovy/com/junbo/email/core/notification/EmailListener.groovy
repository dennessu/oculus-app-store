/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.notification

import com.junbo.common.id.EmailId
import com.junbo.email.spec.model.Email
import com.junbo.notification.core.BaseListener
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


/**
 * EmailListener.
 */
@CompileStatic
@Component
class EmailListener extends BaseListener {

    @Autowired
    private EmailFacade emailFacade

    protected void onMessage(final String eventId, final String message) {
        Long emailId = Long.parseLong(message)
        Email email = new Email()
        email.id = new EmailId(emailId)
        def result = emailFacade.sendEmail(email)
        assert  result != null
    }
}
