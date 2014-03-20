/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.catalogscenario;

import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.testing.buyerscenario.util.BaseTestClass;
import com.junbo.testing.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.property.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 @author Jason
  * Time: 3/14/2014
  * For testing catalog scenarios
 */
public class Catalog extends BaseTestClass {

    private LogHelper logger = new LogHelper(Catalog.class);

    @Property(
            priority = Priority.BVT,
            features = "BuyerScenarios",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get Item APIs",
            steps = {
                    ""
            }
    )
    @Test
    public void testGetItem() throws Exception {

        HashMap<String, String> paraMap = new HashMap();
        paraMap.put("status", "Design");

        ItemServiceImpl itemServiceAPI = new ItemServiceImpl();
        Item itemGet = itemServiceAPI.getItem("218", paraMap);

        Assert.assertNotNull(itemGet);

        ResultList<Item> itemResultList = itemServiceAPI.getItem(paraMap);

        Assert.assertNotNull(itemResultList);
    }

}