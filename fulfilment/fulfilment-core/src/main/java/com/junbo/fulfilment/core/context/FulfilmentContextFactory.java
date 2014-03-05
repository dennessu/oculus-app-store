/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.context;

import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.spec.constant.FulfilmentActionType;

/**
 * FulfilmentContext.
 */
public final class FulfilmentContextFactory {
    private FulfilmentContextFactory() {

    }

    public static <T extends FulfilmentContext> T create(String actionType) {
        FulfilmentContext context = null;

        if (Utils.equals(FulfilmentActionType.GRANT_ENTITLEMENT, actionType)) {
            context = new EntitlementContext();
        } else if (Utils.equals(FulfilmentActionType.DELIVER_PHYSICAL_GOODS, actionType)) {
            context = new PhysicalGoodsContext();
        } else if (Utils.equals(FulfilmentActionType.CREDIT_WALLET, actionType)) {
            context = new WalletContext();
        }

        return (T) context;
    }
}
