/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.ui.csr;

import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;


/**
 * Created by weiyu_000 on 8/11/14.
 */
public class CSR extends CSRTestCaseBase{

    @Property(
            priority = Priority.BVT,
            features = "CSR UI",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "sample test",
            steps = {
                    "1. Launch browser and navigate to specific url"
            }
    )
    @Test
    public void sampleTest() throws Exception {

        driver.navigate().to("http://www.baidu.com");
        WebElement input = driver.findElement(By.id("kw1"));
        input.sendKeys("123");
        WebElement submit = driver.findElement(By.id("su1"));
        submit.click();

    }

}
