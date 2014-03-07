/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.dao.impl;

import com.junbo.email.db.dao.EmailScheduleDao;
import com.junbo.email.db.entity.EmailScheduleEntity;

/**
 * Impl of EmailScheduleDao.
 */
public class EmailScheduleDaoImpl extends BaseDaoImpl<EmailScheduleEntity> implements EmailScheduleDao{

    public void deleteEmailScheduleById(Long id) {
        EmailScheduleEntity entity = get(id);
        if(entity != null) {
            delete(entity);
        }
    }
}
