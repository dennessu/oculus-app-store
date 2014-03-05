package com.junbo.sharding.test.data.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by haomin on 14-3-4.
 */
@Component
public class ShardDAOImpl implements ShardDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public ShardEntity saveShard(ShardEntity entity) {
        currentSession().persist(entity);
        return findById(entity.getId());
    }

    @Override
    public ShardEntity getShard(Long id) {
        return findById(id);
    }

    @Override
    public ShardEntity updateShard(ShardEntity entity) {
        return null;
    }

    @Override
    public void deleteShard(Long id) {

    }

    private ShardEntity findById(Long id) {
        return ((ShardEntity)(currentSession().get(ShardEntity.class, id)));
    }
}
