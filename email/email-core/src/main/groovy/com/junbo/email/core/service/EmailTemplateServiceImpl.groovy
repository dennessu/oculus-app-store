/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.service

import com.junbo.common.id.EmailTemplateId
import com.junbo.common.model.Link
import com.junbo.common.model.Results
import com.junbo.email.common.constant.PageConstants
import com.junbo.email.core.EmailTemplateService
import com.junbo.email.core.validator.EmailTemplateValidator
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.Pagination
import com.junbo.email.spec.model.QueryParam
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.ws.rs.core.UriBuilder
import javax.ws.rs.core.UriInfo

/**
 * Impl of EmailTemplateService.
 */
@CompileStatic
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

    Promise<Results<EmailTemplate>> getEmailTemplates(QueryParam queryParam, Pagination pagination) {
        templateValidator.validateGet(pagination)
        def queries = this.buildQueryParam(queryParam)
        pagination = this.buildPagination(pagination)
        List<EmailTemplate> templates = templateRepository.getEmailTemplates(queries, pagination)
        Results<EmailTemplate> results = buildResults(templates, queryParam, pagination)
        return Promise.pure(results)
    }

    private Results<EmailTemplate> buildResults(List<EmailTemplate> templates,
                                                QueryParam queryParam, Pagination pagination) {
        Results<EmailTemplate> results = new Results<>()
        if (templates != null) {
            results.setItems(templates)
            results.setSelf(this.buildLink(queryParam, pagination, false))
            results.setHasNext(false)
            if (templates.size() == pagination.size) {
                results.setHasNext(true)
                results.setNext(this.buildLink(queryParam, pagination, true))
            }
        }
        return  results
    }

    private Link buildLink(QueryParam queryParam, Pagination pagination, boolean  isNext) {
        Link link = new Link()
        UriBuilder uri = uriInfo.baseUriBuilder.path('email-templates')
        if (queryParam?.source != null) {
            uri.queryParam('source', queryParam.source)
        }
        if (queryParam?.action != null) {
            uri.queryParam('action', queryParam.action)
        }
        if (queryParam?.locale != null) {
            uri.queryParam('locale', queryParam.locale)
        }
        uri.queryParam('size', pagination.size)
        uri.queryParam('page', isNext ? pagination.page + 1 : pagination.page)
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

    private Pagination buildPagination(Pagination pagination) {
        def ret = new Pagination()
        if (pagination?.size == null || pagination?.size < PageConstants.DEFAULT_PAGE_SIZE ) {
            ret.setSize(PageConstants.DEFAULT_PAGE_SIZE)
        }
        else if (pagination?.size > PageConstants.MAX_PAGE_SIZE) {
            ret.setSize(PageConstants.MAX_PAGE_SIZE)
        }
        else {
            ret.setSize(pagination.size)
        }
        if (pagination?.page == null) {
            ret.setPage(PageConstants.DEFAULT_PAGE_NUMBER)
        }
        else {
            ret.setPage(pagination.page)
        }
        return ret
    }
}
