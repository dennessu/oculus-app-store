/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator.impl

import com.junbo.email.core.validator.EmailValidator
import com.junbo.email.db.repo.EmailScheduleRepository
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPii
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Impl of EmailValidator.
 */
@Component
class EmailValidatorImpl extends CommonValidator implements EmailValidator {

    @Autowired
    private EmailScheduleRepository emailScheduleRepository

    void validateCreate(Email email) {
        this.validateEmailId(email)
        super.validateCommonField(email)
        super.validateProhibitedFields(email)
        super.validateScheduleTime(email, false)
        super.validateAuditDate(email)
    }

    void validateUpdate(Email email) {
        super.validateCommonField(email)
        super.validateProhibitedFields(email)
        super.validateScheduleTime(email, true)
        super.validateAuditDate(email)
        validateExistEmailSchedule(email.id.value)
    }

    void validateDelete(Long id) {
        if (id == null) {
            throw AppErrors.INSTANCE.invalidEmailId('').exception()
        }
        if (emailScheduleRepository.getEmailSchedule(id) == null) {
            throw AppErrors.INSTANCE.emailScheduleNotFound('').exception()
        }
    }

    void validateReplacements(Email email) {
        String templateName = "${email.source}.${email.action}.${email.locale}"

        EmailTemplate template = emailTemplateRepository.getEmailTemplateByName(templateName)

        if (template == null) {
            throw AppErrors.INSTANCE.templateNotFound(templateName).exception()
        }
        if (template.listOfVariables != null && email.replacements == null) {
            throw AppErrors.INSTANCE.invalidProperty('replacements').exception()
        }
        if (template.listOfVariables != null) {
            List<String> variables = toLowerCase(template.listOfVariables)
            for (String key : email.replacements.keySet()) {
                if (!variables.contains(key.replaceAll('\\d*(:\\w*)?$','').toLowerCase())) {
                    throw AppErrors.INSTANCE.invalidProperty(key).exception()
                }
            }
        }
    }

    void validateUser(User user) {
        super.validateUser(user)
    }

    void validateUserPii(UserPii userPii) {
        super.validateUserPii(userPii)
    }

    private void validateExistEmailSchedule(Long id) {
        Email email = emailScheduleRepository.getEmailSchedule(id)
        if (email == null) {
            throw AppErrors.INSTANCE.emailScheduleNotFound('').exception()
        }
    }

    private List<String> toLowerCase(List<String> properties) {
        List<String> list = new ArrayList<>()
        for (String property: properties) {
            list.add(property.toLowerCase())
        }
        return list
    }

    private void validateEmailId(Email email) {
        if (email.id != null) {
            throw AppErrors.INSTANCE.invalidEmailId('').exception()
        }
    }
}
