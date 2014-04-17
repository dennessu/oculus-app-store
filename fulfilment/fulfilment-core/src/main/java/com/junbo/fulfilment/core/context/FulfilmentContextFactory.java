/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.context;

import com.junbo.fulfilment.spec.constant.FulfilmentActionType;

/**
 * FulfilmentContext.
 */
public final class FulfilmentContextFactory {
    private FulfilmentContextFactory() {

    }

    public static <T extends FulfilmentContext> T create(String actionType) {
        FulfilmentContext context = null;

        switch (actionType) {
            case FulfilmentActionType.GRANT_ENTITLEMENT:
                context = new EntitlementContext();
                break;
            case FulfilmentActionType.DELIVER_PHYSICAL_GOODS:
                context = new PhysicalGoodsContext();
                break;
            case FulfilmentActionType.CREDIT_WALLET:
                context = new WalletContext();
                break;
        }

        return (T) context;
    }
}
