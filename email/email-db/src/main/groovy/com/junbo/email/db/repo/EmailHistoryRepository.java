/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo;

import com.junbo.common.id.EmailId;
import com.junbo.email.db.dao.EmailHistoryDao;
import com.junbo.email.db.entity.EmailHistoryEntity;
import com.junbo.email.db.mapper.EmailMapper;
import com.junbo.email.spec.model.Email;
import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Repository of EmailHistory.
 */
@Component
@Transactional
public class EmailHistoryRepository {
    @Autowired
    private EmailHistoryDao emailHistoryDao;

    @Autowired
    private EmailMapper emailMapper;

    @Autowired
    private IdGeneratorFacade idGenerator;

    public Long updateEmailHistory(Email email) {
        EmailHistoryEntity entity = emailMapper.toEmailHistoryEntity(email);
        EmailHistoryEntity savedEntity  = emailHistoryDao.get(entity.getId());
        savedEntity.setStatus(entity.getStatus());
        savedEntity.setStatusReason(entity.getStatusReason());
        savedEntity.setRetryCount(entity.getRetryCount());
        savedEntity.setSentDate(entity.getSentDate());
        savedEntity.setIsResend(entity.getIsResend());
        savedEntity.setUpdatedTime(new Date());
        savedEntity.setUpdatedBy("internal system");

        return  emailHistoryDao.update(savedEntity);
    }

    public Long createEmailHistory(Email email) {
        EmailHistoryEntity entity = emailMapper.toEmailHistoryEntity(email);
        entity.setId(getId(email.getUserId() != null ? email.getUserId().getValue() : null));
        entity.setCreatedTime(new Date());
        entity.setCreatedBy("internal system");

        return emailHistoryDao.save(entity);
    }

    public Email getEmail(Long id) {
        EmailHistoryEntity entity = getEmailHistoryEntity(id);

        return emailMapper.toEmail(entity);
    }

    private EmailHistoryEntity getEmailHistoryEntity(Long id) {
        return emailHistoryDao.get(id);
    }

    private Long getId(Long userId) {
        if(userId != null) {
            return idGenerator.nextId(EmailId.class, userId);
        }

        return idGenerator.nextId(EmailId.class);
    }
}
