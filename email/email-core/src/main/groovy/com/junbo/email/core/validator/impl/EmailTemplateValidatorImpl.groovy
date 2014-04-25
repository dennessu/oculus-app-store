/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator.impl

import com.junbo.email.core.validator.EmailTemplateValidator
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.EmailTemplate
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * Impl of EmailTemplate validator.
 */
@Component
class EmailTemplateValidatorImpl extends CommonValidator implements EmailTemplateValidator {

    void validateCreate(EmailTemplate template) {
        this.validateCommonField(template)
        this.validateTemplateName(template.name)
    }

    void validateUpdate(EmailTemplate template) {
        this.validateCommonField(template)
    }

    void validateDelete(Long id) {
        if (id == null) {
            throw AppErrors.INSTANCE.invalidEmailId('').exception()
        }
    }

//    void validateGet(QueryParam queryParam) {
//        if (queryParam.page != null && queryParam.page < 1) {
//            throw AppErrors.INSTANCE.invalidParameter('page').exception()
//        }
//        if (queryParam.size != null && queryParam.size < 1) {
//            throw AppErrors.INSTANCE.invalidParameter('size').exception()
//        }
//    }

    private void validateCommonField(EmailTemplate template) {
        if (template == null) {
            throw AppErrors.INSTANCE.invalidPayload().exception()
        }
        if (template.id != null) {
            throw AppErrors.INSTANCE.unnecessaryField('self').exception()
        }
        if (StringUtils.isEmpty(template.source)) {
            throw AppErrors.INSTANCE.missingField('source').exception()
        }
        if (StringUtils.isEmpty(template.action)) {
            throw AppErrors.INSTANCE.missingField('action').exception()
        }
        if (StringUtils.isEmpty(template.locale)) {
            throw AppErrors.INSTANCE.missingField('locale').exception()
        }
        if (template.providerName == null) {
            throw AppErrors.INSTANCE.missingField('providerName').exception()
        }
        if (template.fromAddress != null && !validateEmailAddress(template.fromAddress)) {
            throw AppErrors.INSTANCE.invalidField('fromAddress').exception()
        }
    }

    private void validateTemplateName(String name) {
        EmailTemplate template = emailTemplateRepository.getEmailTemplateByName(name)
        if (template != null) {
            throw AppErrors.INSTANCE.emailTemplateAlreadyExist('').exception()
        }
    }
}
