package com.junbo.sharding.hibernate

import groovy.transform.CompileStatic
import org.hibernate.HibernateException
import org.hibernate.Session
import org.hibernate.context.internal.JTASessionContext
import org.hibernate.context.spi.AbstractCurrentSessionContext
import org.hibernate.engine.spi.SessionFactoryImplementor

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by Shenhua on 3/31/2014.
 */
@CompileStatic
class MultiTenantJTASessionContext extends AbstractCurrentSessionContext {

    private final transient ConcurrentHashMap<String, JTASessionContext> sessionContextMap

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

        def sessionContext = sessionContextMap.get(tenantId)
        if (sessionContext == null) {
            def newSessionContext = new JTASessionContext(factory())
            sessionContext = sessionContextMap.putIfAbsent(tenantId, newSessionContext)
            if (sessionContext == null) {
                sessionContext = newSessionContext
            }
        }

        return sessionContext.currentSession()
    }
}
