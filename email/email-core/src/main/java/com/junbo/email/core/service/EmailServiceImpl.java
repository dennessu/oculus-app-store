/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.service;

import com.junbo.email.clientproxy.MandrillFacade;
import com.junbo.email.clientproxy.impl.mandrill.MandrillResponse;
import com.junbo.email.clientproxy.impl.mandrill.SendStatus;
import com.junbo.email.core.EmailService;
import com.junbo.email.db.entity.EmailStatus;
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
    private MandrillFacade mandrillFacade;

    @Override
    public Promise<Email> send(Email request) {

        validateRequest(request);
        final Email email = request;
        if(request.getScheduleDate() != null) {
            //handler schedule email
            Email schedule = emailScheduleRepository.saveEmailSchedule(request);
            return Promise.pure(schedule);
        }
        else {
            //send email by mandrill
            return mandrillFacade.send(request).then(new Promise.Func<MandrillResponse, Promise<Email>>() {
                @Override
                public Promise<Email> apply(MandrillResponse response) {
                    if(response.getCode() == 200) {
                        switch (response.getStatus()) {
                            case SendStatus.SENT:{
                                email.setStatus(EmailStatus.SUCCEED.toString());
                                email.setSentDate(new Date());
                                break;
                            }
                            case SendStatus.REJECTED:{
                                email.setStatus(EmailStatus.FAILED.toString());
                                email.setStatusReason(response.getReason());
                                break;
                            }
                            case SendStatus.QUEUED:{
                                email.setStatus(EmailStatus.PENDING.toString());
                                break;
                            }
                            case SendStatus.INVALID:{
                                email.setStatus(EmailStatus.FAILED.toString());
                                email.setStatusReason("invalid");
                                break;
                            }
                            default:{
                                email.setStatus(EmailStatus.FAILED.toString());
                                email.setStatusReason("unknown");
                            }
                        }
                    }
                    else {
                        email.setStatus(EmailStatus.FAILED.toString());
                        email.setStatusReason(response.getBody());
                    }
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
        if(email.getLocale() == null) {
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
