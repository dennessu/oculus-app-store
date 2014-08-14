/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.commerce;

import com.junbo.test.buyerscenario.BuyerTestDataProvider;
import com.junbo.test.buyerscenario.BuyerValidationHelper;
import com.junbo.test.commerce.apiHelper.impl.utility.CommerceTestDataHelper;
import com.junbo.test.commerce.apiHelper.impl.utility.CommerceValidationHelper;
import com.junbo.test.common.ConfigHelper;

/**
 * Created by weiyu_000 on 8/13/14.
 */
public class TestCaseBase {
    protected CommerceTestDataHelper testDataProvider = new CommerceTestDataHelper();
    protected CommerceValidationHelper validationHelper = new CommerceValidationHelper();
    protected BuyerTestDataProvider buyerProvider = new BuyerTestDataProvider();
    protected BuyerValidationHelper buyerValidionHelper = new BuyerValidationHelper();

    protected String offer_digital_normal1;
    protected String offer_digital_normal2;
    protected String offer_physical_normal1;
    protected String offer_physical_normal2;
    protected String offer_digital_preOrder;
    protected String offer_physical_preOrder;
    protected String offer_inApp_consumable1;
    protected String offer_inApp_consumable2;
    protected String offer_storedValue_normal;
    protected String offer_digital_free;
    protected String offer_physical_free;
    protected String offer_digital_uat;
    protected String offer_download_uat;
    protected String offer_physical_uat;

    public TestCaseBase() {
        loadOffers();
    }

    private void loadOffers(){
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
        offer_storedValue_normal = ConfigHelper.getSetting("testdata.offer.storedvalue.normal");
        offer_digital_free = ConfigHelper.getSetting("testdata.offer.digital.free");
        offer_physical_free = ConfigHelper.getSetting("testdata.offer.physical.free");
        offer_digital_uat = ConfigHelper.getSetting("testdata.offer.digital.uat");
        offer_download_uat = ConfigHelper.getSetting("testdata.offer.download.uat");
        offer_physical_uat = ConfigHelper.getSetting("testdate.offer.physical.uat");
    }

}
