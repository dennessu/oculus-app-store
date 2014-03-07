/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo;

import com.junbo.email.db.dao.EmailHistoryDao;
import com.junbo.email.db.entity.EmailHistoryEntity;
import com.junbo.email.db.entity.EmailStatus;
import com.junbo.email.db.mapper.EmailMapper;
import com.junbo.email.spec.model.Email;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Repository of EmailHistory.
 */
@Component
public class EmailHistoryRepository {
    @Autowired
    private EmailHistoryDao emailHistoryDao;

    @Autowired
    private EmailMapper emailMapper;

    @Autowired
    private IdGenerator idGenerator;

    public Long updateEmailHistory(EmailHistoryEntity entity) {
        entity.setUpdatedTime(new Date());
        return emailHistoryDao.update(entity);
    }

    public Long createEmailHistory(Email email) {
        EmailHistoryEntity entity = emailMapper.toEmailHistoryEntity(email);
        entity.setId(getId(email.getUserId()));
        entity.setStatus(EmailStatus.PENDING.getId());
        entity.setCreatedTime(new Date());
        return emailHistoryDao.save(entity);
    }

    public Email getEmail(Long id) {
        EmailHistoryEntity entity = getEmailHistoryEntity(id);
        return emailMapper.toEmail(entity);
    }

    public EmailHistoryEntity getEmailHistory(Long id) {
        return getEmailHistoryEntity(id);
    }

    private EmailHistoryEntity getEmailHistoryEntity(Long id) {
        return emailHistoryDao.get(id);
    }

    private Long getId(Long userId) {
        if(userId != null) {
            return idGenerator.nextId(userId);
        }
        return idGenerator.nextId();
    }
}
