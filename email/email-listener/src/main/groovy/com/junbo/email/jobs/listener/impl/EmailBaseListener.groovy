/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.jobs.listener.impl

import com.junbo.email.clientproxy.EmailProvider
import com.junbo.email.db.repo.EmailHistoryRepository
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.notification.core.BaseListener
import groovy.transform.CompileStatic

/**
 * EmailBaseListener Class.
 */
@CompileStatic
abstract class EmailBaseListener extends BaseListener {
    protected EmailProvider emailProvider

    protected  EmailHistoryRepository emailHistoryRepository

    protected EmailTemplateRepository emailTemplateRepository

    void setEmailProvider(EmailProvider emailProvider) {
        this.emailProvider = emailProvider
    }

    void setEmailHistoryRepository(EmailHistoryRepository emailHistoryRepository) {
        this.emailHistoryRepository = emailHistoryRepository
    }

    void setEmailTemplateRepository(EmailTemplateRepository emailTemplateRepository) {
        this.emailTemplateRepository = emailTemplateRepository
    }
}
