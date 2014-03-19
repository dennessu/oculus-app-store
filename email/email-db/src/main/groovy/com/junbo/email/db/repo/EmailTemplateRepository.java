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
import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

    public Long saveEmailTemplate(EmailTemplateEntity entity){
        entity.setId(idGenerator.nextId(EmailId.class));
        entity.setCreatedBy("internal system");
        entity.setCreatedTime(new Date());

        return emailTemplateDao.save(entity);
    }

    public EmailTemplateEntity getEmailTemplate(Long id) {
        return emailTemplateDao.get(id);
    }


    public EmailTemplate getEmailTemplateByName(String name) {
        EmailTemplateEntity entity = emailTemplateDao.getEmailTemplateByName(name);
        return emailMapper.toEmailTemplate(entity);
    }

    public List<EmailTemplateEntity> getEmailTemplateList() {
        return emailTemplateDao.getEmailTemplateList();
    }
}
