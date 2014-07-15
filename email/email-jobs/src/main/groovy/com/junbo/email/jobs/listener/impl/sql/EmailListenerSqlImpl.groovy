/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.jobs.listener.impl.sql

import com.junbo.email.jobs.listener.EmailListener
import com.junbo.email.jobs.listener.impl.EmailBaseListener
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallback

/**
 * Impl of EmailListener (Sql).
 */
@CompileStatic
class EmailListenerSqlImpl extends EmailBaseListener implements EmailListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailListenerSqlImpl)

    private PlatformTransactionManager transactionManager

    @Required
    void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager
    }

    public void onMessage(final String eventId, final String message) {
        LOGGER.info('EMAIL_LISTENER_INFO. Receive a message with event id:{} and message is:{}', eventId, message)
        try {
            def emailId = message
            this.sendEmail(emailId)
        } catch (NumberFormatException nfe) {
            LOGGER.error('EMAIL_LISTENER_ERROR. Failed to parse message:{}', message)
        } catch (Exception e) {
            LOGGER.error('EMAIL_LISTENER_ERROR. Failed to send email:', e)
        }
    }

    public void sendEmail(String emailId) {
        def email = Promise.get {
            this.findEmail(emailId).recover {Throwable throwable ->
                LOGGER.error('EMAIL_LISTENER_ERROR. Failed to get email:',throwable)
            }.then { Email email ->
                if (email == null) {
                    LOGGER.error('EMAIL_LISTENER_ERROR. Email not found with id:{}', emailId)
                    return Promise.pure(null)
                }
                if (email.templateId == null) {
                    LOGGER.error('EMAIL_LISTENER_ERROR. EmailTemplate id should be not null')
                    return Promise.pure(null)
                }
                return this.findEmailTemplate(email.templateId.value).recover {Throwable throwable ->
                    LOGGER.error('EMAIL_LISTENER_ERROR. Failed to get email template:',throwable)
                }.then { EmailTemplate template ->
                    if (template == null) {
                        LOGGER.error('EMAIL_LISTENER_ERROR. Email template not found')
                        return Promise.pure(null)
                    }
                    return emailProvider.sendEmail(email, template).then { Email updatedEmail ->
                        return this.updateEmail(updatedEmail)
                    }
                }
            }
        }

        if (email != null) {
            LOGGER.info('EMAIL_LISTENER_INFO. Email has been sent')
        }
    }

    private Promise<Email> findEmail(String emailId) {
        def transactionTemplate = new AsyncTransactionTemplate(transactionManager)
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return transactionTemplate.execute(new TransactionCallback<Promise<Email>>() {
            @Override
            public Promise<Email> doInTransaction(TransactionStatus status) {
                return emailHistoryRepository.getEmailHistory(emailId)
            }
        })
    }

    private Promise<EmailTemplate> findEmailTemplate(String emailTemplateId) {
        def transactionTemplate = new AsyncTransactionTemplate(transactionManager)
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        return transactionTemplate.execute(new TransactionCallback<Promise<EmailTemplate>>() {
            @Override
            public Promise<EmailTemplate> doInTransaction(TransactionStatus status) {
                return emailTemplateRepository.getEmailTemplate(emailTemplateId.toString())
            }
        })
    }

    private Promise<Email> updateEmail(Email email) {
        def transactionTemplate = new AsyncTransactionTemplate(transactionManager)
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)
        transactionTemplate.execute(new TransactionCallback<Promise<Email>>() {
            @Override
            public Promise<Email> doInTransaction(TransactionStatus status) {
                return emailHistoryRepository.updateEmailHistory(email)
            }
        })
    }
}
