package com.junbo.sharding.hibernate

import groovy.transform.CompileStatic
import org.hibernate.SessionFactory
import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.event.spi.PostDeleteEventListener
import org.hibernate.event.spi.PostInsertEventListener
import org.hibernate.event.spi.PostUpdateEventListener
import org.hibernate.internal.SessionFactoryImpl
import org.springframework.orm.hibernate4.LocalSessionFactoryBean

/**
 * Created by kg on 4/1/2014.
 */
@CompileStatic
class LocalSessionFactoryWithListenerBean extends LocalSessionFactoryBean {

    private PostInsertEventListener[] postInsertEventListeners

    private PostUpdateEventListener[] postUpdateEventListeners

    private PostDeleteEventListener[] postDeleteEventListeners

    void setPostInsertEventListeners(PostInsertEventListener[] postInsertEventListeners) {
        this.postInsertEventListeners = postInsertEventListeners
    }

    void setPostUpdateEventListeners(PostUpdateEventListener[] postUpdateEventListeners) {
        this.postUpdateEventListeners = postUpdateEventListeners
    }

    void setPostDeleteEventListeners(PostDeleteEventListener[] postDeleteEventListeners) {
        this.postDeleteEventListeners = postDeleteEventListeners
    }


    @Override
    SessionFactory getObject() {
        def sessionFactory = (SessionFactoryImpl) super.object

        EventListenerRegistry registry = sessionFactory.serviceRegistry.getService(EventListenerRegistry)

        if (postInsertEventListeners != null) {
            registry.appendListeners(EventType.POST_INSERT, postInsertEventListeners)
        }

        if (postUpdateEventListeners != null) {
            registry.appendListeners(EventType.POST_UPDATE, postUpdateEventListeners)
        }

        if (postDeleteEventListeners != null) {
            registry.appendListeners(EventType.POST_DELETE, postDeleteEventListeners)
        }

        return sessionFactory
    }
}
