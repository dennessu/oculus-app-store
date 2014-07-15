/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo.impl.sql

import com.junbo.email.db.dao.EmailTemplateDao
import com.junbo.email.db.entity.EmailTemplateEntity
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.Pagination
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Impl of EmailTemplate Repository (Sql).
 */
@CompileStatic
class EmailTemplateRepositorySqlImpl extends EmailBaseRepository implements EmailTemplateRepository {
    private EmailTemplateDao emailTemplateDao

    @Required
    void setEmailTemplateDao(EmailTemplateDao emailTemplateDao) {
        this.emailTemplateDao = emailTemplateDao
    }
    public Promise<EmailTemplate> getEmailTemplate(String id) {
        EmailTemplateEntity entity = emailTemplateDao.get(Long.parseLong(id))
        return Promise.pure(emailMapper.toEmailTemplate(entity))
    }

    public Promise<EmailTemplate> saveEmailTemplate(EmailTemplate template){
        def entity = emailMapper.toEmailTemplateEntity(template)
        entity.setId(idGenerator.nextId())
        entity.setCreatedBy('internal system')
        entity.setCreatedTime(new Date())
        def savedEntity = emailTemplateDao.save(entity)

        return Promise.pure(emailMapper.toEmailTemplate(savedEntity))
    }

    public Promise<EmailTemplate> updateEmailTemplate(EmailTemplate template) {
        def entity = emailMapper.toEmailTemplateEntity(template)
        def savedEntity = emailTemplateDao.get(template.getId().asLong())
        this.merge(entity, savedEntity)
        savedEntity.setUpdatedTime(new Date())
        savedEntity.setUpdatedBy('internal system')
        def updatedEntity = emailTemplateDao.update(savedEntity)

        return Promise.pure(emailMapper.toEmailTemplate(updatedEntity))
    }

    public Promise<EmailTemplate> getEmailTemplateByName(String name) {
        def entity = emailTemplateDao.getEmailTemplateByName(name)

        return Promise.pure(emailMapper.toEmailTemplate(entity))
    }

    public Promise<List<EmailTemplate>> getEmailTemplates(Map<String, String> queries, Pagination pagination) {
        List<EmailTemplateEntity> entities = emailTemplateDao.getEmailTemplatesByQuery(queries, pagination)

        return Promise.pure(emailMapper.toEmailTemplates(entities))
    }

    public Promise<Void> deleteEmailTemplate(String id) {
        def entity = emailTemplateDao.get(Long.parseLong(id))
        if(entity != null) {
            emailTemplateDao.delete(entity)
        }
        return Promise.pure(null)
    }

    private void merge(EmailTemplateEntity updateEntity, EmailTemplateEntity savedEntity) {
        savedEntity.setName(updateEntity.name)
        savedEntity.setSource(updateEntity.source)
        savedEntity.setAction(updateEntity.action)
        savedEntity.setLocale(updateEntity.locale)
        savedEntity.setPlaceholderNames(updateEntity.placeholderNames)
        savedEntity.setProviderIndex(updateEntity.providerIndex)
        savedEntity.setProviderName(updateEntity.providerName)
        savedEntity.setFromAddress(updateEntity.fromAddress)
        savedEntity.setFromName(updateEntity.fromName)
        savedEntity.setSubject(updateEntity.subject)
    }
}
