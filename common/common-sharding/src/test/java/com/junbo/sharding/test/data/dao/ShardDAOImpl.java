package com.junbo.sharding.test.data.dao;

import com.junbo.sharding.core.ShardAwareDaoProxy;
import com.junbo.sharding.core.hibernate.SessionFactoryWrapper;
import com.junbo.sharding.util.Helper;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * Created by haomin on 14-3-4.
 */
@Component
public class ShardDAOImpl implements ShardDAO {
    private SessionFactoryWrapper sessionFactoryWrapper;
    public void setSessionFactoryWrapper(SessionFactoryWrapper sessionFactoryWrapper) {
        this.sessionFactoryWrapper = sessionFactoryWrapper;
    }

    private Session currentSession() {
        return sessionFactoryWrapper.resolve(Helper.getCurrentThreadLocalShardId()).getCurrentSession();
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
