package com.junbo.fulfilment.core.handler;

import com.junbo.fulfilment.common.util.Callback;
import com.junbo.fulfilment.core.BaseTest;
import com.junbo.fulfilment.core.FulfilmentHandler;
import com.junbo.fulfilment.core.context.WalletContext;
import com.junbo.fulfilment.db.repo.FulfilmentActionRepository;
import com.junbo.fulfilment.spec.constant.FulfilmentActionType;
import com.junbo.fulfilment.spec.constant.FulfilmentStatus;
import com.junbo.fulfilment.spec.fusion.LinkedEntry;
import com.junbo.fulfilment.spec.model.FulfilmentAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class WalletHandlerTest extends BaseTest {
    @Autowired
    private FulfilmentActionRepository actionRepo;

    @Test
    public void testBVT() {
        final FulfilmentAction action = new FulfilmentAction() {{
            setFulfilmentId(generateLong());
            setStatus(FulfilmentStatus.PENDING);
            setType(FulfilmentActionType.CREDIT_WALLET);
            setCopyCount(1);
            setItems(new ArrayList<LinkedEntry>() {{
                add(new LinkedEntry() {{
                    setId("10000L");
                    setQuantity(1);
                }});
                add(new LinkedEntry() {{
                    setId("20000L");
                    setQuantity(1);
                }});
            }});
        }};

        executeInNewTransaction(new Callback() {
            public void apply() {
                actionRepo.create(action);
            }
        });

        WalletContext context = new WalletContext();
        context.setUserId(generateLong());

        context.setActions(new ArrayList<FulfilmentAction>() {{
            add(action);
        }});

        FulfilmentHandler handler = HandlerRegistry.resolve(FulfilmentActionType.CREDIT_WALLET);
        handler.process(context);
    }
}
