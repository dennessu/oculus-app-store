package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.billing.spec.resource.AddressValidatorResource
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.Country
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by xmchen on 14-4-15.
 */
@CompileStatic
class AddressValidatorImpl implements PiiValidator {

    CountryRepository countryRepository

    AddressValidatorResource addressValidatorResource

    Boolean externalValidatorEnabled

    @Required
    void setCountryRepository(CountryRepository countryRepository) {
        this.countryRepository = countryRepository
    }

    @Required
    void setAddressValidatorResource(AddressValidatorResource addressValidatorResource) {
        this.addressValidatorResource = addressValidatorResource
    }

    @Required
    void setExternalValidatorEnabled(Boolean externalValidatorEnabled) {
        this.externalValidatorEnabled = externalValidatorEnabled
    }

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.ADDRESS.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        Address address = (Address)JsonHelper.jsonNodeToObj(value, Address)
        return checkAddress(address)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        Address address = (Address)JsonHelper.jsonNodeToObj(value, Address)
        Address oldAddress = (Address)JsonHelper.jsonNodeToObj(oldValue, Address)

        if (address != oldAddress) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('value', 'Value can\'t be updated.').exception()
        }

        return Promise.pure(null)
    }

    @Override
    JsonNode updateJsonNode(JsonNode value) {
        return value
    }

    private Promise<Void> checkAddress(Address address) {
        if (address.street1 == null || address.street1.isEmpty()) {
            throw AppCommonErrors.INSTANCE.fieldRequired("street1").exception()
        }

        if (address.countryId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired("country").exception()
        }

        if (address.city == null || address.city.isEmpty()) {
            throw AppCommonErrors.INSTANCE.fieldRequired("city").exception()
        }

        if (address.postalCode == null || address.postalCode.isEmpty()) {
            throw AppCommonErrors.INSTANCE.fieldRequired("postalCode").exception()
        }

        return countryRepository.get(address.countryId).then { Country country ->
            if (country == null) {
                throw AppErrors.INSTANCE.countryNotFound(address.countryId).exception()
            }

            if (address.countryId.value in ['US', 'CA', 'GB'] && externalValidatorEnabled) {
                return addressValidatorResource.validateAddress(address).then { Address validatedAddress ->
                    address.setPostalCode(validatedAddress.postalCode)
                    address.setCity(validatedAddress.city)
                    address.setCountryId(validatedAddress.countryId)
                    address.setSubCountry(validatedAddress.subCountry)
                    address.setStreet1(validatedAddress.street1)

                    return Promise.pure(null)
                }
            }

            return Promise.pure(null)
        }
    }

}
