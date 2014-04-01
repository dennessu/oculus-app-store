package com.junbo.sharding.view

import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/28/2014.
 */
class DefaultEntityViewRepository implements EntityViewRepository {

    private Map<Class, List<EntityView>> multimap

    @Required
    public void setEntityViews(List<EntityView> entityViews) {
        if (entityViews == null) {
            throw new IllegalArgumentException('entityViews is null')
        }

        multimap = new HashMap<>()

        for (EntityView entityView : entityViews) {
            def viewList = multimap.get(entityView.entityType)

            if (viewList == null) {
                viewList = new ArrayList<EntityView>()
                multimap.put(entityView.entityType, viewList)
            }

            viewList.add(entityView)
        }
    }


    List<EntityView> getViews(Class entityType) {
        if (entityType == null) {
            throw new IllegalArgumentException('entityType is null')
        }

        return multimap.get(entityType) ?: []
    }
}
