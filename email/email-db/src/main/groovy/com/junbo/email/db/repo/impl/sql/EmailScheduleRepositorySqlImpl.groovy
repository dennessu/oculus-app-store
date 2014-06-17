/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo.impl.sql

import com.junbo.email.db.dao.EmailScheduleDao
import com.junbo.email.db.entity.EmailScheduleEntity

import com.junbo.email.db.repo.EmailScheduleRepository
import com.junbo.email.spec.model.Email
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Impl of EmailSchedule Repository (Sql).
 */
@CompileStatic
class EmailScheduleRepositorySqlImpl extends EmailBaseRepository implements EmailScheduleRepository {
    private EmailScheduleDao emailScheduleDao

    @Required
    void setEmailScheduleDao(EmailScheduleDao emailScheduleDao) {
        this.emailScheduleDao = emailScheduleDao
    }

    public Promise<Email> getEmailSchedule(String id) {
        def entity = emailScheduleDao.get(Long.parseLong(id))
        return Promise.pure(emailMapper.toEmailSchedule(entity))
    }

    public Promise<Email> saveEmailSchedule(Email email) {
        def entity = emailMapper.toEmailScheduleEntity(email)
        entity.setId(super.getId(entity.userId))
        entity.setCreatedTime(new Date())
        entity.setCreatedBy("internal system")
        def savedEntity = emailScheduleDao.save(entity)

        return Promise.pure(emailMapper.toEmailSchedule(savedEntity))
    }

    public Promise<Email> updateEmailSchedule(Email email) {
        def entity = emailMapper.toEmailScheduleEntity(email)
        def savedEntity = emailScheduleDao.get(entity.id)
        this.merge(savedEntity, entity)
        savedEntity.setUpdatedTime(new Date())
        savedEntity.setUpdatedBy("internal system")
        def updatedEntity = emailScheduleDao.update(savedEntity)

        return Promise.pure(emailMapper.toEmailSchedule(updatedEntity))
    }

    public Promise<Void> deleteEmailSchedule(String id) {
        emailScheduleDao.deleteEmailScheduleById(Long.parseLong(id))
        return Promise.pure(null)
    }

    private void merge(EmailScheduleEntity savedEntity, EmailScheduleEntity updateEntity){
        if(updateEntity.getPriority()!=null) {
            savedEntity.setPriority(updateEntity.getPriority())
        }
        if(updateEntity.getRecipients()!=null) {
            savedEntity.setRecipients(updateEntity.getRecipients())
        }
        savedEntity.setTemplateId(updateEntity.getTemplateId())
        savedEntity.setScheduleTime(updateEntity.getScheduleTime())
        savedEntity.setPayload(updateEntity.getPayload())
    }
}
