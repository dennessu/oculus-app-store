/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.service;

import com.junbo.common.id.EmailId;
import com.junbo.email.clientproxy.EmailProvider;
import com.junbo.email.core.EmailService;
import com.junbo.email.core.validator.EmailValidator;
import com.junbo.email.db.repo.EmailHistoryRepository;
import com.junbo.email.db.repo.EmailScheduleRepository;
import com.junbo.email.db.repo.EmailTemplateRepository;
import com.junbo.email.spec.model.Email;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Impl of EmailService.
 */
@Component
public class EmailServiceImpl implements EmailService {
    private static final int INIT_RETRY_COUNT = 0;
    private static final int MAX_RETRY_COUNT = 3;

    @Autowired
    private  EmailHistoryRepository emailHistoryRepository;

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Autowired
    private EmailScheduleRepository emailScheduleRepository;

    @Resource
    private EmailProvider emailProvider;

    @Autowired
    private EmailValidator emailValidator;

    @Override
    public Promise<Email> postEmail(Email email) {

        emailValidator.validateCreate(email);
        if(email.getScheduleDate() != null) {
            //handler schedule email
            Email schedule = emailScheduleRepository.saveEmailSchedule(email);
            return Promise.pure(schedule);
        }
        else {
            //send email by mandrill
            return emailProvider.sendEmail(email).then(new Promise.Func<Email, Promise<Email>>() {
                @Override
                public Promise<Email> apply(Email email) {
                    Long id = emailHistoryRepository.createEmailHistory(email);
                    return Promise.pure(emailHistoryRepository.getEmail(id));
                }
            });
        }
    }

    public Promise<Email> getEmail(Long id) {

        Email email = emailHistoryRepository.getEmail(id);
        if(email == null) {
            email = emailScheduleRepository.getEmailSchedule(id);
        }
        return Promise.pure(email);
    }

    public Void deleteEmail(Long id) {
        emailValidator.validateDelete(id);
        emailScheduleRepository.deleteEmailScheduleById(id);
        return null;
    }

    public Promise<Email> updateEmail(Long id, Email email) {
        email.setId(new EmailId(id));
        emailValidator.validateUpdate(email);
        Email schedule = emailScheduleRepository.updateEmailSchedule(email);
        return Promise.pure(schedule);
    }
}
