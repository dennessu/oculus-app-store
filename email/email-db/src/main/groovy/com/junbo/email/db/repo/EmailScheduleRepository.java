/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo;

import com.junbo.common.id.EmailId;
import com.junbo.email.db.dao.EmailScheduleDao;
import com.junbo.email.db.entity.EmailScheduleEntity;
import com.junbo.email.db.mapper.EmailMapper;
import com.junbo.email.spec.model.Email;
import com.junbo.sharding.IdGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Repository of EmailSchedule.
 */
@Component
@Transactional
public class EmailScheduleRepository {
    @Autowired
    private EmailScheduleDao emailScheduleDao;

    @Autowired
    private EmailMapper emailMapper;

    @Autowired
    private IdGeneratorFacade idGenerator;

    public Email saveEmailSchedule(Email email) {
        EmailScheduleEntity entity = emailMapper.toEmailScheduleEntity(email);
        entity.setId(idGenerator.nextId(EmailId.class));
        entity.setCreatedTime(new Date());
        entity.setCreatedBy("internal system");
        Long id = emailScheduleDao.save(entity);
        return emailMapper.toEmailSchedule(emailScheduleDao.get(id));
    }

    public Email updateEmailSchedule(Email email) {
        EmailScheduleEntity entity = emailMapper.toEmailScheduleEntity(email);
        EmailScheduleEntity savedEntity = emailScheduleDao.get(entity.getId());
        mergeEntity(savedEntity,entity);
        savedEntity.setUpdatedTime(new Date());
        savedEntity.setUpdatedBy("internal system");
        Long id = emailScheduleDao.update(savedEntity);
        return emailMapper.toEmailSchedule(emailScheduleDao.get(id));
    }

    public void deleteEmailScheduleById(Long id) {
        emailScheduleDao.deleteEmailScheduleById(id);
    }

    public Email getEmailSchedule(Long id) {
        EmailScheduleEntity entity = emailScheduleDao.get(id);
        return emailMapper.toEmailSchedule(entity);
    }

    private void mergeEntity(EmailScheduleEntity savedEntity,EmailScheduleEntity updateEntity){
        if(updateEntity.getPriority()!=null) {
            savedEntity.setPriority(updateEntity.getPriority());
        }
        if(updateEntity.getRecipient()!=null) {
            savedEntity.setRecipient(updateEntity.getRecipient());
        }
        savedEntity.setScheduleDate(updateEntity.getScheduleDate());
        savedEntity.setSource(updateEntity.getSource());
        savedEntity.setAction(updateEntity.getAction());
        savedEntity.setLocale(updateEntity.getLocale());
        savedEntity.setPayload(updateEntity.getPayload());
    }
}
