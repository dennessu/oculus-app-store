package com.junbo.identity.data.dao.impl

import com.junbo.identity.data.dao.AddressDAO
import com.junbo.identity.data.entity.user.AddressEntity
import groovy.transform.CompileStatic
import org.hibernate.Session

/**
 * Created by xmchen on 14-4-15.
 */
@CompileStatic
class AddressDAOImpl extends BaseDAO implements AddressDAO {
    @Override
    AddressEntity get(Long addressId) {
        return (AddressEntity)currentSession(addressId).get(AddressEntity, addressId)
    }

    @Override
    AddressEntity create(AddressEntity address) {

        address.id = idGenerator.nextId(address.userId)

        Session session = currentSession(address.id)
        session.save(address)
        session.flush()

        return get((Long)address.id)
    }

    @Override
    AddressEntity update(AddressEntity address) {

        Session session = currentSession(address.id)
        session.merge(address)
        session.flush()

        return get((Long)address.id)
    }
}
