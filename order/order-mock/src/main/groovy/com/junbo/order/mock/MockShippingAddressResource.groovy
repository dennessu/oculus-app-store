package com.junbo.order.mock

import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.billing.spec.resource.ShippingAddressResource
import com.junbo.common.id.ShippingAddressId
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.QueryParam

/**
 * Created by chriszhu on 2/24/14.
 */
@Component('mockShippingAddressResource')
@Scope('prototype')
@CompileStatic
class MockShippingAddressResource extends BaseMock implements ShippingAddressResource {

    @Override
    Promise<ShippingAddress> postShippingAddress(Long userId, ShippingAddress address) {
        return null
    }

    @Override
    Promise<List<ShippingAddress>> getShippingAddresses(@QueryParam('user') Long userId) {
        return null
    }

    @Override
    Promise<Void> deleteShippingAddress(Long userId, Long addressId) {
        return null
    }

    @Override
    Promise<ShippingAddress> getShippingAddress(Long userId,  Long addressId) {
        def shippingAddress = new ShippingAddress()
        shippingAddress.addressId = new ShippingAddressId()
        shippingAddress.addressId.value = addressId
        shippingAddress.city = 'San Francisco'
        shippingAddress.country = 'US'
        shippingAddress.firstName = 'Pacific'
        shippingAddress.lastName = 'Ocean'
        shippingAddress.userId = new UserId(userId)
        return Promise.pure(shippingAddress)
    }
}
