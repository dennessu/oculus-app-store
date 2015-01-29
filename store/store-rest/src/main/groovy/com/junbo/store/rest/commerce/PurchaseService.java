/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.commerce;

import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.ApiContext;
import com.junbo.store.spec.model.purchase.*;

/**
 * Created by acer on 2015/1/29.
 */
public interface PurchaseService {

    Promise<MakeFreePurchaseResponse> makeFreePurchase(MakeFreePurchaseRequest makeFreePurchaseRequest, ApiContext apiContext);

    Promise<PreparePurchaseResponse> preparePurchase(PreparePurchaseRequest preparePurchaseRequest, ApiContext apiContext);

    Promise<CommitPurchaseResponse> commitPurchase(CommitPurchaseRequest commitPurchaseRequest, ApiContext apiContext);
}
