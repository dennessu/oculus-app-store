package com.junbo.subscription.db.dao;

import com.junbo.common.id.SubscriptionId;
import com.junbo.sharding.IdGeneratorFacade;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseDao<T extends com.junbo.subscription.db.entity.Entity> {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private IdGeneratorFacade idGenerator;

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
        return idGenerator.nextId(SubscriptionId.class);
    }
}
