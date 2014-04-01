package com.junbo.sharding.view

import com.junbo.common.util.Identifiable
import groovy.transform.CompileStatic

/**
 * Created by kg on 3/28/2014.
 */
@CompileStatic
abstract class AbstractEntityView<ID, E extends Identifiable<ID>, K> implements EntityView<ID, E, K> {

    Class<ID> getIdType() {
        return ID
    }

    Class<E> getEntityType() {
        return E
    }

    Class<K> getKeyType() {
        return K
    }

    abstract String getName()

    abstract boolean handlesEntity(E entity)

    abstract List<K> mapEntity(E entity)
}
