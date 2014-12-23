package com.junbo.sharding.view

import com.junbo.common.util.Identifiable
import groovy.transform.CompileStatic

/**
 * Created by kg on 3/30/2014.
 */
@CompileStatic
class DefaultViewQuery<ID, E extends Identifiable<ID>> implements ViewQuery<ID> {

    private final MultimapDAO multimapDAO

    private final EntityView entityView

    private final E entityExample

    DefaultViewQuery(MultimapDAO multimapDAO, EntityView entityView, E entityExample) {
        this.multimapDAO = multimapDAO
        this.entityView = entityView
        this.entityExample = entityExample
    }

    List<ID> list() {

        def result = []
        for (Object key : entityView.mapEntity(entityExample)) {
            result.addAll(multimapDAO.get(entityView, key))
        }

        return result.unique() as List<ID>
    }
}
