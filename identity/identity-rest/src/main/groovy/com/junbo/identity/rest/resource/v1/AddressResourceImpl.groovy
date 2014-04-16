package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.AddressId
import com.junbo.common.id.Id
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.AddressFilter
import com.junbo.identity.core.service.validator.AddressValidator
import com.junbo.identity.data.repository.AddressRepository
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.resource.AddressResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

import javax.ws.rs.ext.Provider

/**
 * Created by xmchen on 14-4-15.
 */
@Provider
@Component
@Scope('prototype')
@Transactional
@CompileStatic
class AddressResourceImpl implements AddressResource {

    @Autowired
    private AddressRepository addressRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private AddressValidator addressValidator

    @Autowired
    private AddressFilter addressFilter

    @Override
    Promise<Address> create(Address address) {
        if (address == null) {
            throw new IllegalArgumentException('address is null')
        }

        address = addressFilter.filterForCreate(address)

        addressValidator.validateForCreate(address).then {
            addressRepository.create(address).then { Address newAddress ->
                created201Marker.mark((Id)newAddress.id)

                newAddress = addressFilter.filterForGet(newAddress, null)
                return Promise.pure(newAddress)
            }
        }
    }

    @Override
    Promise<Address> get(AddressId addressId) {
        addressValidator.validateForGet(addressId).then { Address newAddress ->
            return Promise.pure(newAddress)
        }
    }
}
