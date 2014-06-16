/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order;

import com.junbo.test.common.ConfigHelper;
import com.junbo.test.order.utility.OrderTestDataProvider;
import com.junbo.test.order.utility.OrderValidationHelper;

/**
 * Created by weiyu_000 on 5/19/14.
 */
public class BaseOrderTestClass {
    protected String offer_digital_normal1;
    protected String offer_digital_normal2;
    protected String offer_physical_normal1;
    protected String offer_physical_normal2;
    protected String offer_digital_preOrder;
    protected String offer_physical_preOrder;

    public BaseOrderTestClass() {
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
    }

    OrderTestDataProvider testDataProvider = new OrderTestDataProvider();
    OrderValidationHelper validationHelper = new OrderValidationHelper();
}
