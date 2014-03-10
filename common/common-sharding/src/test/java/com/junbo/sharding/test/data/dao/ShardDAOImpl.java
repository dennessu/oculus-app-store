package com.junbo.sharding.test.data.dao;

import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.core.hibernate.ShardSessionFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

import java.sql.Connection;
import java.sql.SQLException;


/**
 * Created by haomin on 14-3-4.
 */
@Component
public class ShardDAOImpl implements ShardDAO {


    private Session currentSession() {

        /*
        sessionFactory.getCurrentSession().doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                //connection, finally!
                connection.createStatement().execute("set search_path=shard_1");
            }
        });

        return sessionFactory.getCurrentSession();
        */

        return null;
    }

    @Autowired
    private ShardSessionFactory shardedSessionFactory;

    public void setShardedSessionFactory(ShardSessionFactory shardedSessionFactory) {
        this.shardedSessionFactory = shardedSessionFactory;
    }

    private Session session(int shardId, String db) {
        return this.shardedSessionFactory.getShardSession(shardId,db);
    }

    @Override
    public ShardEntity saveShard(ShardEntity entity) {
        session(0, "test").persist(entity);
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
