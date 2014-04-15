package com.junbo.identity.data.repository

import com.junbo.common.id.AddressId
import com.junbo.identity.spec.v1.model.Address
import com.junbo.langur.core.promise.Promise

/**
 * Created by xmchen on 14-4-15.
 */
interface AddressRepository {
    Promise<Address> get(AddressId groupId)
    Promise<Address> create(Address address)
}