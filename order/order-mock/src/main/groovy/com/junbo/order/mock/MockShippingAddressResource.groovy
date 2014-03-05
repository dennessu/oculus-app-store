package com.junbo.order.mock
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.billing.spec.resource.ShippingAddressResource
import com.junbo.common.id.ShippingAddressId
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam
/**
 * Created by chriszhu on 2/24/14.
 */
@Component('mockShippingAddressResource')
@Scope('prototype')
@CompileStatic
class MockShippingAddressResource extends BaseMock implements ShippingAddressResource {
    @Override
    Promise<ShippingAddress> postShippingAddress(ShippingAddress address) {
        return null
    }

    @Override
    Promise<List<ShippingAddress>> getShippingAddresses(@QueryParam('user') Long userId) {
        return null
    }

    @Override
    Promise<ShippingAddress> getShippingAddress(@PathParam('addressId') Long addressId) {
        def shippingAddress = new ShippingAddress()
        shippingAddress.addressId = new ShippingAddressId()
        shippingAddress.addressId.value = addressId
        shippingAddress.city = 'San Francisco'
        shippingAddress.country = 'US'
        shippingAddress.firstName = 'Pacific'
        shippingAddress.lastName = 'Ocean'
        return Promise.pure(shippingAddress)
    }

    @Override
    Promise<Void> deleteShippingAddress(@PathParam('addressId') Long addressId) {
        return null
    }
}
