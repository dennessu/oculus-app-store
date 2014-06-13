package com.junbo.fulfilment.core.handler;

import com.junbo.fulfilment.core.BaseTest;
import com.junbo.fulfilment.core.FulfilmentHandler;
import com.junbo.fulfilment.core.context.PhysicalGoodsContext;
import com.junbo.fulfilment.spec.constant.FulfilmentActionType;
import com.junbo.fulfilment.spec.fusion.LinkedEntry;
import com.junbo.fulfilment.spec.model.FulfilmentAction;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class PhysicalGoodsHandlerTest extends BaseTest {
    @Test
    public void testBVT() {
        PhysicalGoodsContext context = new PhysicalGoodsContext();
        context.setShippingAddressId(123L);
        context.setShippingMethodId("456L");

        final int copyCount = 100;
        final int quantity1 = 2;
        final int quantity2 = 5;

        context.setActions(new ArrayList<FulfilmentAction>() {{
            add(new FulfilmentAction() {{
                setType(FulfilmentActionType.DELIVER_PHYSICAL_GOODS);
                setCopyCount(copyCount);
                setItems(new ArrayList<LinkedEntry>() {{
                    add(new LinkedEntry() {{
                        setId("10000L");
                        setQuantity(quantity1);
                        setSku("11111");
                    }});
                    add(new LinkedEntry() {{
                        setId("20000L");
                        setQuantity(quantity2);
                        setSku("22222");
                    }});
                }});
            }});
        }});

        FulfilmentHandler handler = HandlerRegistry.resolve(FulfilmentActionType.DELIVER_PHYSICAL_GOODS);
        handler.process(context);

        Assert.assertEquals(context.getShipment().get("10000L#11111"), (Integer) (copyCount * quantity1), "Quantity should match.");
        Assert.assertEquals(context.getShipment().get("20000L#22222"), (Integer) (copyCount * quantity2), "Quantity should match.");
    }
}
