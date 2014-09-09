/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.hibernate

import com.junbo.common.model.EntityAdminInfo
import com.junbo.common.model.EntityAdminInfoString
import com.junbo.langur.core.profiling.ProfilingHelper
import com.junbo.langur.core.track.TrackContextManager
import groovy.transform.CompileStatic
import org.hibernate.event.spi.*
import org.hibernate.persister.entity.EntityPersister
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Created by x on 9/4/14.
 */
@CompileStatic
class SqlEventListeners {
    private static final Logger logger = LoggerFactory.getLogger(SqlEventListeners)

    static class preInsert implements PreInsertEventListener {
        @Override
        boolean onPreInsert(PreInsertEvent event) {
            if (event.entity instanceof EntityAdminInfo) {
                def tracker = TrackContextManager.get()
                def entity = (EntityAdminInfo)event.entity
                entity.createdBy = tracker.currentUserId
                entity.createdByClient = tracker.currentClientId
                entity.createdTime = new Date()
            } else if (event.entity instanceof EntityAdminInfoString) {
                def tracker = TrackContextManager.get()
                def entity = (EntityAdminInfoString)event.entity
                entity.createdBy = tracker.currentUserId?.toString()
                entity.createdByClient = tracker.currentClientId
                entity.createdTime = new Date()
            }
            begin("insert", event.entity.class.simpleName, event.id.toString())
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
            if (event.entity instanceof EntityAdminInfo) {
                def tracker = TrackContextManager.get()
                def entity = (EntityAdminInfo)event.entity
                entity.updatedBy = tracker.currentUserId
                entity.updatedByClient = tracker.currentClientId
                entity.updatedTime = new Date()
            } else if (event.entity instanceof EntityAdminInfoString) {
                def tracker = TrackContextManager.get()
                def entity = (EntityAdminInfoString)event.entity
                entity.updatedBy = tracker.currentUserId?.toString()
                entity.updatedByClient = tracker.currentClientId
                entity.updatedTime = new Date()
            }
            begin("update", event.entity.class.simpleName, event.id.toString())
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
            begin("select", event.entity.class.simpleName, event.id.toString())
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
            begin("delete", event.entity.class.simpleName, event.id.toString())
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

    private static void begin(String event, String entityName, String id) {
        ProfilingHelper.appendRow(logger, "(SQL) BEGIN %s %s %s", event, entityName, id)
    }

    private static void end(String event, String entityName, String id) {
        ProfilingHelper.appendRow(logger, "(SQL) END %s %s %s", event, entityName, id)
    }
}
