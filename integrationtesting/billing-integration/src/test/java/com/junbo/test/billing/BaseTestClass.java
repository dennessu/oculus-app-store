/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing;

import com.junbo.test.billing.utility.BillingTestDataProvider;
import com.junbo.test.billing.utility.BillingValidationHelper;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Utility.TestClass;

/**
 * Created by Yunlong on 4/8/14.
 */
public abstract class BaseTestClass extends TestClass {
    protected BillingTestDataProvider testDataProvider = new BillingTestDataProvider();
    protected BillingValidationHelper validationHelper = new BillingValidationHelper();

    protected String offer_digital_normal1;
    protected String offer_digital_normal2;
    protected String offer_physical_normal1;
    protected String offer_physical_normal2;

    public BaseTestClass() {
        super();
        loadOffers();
    }

    private void loadOffers() {
        offer_digital_normal1 = ConfigHelper.getSetting("testdata.offer.digital.normal1");
        offer_digital_normal2 = ConfigHelper.getSetting("testdata.offer.digital.normal2");
        offer_physical_normal1 = ConfigHelper.getSetting("testdata.offer.physical.normal1");
        offer_physical_normal2 = ConfigHelper.getSetting("testdata.offer.physical.normal2");
    }

}
