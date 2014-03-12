/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.service;

import com.junbo.email.common.exception.AppExceptions;
import com.junbo.email.common.util.Utils;
import com.junbo.email.core.EmailService;
import com.junbo.email.core.provider.EmailProvider;
import com.junbo.email.core.provider.Request;
import com.junbo.email.core.provider.Response;
import com.junbo.email.core.util.Convert;
import com.junbo.email.db.entity.EmailStatus;
import com.junbo.email.db.entity.EmailTemplateEntity;
import com.junbo.email.db.repo.EmailScheduleRepository;
import com.junbo.email.db.repo.EmailTemplateRepository;
import com.junbo.email.spec.model.Email;
import com.junbo.email.db.repo.EmailHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Impl of EmailService.
 */
@Component
@Transactional
public class EmailServiceImpl implements EmailService {
    private static final int INIT_RETRY_COUNT = 0;
    private static final int MAX_RETRY_COUNT = 3;

    @Autowired
    private  EmailHistoryRepository emailHistoryRepository;

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Autowired
    private EmailScheduleRepository emailScheduleRepository;

    @Autowired
    private EmailProvider emailProvider;

    @Override
    public Email send(Email email) {

        validateRequest(email);
        Email result;
        if(email.getScheduleDate() != null) {
            //handler schedule email
            result = emailScheduleRepository.saveEmailSchedule(email);
        }
        else {
            Long id = emailHistoryRepository.createEmailHistory(email);
            Email history = emailHistoryRepository.getEmail(id);
            //send email
            Request request = Convert.toRequest(history);
            Response response = emailProvider.send(request);
            if(response.getStatusCode() == 200) {
                history.setStatus(EmailStatus.SUCCEED.toString());
                history.setIsResend(false);
                history.setSentDate(new Date());
                Long updateId = emailHistoryRepository.updateEmailHistory(history);
                result = emailHistoryRepository.getEmail(updateId);
                //result = history;
            }
            else {
                throw AppExceptions.INSTANCE.internalError().exception();
            }
        }
        return result;
    }

    private void validateRequest(Email email) {
        if(email == null) {
            throw AppExceptions.INSTANCE.invalidPayload().exception();
        }
        if(email.getUserId() == null && email.getRecipients() == null) {
            throw AppExceptions.INSTANCE.invalidInput().exception();
        }
        if(email.getSource() == null) {
            throw AppExceptions.INSTANCE.missingSource().exception();
        }
        if(email.getAction() == null) {
            throw AppExceptions.INSTANCE.missingAction().exception();
        }
        if(email.getLocale() == null) {
            throw AppExceptions.INSTANCE.missingLocale().exception();
        }
        if(email.getScheduleDate() != null && email.getScheduleDate().before(new Date())) {
            throw AppExceptions.INSTANCE.invalidScheduleDate().exception();
        }
        checkProperties(email);
    }

    private void checkProperties(Email email) {
        String templateName = String.format("%s.%s.%s",email.getSource(), email.getAction(), email.getLocale());
        EmailTemplateEntity templateEntity = emailTemplateRepository.getEmailTemplateByName(templateName);

        if(templateEntity == null) {
            throw AppExceptions.INSTANCE.templateNotFound().exception();
        }
        if(templateEntity.getVars() != null && email.getProperties() == null) {
            throw AppExceptions.INSTANCE.invalidPayload().exception();
        }
        if(templateEntity.getVars() != null) {
            List<String> variables = Utils.toObject(templateEntity.getVars(),List.class);
            //properties check
            for(String key : email.getProperties().keySet()) {
                if(!variables.contains(key.replaceAll("\\d*$",""))) {
                    throw AppExceptions.INSTANCE.invalidProperty(key).exception();
                }
            }
        }
    }

}
