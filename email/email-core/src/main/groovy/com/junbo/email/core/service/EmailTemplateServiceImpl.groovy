/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.service

import com.junbo.common.id.EmailTemplateId
import com.junbo.common.model.Link
import com.junbo.common.model.Results
import com.junbo.email.core.EmailTemplateService
import com.junbo.email.core.validator.EmailTemplateValidator
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.langur.core.promise.Promise
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.ws.rs.core.UriBuilder
import javax.ws.rs.core.UriInfo

/**
 * Impl of EmailTemplateService.
 */
@Component
 class EmailTemplateServiceImpl implements EmailTemplateService {

    @Autowired
    private EmailTemplateRepository templateRepository

    @Autowired
    private EmailTemplateValidator templateValidator

    @Autowired
    private UriInfo uriInfo

    Promise<EmailTemplate> postEmailTemplate(EmailTemplate template) {
        this.build(template)
        templateValidator.validateCreate(template)
        Long id = templateRepository.saveEmailTemplate(template)
        return Promise.pure(templateRepository.getEmailTemplate(id))
    }

    Promise<EmailTemplate> getEmailTemplate(Long id) {
        EmailTemplate template = templateRepository.getEmailTemplate(id)
        return Promise.pure(template)
    }

    Promise<EmailTemplate> putEmailTemplate(Long id, EmailTemplate template) {
        templateValidator.validateUpdate(template)
        template.setId(new EmailTemplateId(id))
        this.build(template)
        return Promise.pure(templateRepository.updateEmailTemplate(template))
    }

    Void deleteEmailTemplate(Long id) {
        templateValidator.validateDelete(id)
        templateRepository.deleteEmailTemplate(id)
        return null
    }

    Promise<Results<EmailTemplate>> getEmailTemplates(QueryParam queryParam) {
        def queries = this.buildQueryParam(queryParam)
        List<EmailTemplate> templates = templateRepository.getEmailTemplates(queries, null)
        Results<EmailTemplate> results = buildResults(templates, queryParam)
        return Promise.pure(results)
    }

    private Results<EmailTemplate> buildResults(List<EmailTemplate> templates, QueryParam queryParam) {
        Results<EmailTemplate> results = new Results<>()
        if (templates != null) {
            results.setItems(templates)
            results.setSelf(buildLink(queryParam))
            results.setHasNext(false)
        }
        return  results
    }

    private Link buildLink(QueryParam queryParam) {
        Link link = new Link()
        UriBuilder uri = uriInfo.baseUriBuilder.path('email-templates')
        if (queryParam.source != null) {
            uri.queryParam('source', queryParam.source)
        }
        if (queryParam.action != null) {
            uri.queryParam('action', queryParam.action)
        }
        if (queryParam.locale != null) {
            uri.queryParam('locale', queryParam.locale)
        }
        link.setHref(uri.toTemplate())
        return link
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
