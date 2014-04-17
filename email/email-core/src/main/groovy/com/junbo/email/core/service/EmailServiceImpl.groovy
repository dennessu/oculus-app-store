/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.service

import com.junbo.common.id.EmailId
//import com.junbo.email.clientproxy.EmailProvider
import com.junbo.email.clientproxy.IdentityFacade
import com.junbo.email.core.EmailService
import com.junbo.email.core.validator.EmailValidator
import com.junbo.email.db.repo.EmailHistoryRepository
import com.junbo.email.db.repo.EmailScheduleRepository
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailStatus
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.langur.core.promise.Promise
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.annotation.Resource

/**
 * Impl of EmailService.
 */
@Component
class EmailServiceImpl implements EmailService {

    @Autowired
    private  EmailHistoryRepository emailHistoryRepository

//    @Autowired
//    private EmailTemplateRepository emailTemplateRepository

    @Autowired
    private EmailScheduleRepository emailScheduleRepository

//    @Resource
//    private EmailProvider emailProvider

    @Autowired
    private EmailValidator emailValidator

    @Autowired
    @Qualifier('mockIdentityFacade')
    private IdentityFacade identityFacade

    @Override
    Promise<Email> postEmail(Email email) {

        emailValidator.validateCreate(email)
        email.setStatus(EmailStatus.FAILED.toString())
        if (StringUtils.isEmpty(email.locale)) {
            identityFacade.getUser(email.userId.value).then {
                def user = it as User
                emailValidator.validateUser(user)
                email.setLocale(user.locale)
                emailValidator.validateReplacements(email)

                if (email.recipients == null) {
                    identityFacade.getUserPii(email.userId.value).then {
                        def userPii = it as UserPii
                        emailValidator.validateUserPii(userPii)

                        email.setRecipients(this.getEmailAddress(userPii))

                        if (email.scheduleTime != null) {
                            def scheduleEmail = emailScheduleRepository.saveEmailSchedule(email)
                            return Promise.pure(scheduleEmail)
                        }
                        Long id = emailHistoryRepository.createEmailHistory(email)
                        def retEmail = emailHistoryRepository.getEmail(id)
                        return Promise.pure(retEmail)
                    }
                }
                else {
                    if (email.scheduleTime != null) {
                        def scheduleEmail = emailScheduleRepository.saveEmailSchedule(email)
                        return Promise.pure(scheduleEmail)
                    }
                    Long id = emailHistoryRepository.createEmailHistory(email)
                    def retEmail = emailHistoryRepository.getEmail(id)
                    return Promise.pure(retEmail)
                }
            }
        }
        else {
            emailValidator.validateReplacements(email)

            if (email.recipients == null) {
                identityFacade.getUserPii(email.userId.value).then {
                    def userPii = it as UserPii
                    emailValidator.validateUserPii(userPii)
                    email.setRecipients(this.getEmailAddress(userPii))

                    if (email.scheduleTime != null) {
                        def scheduleEmail = emailScheduleRepository.saveEmailSchedule(email)
                        return Promise.pure(scheduleEmail)
                    }
                    Long id = emailHistoryRepository.createEmailHistory(email)
                    def retEmail = emailHistoryRepository.getEmail(id)
                    return Promise.pure(retEmail)
                }
            }
            else {
                if (email.scheduleTime != null) {
                    def scheduleEmail = emailScheduleRepository.saveEmailSchedule(email)
                    return Promise.pure(scheduleEmail)
                }
                Long id = emailHistoryRepository.createEmailHistory(email)
                def retEmail = emailHistoryRepository.getEmail(id)
                return Promise.pure(retEmail)
            }
        }

//        if (email.scheduleDate != null) {
//            //handler schedule email
//            Email schedule = emailScheduleRepository.saveEmailSchedule(email)
//            return Promise.pure(schedule)
//        }
//        //send email by mandrill
//        return emailProvider.sendEmail(email).then {
//            Long id = emailHistoryRepository.createEmailHistory(it)
//            return Promise.pure(emailHistoryRepository.getEmail(id))
//        }
    }

    Promise<Email> getEmail(Long id) {

        Email email = emailHistoryRepository.getEmail(id)
        if (email == null) {
            email = emailScheduleRepository.getEmailSchedule(id)
        }
        return Promise.pure(email)
    }

    Void deleteEmail(Long id) {
        emailValidator.validateDelete(id)
        emailScheduleRepository.deleteEmailScheduleById(id)
        return null
    }

    Promise<Email> updateEmail(Long id, Email email) {
        email.setId(new EmailId(id))
        emailValidator.validateUpdate(email)
        Email schedule = emailScheduleRepository.updateEmailSchedule(email)
        return Promise.pure(schedule)
    }

    private List<String> getEmailAddress(UserPii userPii) {
        def type = userPii?.emails?.keySet()?.first()
        def emailAddress = StringUtils.isEmpty(type) ? null : userPii?.emails?.get(type)?.value
        return StringUtils.isEmpty(emailAddress) ? null : [emailAddress]
    }

    private Promise<Email> save(Email email) {
        return null
    }
}
