/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.reloadable;

import com.junbo.configuration.reloadable.impl.ReloadableConfig;

import java.math.BigDecimal;

/**.
 * Java doc
 */
public class BigDecimalConfig extends ReloadableConfig<BigDecimal> {
    @Override
    protected BigDecimal parseValue(String value) {
        return new BigDecimal(value);
    }
}
