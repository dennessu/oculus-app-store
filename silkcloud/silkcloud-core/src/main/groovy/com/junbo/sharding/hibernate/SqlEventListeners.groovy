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
import org.apache.commons.lang3.ArrayUtils
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

    // refer to http://anshuiitk.blogspot.jp/2010/11/hibernate-pre-database-opertaion-event.html

    static class preInsert implements PreInsertEventListener {
        @Override
        boolean onPreInsert(PreInsertEvent event) {
            if (event.entity instanceof EntityAdminInfo) {
                def tracker = TrackContextManager.get()
                def entity = (EntityAdminInfo)event.entity

                entity.createdBy = tracker.currentUserId ?: 0L
                entity.createdByClient = tracker.currentClientId ?: ""
                entity.createdTime = new Date()
                entity.updatedBy = entity.createdBy
                entity.updatedByClient = entity.createdByClient
                entity.updatedTime = entity.createdTime

                updateState(event.persister, event.state, entity);
            } else if (event.entity instanceof EntityAdminInfoString) {
                def tracker = TrackContextManager.get()
                def entity = (EntityAdminInfoString)event.entity
                entity.createdBy = tracker.currentUserId?.toString() ?: ""
                entity.createdByClient = tracker.currentClientId ?: ""
                entity.createdTime = new Date()
                entity.updatedBy = entity.createdBy
                entity.updatedByClient = entity.createdByClient
                entity.updatedTime = entity.createdTime

                updateState(event.persister, event.state, entity);
            }

            begin("insert", event.entity.class.simpleName, event.id.toString())
            return false
        }
    }

    static class postInsert implements PostInsertEventListener {
        @Override
        void onPostInsert(PostInsertEvent event) {
            end()
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
                entity.updatedBy = tracker.currentUserId ?: 0L
                entity.updatedByClient = tracker.currentClientId ?: ""
                entity.updatedTime = new Date()

                updateState(event.persister, event.state, entity);
            } else if (event.entity instanceof EntityAdminInfoString) {
                def tracker = TrackContextManager.get()
                def entity = (EntityAdminInfoString)event.entity
                entity.updatedBy = tracker.currentUserId?.toString() ?: ""
                entity.updatedByClient = tracker.currentClientId ?: ""
                entity.updatedTime = new Date()

                updateState(event.persister, event.state, entity);
            }
            begin("update", event.entity.class.simpleName, event.id.toString())
            return false
        }
    }

    static class postUpdate implements PostUpdateEventListener {
        @Override
        void onPostUpdate(PostUpdateEvent event) {
            end()
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
            end()
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
            end()
        }

        @Override
        boolean requiresPostCommitHanding(EntityPersister persister) {
            return false
        }
    }

    private static void begin(String event, String entityName, String id) {
        ProfilingHelper.begin("SQL", "%s %s %s", event, entityName, id)
    }

    private static void end() {
        ProfilingHelper.end(logger, "(DONE)")
    }

    private static void setValue(Object[] currentState, String[] propertyNames, String propertyToSet, Object value, Object entity) {
        int index = ArrayUtils.indexOf(propertyNames, propertyToSet);
        if (index >= 0) {
            currentState[index] = value;
        } else {
            logger.error("Field '{}' not found on entity '{}'", propertyToSet, entity.getClass().getName());
            throw new RuntimeException(String.format("Field %s not found on entity %s", propertyToSet, entity.getClass().getName()));
        }
    }

    private static void updateState(EntityPersister persister, Object[] state, EntityAdminInfo entity) {
        def propertyNames = persister.getEntityMetamodel().getPropertyNames();

        // update the state
        for (int i = 0; i < propertyNames.size(); ++i) {
            switch (propertyNames[i]) {
                case "createdBy":
                    state[i] = entity.createdBy
                    break
                case "createdByClient":
                    state[i] = entity.createdByClient
                    break
                case "createdTime":
                    state[i] = entity.createdTime
                    break
                case "updatedBy":
                    state[i] = entity.updatedBy
                    break
                case "updatedByClient":
                    state[i] = entity.updatedByClient
                    break
                case "updatedTime":
                    state[i] = entity.updatedTime
                    break
            }
        }
    }

    private static void updateState(EntityPersister persister, Object[] state, EntityAdminInfoString entity) {
        def propertyNames = persister.getEntityMetamodel().getPropertyNames();

        // update the state
        for (int i = 0; i < propertyNames.size(); ++i) {
            switch (propertyNames[i]) {
                case "createdBy":
                    state[i] = entity.createdBy
                    break
                case "createdByClient":
                    state[i] = entity.createdByClient
                    break
                case "createdTime":
                    state[i] = entity.createdTime
                    break
                case "updatedBy":
                    state[i] = entity.updatedBy
                    break
                case "updatedByClient":
                    state[i] = entity.updatedByClient
                    break
                case "updatedTime":
                    state[i] = entity.updatedTime
                    break
            }
        }
    }
}
