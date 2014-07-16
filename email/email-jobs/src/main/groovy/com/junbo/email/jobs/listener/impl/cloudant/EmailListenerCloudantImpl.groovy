/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.jobs.listener.impl.cloudant

import com.junbo.email.jobs.listener.EmailListener
import com.junbo.email.jobs.listener.impl.EmailBaseListener
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.promise.SyncModeScope
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Impl of EmailListener(Cloudant).
 */
@CompileStatic
class EmailListenerCloudantImpl extends EmailBaseListener implements EmailListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailListenerCloudantImpl)

    public void onMessage(final String eventId, final String message) {
        LOGGER.info('EMAIL_LISTENER_INFO. Receive a message with event id:{} and message is:{}', eventId, message)
        try {
            String emailId = message
            this.sendEmail(emailId)
        } catch (NumberFormatException nfe) {
            LOGGER.error('EMAIL_LISTENER_ERROR. Failed to parse message:{}', message)
        } catch (Exception e) {
            LOGGER.error('EMAIL_LISTENER_ERROR. Failed to send email:', e)
        }
    }

    private void sendEmail(String emailId) {
        emailHistoryRepository.getEmailHistory(emailId).recover { Throwable throwable ->
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
            def result = SyncModeScope.with {
                return emailTemplateRepository.getEmailTemplate(email.templateId.value).recover {
                    Throwable throwable ->
                        LOGGER.error('EMAIL_LISTENER_ERROR. Failed to get email template:',throwable)
                }.then { EmailTemplate template ->
                    if (template == null) {
                        LOGGER.error('EMAIL_LISTENER_ERROR. Email template not found')
                        return Promise.pure(null)
                    }
                    return emailProvider.sendEmail(email, template).then {Email retEmail ->
                        return emailHistoryRepository.updateEmailHistory(retEmail, retEmail)
                    }
                }.syncGet()
            }

            if (result != null) {
                LOGGER.info('EMAIL_LISTENER_INFO. Email has been sent')
            }
        }

    }
}
