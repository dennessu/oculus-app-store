package com.junbo.identity.core.service.validator

import com.junbo.common.id.AddressId
import com.junbo.identity.spec.v1.model.Address
import com.junbo.langur.core.promise.Promise

/**
 * Created by xmchen on 14-4-15.
 */
interface AddressValidator {
    Promise<Address> validateForGet(AddressId addressId)
    Promise<Void> validateForCreate(Address address)
}