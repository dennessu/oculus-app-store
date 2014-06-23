/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.common.util;

import java.math.BigDecimal;

/**
 * Configuration data.
 */
public class Configuration {
    private BigDecimal developerRatio;

    public BigDecimal getDeveloperRatio() {
        return developerRatio;
    }

    public void setDeveloperRatio(BigDecimal developerRatio) {
        this.developerRatio = developerRatio;
    }
}
