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
        ShardScope shardScope = new ShardScope(0);
        try {
            return sessionFactory.getCurrentSession();
        } finally {
            shardScope.close();
        }

    }

    public Long save(EmailTemplateEntity entity) {
        return (Long) currentSession().save(entity);
    }

    public Long update(EmailTemplateEntity entity) {
        currentSession().merge(entity);
        currentSession().flush();
        return entity.getId();

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
                    //criteria.add(Restrictions.allEq(queries));
                    for(String key : queries.keySet()) {
                        criteria.add(Restrictions.eq(key, queries.get(key)).ignoreCase());
                    }
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
