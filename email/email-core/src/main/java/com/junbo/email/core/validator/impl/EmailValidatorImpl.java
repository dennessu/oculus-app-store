/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator.impl;

import com.junbo.email.core.validator.EmailValidator;
import com.junbo.email.db.repo.EmailScheduleRepository;
import com.junbo.email.spec.error.AppErrors;
import com.junbo.email.spec.model.Email;
import com.junbo.email.spec.model.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Impl of EmailValidator.
 */
@Component
public class EmailValidatorImpl extends CommonValidator implements EmailValidator {

    @Autowired
    private EmailScheduleRepository emailScheduleRepository;

    public void validateCreate(Email email) {
        super.validateCommonField(email);
        super.validateProhibitedFields(email);
        super.validateScheduleDate(email, false);
        super.validateAuditDate(email);
        //TODO:need to remove it when integration with identity
        validateProperties(email);
    }

    public void validateUpdate(Email email) {
        super.validateCommonField(email);
        super.validateProhibitedFields(email);
        super.validateScheduleDate(email,true);
        super.validateAuditDate(email);
        validateExistEmailSchedule(email.getId().getValue());
        //TODO:need to remove it when integration with identity
        validateProperties(email);
    }

    public void validateDelete(Long id) {
        if(id == null) {
            throw AppErrors.INSTANCE.invalidEmailId("id").exception();
        }
        if(emailScheduleRepository.getEmailSchedule(id) == null) {
            throw AppErrors.INSTANCE.emailScheduleNotFound(id.toString()).exception();
        }
    }

    private void validateExistEmailSchedule(Long id) {
        Email email = emailScheduleRepository.getEmailSchedule(id);
        if(email == null) {
            throw AppErrors.INSTANCE.emailScheduleNotFound(id.toString()).exception();
        }
    }

    private void validateProperties(Email email) {
        String templateName = String.format("%s.%s.%s",email.getSource(), email.getAction(), email.getLocale());
        EmailTemplate template = emailTemplateRepository.getEmailTemplateByName(templateName);

        if(template == null) {
            throw AppErrors.INSTANCE.templateNotFound(templateName).exception();
        }
        if(template.getListOfVariables() != null && email.getProperties() == null) {
            throw AppErrors.INSTANCE.invalidProperty("properties").exception();
        }
        if(template.getListOfVariables() != null) {
            List<String> variables = toLowerCase(template.getListOfVariables());
            for(String key : email.getProperties().keySet()) {
                if(!variables.contains(key.replaceAll("\\d*$","").toLowerCase())) {
                    throw AppErrors.INSTANCE.invalidProperty(key).exception();
                }
            }
        }
    }

    private List<String> toLowerCase(List<String> properties) {
        List<String> list = new ArrayList<>();
        for(String property: properties) {
            list.add(property.toLowerCase());
        }
        return list;
    }
}
