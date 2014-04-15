package com.junbo.order.clientproxy.common

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.common.id.OrderId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Created by chriszhu on 2/19/14.
 */
@CompileStatic
@TypeChecked
class TestBuilder {

    static long generateLong() {
        sleep(1)
        return System.currentTimeMillis()
    }

    static UUID generateUUID() {
        return UUID.randomUUID()
    }


    static Balance buildBalance() {
        def balance = new Balance()
        def orderId = new OrderId()
        orderId.value = generateLong()
        balance.setOrderId(orderId)
        balance.setCountry('US')
        balance.setCurrency('USD')
        def piId = new PaymentInstrumentId()
        piId.value = generateLong()
        balance.setPiId(piId)
        balance.setType(BalanceType.DEBIT.toString())
        balance.setTotalAmount(BigDecimal.valueOf(99.99D))
        return balance
    }

    static User buildUser() {
        def user = new User()
        def userId = new UserId()
        userId.value = generateLong()
        user.setId(userId)
        user.setUsername('fake_user')
        return user
    }

}
