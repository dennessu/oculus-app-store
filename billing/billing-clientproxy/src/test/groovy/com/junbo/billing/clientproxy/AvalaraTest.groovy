package com.junbo.billing.clientproxy

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.common.id.BalanceItemId
import com.junbo.common.id.ShippingAddressId
import org.testng.annotations.Test;

import javax.annotation.Resource;

/**
 * Created by LinYi on 14-3-10.
 */
public class AvalaraTest extends BaseTest {
    @Resource
    AvalaraFacade avalaraFacade

    @Test
    public void testAvalaraFacade() {
        def balance = new Balance()
        balance.shippingAddressId = new ShippingAddressId(123L)
        def item = new BalanceItem()
        item.amount = 100
        item.balanceItemId = new BalanceItemId(456L)
        balance.addBalanceItem(item)

        def shippingAddress = new ShippingAddress()
        shippingAddress.street = "7462 Kearny Street"
        shippingAddress.city = "Commerce City"
        shippingAddress.state = "CO"
        shippingAddress.postalCode = "80022"
        shippingAddress.country = "USA"

        avalaraFacade.calculateTax(balance, shippingAddress, null)

    }


}
