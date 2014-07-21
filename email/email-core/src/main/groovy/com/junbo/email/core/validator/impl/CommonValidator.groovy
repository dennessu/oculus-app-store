/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.model.ResourceMeta
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.langur.core.promise.Promise
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
            throw AppCommonErrors.INSTANCE.fieldRequired('scheduleTime').exception()
        }
        if (email.scheduleTime != null && email.scheduleTime.before(new Date())) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('scheduleTime', 'scheduleTime cannot be before now').exception()
        }
    }

    protected void validateAuditDate(ResourceMeta model) {
        if (model.createdTime != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('createTime').exception()
        }
        if (model.updatedTime != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('updatedTime').exception()
        }
    }

    protected boolean validateEmailAddress(String email) {
        if (!StringUtils.isEmpty(email)) {
            return emailPattern.matcher(email).matches()
        }
        return false
    }

    protected void validateEmailTemplate(Email email) {
        EmailTemplate template = emailTemplateRepository.getEmailTemplate(email.templateId.value).get()

        if (template == null) {
            throw AppErrors.INSTANCE.templateNotFound(email.templateId).exception()
        }
        if (template.placeholderNames?.any() && !email.replacements?.any()) {
            throw AppErrors.INSTANCE.invalidReplacements().exception()
        }
        if (!template.placeholderNames?.any() && email.replacements?.any()) {
            throw AppErrors.INSTANCE.invalidReplacements().exception()
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
