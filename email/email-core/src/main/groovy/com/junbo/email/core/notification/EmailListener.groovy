/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.notification

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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionCallbackWithoutResult


/**
 * EmailListener.
 */
@CompileStatic
class EmailListener extends BaseListener {

    @Autowired
    private EmailProvider emailProvider
    @Autowired
    private  EmailHistoryRepository emailHistoryRepository
    @Autowired
    private EmailTemplateRepository emailTemplateRepository
    @Autowired
    protected PlatformTransactionManager transactionManager;

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailListener)

    protected void onMessage(final String eventId, final String message) {
        LOGGER.info("Receive a message with event id: {} and message is: {}", eventId, message)
        Long emailId = Long.parseLong(message)
        this.sendEmail(emailId)
    }

    public void sendEmail(Long emailId) {
        def email = this.findEmail(emailId)
        if (email == null) {
            LOGGER.error("Email don't found with id: {}", emailId)
            return
        }
        def template = this.findEmailTemplate(email.templateId.value)
        if (template == null) {
            LOGGER.error("EmailTemplate don't found with id: {}", email.templateId)
            return
        }
        def result = emailProvider.sendEmail(email, template).then { Email retEmail ->
            this.updateEmail(retEmail)
            return Promise.pure(retEmail)
        }
        assert result != null, 'email should be not null'
    }

    private Email findEmail(Long emailId) {
        def transactionTemplate = new AsyncTransactionTemplate(transactionManager)
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return transactionTemplate.execute(new TransactionCallback<Email>() {
            @Override
            public Email doInTransaction(TransactionStatus status) {
                return emailHistoryRepository.getEmail(emailId)
            }
        })
    }

    private EmailTemplate findEmailTemplate(Long emailTemplateId) {
        def transactionTemplate = new AsyncTransactionTemplate(transactionManager)
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return transactionTemplate.execute(new TransactionCallback<EmailTemplate>() {
            @Override
            public EmailTemplate doInTransaction(TransactionStatus status) {
                return emailTemplateRepository.getEmailTemplate(emailTemplateId)
            }
        })
    }

    private void updateEmail(Email email) {
        def transactionTemplate = new AsyncTransactionTemplate(transactionManager)
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                    emailHistoryRepository.updateEmailHistory(email)
                }
            })
    }
}
