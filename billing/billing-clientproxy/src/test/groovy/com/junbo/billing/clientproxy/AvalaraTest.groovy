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
    public void testAvalaraFacade() {
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
        shippingAddress.country = "USA"

        balance.balanceItems.each { BalanceItem balanceItem ->
            item.orderItemId = new OrderItemId(321L)
        }

        avalaraFacade.calculateTax(balance, shippingAddress, null)
        Assert.assertNotNull(balance.taxAmount, "Fail to calculate tax.")
        Assert.assertNotEquals(balance.taxAmount, BigDecimal.ZERO,
                "Tax amount should not be zero in this test case.")
    }


}
