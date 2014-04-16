/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.dao.impl;

import com.junbo.email.common.util.Action;
import com.junbo.email.db.dao.BaseDao;
import com.junbo.email.db.dao.EmailTemplateDao;
import com.junbo.email.db.entity.EmailTemplateEntity;
import com.junbo.email.spec.model.Paging;
import com.junbo.sharding.hibernate.ShardScope;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.MethodClosure;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Map;

/**
 * Impl of EmailTemplateDao.
 */
public class EmailTemplateDaoImpl implements EmailTemplateDao, BaseDao<EmailTemplateEntity> {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    protected Session currentSession() {
        Closure mc = new MethodClosure(this,"getCurrentSession");
        return (Session) ShardScope.with(0, mc);
    }

    public Long save(EmailTemplateEntity entity) {
        return (Long) currentSession().save(entity);
    }

    public Long update(EmailTemplateEntity entity) {
        return null;

    }

    public EmailTemplateEntity get(Long id) {
        return (EmailTemplateEntity) currentSession().get(EmailTemplateEntity.class, id);
    }

    public void delete(EmailTemplateEntity entity) {
        currentSession().delete(entity);
    }

    public void flush(Long id) {
        currentSession().flush();
    }

    public EmailTemplateEntity getEmailTemplateByName(final String name) {
        return findBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("name", name).ignoreCase());
            }
        });
    }

    public List<EmailTemplateEntity> getEmailTemplateList() {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
            }
        });
    }

    public List<EmailTemplateEntity> getEmailTemplatesByQuery(final Map<String, String> queries, final Paging paging) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                if(queries != null) {
                    criteria.add(Restrictions.allEq(queries));
                }
                if(paging != null) {
                    criteria.setFirstResult((paging.getPage()-1)*paging.getSize());
                    criteria.setMaxResults(paging.getSize());
                }
            }
        });
    }

    private  EmailTemplateEntity findBy(Action<Criteria> filter) {
        Criteria criteria = currentSession().createCriteria(EmailTemplateEntity.class);
        filter.apply(criteria);

        return (EmailTemplateEntity) criteria.uniqueResult();
    }

    private List<EmailTemplateEntity> findAllBy(Action<Criteria> filter) {
        Criteria criteria = currentSession().createCriteria(EmailTemplateEntity.class);
        filter.apply(criteria);

        return criteria.list();
    }
}
