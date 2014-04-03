package com.junbo.sharding.view

import com.junbo.common.util.Identifiable

/**
 * Created by kg on 3/28/2014.
 */
interface EntityView<ID, E extends Identifiable<ID>, K> {

    String getName()

    Class<ID> getIdType()

    Class<E> getEntityType()

    Class<K> getKeyType()

    boolean handlesEntity(E entity)

    List<K> mapEntity(E entity)
}
