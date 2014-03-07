/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.dao

import com.junbo.email.db.entity.EmailScheduleEntity

/**
 * Interface of EmailScheduleDao.
 */
interface EmailScheduleDao extends BaseDao<EmailScheduleEntity> {
    void deleteEmailScheduleById(Long id)
}
