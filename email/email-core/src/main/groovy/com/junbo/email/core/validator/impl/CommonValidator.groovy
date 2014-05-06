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
import com.junbo.identity.spec.v1.model.User
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Common Validator.
 */
@CompileStatic
@Component('emailCommonValidator')
class CommonValidator {
    @Autowired
    protected EmailTemplateRepository emailTemplateRepository

    protected void validateUser(User user) {
        if (user == null) {
            throw AppErrors.INSTANCE.invalidUserId('').exception()
        }
        if (user.status != 'ACTIVE') {
            throw AppErrors.INSTANCE.invalidUserStatus('').exception()
        }
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
        //TODO:need to update later.
        if (email == null) {
            return false
        }
        return true
    }

    protected void validateEmailTemplate(Email email) {
        EmailTemplate template = emailTemplateRepository.getEmailTemplate(email.templateId.value)

        if (template == null) {
            throw AppErrors.INSTANCE.templateNotFound('').exception()
        }
        if (template.placeholderNames != null && email.replacements == null) {
            throw AppErrors.INSTANCE.invalidProperty('replacements').exception()
        }
        if (template.placeholderNames != null) {
            List<String> placeholderNames = template.placeholderNames.collect { String placeholderName ->
                placeholderName.toLowerCase() }
            for (String key : email.replacements.keySet()) {
                if (!placeholderNames.contains(key.replaceAll('\\d*(:\\w*)?$', '').toLowerCase())) {
                    throw AppErrors.INSTANCE.invalidProperty(key).exception()
                }
            }
        }
    }
}
