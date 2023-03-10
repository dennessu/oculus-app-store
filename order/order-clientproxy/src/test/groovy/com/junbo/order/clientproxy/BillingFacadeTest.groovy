package com.junbo.order.clientproxy
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.billing.BillingFacade
import com.junbo.order.clientproxy.common.TestBuilder
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.testng.annotations.Test

import javax.annotation.Resource
/**
 * Created by chriszhu on 2/19/14.
 */
@CompileStatic
@TypeChecked
class BillingFacadeTest extends BaseTest {

    @Resource(name = 'mockBillingFacade')
    BillingFacade billingFacade

    @Test(enabled = true)
    void testCreateBalance() {
        def balance = TestBuilder.buildBalance()
        def promise = billingFacade.createBalance(balance, null)
        promise?.then(new Promise.Func<Balance, Promise>() {
            @Override
            Promise apply(Balance b1) {
                assert(b1 != null)
                def promiseGet = billingFacade.getBalanceById(b1.getId().value)
                promiseGet.then(new Promise.Func<Balance, Promise>() {
                    @Override
                    Promise apply(Balance b2) {
                        assert(b1.getId() == b2.getId())
                    }
                } )
            }
        } )
        assert (promise != null)
    }
}
