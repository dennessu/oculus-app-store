/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.service

import com.junbo.common.id.EmailId
import com.junbo.email.clientproxy.EmailProvider
import com.junbo.email.clientproxy.IdentityFacade
import com.junbo.email.core.EmailService
import com.junbo.email.core.notification.EmailPublisher
import com.junbo.email.core.validator.EmailValidator
import com.junbo.email.db.repo.EmailHistoryRepository
import com.junbo.email.db.repo.EmailScheduleRepository
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailStatus
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Impl of EmailService.
 */
@CompileStatic
@Component
@Transactional
class EmailServiceImpl implements EmailService {

    @Autowired
    private  EmailHistoryRepository emailHistoryRepository

    @Autowired
    private EmailScheduleRepository emailScheduleRepository

    @Autowired
    private EmailTemplateRepository emailTemplateRepository

    @Autowired
    private EmailValidator emailValidator

    @Autowired
    private EmailProvider emailProvider

    @Autowired
    private IdentityFacade identityFacade

    @Autowired
    private EmailPublisher emailPublisher

    @Override
    Promise<Email> postEmail(Email email) {
        emailValidator.validateCreate(email)
        email.setStatus(EmailStatus.PENDING.toString())
        return this.handle(email)
    }

    @Override
    Promise<Email> getEmail(Long id) {
        Email email = emailHistoryRepository.getEmail(id)
        if (email == null) {
            email = emailScheduleRepository.getEmailSchedule(id)
        }
        return Promise.pure(email)
    }

    @Override
    Void deleteEmail(Long id) {
        emailValidator.validateDelete(id)
        emailScheduleRepository.deleteEmailScheduleById(id)
        return null
    }

    @Override
    Promise<Email> updateEmail(Long id, Email email) {
        email.setId(new EmailId(id))
        emailValidator.validateUpdate(email)
        return this.handle(email)
    }

    private Promise<Email> handle(Email email) {
        if (email.recipients == null) {
            return identityFacade.getUserEmail(email.userId.value).then { String strEmail ->
                if (email == null) {
                    throw AppErrors.INSTANCE.missingField('recipients').exception()
                }
                def recipients = [] as List<String>
                recipients << strEmail
                email.setRecipients(recipients)
                return email.id == null ? this.save(email) : this.update(email)
            }
        }
        else {
            return email.id == null ? this.save(email) : this.update(email)
        }
    }

    private Promise<Email> save(Email email) {
        if (email.scheduleTime != null) {
            def scheduleEmail = emailScheduleRepository.saveEmailSchedule(email)
            return Promise.pure(scheduleEmail)
        }
//        def template = emailTemplateRepository.getEmailTemplate(email.templateId.value)
//        return emailProvider.sendEmail(email, template).then {
//            Long id = emailHistoryRepository.createEmailHistory(it as Email)
//            return Promise.pure(emailHistoryRepository.getEmail(id))
//        }
        Long id = emailHistoryRepository.createEmailHistory(email)
        emailPublisher.send(id)
        return Promise.pure(emailHistoryRepository.getEmail(id))
    }

    private Promise<Email> update(Email email) {
        Email scheduleEmail = emailScheduleRepository.updateEmailSchedule(email)
        return Promise.pure(scheduleEmail)
    }

    @Override
    Promise<Email> sendEmail(Email request) {
        def email = emailHistoryRepository.getEmail(request.id.value)
        if (email != null) {
            def template = emailTemplateRepository.getEmailTemplate(email.templateId.value)
            if (template != null) {
                return emailProvider.sendEmail(email, template).then { Email retEmail ->
                    def emailId = emailHistoryRepository.updateEmailHistory(retEmail)
                    return Promise.pure(emailHistoryRepository.getEmail(emailId))
                }
            }
        }
        return Promise.pure(null)
    }
}
