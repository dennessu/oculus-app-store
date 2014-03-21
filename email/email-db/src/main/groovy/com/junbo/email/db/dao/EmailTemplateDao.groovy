/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.dao

import com.junbo.email.db.entity.EmailTemplateEntity
import com.junbo.email.spec.model.Paging

/**
 * Interface of EmailTemplateDao
 */
interface EmailTemplateDao extends BaseDao<EmailTemplateEntity> {
    EmailTemplateEntity getEmailTemplateByName(String name)

    List<EmailTemplateEntity> getEmailTemplateList()

    List<EmailTemplateEntity> getEmailTemplatesByQuery(Map<String, String> queries, Paging paging)
}
