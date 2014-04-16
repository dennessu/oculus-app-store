package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.Address
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by xmchen on 14-4-15.
 */
@CompileStatic
class AddressFilter extends ResourceFilterImpl<Address> {
    @Override
    protected Address filter(Address address, MappingContext context) {
        return selfMapper.filterAddress(address, context)
    }

    @Override
    protected Address merge(Address source, Address base, MappingContext context) {
        return selfMapper.mergeAddress(source, base, context)
    }
}
