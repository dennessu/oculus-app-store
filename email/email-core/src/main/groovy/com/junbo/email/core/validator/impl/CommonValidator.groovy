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
            throw AppErrors.INSTANCE.emailTemplateNotFound(email.templateId).exception()
        }
        if (!template.placeholderNames?.any()) {
            return
        }
        def replacements = new HashMap<String,String>()
        def placeholders = template.placeholderNames.collect { String placeholder ->
            placeholder.toLowerCase(Locale.ENGLISH)
        }
        if (placeholders?.any()) {
            placeholders.each{String placeholder ->
                replacements.put(placeholder,'')
            }
            email.replacements.each { Map.Entry<String, String> replacement ->
                def key = replacement.key
                if (key.contains(':') && !key.startsWith(':')) {
                    def prefix = key.split(':').first()
                    replacements.remove(prefix.toLowerCase(Locale.ENGLISH))
                    key = key.replaceFirst(prefix, prefix.toLowerCase(Locale.ENGLISH))
                }
                else {
                    key = key.toLowerCase(Locale.ENGLISH)
                }
                if (placeholders.contains(key.replaceAll('\\d*(:\\w*)?$', ''))) {
                    replacements.put(key,replacement.value)
                }
            }
        }

        email.replacements = replacements
    }
}
