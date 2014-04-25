package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.AddressId
import com.junbo.identity.core.service.validator.AddressValidator
import com.junbo.identity.data.repository.AddressRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by xmchen on 14-4-15.
 */
@CompileStatic
class AddressValidatorImpl implements AddressValidator {

    private UserRepository userRepository

    private AddressRepository addressRepository

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setAddressRepository(AddressRepository addressRepository) {
        this.addressRepository = addressRepository
    }

    @Override
    Promise<Address> validateForGet(AddressId addressId) {

        if (addressId == null) {
            throw AppErrors.INSTANCE.parameterRequired('addressId').exception()
        }

        return addressRepository.get(addressId).then { Address address ->
            if (address == null) {
                throw AppErrors.INSTANCE.addressNotFound(addressId).exception()
            }

            return Promise.pure(address)
        }
    }

    @Override
    Promise<Void> validateForCreate(Address address) {

        return checkBasicAddressInfo(address).then {
            if (address.id != null) {
                throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
            }

            return Promise.pure(null)
        }
    }

    private Promise<Void> checkBasicAddressInfo(Address address) {
        if (address == null) {
            throw new IllegalArgumentException('address is null')
        }

        if (address.userId == null) {
            throw AppErrors.INSTANCE.fieldRequired('userId').exception()
        }

        if (address.countryId == null) {
            throw AppErrors.INSTANCE.fieldRequired('country').exception()
        }

        if (address.postalCode == null) {
            throw AppErrors.INSTANCE.fieldRequired('postalCode').exception()
        }

        return userRepository.get(address.userId).then { User existingUser ->
            if (existingUser == null) {
                throw AppErrors.INSTANCE.userNotFound(address.userId).exception()
            }

            /*
            if (existingUser.active == null || existingUser.active == false) {
                throw AppErrors.INSTANCE.userInInvalidStatus(address.userId).exception()
            }
            */
            return Promise.pure(null)
        }
    }
}
