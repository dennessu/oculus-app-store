/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo;

import com.junbo.email.db.dao.EmailScheduleDao;
import com.junbo.email.db.entity.EmailScheduleEntity;
import com.junbo.email.db.mapper.EmailMapper;
import com.junbo.email.spec.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;

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

    private Random r = new Random();

    public Email saveEmailSchedule(Email email) {
        EmailScheduleEntity entity = emailMapper.toEmailScheduleEntity(email);
        Long id = emailScheduleDao.save(entity);
        return emailMapper.toEmail(emailScheduleDao.get(id));
    }

    public Email updateEmailSchedule(Email email) {
        EmailScheduleEntity entity = emailMapper.toEmailScheduleEntity(email);
        entity.setUpdatedTime(new Date());
        Long id = emailScheduleDao.update(entity);
        return emailMapper.toEmail(emailScheduleDao.get(id));
    }

    public void deleteEmailScheduleById(Long id) {
        emailScheduleDao.deleteEmailScheduleById(id);
    }

    public Email getEmailSchedule(Long id) {
        EmailScheduleEntity entity = emailScheduleDao.get(id);
        return emailMapper.toEmail(entity);
    }
}
