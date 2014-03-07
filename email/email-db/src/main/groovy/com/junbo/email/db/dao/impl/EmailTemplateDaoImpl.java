/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.dao.impl;

import com.junbo.email.common.util.Action;
import com.junbo.email.db.dao.EmailTemplateDao;
import com.junbo.email.db.entity.EmailTemplateEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Impl of EmailTemplateDao.
 */
public class EmailTemplateDaoImpl extends BaseDaoImpl<EmailTemplateEntity> implements EmailTemplateDao {

    public EmailTemplateEntity getEmailTemplateByName(final String name) {
        return findBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("name", name));
            }
        });
    }

    public List<EmailTemplateEntity> getEmailTemplateList() {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
            }
        });
    }
}
