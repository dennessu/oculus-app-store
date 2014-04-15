package com.junbo.identity.data.dao

import com.junbo.identity.data.entity.user.AddressEntity

/**
 * Created by xmchen on 14-4-15.
 */
interface AddressDAO {
    AddressEntity get(Long addressId)
    AddressEntity create(AddressEntity address)
    AddressEntity update(AddressEntity address)
}