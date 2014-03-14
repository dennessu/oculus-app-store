/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.service;

import com.junbo.email.clientproxy.EmailProvider;
import com.junbo.email.core.EmailService;
import com.junbo.email.db.repo.EmailHistoryRepository;
import com.junbo.email.db.repo.EmailScheduleRepository;
import com.junbo.email.db.repo.EmailTemplateRepository;
import com.junbo.email.spec.error.AppErrors;
import com.junbo.email.spec.model.Email;
import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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

    @Override
    public Promise<Email> send(Email request) {

        validateRequest(request);
        if(request.getScheduleDate() != null) {
            //handler schedule email
            Email schedule = emailScheduleRepository.saveEmailSchedule(request);
            return Promise.pure(schedule);
        }
        else {
            //send email by mandrill
            return emailProvider.sendEmail(request).then(new Promise.Func<Email, Promise<Email>>() {
                @Override
                public Promise<Email> apply(Email email) {
                    Long id = emailHistoryRepository.createEmailHistory(email);
                    return Promise.pure(emailHistoryRepository.getEmail(id));
                }
            });
        }
    }

    private void validateRequest(Email email) {
        if(email == null) {
            throw AppErrors.INSTANCE.invalidPayload().exception();
        }
        if(email.getUserId() == null && email.getRecipient() == null) {
            throw AppErrors.INSTANCE.fieldMissingValue("user or recipient").exception();
        }
        if(email.getSource() == null) {
            throw AppErrors.INSTANCE.fieldMissingValue("source").exception();
        }
        if(email.getAction() == null) {
            throw AppErrors.INSTANCE.fieldMissingValue("action").exception();
        }
        if(email.getLocale() == null && email.getUserId() == null) {
            throw AppErrors.INSTANCE.fieldMissingValue("locale").exception();
        }
        if(email.getScheduleDate() != null && email.getScheduleDate().before(new Date())) {
            throw AppErrors.INSTANCE.fieldInvalid("scheduleDate").exception();
        }
        checkProperties(email);
    }

    private void checkProperties(Email email) {
        String templateName = String.format("%s.%s.%s",email.getSource(), email.getAction(), email.getLocale());
        EmailTemplate template = emailTemplateRepository.getEmailTemplateByName(templateName);

        if(template == null) {
            throw AppErrors.INSTANCE.templateNotFound(templateName).exception();
        }
        if(template.getListOfVariables() != null && email.getProperties() == null) {
            throw AppErrors.INSTANCE.invalidProperty("properties").exception();
        }
        if(template.getListOfVariables() != null) {
            List<String> variables = template.getListOfVariables();
            //properties check
            for(String key : email.getProperties().keySet()) {
                if(!variables.contains(key.replaceAll("\\d*$",""))) {
                    throw AppErrors.INSTANCE.invalidProperty(key).exception();
                }
            }
        }
    }

}
