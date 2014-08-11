/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.ui;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import org.openqa.selenium.WebDriver;

/**
 * Created by weiyu_000 on 8/11/14.
 */
public class WorkflowArgument {
    public Country global_country;
    public Currency global_taxCountry;


    public WorkflowArgument(WebDriver driver) {
        global_country = Country.DEFAULT;
        global_taxCountry = Currency.DEFAULT;

    }

}

