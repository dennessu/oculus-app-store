/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo.impl.sql

import com.junbo.email.db.dao.EmailHistoryDao

import com.junbo.email.db.repo.EmailHistoryRepository
import com.junbo.email.spec.model.Email
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Repository of EmailHistory (Sql).
 */
@CompileStatic
class EmailHistoryRepositorySqlImpl extends EmailBaseRepository implements EmailHistoryRepository {
    private EmailHistoryDao emailHistoryDao

    @Required
    void setEmailHistoryDao(EmailHistoryDao emailHistoryDao) {
        this.emailHistoryDao = emailHistoryDao
    }

    public Promise<Email> getEmailHistory(String id) {
        def entity = emailHistoryDao.get(Long.parseLong(id))

        return Promise.pure(emailMapper.toEmail(entity))
    }

    public Promise<Email> createEmailHistory(Email email) {
        def entity = emailMapper.toEmailHistoryEntity(email)
        entity.setId(super.getId(email?.userId?.value))
        entity.setCreatedTime(new Date())
        entity.setCreatedBy("internal system")
        def savedEntity = emailHistoryDao.save(entity)

        return Promise.pure(emailMapper.toEmail(savedEntity))
    }

    public Promise<Email> updateEmailHistory(Email email) {
        def entity = emailMapper.toEmailHistoryEntity(email)
        def savedEntity  = emailHistoryDao.get(entity.id)
        savedEntity.setStatus(entity.status)
        savedEntity.setStatusReason(entity.statusReason)
        savedEntity.setRetryCount(entity.retryCount)
        savedEntity.setSentTime(entity.sentTime)
        savedEntity.setIsResend(entity.isResend)
        savedEntity.setUpdatedTime(new Date())
        savedEntity.setUpdatedBy("internal system")
        def updatedEntity = emailHistoryDao.update(savedEntity)

        return Promise.pure(emailMapper.toEmail(updatedEntity))
    }

    public Promise<Void> deleteEmailHistory(String id) {
        throw new Exception('not implement')
    }
}
