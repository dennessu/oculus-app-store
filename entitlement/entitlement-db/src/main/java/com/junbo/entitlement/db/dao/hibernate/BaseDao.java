/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.hibernate;

import com.junbo.entitlement.common.def.EntitlementConsts;
import com.junbo.entitlement.common.lib.CommonUtils;
import com.junbo.entitlement.common.lib.EntitlementContext;
import com.junbo.entitlement.db.entity.Entity;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.sharding.IdGenerator;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Base dao for hibernate Dao.
 *
 * @param <T> Entity type
 */
public class BaseDao<T extends Entity> {
    @Autowired
    @Qualifier("entitlementSessionFactory")
    private SessionFactory sessionFactory;
    @Autowired
    @Qualifier("oculus48IdGenerator")
    private IdGenerator idGenerator;

    private Class<T> entityType;

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public T insert(T t) {
        t.setId(generateId(t.getShardMasterId()));
        Date now = EntitlementContext.current().getNow();
        t.setCreatedBy("DEFAULT");  //TODO
        t.setCreatedTime(now);
        t.setModifiedBy("DEFAULT"); //TODO
        t.setModifiedTime(now);
        return get((Long) currentSession().save(t));
    }

    public T get(Long id) {
        return (T) currentSession().get(entityType, id);
    }

    public T update(T t) {
        T existed = (T) currentSession().load(entityType, t.getId());
        t.setCreatedTime(existed.getCreatedTime());
        t.setCreatedBy(existed.getCreatedBy());
        t.setModifiedBy("DEFAULT"); //TODO
        t.setModifiedTime(EntitlementContext.current().getNow());
        return (T) currentSession().merge(t);
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Long generateId(Long shardId) {
        return idGenerator.nextId(shardId);
    }

    protected void addSingleParam(String columnName, String paramName,
                                  Object value, String op,
                                  StringBuilder queryStringBuilder, Map<String, Object> params) {
        if (CommonUtils.isNotNull(value)) {
            queryStringBuilder.append(" and " + columnName + " " + op + " (:" + paramName + ")");
            params.put(paramName, value);
        }
    }

    protected void addCollectionParam(String columnName, String paramName,
                                      Collection collection, StringBuilder queryStringBuilder,
                                      Map<String, Object> params) {
        if (!CollectionUtils.isEmpty(collection)) {
            if (collection.size() == 1) {
                Object value = collection.iterator().next();
                if (CommonUtils.isNotNull(value)) {
                    queryStringBuilder.append(" and " + columnName + " = (:" + paramName + ")");
                    params.put(paramName, value);
                }
            } else {
                queryStringBuilder.append(" and " + columnName + " in (:" + paramName + ")");
                params.put(paramName, collection);
            }
        }
    }

    protected Query addParams(Query q, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof Collection) {
                q.setParameterList(entry.getKey(),
                        new ArrayList((Collection) entry.getValue()));
            } else if (entry.getValue() instanceof Date) {
                q.setTimestamp(entry.getKey(), (Date) entry.getValue());
            } else {
                q.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return q;
    }

    protected Query addPageMeta(Query q, PageMetadata pageMetadata) {
        int size = pageMetadata.getCount() == null ||
                pageMetadata.getCount() > EntitlementConsts.MAX_PAGE_SIZE
                ? EntitlementConsts.DEFAULT_PAGE_SIZE : pageMetadata.getCount();
        int start = pageMetadata.getStart() == null ?
                EntitlementConsts.DEFAULT_PAGE_NUMBER :
                pageMetadata.getStart();
        q.setMaxResults(size);
        q.setFirstResult(start);
        return q;
    }
}
