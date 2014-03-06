package com.junbo.order.core.orderflow
import com.junbo.langur.core.promise.Promise
import com.junbo.order.core.BaseTest
import com.junbo.order.core.FlowType
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.common.TestBuilder
import com.junbo.order.core.impl.orderflow.DefaultFlowSelector
import groovy.transform.CompileStatic
import org.testng.annotations.Test
/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
class FlowSelectorTest extends BaseTest {

    @Test
    void testSelector_CREATE_PayIn_CreditCard_Digital() {
        def context = TestBuilder.buildDefaultContext()
        context.paymentInstruments = Promise.pure([TestBuilder.buildCreditCartPI()])
        new DefaultFlowSelector().select(context, OrderServiceOperation.CREATE).syncThen { FlowType type ->
            assert(type == FlowType.IMMEDIATE_SETTLE)
        }
    }

    @Test
    void testSelector_CREATE_PayIn_NoPI_Digital() {
        def context = TestBuilder.buildDefaultContext()
        context.order.paymentInstruments = null
        new DefaultFlowSelector().select(context, OrderServiceOperation.CREATE).syncThen { FlowType type ->
            assert(type == FlowType.FREE_SETTLE)
        }
    }

}
