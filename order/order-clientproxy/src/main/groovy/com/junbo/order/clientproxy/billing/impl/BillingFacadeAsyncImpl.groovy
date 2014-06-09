package com.junbo.order.clientproxy.billing.impl
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.util.concurrent.atomic.AtomicInteger
/**
 * Created by fzhang on 4/23/2014.
 */
@CompileStatic
@Component('orderAsyncBillingFacade')
class BillingFacadeAsyncImpl extends BillingFacadeImpl {

    private final static Logger LOGGER = LoggerFactory.getLogger(BillingFacadeAsyncImpl)

    private final AtomicInteger pendingUserNumber = new AtomicInteger()

    private int pendingUserNumberLimit

    @Value('${order.balance.async.pending.limit}')
    void setPendingUserNumberLimit(int pendingUserNumberLimit) {
        this.pendingUserNumberLimit = pendingUserNumberLimit
    }

    @Override
    Promise<Balance> createBalance(Balance balance, Boolean isAsyncCharge) {
        boolean exceedPendingLimit =  pendingUserNumber.incrementAndGet() > pendingUserNumberLimit
        Promise<Balance> result

        try {
            result = super.createBalance(balance, isAsyncCharge == null ? exceedPendingLimit : isAsyncCharge)
            return result.syncRecover { Throwable throwable ->
                pendingUserNumber.decrementAndGet()
                throw throwable
            }.syncThen { Balance b ->
                if (b.isAsyncCharge) {
                    LOGGER.info('name=CreateBalanceWithAsyncCharge, orderIds={}', balance.orderIds)
                }
                this.pendingUserNumber.decrementAndGet()
                return b
            }
        } finally {
            if (result == null) { // error in createBalance, release the
                pendingUserNumber.decrementAndGet()
            }
        }
    }

    int getPendingUserNumberInt() {
        return pendingUserNumber.get()
    }

}
