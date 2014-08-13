/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.purchase;

import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.purchase.PurchaseState;

/**
 * The PurchaseStateSerializer class.
 */
public interface PurchaseTokenProcessor {

    Promise<String> toPurchaseToken(PurchaseState state);

    Promise<PurchaseState> toPurchaseState(String purchaseToken);
}
