package com.junbo.sharding.view
/**
 * Created by kg on 3/28/2014.
 */
interface EntityView<ID, E, K> {

    String getName()

    Class<ID> getIdType()

    Class<E> getEntityType()

    Class<K> getKeyType()

    boolean handlesEntity(E entity)

    List<K> mapEntity(E entity)
}
