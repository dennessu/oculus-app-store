/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.dao.impl;

import com.junbo.email.db.dao.EmailHistoryDao;
import com.junbo.email.db.entity.EmailHistoryEntity;
import org.springframework.stereotype.Component;

/**
 * Impl of EmailHistoryDao.
 */
@Component
public class EmailHistoryDaoImpl extends BaseDaoImpl<EmailHistoryEntity> implements EmailHistoryDao {

    public Long updateStatus(Long id, Short emailStatus) {
        EmailHistoryEntity entity = get(id);
        entity.setStatus(emailStatus);
        return update(entity);
    }
}
