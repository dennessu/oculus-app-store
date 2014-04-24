package com.junbo.order.clientproxy.billing.impl
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.util.concurrent.Semaphore
/**
 * Created by fzhang on 4/23/2014.
 */
@CompileStatic
@Component('orderAsyncBillingFacade')
class BillingFacadeAsyncImpl extends BillingFacadeImpl implements InitializingBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(BillingFacadeAsyncImpl)

    private Semaphore syncChargeLock

    private int syncChargeCapacity

    @Value('${order.balance.sync.capacity}')
    void setSyncChargeCapacity(int syncChargeCapacity) {
        this.syncChargeCapacity = syncChargeCapacity
    }

    int getSyncChargeCapacity() {
        return syncChargeCapacity
    }

    @Override
    Promise<Balance> createBalance(Balance balance) {
        balance.isAsyncCharge = !syncChargeLock.tryAcquire()
        Promise<Balance> result
        try {
            if (balance.isAsyncCharge) {
                LOGGER.info('name=CreateBalanceWithAsyncCharge, orderId={}', balance.orderId)
            }

            result = super.createBalance(balance)

            return result.syncRecover { Throwable throwable ->
                release(balance.isAsyncCharge)
                throw throwable
            }.syncThen { Balance b ->
                release(balance.isAsyncCharge)
                return b
            }

        } finally {
            if (result == null) { // error in createBalance, release the
                release(balance.isAsyncCharge)
            }
        }
    }

    int getSyncChargeLockPermit() {
        return syncChargeLock.availablePermits()
    }

    private void release(boolean isAsyncCharge) {
        if (!isAsyncCharge) {
            syncChargeLock.release()
        }
    }

    @Override
    void afterPropertiesSet() throws Exception {
        syncChargeLock = new Semaphore(syncChargeCapacity)
    }
}
