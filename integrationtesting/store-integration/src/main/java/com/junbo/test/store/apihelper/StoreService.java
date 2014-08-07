/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper;

import com.junbo.store.spec.model.billing.BillingProfileUpdateRequest;
import com.junbo.store.spec.model.billing.BillingProfileUpdateResponse;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeResponse;
import com.junbo.store.spec.model.purchase.*;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public interface StoreService {

    BillingProfileUpdateResponse updateBillingProfile(BillingProfileUpdateRequest billingProfileUpdateRequest) throws Exception;

    BillingProfileUpdateResponse updateBillingProfile(BillingProfileUpdateRequest billingProfileUpdateRequest, int expectedResponseCode) throws Exception;

    PreparePurchaseResponse preparePurchase(PreparePurchaseRequest preparePurchaseRequest) throws Exception;

    PreparePurchaseResponse preparePurchase(PreparePurchaseRequest preparePurchaseRequest, int expectedResponseCode) throws Exception;

    SelectInstrumentResponse selectInstrumentForPurchase(SelectInstrumentRequest selectInstrumentRequest) throws Exception;

    SelectInstrumentResponse selectInstrumentForPurchase(SelectInstrumentRequest selectInstrumentRequest, int expectedResponseCode) throws Exception;

    CommitPurchaseResponse commitPurchase(CommitPurchaseRequest commitPurchaseRequest) throws Exception;

    CommitPurchaseResponse commitPurchase(CommitPurchaseRequest commitPurchaseRequest, int expectedResponseCode) throws Exception;

    IAPEntitlementConsumeResponse iapConsumeEntitlement(IAPEntitlementConsumeResponse iapEntitlementConsumeResponse) throws Exception;

    IAPEntitlementConsumeResponse iapConsumeEntitlement(IAPEntitlementConsumeResponse iapEntitlementConsumeResponse, int expectedResponseCode) throws Exception;

}
