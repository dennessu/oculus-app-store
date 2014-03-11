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
import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Repository of EmailTemplate.
 */
@Component
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
        return (Long) emailTemplateDao.save(entity);
    }

    public EmailTemplateEntity getEmailTemplate(Long id) {
        return emailTemplateDao.get(id);
    }


    public EmailTemplateEntity getEmailTemplateByName(String name) {
        return emailTemplateDao.getEmailTemplateByName(name);
    }

    public List<EmailTemplateEntity> getEmailTemplateList() {
        return emailTemplateDao.getEmailTemplateList();
    }

    public void flush() {
        emailTemplateDao.flush();
    }
}
