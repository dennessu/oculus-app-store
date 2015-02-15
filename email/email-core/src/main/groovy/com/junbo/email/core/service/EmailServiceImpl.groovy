/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.service

import com.junbo.common.id.EmailId
import com.junbo.common.model.Results
import com.junbo.email.clientproxy.IdentityFacade
import com.junbo.email.core.EmailService
import com.junbo.email.core.publisher.EmailPublisher
import com.junbo.email.core.validator.EmailValidator
import com.junbo.email.db.repo.EmailHistoryRepository
import com.junbo.email.db.repo.EmailScheduleRepository
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailSearchOption
import com.junbo.email.spec.model.EmailStatus
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Impl of EmailService.
 */
@CompileStatic
@Component
@Transactional
class EmailServiceImpl implements EmailService {

    private  EmailHistoryRepository emailHistoryRepository

    private EmailScheduleRepository emailScheduleRepository

    private EmailValidator emailValidator

    private IdentityFacade identityFacade

    private EmailPublisher emailPublisher

    void setEmailHistoryRepository(EmailHistoryRepository emailHistoryRepository) {
        this.emailHistoryRepository = emailHistoryRepository
    }

    void setEmailScheduleRepository(EmailScheduleRepository emailScheduleRepository) {
        this.emailScheduleRepository = emailScheduleRepository
    }

    void setEmailValidator(EmailValidator emailValidator) {
        this.emailValidator = emailValidator
    }

    void setIdentityFacade(IdentityFacade identityFacade) {
        this.identityFacade = identityFacade
    }

    void setEmailPublisher(EmailPublisher emailPublisher) {
        this.emailPublisher = emailPublisher
    }

    @Override
    Promise<Email> postEmail(Email email) {
        emailValidator.validateCreate(email)
        email.setStatus(EmailStatus.PENDING.toString())
        return this.handle(email)
    }

    @Override
    Promise<Email> getEmail(String id) {
        return  emailHistoryRepository.getEmailHistory(id).then {Email email ->
            if (email == null) {
               return emailScheduleRepository.getEmailSchedule(id)
            }
            return Promise.pure(email)
        }
    }

    @Override
    Promise<Void> deleteEmail(String id) {
        emailValidator.validateDelete(id)
        emailScheduleRepository.deleteEmailSchedule(id)
        return Promise.pure(null)
    }

    @Override
    Promise<Results<Email>> searchEmail(EmailSearchOption option) {
        emailValidator.validateSearch(option)
        return  emailHistoryRepository.searchEmail(option).then {List<Email> list ->
            def results = new Results<Email>(items:[])
            if (list != null && list.size() !=0) {
                results.items.addAll(list)
            }
            return Promise.pure(results)
        }
    }

    @Override
    Promise<Email> updateEmail(String id, Email email) {
        email.setId(new EmailId(id))
        emailValidator.validateUpdate(email)
        return this.handle(email)
    }

    private Promise<Email> handle(Email email) {
        if (email.recipients == null) {
            return identityFacade.getUserEmail(email.userId.value).then { String strEmail ->
                if (email == null) {
                    throw AppErrors.INSTANCE.noValidatedUserEmail().exception()
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
            return emailScheduleRepository.saveEmailSchedule(email)
        }
        return emailHistoryRepository.createEmailHistory(email).then {Email savedEmail ->
            emailPublisher.send(savedEmail.getId().value)
            return Promise.pure(savedEmail)
        }
    }

    private Promise<Email> update(Email email) {
        return emailScheduleRepository.updateEmailSchedule(email)
    }
}
