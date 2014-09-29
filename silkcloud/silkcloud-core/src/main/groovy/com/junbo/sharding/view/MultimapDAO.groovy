package com.junbo.sharding.view

/**
 * Created by kg on 3/30/2014.
 */
interface MultimapDAO {

    void put(EntityView view, Object key, Object value)

    void remove(EntityView view, Object key, Object value)

    List get(EntityView view, Object key)

}
