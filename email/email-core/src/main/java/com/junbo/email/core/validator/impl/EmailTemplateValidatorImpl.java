/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator.impl;

import com.junbo.email.core.validator.EmailTemplateValidator;
import com.junbo.email.spec.error.AppErrors;
import com.junbo.email.spec.model.EmailTemplate;
import org.springframework.stereotype.Component;

/**
 * Impl of EmailTemplate validator.
 */
@Component
public class EmailTemplateValidatorImpl extends CommonValidator implements EmailTemplateValidator {

    public void validateCreate(EmailTemplate template) {
        if(template.getProviderName() == null) {
            throw AppErrors.INSTANCE.missingField("providerName").exception();
        }
        if(template.getFromAddress() != null && !validateEmailAddress(template.getFromAddress())) {
            throw AppErrors.INSTANCE.invalidField("fromAddress").exception();
        }
        if(template.getName() == null){
            throw AppErrors.INSTANCE.missingField("name").exception();
        }
        validateTemplateNameExist(template.getName());
        validateAuditDate(template);
    }

    public void validateUpdate(EmailTemplate template) {
        if(template.getName() != null) {
            throw AppErrors.INSTANCE.unnecessaryField("name").exception();
        }
        if(template.getFromAddress() != null && !validateEmailAddress(template.getFromAddress())) {
            throw AppErrors.INSTANCE.invalidField("fromAddress").exception();
        }
        validateAuditDate(template);
    }

    private void validateTemplateNameExist(String name) {
        EmailTemplate template = emailTemplateRepository.getEmailTemplateByName(name);
        if(template != null) {
            throw AppErrors.INSTANCE.emailTemplateAlreadyExist(name).exception();
        }
    }
}
