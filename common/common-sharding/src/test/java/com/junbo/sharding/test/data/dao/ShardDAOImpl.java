package com.junbo.sharding.test.data.dao;

import com.junbo.sharding.core.hibernate.SessionFactoryWrapper;
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

    private Session session(final int shardId) {
        Session s = sessionFactoryWrapper.resolve(shardId).getCurrentSession();
        s.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                //connection, finally!
                //connection.createStatement().execute("set search_path=shard_" + shardId);
                connection.createStatement().execute("show search_path");
            }
        });
        return s;
    }

    @Override
    public ShardEntity saveShard(ShardEntity entity) {
        session((int) (entity.getId()%4)).persist(entity);
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
        return ((ShardEntity)(session((int) (id%4)).get(ShardEntity.class, id)));
    }
}
