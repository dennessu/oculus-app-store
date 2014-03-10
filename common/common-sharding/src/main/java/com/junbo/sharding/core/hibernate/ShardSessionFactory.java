package com.junbo.sharding.core.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Created by haomin on 14-3-10.
 */
public interface ShardSessionFactory extends SessionFactory{
    public Session getShardSession(int shardId, String dbName) throws HibernateException;
}
