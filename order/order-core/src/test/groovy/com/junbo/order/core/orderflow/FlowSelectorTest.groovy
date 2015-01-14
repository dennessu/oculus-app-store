package com.junbo.order.core.orderflow

import com.junbo.common.error.AppErrorException
import com.junbo.order.core.BaseTest
import com.junbo.order.core.FlowType
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.common.TestBuilder
import com.junbo.order.core.impl.orderflow.DefaultFlowSelector
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.testng.annotations.Test

import javax.annotation.Resource
/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
@TypeChecked
class FlowSelectorTest extends BaseTest {

    @Resource(name = 'defaultFlowSelector')
    DefaultFlowSelector flowSelector

    @Test
    void testSelector_CREATE_PayIn_CreditCard_Digital() {
        def context = TestBuilder.buildDefaultContext()
        context.paymentInstruments = [TestBuilder.buildCreditCartPI()]
        flowSelector.select(context, OrderServiceOperation.CREATE).syncThen { String name ->
            assert(name == FlowType.IMMEDIATE_SETTLE.name())
        }.get()
    }

    @Test
    void testSelector_CREATE_PayIn_NoPI_Digital() {
        def context = TestBuilder.buildDefaultContext()
        context.order.payments = null
        try {
            flowSelector.select(context, OrderServiceOperation.CREATE).get()
        }catch (ex) {
            assert ((AppErrorException)(ex)).error.error().code == '199.001'
            return
        }
        assert false
    }

}
