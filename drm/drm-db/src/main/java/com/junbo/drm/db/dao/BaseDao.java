package com.junbo.drm.db.dao;

import com.junbo.drm.db.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseDao<T extends Entity> {
    private SessionFactory sessionFactory;

    private Class<T> entityType;

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public Long insert(T t) {
        return (Long) currentSession().save(t);
    }

    public T get(Long id) {
        return (T) currentSession().get(entityType, id);
    }

    public Long update(T t) {
        currentSession().update(t);
        return t.getId();
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<T> entityType) {
        this.entityType = entityType;
    }
}
