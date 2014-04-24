/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.service

import com.junbo.common.id.EmailId
import com.junbo.common.model.Link
import com.junbo.common.model.Results
import com.junbo.email.common.constant.PagingConstants
import com.junbo.email.core.EmailTemplateService
import com.junbo.email.core.validator.EmailTemplateValidator
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.Paging
import com.junbo.langur.core.promise.Promise
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

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
        templateValidator.validateCreate(template)
        Long id = templateRepository.saveEmailTemplate(template)
        return Promise.pure(templateRepository.getEmailTemplate(id))
    }

    Promise<EmailTemplate> getEmailTemplate(Long id) {
        EmailTemplate template = templateRepository.getEmailTemplate(id)
        return Promise.pure(template)
    }

    Promise<EmailTemplate> putEmailTemplate(Long id, EmailTemplate template) {
        template.setId(new EmailId(id))
        templateValidator.validateUpdate(template)
        return Promise.pure(templateRepository.updateEmailTemplate(template))
    }

    Void deleteEmailTemplate(Long id) {
        templateValidator.validateDelete(id)
        templateRepository.deleteEmailTemplate(id)
        return null
    }

    Promise<Results<EmailTemplate>> getEmailTemplates(Paging paging) {
        templateValidator.validateGet(paging)
        setupPaging(paging)
        List<EmailTemplate> templates = templateRepository.getEmailTemplates(null, paging)
        Results<EmailTemplate> results = buildResults(templates, paging)
        return Promise.pure(results)
    }

    private Results<EmailTemplate> buildResults(List<EmailTemplate> templates, Paging paging) {
        Results<EmailTemplate> results = new Results<>()
        if (templates != null) {
            results.setItems(templates)
            results.setSelf(buildLink(paging, false))
            results.setHasNext(false)
            if (templates.size() == paging.size) {
                results.setNext(buildLink(paging, true))
                results.setHasNext(true)
            }
        }
        return  results
    }

    private void setupPaging(Paging paging) {
        if (paging.size == null) {
            paging.setSize(PagingConstants.DEFAULT_PAGE_SIZE)
        }
        if (paging.page == null) {
            paging.setPage(PagingConstants.DEFAULT_PAGE_NUMBER)
        }
        if (paging.size > PagingConstants.MAX_PAGE_SIZE) {
            paging.setSize(PagingConstants.MAX_PAGE_SIZE)
        }
    }

    private Link buildLink(Paging paging, boolean isNext) {
        Link link = new Link()
        UriBuilder uri = uriInfo.baseUriBuilder.path('email-templates')
        uri.queryParam('page', isNext ? paging.page + 1 : paging.page)
        uri.queryParam('size', paging.size)
        link.setHref(uri.toTemplate())
        return link
    }
}