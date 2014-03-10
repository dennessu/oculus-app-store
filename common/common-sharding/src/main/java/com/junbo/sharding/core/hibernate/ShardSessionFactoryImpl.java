package com.junbo.sharding.core.hibernate;

import com.junbo.sharding.core.ds.ShardDataSourceKey;
import com.junbo.sharding.core.ds.ShardDataSourceRegistry;
import org.hibernate.*;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

/**
 * Created by haomin on 14-3-10.
 */
public class ShardSessionFactoryImpl implements ShardSessionFactory, ApplicationContextAware {

    private SessionFactory proxy;
    private ShardDataSourceRegistry registry;
    private ApplicationContext applicationContext;

    public ShardSessionFactoryImpl(SessionFactory sessionFactory, ShardDataSourceRegistry registry) {
        this.proxy = sessionFactory;
        this.registry = registry;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Session getShardSession(int shardId, String dbName) throws HibernateException {
        if (this.applicationContext == null) {
            throw new RuntimeException("applicationContext is null in ShardSessionFactoryImpl!");
        }

        DataSource ds = registry.resolve(new ShardDataSourceKey(shardId, dbName));
        Object object = this.applicationContext.getBean("&shardedSessionFactory");
        ShardSessionFactoryBean factoryBean = null;
        if (object instanceof ShardSessionFactoryBean) {
            try {
                factoryBean = (ShardSessionFactoryBean)object;
                factoryBean.setDataSource(ds);
                factoryBean.afterPropertiesSet();
            }
            catch (Exception e) {
                throw new RuntimeException("reset datasource of session factory bean failed", e);
            }
        }

        return ((ShardSessionFactory)factoryBean.getObject()).getShardSession(shardId, dbName);
    }

    @Override
    public SessionFactoryOptions getSessionFactoryOptions() {
        return this.proxy.getSessionFactoryOptions();
    }

    @Override
    public SessionBuilder withOptions() {
        return this.proxy.withOptions();
    }

    @Override
    public Session openSession() throws HibernateException {
        return this.proxy.openSession();
    }

    @Override
    public Session getCurrentSession() throws HibernateException {
        return this.proxy.getCurrentSession();
    }

    @Override
    public StatelessSessionBuilder withStatelessOptions() {
        return this.proxy.withStatelessOptions();
    }

    @Override
    public StatelessSession openStatelessSession() {
        return this.proxy.openStatelessSession();
    }

    @Override
    public StatelessSession openStatelessSession(Connection connection) {
        return this.proxy.openStatelessSession();
    }

    @Override
    public ClassMetadata getClassMetadata(Class entityClass) {
        return this.proxy.getClassMetadata(entityClass);
    }

    @Override
    public ClassMetadata getClassMetadata(String entityName) {
        return this.proxy.getClassMetadata(entityName);
    }

    @Override
    public CollectionMetadata getCollectionMetadata(String roleName) {
        return this.proxy.getCollectionMetadata(roleName);
    }

    @Override
    public Map<String, ClassMetadata> getAllClassMetadata() {
        return this.proxy.getAllClassMetadata();
    }

    @Override
    public Map getAllCollectionMetadata() {
        return this.proxy.getAllCollectionMetadata();
    }

    @Override
    public Statistics getStatistics() {
        return this.proxy.getStatistics();
    }

    @Override
    public void close() throws HibernateException {
        this.proxy.close();
    }

    @Override
    public boolean isClosed() {
        return this.proxy.isClosed();
    }

    @Override
    public Cache getCache() {
        return this.proxy.getCache();
    }

    @Override
    public void evict(Class persistentClass) throws HibernateException {
        this.proxy.evict(persistentClass);
    }

    @Override
    public void evict(Class persistentClass, Serializable id) throws HibernateException {
        this.proxy.evict(persistentClass, id);
    }

    @Override
    public void evictEntity(String entityName) throws HibernateException {
        this.proxy.evictEntity(entityName);
    }

    @Override
    public void evictEntity(String entityName, Serializable id) throws HibernateException {
        this.proxy.evictEntity(entityName, id);
    }

    @Override
    public void evictCollection(String roleName) throws HibernateException {
        this.proxy.evictCollection(roleName);
    }

    @Override
    public void evictCollection(String roleName, Serializable id) throws HibernateException {
        this.proxy.evictCollection(roleName, id);
    }

    @Override
    public void evictQueries(String cacheRegion) throws HibernateException {
        this.proxy.evictQueries(cacheRegion);
    }

    @Override
    public void evictQueries() throws HibernateException {
        this.proxy.evictQueries();
    }

    @Override
    public Set getDefinedFilterNames() {
        return this.proxy.getDefinedFilterNames();
    }

    @Override
    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        return this.proxy.getFilterDefinition(filterName);
    }

    @Override
    public boolean containsFetchProfileDefinition(String name) {
        return this.proxy.containsFetchProfileDefinition(name);
    }

    @Override
    public TypeHelper getTypeHelper() {
        return this.proxy.getTypeHelper();
    }

    @Override
    public Reference getReference() throws NamingException {
        return this.proxy.getReference();
    }
}
