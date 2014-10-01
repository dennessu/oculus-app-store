package com.junbo.sharding.hibernate

import com.junbo.sharding.transaction.SimpleDataSourceProxy
import groovy.transform.CompileStatic
import org.hibernate.HibernateException
import org.hibernate.Session
import org.hibernate.context.internal.JTASessionContext
import org.hibernate.context.spi.AbstractCurrentSessionContext
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.hibernate.engine.spi.SessionFactoryImplementor

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by Shenhua on 3/31/2014.
 */
@CompileStatic
@SuppressWarnings('ImplementationAsType')
class MultiTenantJTASessionContext extends AbstractCurrentSessionContext implements Serializable {

    private final transient ConcurrentHashMap<Object, JTASessionContext> sessionContextMap

    MultiTenantJTASessionContext(SessionFactoryImplementor factory) {
        super(factory)

        sessionContextMap = new ConcurrentHashMap<>()
    }

    @Override
    Session currentSession() throws HibernateException {

        def resolver = factory().currentTenantIdentifierResolver
        if (resolver == null) {
            throw new IllegalStateException('currentTenantIdentifierResolver is null')
        }

        def tenantId = resolver.resolveCurrentTenantIdentifier()
        if (tenantId == null) {
            throw new IllegalStateException('currentTenantIdentifier is null')
        }

        def connectionProvider = (ShardMultiTenantConnectionProvider) factory().serviceRegistry.getService(
                MultiTenantConnectionProvider.class);

        def dataSource = connectionProvider.getDataSource(tenantId);

        def sessionContext = sessionContextMap.get(dataSource)
        if (sessionContext == null) {
            def newSessionContext = new JTASessionContext(factory())
            sessionContext = sessionContextMap.putIfAbsent(dataSource, newSessionContext)
            if (sessionContext == null) {
                sessionContext = newSessionContext
            }
        }

        return sessionContext.currentSession()
    }
}
