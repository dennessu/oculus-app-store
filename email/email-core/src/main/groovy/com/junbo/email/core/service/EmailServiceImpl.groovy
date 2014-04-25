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
import com.junbo.email.core.validator.EmailValidator
import com.junbo.email.db.repo.EmailHistoryRepository
import com.junbo.email.db.repo.EmailScheduleRepository
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailStatus
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.langur.core.promise.Promise
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * Impl of EmailService.
 */
@Component
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

    private List<String> getEmailAddress(UserPii userPii) {
        def type = userPii?.emails?.keySet()?.first()
        def emailAddress = StringUtils.isEmpty(type) ? '' : userPii?.emails?.get(type)?.value
        return StringUtils.isEmpty(emailAddress) ? [] : [emailAddress]
    }

    private Promise<Email> handle(Email email) {
        if (email.recipients == null) {
            identityFacade.getUserPii(email.userId.value).then {
                def userPii = it as UserPii
                emailValidator.validateUserPii(userPii)
                email.setRecipients(this.getEmailAddress(userPii))
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
        def template = emailTemplateRepository.getEmailTemplate(email.templateId.value)
        return emailProvider.sendEmail(email, template).then {
            Long id = emailHistoryRepository.createEmailHistory(it as Email)
            return Promise.pure(emailHistoryRepository.getEmail(id))
        }
    }

    private Promise<Email> update(Email email) {
        Email scheduleEmail = emailScheduleRepository.updateEmailSchedule(email)
        return Promise.pure(scheduleEmail)
    }
}
