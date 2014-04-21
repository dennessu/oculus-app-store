/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator.impl

import com.junbo.email.core.validator.EmailTemplateValidator
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.Paging
import org.springframework.stereotype.Component

/**
 * Impl of EmailTemplate validator.
 */
@Component
class EmailTemplateValidatorImpl extends CommonValidator implements EmailTemplateValidator {

    void validateCreate(EmailTemplate template) {
        if (template.providerName == null) {
            throw AppErrors.INSTANCE.missingField('providerName').exception()
        }
        if (template.fromAddress != null && !validateEmailAddress(template.fromAddress)) {
            throw AppErrors.INSTANCE.invalidField('fromAddress').exception()
        }
        if (template.name == null) {
            throw AppErrors.INSTANCE.missingField('name').exception()
        }
        validateTemplateNameExist(template.name())
        validateAuditDate(template)
    }

    void validateUpdate(EmailTemplate template) {
        if (template.name != null) {
            throw AppErrors.INSTANCE.unnecessaryField('name').exception()
        }
        if (template.fromAddress != null && !validateEmailAddress(template.fromAddress)) {
            throw AppErrors.INSTANCE.invalidField('fromAddress').exception()
        }
        validateAuditDate(template)
    }

    void validateDelete(Long id) {
        if (id == null) {
            throw AppErrors.INSTANCE.invalidEmailId('').exception()
        }
    }

    void validateGet(Paging paging) {
        if (paging.page != null && paging.page < 1) {
            throw AppErrors.INSTANCE.invalidParameter('page').exception()
        }
        if (paging.size != null && paging.size < 1) {
            throw AppErrors.INSTANCE.invalidParameter('size').exception()
        }
    }

    private void validateTemplateNameExist(String name) {
        EmailTemplate template = emailTemplateRepository.getEmailTemplateByName(name)
        if (template != null) {
            throw AppErrors.INSTANCE.emailTemplateAlreadyExist(name).exception()
        }
    }
}
