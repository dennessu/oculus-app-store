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
 * EmailListener.
 */
@CompileStatic
class EmailListener extends BaseListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmailListener)

    private EmailProvider emailProvider

    private  EmailHistoryRepository emailHistoryRepository

    private EmailTemplateRepository emailTemplateRepository

    private PlatformTransactionManager transactionManager

    void setEmailProvider(EmailProvider emailProvider) {
        this.emailProvider = emailProvider
    }

    void setEmailHistoryRepository(EmailHistoryRepository emailHistoryRepository) {
        this.emailHistoryRepository = emailHistoryRepository
    }

    void setEmailTemplateRepository(EmailTemplateRepository emailTemplateRepository) {
        this.emailTemplateRepository = emailTemplateRepository
    }

    void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager
    }

    protected void onMessage(final String eventId, final String message) {
        LOGGER.info("Receive a message with event id: {} and message is: {}", eventId, message)
        Long emailId = null
        try {
            emailId = Long.parseLong(message)
        } catch (NumberFormatException ex) {
            LOGGER.error("Failed to parse message: {}", message)
        }
        if (emailId == null) {
            return
        }
        this.sendEmail(emailId)
    }

    public void sendEmail(Long emailId) {
        def email = this.findEmail(emailId)
        if (email == null) {
            LOGGER.error("Email don't found with id: {}", emailId)
            return
        }
        if (email.templateId == null) {
            LOGGER.error("EmailTemplate id should be not null")
            return
        }
        def template = this.findEmailTemplate(email.templateId.value)
        def result = emailProvider.sendEmail(email, template).then { Email retEmail ->
            this.updateEmail(retEmail)
            return Promise.pure(retEmail)
        }
        assert result != null, 'Email should be not null'
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
