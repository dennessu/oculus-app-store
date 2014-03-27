package com.junbo.billing.clientproxy

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.common.id.OrderItemId
import com.junbo.common.id.ShippingAddressId
import org.testng.Assert
import org.testng.annotations.Test;

import javax.annotation.Resource;

/**
 * Created by LinYi on 14-3-10.
 */
public class AvalaraTest extends BaseTest {
    @Resource
    TaxFacade avalaraFacade

    @Test(enabled = false)
    public void testTaxCalculation() {
        def balance = new Balance()
        balance.shippingAddressId = new ShippingAddressId(123L)
        def item = new BalanceItem()
        item.amount = 100
        item.balanceItemId = 456L
        balance.addBalanceItem(item)

        def shippingAddress = new ShippingAddress()
        shippingAddress.addressId = new ShippingAddressId(123L)
        shippingAddress.street = "7462 Kearny Street"
        shippingAddress.city = "Commerce City"
        shippingAddress.state = "CO"
        shippingAddress.postalCode = "80022"
        shippingAddress.country = "US"

        balance.balanceItems.each { BalanceItem balanceItem ->
            item.orderItemId = new OrderItemId(321L)
        }

        Balance balanceWithTax = avalaraFacade.calculateTax(balance, shippingAddress, null).wrapped().get()
        Assert.assertNotNull(balanceWithTax.taxAmount, "Fail to calculate tax.")
        Assert.assertNotEquals(balanceWithTax.taxAmount, BigDecimal.ZERO,
                "Tax amount should not be zero in this test case.")
    }

    @Test(enabled = false)
    public void testAddressValidation() {
        def shippingAddress = new ShippingAddress()
        shippingAddress.addressId = new ShippingAddressId(123L)
        shippingAddress.street = "7462 Kearny Street"
        shippingAddress.city = "Commerce City"
        shippingAddress.state = "CO"
        shippingAddress.postalCode = "80022"
        shippingAddress.country = "US"

        def validatedAddress = avalaraFacade.validateShippingAddress(shippingAddress).wrapped().get()
        Assert.assertNotNull(validatedAddress, "Fail to validate address.")
    }
}
