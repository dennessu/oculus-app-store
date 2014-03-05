package com.junbo.subscription.db.dao;

import com.junbo.subscription.db.entity.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;

public class BaseDao<T extends Entity> {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private IdGenerator idGenerator;

    private Class<T> classType;

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public Long insert(T t) {
        t.setId(generateId());
        return (Long) currentSession().save(t);
    }

    public T get(Long id) {
        return (T) currentSession().get(classType, id);
    }

    public Long update(T t) {
        currentSession().update(t);
        return t.getId();
    }

    public void delete(T entity) {
        currentSession().delete(entity);
    }

    public Class<T> getClassType() {
        return classType;
    }

    public void setClassType(Class<T> classType) {
        this.classType = classType;
    }

    protected Long generateId(){
        return idGenerator.nextId();
    }
}
