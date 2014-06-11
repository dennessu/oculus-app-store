/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.dao

import com.junbo.email.db.entity.EmailHistoryEntity

/**
 * Interface of EmailHistoryDao
 */
interface EmailHistoryDao extends BaseDao<EmailHistoryEntity> {
    EmailHistoryEntity updateStatus(Long id, Short emailStatus)
}
