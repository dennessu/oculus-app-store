package com.junbo.sharding.view

import com.junbo.common.util.Identifiable
import groovy.transform.CompileStatic
import org.hibernate.event.spi.*
import org.hibernate.persister.entity.EntityPersister
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/28/2014.
 */
@CompileStatic
class EntityListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    private EntityViewRepository entityViewRepository

    private MultimapDAO multimapDAO

    @Required
    void setEntityViewRepository(EntityViewRepository entityViewRepository) {
        this.entityViewRepository = entityViewRepository
    }

    @Required
    void setMultimapDAO(MultimapDAO multimapDAO) {
        this.multimapDAO = multimapDAO
    }

    @Override
    void onPostInsert(PostInsertEvent event) {
        if (event == null) {
            throw new IllegalArgumentException('event is null')
        }

        if (event.entity == null) {
            throw new IllegalArgumentException('event.entity is null')
        }

        if (!(event.entity instanceof Identifiable)) {
            return
        }

        def entityType = event.entity.class
        def entity = (Identifiable) event.entity

        for (EntityView entityView : entityViewRepository.getViews(entityType)) {
            if (entityView.handlesEntity(entity)) {
                def keys = entityView.mapEntity(entity)

                for (Object key : keys.unique()) {
                    multimapDAO.put(entityView, key, entity.id)
                }
            }
        }
    }

    @Override
    void onPostUpdate(PostUpdateEvent event) {
        if (event == null) {
            throw new IllegalArgumentException('event is null')
        }

        if (event.entity == null) {
            throw new IllegalArgumentException('event.entity is null')
        }

        if (!(event.entity instanceof Identifiable)) {
            return
        }

        def entityType = event.entity.class
        def entity = (Identifiable) event.entity

        def oldEntity = (Identifiable) event.persister.entityTuplizer.instantiate()
        event.persister.setPropertyValues(oldEntity, event.oldState)

        for (EntityView entityView : entityViewRepository.getViews(entityType)) {
            def keys = entityView.handlesEntity(entity) ? entityView.mapEntity(entity) : []
            def oldKeys = entityView.handlesEntity(oldEntity) ? entityView.mapEntity(oldEntity) : []


            for (Object key : (oldKeys - keys).unique()) {
                multimapDAO.remove(entityView, key, entity.id)
            }
            for (Object key : (keys - oldKeys).unique()) {
                multimapDAO.put(entityView, key, entity.id)
            }
        }

    }

    @Override
    void onPostDelete(PostDeleteEvent event) {
        if (event == null) {
            throw new IllegalArgumentException('event is null')
        }

        if (event.entity == null) {
            throw new IllegalArgumentException('event.entity is null')
        }

        if (!(event.entity instanceof Identifiable)) {
            return
        }

        def entityType = event.entity.class
        def entity = (Identifiable) event.entity

        for (EntityView entityView : entityViewRepository.getViews(entityType)) {
            if (entityView.handlesEntity(entity)) {
                def keys = entityView.mapEntity(entity)

                for (Object key : keys.unique()) {
                    multimapDAO.remove(entityView, key, entity.id)
                }
            }
        }
    }

    @Override
    boolean requiresPostCommitHanding(EntityPersister persister) {
        return false
    }
}
