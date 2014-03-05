package com.junbo.order.core.orderflow
import com.junbo.order.core.BaseTest
import com.junbo.order.core.FlowType
import com.junbo.order.core.OrderService
import com.junbo.order.core.OrderServiceOperation
import com.junbo.order.core.common.TestBuilder
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.orderflow.DefaultFlowSelector
import com.junbo.order.core.impl.orderflow.FreeSettleFlow
import com.junbo.order.core.impl.orderflow.ImmediateSettleFlow
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test

/**
 * Created by chriszhu on 2/7/14.
 */
@CompileStatic
class FlowSelectorTest extends BaseTest {

    @Test
    void testSelector_CREATE_PayIn_CreditCard_Digital() {
        def context = TestBuilder.buildDefaultContext()
        context.paymentInstruments = [TestBuilder.buildCreditCartPI()]
        assert(new DefaultFlowSelector().select(context,
                    OrderServiceOperation.CREATE) == FlowType.IMMEDIATE_SETTLE)
    }

    @Test
    void testSelector_CREATE_PayIn_NoPI_Digital() {
        def context = TestBuilder.buildDefaultContext()
        context.order.paymentInstruments = null
        assert(new DefaultFlowSelector().select(context,
                    OrderServiceOperation.CREATE) == FlowType.FREE_SETTLE)
    }

}
