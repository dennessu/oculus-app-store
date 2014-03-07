/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.dao

import com.junbo.email.db.entity.EmailTemplateEntity

/**
 * Interface of EmailTemplateDao
 */
interface EmailTemplateDao extends BaseDao<EmailTemplateEntity> {
    EmailTemplateEntity getEmailTemplateByName(String name)

    List<EmailTemplateEntity> getEmailTemplateList()
}
