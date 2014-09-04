/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.hibernate

import org.hibernate.event.spi.*
import org.hibernate.persister.entity.EntityPersister
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by x on 9/4/14.
 */
class ProfileEventListeners {
    private static final Logger logger = LoggerFactory.getLogger(ProfileEventListeners)
    private static ThreadLocal<Map<String, Long>> timer = new ThreadLocal<>()

    static class preInsert implements PreInsertEventListener {
        @Override
        boolean onPreInsert(PreInsertEvent event) {
            begin(event.entity.class.simpleName, event.id.toString())
            return false
        }
    }

    static class postInsert implements PostInsertEventListener {
        @Override
        void onPostInsert(PostInsertEvent event) {
            end("insert", event.entity.class.simpleName, event.id.toString())
        }

        @Override
        boolean requiresPostCommitHanding(EntityPersister persister) {
            return false
        }
    }

    static class preUpdate implements PreUpdateEventListener {
        @Override
        boolean onPreUpdate(PreUpdateEvent event) {
            begin(event.entity.class.simpleName, event.id.toString())
            return false
        }
    }

    static class postUpdate implements PostUpdateEventListener {
        @Override
        void onPostUpdate(PostUpdateEvent event) {
            end("update", event.entity.class.simpleName, event.id.toString())
        }

        @Override
        boolean requiresPostCommitHanding(EntityPersister persister) {
            return false
        }
    }

    static class preLoad implements PreLoadEventListener {
        @Override
        void onPreLoad(PreLoadEvent event) {
            begin(event.entity.class.simpleName, event.id.toString())
        }
    }

    static class postLoad implements PostLoadEventListener {
        @Override
        void onPostLoad(PostLoadEvent event) {
            end("select", event.entity.class.simpleName, event.id.toString())
        }
    }

    static class preDelete implements PreDeleteEventListener {
        @Override
        boolean onPreDelete(PreDeleteEvent event) {
            begin(event.entity.class.simpleName, event.id.toString())
            return false
        }
    }

    static class postDelete implements PostDeleteEventListener {
        @Override
        void onPostDelete(PostDeleteEvent event) {
            end("delete", event.entity.class.simpleName, event.id.toString())
        }

        @Override
        boolean requiresPostCommitHanding(EntityPersister persister) {
            return false
        }
    }

    private static void begin(String entityName, String id) {
        if (true) {
            return
        }
        if (timer.get() == null) {
            timer.set(new HashMap<String, Long>())
        }
        timer.get().put(entityName + ":" + id.toString(), new Date().getTime())
    }

    private static void end(String event, String entityName, String id) {
        if (true) {
            return
        }
        String key = entityName + ":" + id.toString()
        logger.info(event + " spent " + (new Date().getTime() - timer.get().get(key)) + "ms")
        timer.get().remove(key)
    }
}
