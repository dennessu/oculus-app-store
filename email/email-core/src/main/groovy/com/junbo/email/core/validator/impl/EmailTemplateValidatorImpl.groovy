/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.email.common.util.PlaceholderUtils
import com.junbo.email.core.validator.EmailTemplateValidator
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.Pagination
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * Impl of EmailTemplate validator.
 */
@CompileStatic
@Component
class EmailTemplateValidatorImpl extends CommonValidator implements EmailTemplateValidator {

    @Override
    void validateCreate(EmailTemplate template) {
        this.validateCommonField(template)
        this.validatePlaceholderNamesField(template)
        this.validateTemplateName(template.name)
    }

    @Override
    void validateUpdate(EmailTemplate template, String templateId) {
        this.validateTemplateId(templateId)
        this.validateCommonField(template)
        this.validatePlaceholderNamesField(template)
    }

    @Override
    void validateDelete(String id) {
        if (id == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('id').exception()
        }
    }

    @Override
    void validateGet(Pagination pagination) {
        if (pagination?.page != null && pagination.page < 1) {
            throw AppCommonErrors.INSTANCE.parameterInvalid('page').exception()
        }
        if (pagination?.size != null && pagination.size < 1) {
            throw AppCommonErrors.INSTANCE.parameterInvalid('size').exception()
        }
    }

    private void validateCommonField(EmailTemplate template) {
        if (template == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        if (template.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('self').exception()
        }
        if (StringUtils.isEmpty(template.source)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('source').exception()
        }
        if (StringUtils.isEmpty(template.action)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('action').exception()
        }
        if (StringUtils.isEmpty(template.locale)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('locale').exception()
        }
        if (template.providerName == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('providerName').exception()
        }
        if (template.fromAddress != null && !validateEmailAddress(template.fromAddress)) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('fromAddress').exception()
        }
    }

    private void validateTemplateName(String name) {
        EmailTemplate template = Promise.get { emailTemplateRepository.getEmailTemplateByName(name) }
        if (template != null) {
            throw AppErrors.INSTANCE.emailTemplateAlreadyExist(name).exception()
        }
    }

    private void validatePlaceholderNamesField(EmailTemplate template) {
        if (!StringUtils.isEmpty(template.subject)) {
            List<String> placeholders = PlaceholderUtils.retrievePlaceholders(template.subject);
            if (!PlaceholderUtils.compare(placeholders, template.placeholderNames)) {
                throw AppErrors.INSTANCE.invalidPlaceholderNames("subject").exception()
            }
        }
    }

    private void validateTemplateId(String id) {
        EmailTemplate template = Promise.get { emailTemplateRepository.getEmailTemplate(id) }
        if (template == null) {
            throw AppErrors.INSTANCE.templateNotFound(id).exception()
        }
    }
}
