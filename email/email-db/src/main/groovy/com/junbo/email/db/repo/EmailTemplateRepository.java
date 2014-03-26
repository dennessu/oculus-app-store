/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo;

import com.junbo.common.id.EmailId;
import com.junbo.email.db.dao.EmailTemplateDao;
import com.junbo.email.db.entity.EmailTemplateEntity;
import com.junbo.email.db.mapper.EmailMapper;
import com.junbo.email.spec.model.EmailTemplate;
import com.junbo.email.spec.model.Paging;
import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Repository of EmailTemplate.
 */
@Component
@Transactional
public class EmailTemplateRepository {
    @Autowired
    private EmailTemplateDao emailTemplateDao;

    @Autowired
    private EmailMapper emailMapper;

    @Autowired
    private IdGeneratorFacade idGenerator;

    public Long saveEmailTemplate(EmailTemplate template){
        EmailTemplateEntity entity = emailMapper.toEmailTemplateEntity(template);
        entity.setId(idGenerator.nextId(EmailId.class));
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
        entity.setName(savedEntity.getName());
        entity.setCreatedTime(savedEntity.getCreatedTime());
        entity.setCreatedBy(savedEntity.getCreatedBy());
        entity.setUpdatedTime(new Date());
        entity.setUpdatedBy("internal system");
        Long id = emailTemplateDao.update(entity);
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

    public List<EmailTemplate> getEmailTemplates(Map<String, String> queries, Paging paging) {
        List<EmailTemplateEntity> entities = emailTemplateDao.getEmailTemplatesByQuery(queries, paging);
        return emailMapper.toEmailTemplates(entities);
    }
}
