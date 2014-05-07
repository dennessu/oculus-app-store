/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo;

import com.junbo.email.db.dao.EmailTemplateDao;
import com.junbo.email.db.entity.EmailTemplateEntity;
import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.email.spec.model.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Repository of EmailTemplate.
 */
@Component
public class EmailTemplateRepository extends EmailBaseRepository {
    @Autowired
    private EmailTemplateDao emailTemplateDao;

    public Long saveEmailTemplate(EmailTemplate template){
        EmailTemplateEntity entity = emailMapper.toEmailTemplateEntity(template);
        entity.setId(idGenerator.nextIdByShardId(0));
        entity.setCreatedBy("internal system");
        entity.setCreatedTime(new Date());

        return emailTemplateDao.save(entity);
    }

    public EmailTemplate getEmailTemplate(Long id) {
        EmailTemplateEntity entity = emailTemplateDao.get(id);
        return emailMapper.toEmailTemplate(entity);
    }

    public EmailTemplate updateEmailTemplate(EmailTemplate template) {
        EmailTemplateEntity entity = emailMapper.toEmailTemplateEntity(template);
        EmailTemplateEntity savedEntity = emailTemplateDao.get(template.getId().getValue());
        this.merge(entity, savedEntity);
        savedEntity.setUpdatedTime(new Date());
        savedEntity.setUpdatedBy("internal system");
        Long id = emailTemplateDao.update(savedEntity);
        return emailMapper.toEmailTemplate(emailTemplateDao.get(id));
    }

    public void deleteEmailTemplate(Long id) {
        EmailTemplateEntity entity = emailTemplateDao.get(id);
        if(entity != null) {
            emailTemplateDao.delete(entity);
        }
    }

    public EmailTemplate getEmailTemplateByName(String name) {
        EmailTemplateEntity entity = emailTemplateDao.getEmailTemplateByName(name);
        return emailMapper.toEmailTemplate(entity);
    }

    public List<EmailTemplate> getEmailTemplates(Map<String, String> queries, Pagination pagination) {
        List<EmailTemplateEntity> entities = emailTemplateDao.getEmailTemplatesByQuery(queries, pagination);
        return emailMapper.toEmailTemplates(entities);
    }

    private void merge(EmailTemplateEntity updateEntity, EmailTemplateEntity savedEntity) {
        savedEntity.setName(updateEntity.getName());
        savedEntity.setSource(updateEntity.getSource());
        savedEntity.setAction(updateEntity.getAction());
        savedEntity.setLocale(updateEntity.getLocale());
        savedEntity.setPlaceholderNames(updateEntity.getPlaceholderNames());
        savedEntity.setProviderIndex(updateEntity.getProviderIndex());
        savedEntity.setProviderName(updateEntity.getProviderName());
        savedEntity.setFromAddress(updateEntity.getFromAddress());
        savedEntity.setFromName(updateEntity.getFromName());
        savedEntity.setSubject(updateEntity.getSubject());
    }
}
