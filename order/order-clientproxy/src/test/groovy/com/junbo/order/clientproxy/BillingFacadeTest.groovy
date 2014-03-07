package com.junbo.order.clientproxy
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.billing.BillingFacade
import com.junbo.order.clientproxy.common.TestBuilder
import groovy.transform.CompileStatic
import org.testng.annotations.Test

import javax.annotation.Resource
/**
 * Created by chriszhu on 2/19/14.
 */
@CompileStatic
class BillingFacadeTest extends BaseTest {

    @Resource(name = 'mockBillingFacade')
    BillingFacade billingFacade

    @Test(enabled = true)
    void testCreateBalance() {
        def balance = TestBuilder.buildBalance()
        def promise = billingFacade.createBalance(balance)
        promise?.then(new Promise.Func<Balance, Promise>() {
            @Override
            Promise apply(Balance b1) {
                assert(b1 != null)
                def promiseGet = billingFacade.getBalanceById(b1.balanceId.value)
                promiseGet.then(new Promise.Func<Balance, Promise>() {
                    @Override
                    Promise apply(Balance b2) {
                        assert(b1.balanceId == b2.balanceId)
                    }
                } )
            }
        } )
        assert (promise != null)
    }

    @Test(enabled = true)
    void testGetShippingAddress() {
//        def balance = TestBuilder.buildBalance();
        def promise = billingFacade.getShippingAddress(TestBuilder.generateLong(), TestBuilder.generateLong())
        promise.then(new Promise.Func<ShippingAddress, Promise>() {
            @Override
            Promise apply(ShippingAddress shippingAddress) {
                assert(shippingAddress != null)
                assert(shippingAddress.lastName == 'Ocean')
            }
        } )

        assert (promise != null)
    }
}
