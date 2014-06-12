/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.service

import com.junbo.common.id.EmailTemplateId
import com.junbo.common.model.Results
import com.junbo.email.core.EmailTemplateService
import com.junbo.email.core.validator.EmailTemplateValidator
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.transaction.Transactional

/**
 * Impl of EmailTemplateService.
 */
@CompileStatic
@Component
@Transactional
 class EmailTemplateServiceImpl implements EmailTemplateService {

    private EmailTemplateRepository templateRepository

    private EmailTemplateValidator templateValidator

    void setTemplateRepository(EmailTemplateRepository templateRepository) {
        this.templateRepository = templateRepository
    }

    void setTemplateValidator(EmailTemplateValidator templateValidator) {
        this.templateValidator = templateValidator
    }

    Promise<EmailTemplate> postEmailTemplate(EmailTemplate template) {
        this.build(template)
        templateValidator.validateCreate(template)
        return templateRepository.saveEmailTemplate(template)
    }

    Promise<EmailTemplate> getEmailTemplate(Long id) {
        return templateRepository.getEmailTemplate(id)
    }

    Promise<EmailTemplate> putEmailTemplate(Long id, EmailTemplate template) {
        templateValidator.validateUpdate(template, id)
        template.setId(new EmailTemplateId(id))
        this.build(template)
        return templateRepository.updateEmailTemplate(template)
    }

    Void deleteEmailTemplate(Long id) {
        templateValidator.validateDelete(id)
        templateRepository.deleteEmailTemplate(id)
        return null
    }

    Promise<Results<EmailTemplate>> getEmailTemplates(QueryParam queryParam) {
        def queries = this.buildQueryParam(queryParam)
        return templateRepository.getEmailTemplates(queries, null).then { List<EmailTemplate> templates ->
            def results = new Results<EmailTemplate>(items:[])
            if (templates != null && templates.size() !=0) {
                results.items.addAll(templates)
            }
            return Promise.pure(results)
        }
    }

    private void build(EmailTemplate template) {
        if (template != null) {
           template.name = "${template.source}.${template.action}.${template.locale}"
        }
    }

    private Map<String, String> buildQueryParam(QueryParam queryParam) {
        def map = [:]
        if (!StringUtils.isEmpty(queryParam?.action)) {
            map.put('action', queryParam.action)
        }
        if (!StringUtils.isEmpty(queryParam?.source)) {
            map.put('source', queryParam.source)
        }
        if (!StringUtils.isEmpty(queryParam?.locale)) {
            map.put('locale', queryParam.locale)
        }
        return map
    }
}
