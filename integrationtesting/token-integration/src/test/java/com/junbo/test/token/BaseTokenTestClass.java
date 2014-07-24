/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.token;

import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.token.utility.TokenTestDataProvider;
import com.junbo.test.token.utility.TokenValidationHelper;

/**
 * Created by weiyu_000 on 7/24/14.
 */
public class BaseTokenTestClass extends TestClass {
    protected String offer_digital_normal1;
    protected String offer_digital_normal2;
    protected String offer_physical_normal1;
    protected String offer_physical_normal2;
    protected String offer_digital_preOrder;
    protected String offer_physical_preOrder;
    protected String offer_inApp_consumable1;
    protected String offer_inApp_consumable2;
    protected String offer_digital_free;
    protected String offer_physical_free;

    public BaseTokenTestClass() {
        super();
        loadOffers();
    }

    private void loadOffers() {
        offer_digital_normal1 = ConfigHelper.getSetting("testdata.offer.digital.normal1");
        offer_digital_normal2 = ConfigHelper.getSetting("testdata.offer.digital.normal2");
        offer_physical_normal1 = ConfigHelper.getSetting("testdata.offer.physical.normal1");
        offer_physical_normal2 = ConfigHelper.getSetting("testdata.offer.physical.normal2");
        offer_digital_preOrder = ConfigHelper.getSetting("testdata.offer.preorder.digital.normal");
        offer_physical_preOrder = ConfigHelper.getSetting("testdata.offer.preorder.physical.normal");
        offer_inApp_consumable1 = ConfigHelper.getSetting("testdata.offer.inApp.consumable.normal1");
        offer_inApp_consumable2 = ConfigHelper.getSetting("testdata.offer.inApp.consumable.normal2");
        offer_digital_free = ConfigHelper.getSetting("testdata.offer.digital.free");
        offer_physical_free = ConfigHelper.getSetting("testdata.offer.physical.free");
    }

    TokenTestDataProvider testDataProvider = new TokenTestDataProvider();
    TokenValidationHelper validationHelper = new TokenValidationHelper();

}
