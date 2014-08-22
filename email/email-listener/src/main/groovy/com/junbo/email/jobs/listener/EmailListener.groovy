/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.jobs.listener

import com.junbo.email.clientproxy.EmailProvider
import com.junbo.email.db.repo.EmailHistoryRepository
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import com.junbo.notification.core.BaseListener
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionCallbackWithoutResult


/**
 * Interface of EmailListener.
 */
@CompileStatic
interface EmailListener {
    void onMessage(String eventId, String message)
}
