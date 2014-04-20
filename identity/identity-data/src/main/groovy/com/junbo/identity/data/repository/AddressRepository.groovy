package com.junbo.identity.data.repository

import com.junbo.common.id.AddressId
import com.junbo.identity.spec.v1.model.Address
import groovy.transform.CompileStatic

/**
 * Created by xmchen on 14-4-15.
 */
@CompileStatic
interface AddressRepository extends IdentityBaseRepository<Address, AddressId> {
}