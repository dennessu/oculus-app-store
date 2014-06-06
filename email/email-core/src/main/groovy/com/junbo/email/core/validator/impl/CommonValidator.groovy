/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator.impl

import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.Model
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

import java.util.regex.Pattern

/**
 * Common Validator.
 */
@CompileStatic
abstract class CommonValidator {

    protected EmailTemplateRepository emailTemplateRepository

    private Pattern emailPattern

    void setEmailTemplateRepository(EmailTemplateRepository emailTemplateRepository) {
        this.emailTemplateRepository = emailTemplateRepository
    }

    void setEmailPattern(String emailPattern) {
        this.emailPattern = Pattern.compile(emailPattern)
    }

    protected void validateScheduleTime(Email email, boolean required) {
        if (required && email.scheduleTime == null) {
            throw AppErrors.INSTANCE.missingField('scheduleTime').exception()
        }
        if (email.scheduleTime != null && email.scheduleTime.before(new Date())) {
            throw AppErrors.INSTANCE.invalidField('scheduleTime').exception()
        }
    }

    protected void validateAuditDate(Model model) {
        if (model.createdTime != null) {
            throw AppErrors.INSTANCE.unnecessaryField('createTime').exception()
        }
        if (model.modifiedTime != null) {
            throw AppErrors.INSTANCE.unnecessaryField('modifiedTime').exception()
        }
    }

    protected boolean validateEmailAddress(String email) {
        if (!StringUtils.isEmpty(email)) {
            return emailPattern.matcher(email).matches()
        }
        return false
    }

    protected void validateEmailTemplate(Email email) {
        EmailTemplate template = emailTemplateRepository.getEmailTemplate(email.templateId.value)

        if (template == null) {
            throw AppErrors.INSTANCE.templateNotFound().exception()
        }
        if (template.placeholderNames?.any() && !email.replacements?.any()) {
            throw AppErrors.INSTANCE.invalidReplacements('replacements').exception()
        }
        if (!template.placeholderNames?.any() && email.replacements?.any()) {
            throw AppErrors.INSTANCE.invalidReplacements('replacements').exception()
        }
        if (template.placeholderNames?.any()) {
            List<String> placeholderNames = template.placeholderNames.collect { String placeholderName ->
                placeholderName.toLowerCase() }
            for (String key : email.replacements.keySet()) {
                if (!placeholderNames.contains(key.replaceAll('\\d*(:\\w*)?$', '').toLowerCase())) {
                    throw AppErrors.INSTANCE.invalidReplacements(key).exception()
                }
            }
        }
    }
}
