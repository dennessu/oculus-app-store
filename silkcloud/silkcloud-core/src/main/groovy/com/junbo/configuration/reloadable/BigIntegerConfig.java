/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.reloadable;

import com.junbo.configuration.reloadable.impl.ReloadableConfig;

import java.math.BigInteger;

/**.
 * Java doc
 */
public class BigIntegerConfig extends ReloadableConfig<BigInteger> {
    @Override
    protected BigInteger parseValue(String value) {
        return new BigInteger(value);
    }
}
