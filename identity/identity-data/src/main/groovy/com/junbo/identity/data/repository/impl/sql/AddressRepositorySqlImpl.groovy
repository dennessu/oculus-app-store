package com.junbo.identity.data.repository.impl.sql

import com.junbo.common.id.AddressId
import com.junbo.identity.data.dao.AddressDAO
import com.junbo.identity.data.entity.user.AddressEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.AddressRepository
import com.junbo.identity.spec.v1.model.Address
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by xmchen on 14-4-15.
 */
@CompileStatic
class AddressRepositorySqlImpl implements AddressRepository {

    @Autowired
    private AddressDAO addressDAO

    @Autowired
    private ModelMapper modelMapper

    @Override
    Promise<Address> get(AddressId addressId) {

        AddressEntity addressEntity = addressDAO.get(addressId.value)

        if (addressEntity == null) {
            return Promise.pure(null)
        }
        return Promise.pure(modelMapper.toAddress(addressEntity, new MappingContext()))
    }

    @Override
    Promise<Address> create(Address address) {

        AddressEntity entity = modelMapper.toAddress(address, new MappingContext())
        entity = addressDAO.create(entity)

        return get(new AddressId((Long)entity.id))
    }

    @Override
    Promise<Address> update(Address model) {
        throw new IllegalStateException('update address not support')
    }

    @Override
    Promise<Void> delete(AddressId id) {
        throw new IllegalStateException('delete address not support')
    }
}
