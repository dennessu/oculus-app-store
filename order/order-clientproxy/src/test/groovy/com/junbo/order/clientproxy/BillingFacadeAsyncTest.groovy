package com.junbo.order.clientproxy

import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.promise.ThreadLocalRequireNew
import com.junbo.order.clientproxy.billing.impl.BillingFacadeAsyncImpl
import com.junbo.order.mock.MockBalanceResource
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

/**
 * Created by fzhang on 4/24/2014.
 */
class BillingFacadeAsyncTest extends BaseTest {

    private BillingFacadeAsyncImpl billingFacadeAsync = new BillingFacadeAsyncImpl()

    private Semaphore semaphore = new Semaphore(0)

    ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(20));

    @BeforeMethod
    @SuppressWarnings("deprecation")
    void setUp() {
        billingFacadeAsync.balanceResource = new MockBalanceResource() {
            @Override
            Promise<Balance> postBalance(Balance balance) {
                return Promise.wrap(service.submit(new Callable<Balance>() {
                    @Override
                    Balance call() throws Exception {
                        semaphore.acquire()
                        return balance
                    }
                }))
            }
        }
        billingFacadeAsync.pendingUserNumberLimit = 10
    }

    @AfterMethod
    void cleanUp() {

    }

    @Test
    void testCreateBalance() {
        List<Promise<Balance>> balanceList = []
        for (int i = 0; i < 15; ++i) {
            ThreadLocalRequireNew scope = new ThreadLocalRequireNew()
            try {
                balanceList.add(billingFacadeAsync.createBalance(new Balance(), null))
            }
            finally {
                scope.close()
            }
        }

        semaphore.release(15)

        for (int i = 0;i < 15; ++i) {
            Balance result = balanceList[i].get()
            if (i < 10) {
                assert !result.isAsyncCharge
            } else {
                assert result.isAsyncCharge
            }
        }

        assert billingFacadeAsync.pendingUserNumberInt == 0
    }

    @Test
    void testCreateBalanceOnError() {
        int flag = 0

        billingFacadeAsync.balanceResource = new MockBalanceResource() {
            @Override
            Promise<Balance> postBalance(Balance balance) {
                if ((flag % 2) == 0) {
                    throw new RuntimeException()
                } else {
                    return Promise.throwing(new RuntimeException())
                }
            }
        }

        for (flag = 0; flag < 15; ++flag) {
            try {
                billingFacadeAsync.createBalance(new Balance()).get()
                assert false
            } catch (RuntimeException) {
            }
        }

        assert billingFacadeAsync.pendingUserNumberInt == 0
    }

}
