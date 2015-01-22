/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.service

import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.EmailTemplateId
import com.junbo.common.model.Results
import com.junbo.email.core.EmailTemplateLocaleService
import com.junbo.email.core.EmailTemplateService
import com.junbo.email.core.validator.EmailTemplateValidator
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.identity.spec.v1.resource.LocaleResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required
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

    private LocaleResource localeResource

    private EmailTemplateLocaleService emailTemplateLocaleService

    private Boolean enableEmailTemplate

    private String defaultEmailTemplateLocale

    void setTemplateRepository(EmailTemplateRepository templateRepository) {
        this.templateRepository = templateRepository
    }

    void setTemplateValidator(EmailTemplateValidator templateValidator) {
        this.templateValidator = templateValidator
    }

    void setLocaleResource(LocaleResource localeResource) {
        this.localeResource = localeResource
    }

    void setEmailTemplateLocaleService(EmailTemplateLocaleService emailTemplateLocaleService) {
        this.emailTemplateLocaleService = emailTemplateLocaleService
    }

    void setEnableEmailTemplate(Boolean enableEmailTemplate) {
        this.enableEmailTemplate = enableEmailTemplate
    }

    void setDefaultEmailTemplateLocale(String defaultEmailTemplateLocale) {
        this.defaultEmailTemplateLocale = defaultEmailTemplateLocale
    }

    Promise<EmailTemplate> postEmailTemplate(EmailTemplate template) {
        this.build(template)
        templateValidator.validateCreate(template)
        return templateRepository.saveEmailTemplate(template)
    }

    Promise<EmailTemplate> getEmailTemplate(String id) {
        return templateRepository.getEmailTemplate(id).then { EmailTemplate emailTemplate ->
            if (emailTemplate == null) {
                throw AppErrors.INSTANCE.emailTemplateNotFound(id).exception()
            }

            return Promise.pure(emailTemplate)
        }
    }

    Promise<EmailTemplate> putEmailTemplate(String id, EmailTemplate template) {
        templateValidator.validateUpdate(template, id)
        template.setId(new EmailTemplateId(id))
        this.build(template)
        return templateRepository.updateEmailTemplate(template)
    }

    Promise<Void> deleteEmailTemplate(String id) {
        templateValidator.validateDelete(id)
        return templateRepository.deleteEmailTemplate(id)
    }

    Promise<Results<EmailTemplate>> getEmailTemplates(QueryParam queryParam) {
        def queries = this.buildQueryParam(queryParam)

        return templateRepository.getEmailTemplates(queries, null).then { List<EmailTemplate> templates ->
            if (queries.get('locale') != null && CollectionUtils.isEmpty(templates)) {
                return Promise.pure(getFallbackEmailTemplates(queries, queries.get('locale')))
            }
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

        if (enableEmailTemplate) {
            String locale = emailTemplateLocaleService.getEmailTemplateLocale(queryParam?.locale, queryParam?.userId).get();
            if (!StringUtils.isEmpty(locale)) {
                map.put('locale', locale)
            }
        } else {
            if (!StringUtils.isEmpty(queryParam?.locale) || queryParam?.userId != null) {
                map.put('locale', defaultEmailTemplateLocale)
            }
        }
        return map
    }

    private Results<EmailTemplate> getFallbackEmailTemplates(Map<String, String> queries, String currentLocale) {
        Map<String, Boolean> circle = new HashMap<>()

        LocaleId current = new LocaleId(currentLocale)
        LocaleId fallback = null
        while (true) {
            com.junbo.identity.spec.v1.model.Locale locale = localeResource.get(current, new LocaleGetOptions()).get()
            Boolean visited = circle.get(locale.getId().toString())
            if (visited) {
                break
            }
            circle.put(locale.getId().toString(), true)

            fallback = locale.fallbackLocale
            if (current == fallback || fallback == null) {
                break
            }

            queries.put('locale', fallback.toString())
            List<EmailTemplate> emailTemplates = templateRepository.getEmailTemplates(queries, null).get()
            if (!CollectionUtils.isEmpty(emailTemplates)) {
                return new Results<EmailTemplate>(
                        items: emailTemplates
                )
            }

            current = fallback
        }

        return new Results<EmailTemplate>()
    }
}
