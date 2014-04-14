package com.junbo.sharding.view

import com.junbo.common.util.Identifiable
import com.junbo.identity.data.view.*
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/30/2014.
 */
@CompileStatic
class DefaultViewQueryFactory implements ViewQueryFactory {

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
    def <ID, E extends Identifiable<ID>> ViewQuery<ID> from(E entityExample) {
        if (entityExample == null) {
            throw new IllegalArgumentException('entityExample is null')
        }

        def views = entityViewRepository.getViews(entityExample.class)
        for (EntityView view : views) {
            if (view.handlesEntity(entityExample)) {
                return new DefaultViewQuery(multimapDAO, view, entityExample)
            }
        }

        return null
    }
}
