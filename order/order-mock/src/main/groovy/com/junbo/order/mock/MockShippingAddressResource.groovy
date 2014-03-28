package com.junbo.order.mock
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.billing.spec.resource.ShippingAddressResource
import com.junbo.common.id.ShippingAddressId
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
/**
 * Created by chriszhu on 2/24/14.
 */
@Component('mockShippingAddressResource')
@Scope('prototype')
@CompileStatic
@TypeChecked
class MockShippingAddressResource extends BaseMock implements ShippingAddressResource {

    @Override
    Promise<ShippingAddress> postShippingAddress(UserId userId, ShippingAddress address) {
        return null
    }

    @Override
    Promise<List<ShippingAddress>> getShippingAddresses(UserId userId) {
        return null
    }

    @Override
    Promise<ShippingAddress> getShippingAddress(
            UserId userId, ShippingAddressId addressId) {
        def shippingAddress = new ShippingAddress()
        shippingAddress.addressId = new ShippingAddressId()
        shippingAddress.addressId = addressId
        shippingAddress.city = 'San Francisco'
        shippingAddress.country = 'US'
        shippingAddress.firstName = 'Pacific'
        shippingAddress.lastName = 'Ocean'
        shippingAddress.userId = userId
        return Promise.pure(shippingAddress)
    }

    @Override
    Promise<Void> deleteShippingAddress(
            UserId userId, ShippingAddressId addressId) {
        return null
    }
}
