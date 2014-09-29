package com.junbo.sharding.view
/**
 * Created by kg on 3/28/2014.
 */
interface EntityViewRepository {

    List<EntityView> getViews(Class entityType)
}
